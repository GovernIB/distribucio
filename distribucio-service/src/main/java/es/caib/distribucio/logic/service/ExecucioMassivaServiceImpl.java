package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.ExecucioMassivaHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.UsuariHelper;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaAccioDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaContingutEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.ExecucioMassivaContingutRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;

/**
 * Implementació del servei per gestionar les execucions massives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ExecucioMassivaServiceImpl implements ExecucioMassivaService {

	private Map<Long, Semaphore> semafors = new HashMap<Long, Semaphore>();
	
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ExecucioMassivaHelper execucioMassivaHelper;
	
	@Autowired
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private PluginHelper pluginHelper;
	
	@Transactional
	@Override
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto)
			throws NotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		UsuariEntity usuari = usuariRepository.findByCodi(auth.getName());
		
		ExecucioMassivaEntity execucioMassiva = ExecucioMassivaEntity.hiddenBuilder()
				.entitat(entitat)
				.usuari(usuari)
				.tipus(dto.getTipus())
				.estat(ExecucioMassivaEstatDto.PENDENT)
				.dataCreacio(dto.getDataCreacio())
				.parametres(dto.getParametres())
				.build();
				
		int ordre = 0;
		for (ExecucioMassivaContingutDto execucioMassivaContingutDto : dto.getContinguts()) {
			ExecucioMassivaContingutEntity execucioMassivaContingut = ExecucioMassivaContingutEntity.hiddenBuilder()
					.execucioMassiva(execucioMassiva)
					.dataCreacio(execucioMassivaContingutDto.getDataCreacio())
					.estat(ExecucioMassivaContingutEstatDto.PENDENT)
					.elementId(execucioMassivaContingutDto.getElementId())
					.elementNom(execucioMassivaContingutDto.getElementNom())
					.elementTipus(execucioMassivaContingutDto.getElementTipus())
					.ordre(ordre++)
					.build();
			
			execucioMassiva.addContingut(execucioMassivaContingut);
		}
		
		execucioMassivaRepository.save(execucioMassiva);
	}

	@Transactional
	@Override
	public void updateExecucioMassiva(ExecucioMassivaAccioDto accio, Long exm_id) throws NotFoundException {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(exm_id).orElse(null);
		if (execucioMassiva == null)
			throw new NotFoundException(exm_id, ExecucioMassivaEntity.class);
		
		switch (accio) {
		case CANCELAR:
			execucioMassivaHelper.updateCancelatNewTransaction(execucioMassiva);
			break;
		case PAUSAR:
			execucioMassivaHelper.updatePausatNewTransaction(execucioMassiva);
			break;
		case REPRENDRE:
			execucioMassivaHelper.updatePendentNewTransaction(execucioMassiva);
			break;
		}
		
	}
	
	@Override
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		
		Pageable paginacio = PageRequest.of(pagina, 8, Direction.DESC, "dataInici");
		
		List<ExecucioMassivaEntity> exmEntities = new ArrayList<ExecucioMassivaEntity>();
		if (usuari == null) {
			exmEntities = execucioMassivaRepository.findByEntitatIdOrderByCreatedDateDesc(
					entitat.getId(), 
					paginacio);
		} else {
			UsuariEntity usuariEntity = usuariRepository.findByCodi(usuari.getCodi());
			exmEntities = execucioMassivaRepository.findByUsuariAndEntitatIdOrderByCreatedDateDesc(
					usuariEntity, 
					entitat.getId(), 
					paginacio);
		}
		
		return recompteErrors(exmEntities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(exm_id).orElse(null);
		if (execucioMassiva == null)
			throw new NotFoundException(exm_id, ExecucioMassivaEntity.class);
		
		List<ExecucioMassivaContingutEntity> continguts = execucioMassivaContingutRepository.findByExecucioMassivaOrderByOrdreAsc(execucioMassiva);
		List<ExecucioMassivaContingutDto> dtos = conversioTipusHelper.convertirList(continguts, ExecucioMassivaContingutDto.class);
		
		return dtos;
	}

	@Transactional
	@Override
	public void executeNextMassiveScheduledTask(Long entitatId) {
		logger.trace("Execució tasca periòdica: Execucions massives");

		try {
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
			List<ExecucioMassivaEntity> ems = execucioMassivaRepository.findMassivesAmbPendentsByEntitatPerProcessar(new Date(), entitatId);
			String missatge = null;
			
			if (ems != null && ems.size() > 0) {
				ExecucioMassivaEntity em = ems.get(0);
				if (em.getContinguts() != null) {
					execucioMassivaHelper.updateProcessantNewTransaction(em, new Date());
					
					for (ExecucioMassivaContingutEntity emc: em.getContinguts()) {
						setAuthentication(emc);
						execucioMassivaHelper.updateProcessantNewTransaction(emc, new Date());
						
						if (execucioMassivaHelper.isEmcDisponibleNewTransaction(emc)) {
							
							entrarSemafor(emc.getElementId());
							
							try {
								switch (em.getTipus()) {
								case CLASSIFICAR:
									execucioMassivaHelper.classificarNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case REENVIAR:
									execucioMassivaHelper.reenviarNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case MARCAR_PROCESSAT:
									execucioMassivaHelper.marcarProcessatNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case MARCAR_PENDENT:
									execucioMassivaHelper.marcarPendentNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case ENVIAR_VIA_EMAIL:
									execucioMassivaHelper.enviarViaEmailNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case ENVIAR_VIA_EMAIL_PROCESSAR:
									execucioMassivaHelper.enviarViaEmailNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									
									execucioMassivaHelper.marcarProcessatNewTransaction(
											entitat.getId(), 
											emc.getElementId(),
											em.getParametres());
									break;
								case CUSTODIAR:
									missatge = execucioMassivaHelper.custodiarAnnexosNewTransaction(emc.getElementId());
									
									emc.updateMissatge(missatge);
									break;
								case PROCESSAR:
									missatge = execucioMassivaHelper.reintentarProcessamentNewTransaction(
											entitat.getId(), 
											emc.getElementId());
									
									emc.updateMissatge(missatge);
									break;
								case BACKOFFICE:
									missatge = execucioMassivaHelper.reintentarBackofficeNewTransaction(
											entitat.getId(), 
											emc.getElementId());
									
									emc.updateMissatge(missatge);
									break;
								case SOBREESCRIURE:
									missatge = execucioMassivaHelper.sobreescriureNewTransaction(
											entitat.getId(), 
											emc.getElementId());
									
									emc.updateMissatge(missatge);
									break;
									
								default:
									break;
								}
							} catch (Exception e) {
								logger.error("Hi ha hagut un error executant el contingut de l'acció massiva [id=" + emc.getId() + "]", e);
								execucioMassivaHelper.updateErrorNewTransaction(
										emc,
										new Date(),
										e.getMessage());
								continue;
							} finally {
								sortirSemafor(emc.getElementId());
								removeAuthentication();
							}
						} else {
							// Si s'ha cancel·lat pausat abans d'acabar
							continue;
						}
						execucioMassivaHelper.updateFinalitzatNewTransaction(emc, new Date());
					}
					execucioMassivaHelper.updateFinalitzatNewTransaction(em, new Date());
				}
			}
		} catch (Exception e) {
			logger.error("Ha habido un error procesando las ejecuciones masivas", e);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<ExecucioMassivaContingutDto> findExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		
		List<ExecucioMassivaContingutEntity> execucioMassivaContinguts = new ArrayList<> ();
		if (continguts != null) {
			// Consulta de 1000 en 1000 els que estan en el llistat.
			for (int i = 0; i < continguts.size(); i += 1000) {
				execucioMassivaContinguts.addAll(
						execucioMassivaContingutRepository.findByContingutsAndEstatIn(
						continguts.subList(i, Math.min(continguts.size(), i+1000)),
						new ArrayList<> (
								Arrays.asList(
										ExecucioMassivaContingutEstatDto.PENDENT, 
										ExecucioMassivaContingutEstatDto.PROCESSANT)
								)
						));
				
			}
		}
		
		return conversioTipusHelper.convertirList(
				execucioMassivaContinguts, 
				ExecucioMassivaContingutDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<String> findElementNomExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		
		List<String> elementsNom = new ArrayList<>();		
		if (continguts != null) {
			// Consulta de 1000 en 1000 els que estan en el llistat.
			for (int i = 0; i < continguts.size(); i += 1000) {
				elementsNom.addAll(
						execucioMassivaContingutRepository.findElementNomByContingutsAndEstatIn(
								continguts.subList(i, Math.min(continguts.size(), i+1000)),
								new ArrayList<> (
										Arrays.asList(
												ExecucioMassivaContingutEstatDto.PENDENT, 
												ExecucioMassivaContingutEstatDto.PROCESSANT)
										)
								));
			}
		}

		
		return elementsNom;
	}
	
	private void setAuthentication(ExecucioMassivaContingutEntity emc) {
		UsuariEntity usuariActual = usuariHelper.getUsuariAutenticat();
		UsuariEntity usuariEmc = emc.getExecucioMassiva().getUsuari();
		if (usuariActual == null && usuariEmc != null) {
			List<String> rolsUsuariActual = pluginHelper.findRolsPerUsuari(usuariEmc.getCodi());
			if (rolsUsuariActual.isEmpty())
				rolsUsuariActual.add("tothom");
	
			List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
			for (String rol : rolsUsuariActual) {
				SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(rol);
				authorities.add(simpleGrantedAuthority);
			}
			
			Authentication authentication =  new UsernamePasswordAuthenticationToken(
					usuariEmc.getCodi(), 
					"N/A",
					authorities);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}
	
	private void removeAuthentication() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}
	
	private List<ExecucioMassivaDto> recompteErrors(List<ExecucioMassivaEntity> exmEntities) {
		List<ExecucioMassivaDto> dtos = new ArrayList<ExecucioMassivaDto>();
		for (ExecucioMassivaEntity exm: exmEntities) {
			ExecucioMassivaDto dto = conversioTipusHelper.convertir(exm, ExecucioMassivaDto.class);
			int errors = 0;
			Long pendents = 0L;
			for (ExecucioMassivaContingutEntity emc: exm.getContinguts()) {
				if (emc.getEstat() == ExecucioMassivaContingutEstatDto.ERROR)
					errors ++;
				if (emc.getDataFi() == null)
					pendents++;
				dto.getContingutIds().add(emc.getId());
			}
			dto.setErrors(errors);
			Long total = Long.valueOf(dto.getContingutIds().size());
			dto.setExecutades(getPercent((total - pendents), total));
			dtos.add(dto);
		}
		return dtos;
	}
	
	private double getPercent(Long value, Long total) {
		if (total == 0)
			return 100L;
		else if (value == 0L)
			return 0L;
	    return Math.round(value * 100 / total);
	}

	private void entrarSemafor(Long registreId) throws InterruptedException {
		Semaphore semafor = null;
		synchronized(semafors) {
			if (semafors.containsKey(registreId)) {
				semafor = semafors.get(registreId);
			} else {
				semafor = new Semaphore(1);
				semafors.put(registreId, semafor);
			}
		}
		semafor.acquire();
	}
	
	private void sortirSemafor(Long registreId) {
		synchronized(semafors) {
			Semaphore semafor = semafors.get(registreId);
			if (semafor != null) {
				if (semafor.getQueueLength()==0) {
					semafors.remove(registreId);
				}
				semafor.release();
			}
		}
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ExecucioMassivaServiceImpl.class);

}
