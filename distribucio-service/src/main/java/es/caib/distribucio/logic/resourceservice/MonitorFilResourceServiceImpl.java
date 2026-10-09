package es.caib.distribucio.logic.resourceservice;

import es.caib.comanda.model.server.monitoring.InformacioSistema;
import es.caib.comanda.ms.salut.helper.MonitorHelper.CpuUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.DiskUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.JvmInfo;
import es.caib.comanda.ms.salut.helper.MonitorHelper.MemoryUsage;
import es.caib.comanda.ms.salut.helper.MonitorHelper.SystemInfo;
import es.caib.distribucio.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.distribucio.logic.helper.MessageHelper;
import es.caib.distribucio.logic.helper.MonitorHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.distribucio.logic.intf.model.MonitorFilResource;
import es.caib.distribucio.logic.intf.resourceservice.MonitorFilResourceService;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei del recurs {@link MonitorFilResource}: fils d'execució de la JVM i informació general del
 * sistema. Les dades es llegeixen en temps real de la JVM. No hi ha accés a base de dades.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonitorFilResourceServiceImpl
		extends BaseNoDatabaseMutableResourceService<MonitorFilResource, Long>
		implements MonitorFilResourceService {

	private final MessageHelper messageHelper;

	@PostConstruct
	public void init() {
		register(MonitorFilResource.ACTION_INFORMACIO_SISTEMA_CODE, new InformacioSistemaActionExecutor());
	}

	@Override
	public Page<MonitorFilResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		String quickFilterLower = Utils.hasValue(quickFilter) ? quickFilter.toLowerCase(Locale.ROOT) : null;
		List<MonitorFilResource> resultat = MonitorHelper.getFils().stream()
				.filter(fil -> quickFilterLower == null || fil.getNom().toLowerCase(Locale.ROOT).contains(quickFilterLower))
				.map(fil -> {
					MonitorFilResource resource = new MonitorFilResource();
					resource.setId(fil.getId());
					resource.setNom(fil.getNom());
					resource.setEstat(messageHelper.getMessage("monitor." + fil.getEstat()));
					resource.setTempsCpu(fil.getTempsCpuPercent() + " %");
					resource.setTempsEspera(fil.getTempsEsperaNs() + " ns");
					resource.setTempsBloqueig(fil.getTempsBloqueigNs() + " ns");
					return resource;
				})
				.collect(Collectors.toList());
		return new PageImpl<>(resultat, pageable, resultat.size());
	}

	@Override
	public MonitorFilResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		throw new ResourceNotFoundException(MonitorFilResource.class, String.valueOf(id));
	}

	private class InformacioSistemaActionExecutor implements
			ActionExecutor<NoDatabaseResourceEntity<MonitorFilResource, Long>, Serializable, MonitorFilResource.InformacioSistema> {

		@Override
		public MonitorFilResource.InformacioSistema exec(
				String code,
				NoDatabaseResourceEntity<MonitorFilResource, Long> entity,
				Serializable params) throws ActionExecutionException {
			try {
				MonitorFilResource.InformacioSistema info = new MonitorFilResource.InformacioSistema();
				// Dades de la pantalla /monitor de la interfície JSP
				info.setArquitectura(MonitorHelper.getArch());
				info.setFilsDaemon(MonitorHelper.getFilsDaemon());
				info.setFilsDeadlock(MonitorHelper.getFilsDeadlock());
				long memoriaMaxima = MonitorHelper.getMemoriaMaxima();
				info.setMemoriaMaxima(memoriaMaxima >= 0 ? MonitorHelper.humanReadableByteCount(memoriaMaxima) : null);
				// Dades de la llibreria de COMANDA
				InformacioSistema informacioSistema = es.caib.comanda.ms.salut.helper.MonitorHelper.getInfoSistema();
				info.setSistemaOperatiu(informacioSistema.getSistemaOperatiu());
				info.setProcessadors(informacioSistema.getProcessadors());
				info.setTempsFuncionant(informacioSistema.getTempsFuncionant());
				SystemInfo systemInfo = es.caib.comanda.ms.salut.helper.MonitorHelper.getSystemInfo();
				info.setJvm(systemInfo.getJvm());
				info.setVersioJdk(systemInfo.getJdkVersion());
				info.setDataArrencada(systemInfo.getFormatedStartTime());
				info.setVersioJboss(es.caib.comanda.ms.salut.helper.MonitorHelper.getJBossVersion());
				info.setServidorAplicacions(es.caib.comanda.ms.salut.helper.MonitorHelper.getApplicationServerInfo());
				JvmInfo jvmInfo = es.caib.comanda.ms.salut.helper.MonitorHelper.getJvmInfo();
				info.setFilsActius(jvmInfo.getThreadCount());
				info.setFilsPic(jvmInfo.getPeakThreadCount());
				info.setGcExecucions(jvmInfo.getGcCount());
				info.setGcTemps(jvmInfo.getGcTime());
				info.setMemoriaJvm(toMemoriaUs(es.caib.comanda.ms.salut.helper.MonitorHelper.getJvmMemory()));
				info.setMemoriaFisica(toMemoriaUs(es.caib.comanda.ms.salut.helper.MonitorHelper.getPhisicalMemory()));
				CpuUsage cpuUsage = es.caib.comanda.ms.salut.helper.MonitorHelper.getCpuUsage();
				info.setNuclis(cpuUsage.getCores());
				info.setCarregaMitjana(cpuUsage.getFormatedLoadAverage());
				info.setCarregaCpuSistema(cpuUsage.isValidSystemCpuLoad() ? cpuUsage.getFormatedSystemCpuLoad() : null);
				info.setCarregaCpuProces(cpuUsage.isValidProcessCpuLoad() ? cpuUsage.getFormatedProcessCpuLoad() : null);
				List<MonitorFilResource.EspaiDisc> discos = new ArrayList<>();
				List<DiskUsage> disksUsage = es.caib.comanda.ms.salut.helper.MonitorHelper.getDisksUsage();
				if (disksUsage != null) {
					for (DiskUsage disk : disksUsage) {
						MonitorFilResource.EspaiDisc espaiDisc = new MonitorFilResource.EspaiDisc();
						espaiDisc.setNom(disk.getNom());
						espaiDisc.setUsat(disk.getUsedSpace());
						espaiDisc.setTotal(disk.getTotalSpace());
						espaiDisc.setUsatFormatat(disk.getFormatedUsedSpace());
						espaiDisc.setTotalFormatat(disk.getFormatedTotalSpace());
						discos.add(espaiDisc);
					}
				}
				info.setDiscos(discos);
				return info;
			} catch (Exception ex) {
				log.error("Error obtenint la informació del sistema", ex);
				throw new ActionExecutionException(MonitorFilResource.class, null, code, ex);
			}
		}

		private MonitorFilResource.MemoriaUs toMemoriaUs(MemoryUsage memoryUsage) {
			if (memoryUsage == null) {
				return null;
			}
			MonitorFilResource.MemoriaUs memoria = new MonitorFilResource.MemoriaUs();
			memoria.setUsada(memoryUsage.getUsedMemory());
			memoria.setTotal(memoryUsage.getTotalMemory());
			memoria.setUsadaFormatada(memoryUsage.getFormatedUsedMemory());
			memoria.setTotalFormatada(memoryUsage.getFormatedTotalMemory());
			return memoria;
		}

		@Override
		public void onChange(
				Serializable id,
				Serializable previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerValue> answers,
				String[] previousFieldNames,
				Serializable target) {
		}

	}

}
