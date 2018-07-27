/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.helper.AlertaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ExecucioMassivaService;
import es.caib.ripea.core.api.service.SegonPlaService;
import es.caib.ripea.core.entity.ContingutMovimentEmailEntity;
import es.caib.ripea.core.entity.ExecucioMassivaContingutEntity;
import es.caib.ripea.core.entity.ExecucioMassivaEntity.ExecucioMassivaTipus;
import es.caib.ripea.core.repository.ContingutMovimentEmailRepository;
import es.caib.ripea.core.repository.ContingutMovimentRepository;
import es.caib.ripea.core.repository.ExecucioMassivaContingutRepository;
import es.caib.ripea.core.repository.ExecucioMassivaRepository;
import es.caib.ripea.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar documents.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SegonPlaServiceImpl implements SegonPlaService {
	
	
	@Resource
	private AlertaHelper alertaHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private EmailHelper emailHelper;
	@Resource
	private CacheHelper cacheHelper;
	
	@Autowired
	private ExecucioMassivaService execucioMassivaService;
	
	
	@Resource
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Resource
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	@Resource
	private ContingutMovimentRepository contingutMovimentRepository;
	@Resource
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Resource
	private UsuariRepository usuariRepository;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.ripea.segonpla.massives.periode.comprovacio}")
	public void comprovarExecucionsMassives() {
		boolean active = true;
		Long ultimaExecucioMassiva = null;
		
		int timeBetweenExecutions = 500;
		try {
			timeBetweenExecutions = Integer.parseInt(
					PropertiesHelper.getProperties().getProperty("es.caib.ripea.plugin.arxiu.class"));
		} catch (Exception ex) {}
		
		while (active) {
			Long cmasiu_id = null;
			boolean alertat = false;
			try {
				cmasiu_id = execucioMassivaService.getExecucionsMassivesActiva(ultimaExecucioMassiva);
				if (cmasiu_id != null) {
					ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(cmasiu_id);
					
					try {
						execucioMassivaService.executarExecucioMassiva(cmasiu_id);
						
						if (emc != null)
							alertaHelper.crearAlerta(
									messageHelper.getMessage(
											"alertes.segon.pla.execucio.massiva",
											new Object[] {cmasiu_id}),
									null,
									emc.getContingut().getId());
					}
					catch (Exception e) {
						// recuperem l'error de la aplicació
						String errMsg = getError(cmasiu_id);
						if (errMsg == null || "".equals(errMsg))
							errMsg = e.getMessage();
						execucioMassivaService.generaInformeError(cmasiu_id, errMsg);
						
						if (emc != null)
							alertaHelper.crearAlerta(
									messageHelper.getMessage(
											"alertes.segon.pla.executar.execucio.massiva.error",
											new Object[] {cmasiu_id}),
									e,
									emc.getContingut().getId());
						
						alertat = true;
					}
					if (emc == null)
						throw new NotFoundException(cmasiu_id, ExecucioMassivaContingutEntity.class);
					ultimaExecucioMassiva = emc.getExecucioMassiva().getId();
					execucioMassivaService.actualitzaUltimaOperacio(emc.getId());
				} else {
					active = false;
				}
				Thread.sleep(timeBetweenExecutions);
			} catch (Exception e) {
				logger.error("La execució de execucions massives ha estat interromput");
				active = false;
				
				if(!alertat && cmasiu_id != null) {
					ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findOne(cmasiu_id);
					alertaHelper.crearAlerta(
							messageHelper.getMessage(
									"alertes.segon.pla.execucio.massiva.error",
									new Object[] {cmasiu_id}),
							e,
							emc.getContingut().getId());
					alertat = true;
				}
			}
		}
	}
	
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${config:es.caib.ripea.segonpla.email.bustia.periode.enviament.no.agrupat}")
	public void comprovarEnviamentEmailsNoAgrupatsBustia() {
		comprovarEnviamentEmailsBustia(false);
	}
	
	@Override
	@Transactional
	@Scheduled(cron = "${config:es.caib.ripea.segonpla.email.bustia.cron.enviament.agrupat}")
	public void comprovarEnviamentEmailsAgrupatsBustia() {
		comprovarEnviamentEmailsBustia(true);
	}
	
	private void comprovarEnviamentEmailsBustia(boolean agrupats) {
		
		List<ContingutMovimentEmailEntity> moviments = null;
		
		if (agrupats)
			moviments = contingutMovimentEmailRepository.findByEnviamentAgrupatTrueOrderByDestinatariAscBustiaAsc();
		else
			moviments = contingutMovimentEmailRepository.findByEnviamentAgrupatFalseOrderByDestinatariAscBustiaAsc();
		
		HashMap<String, List<ContingutMovimentEmailEntity>> contingutsEmail = new HashMap<String, List<ContingutMovimentEmailEntity>>();
		for (ContingutMovimentEmailEntity contingutEmail : moviments) {
			if (contingutsEmail.containsKey(contingutEmail.getEmail())) {
				contingutsEmail.get(contingutEmail.getEmail()).add(contingutEmail);
			} else {
				List<ContingutMovimentEmailEntity> lContingutEmails = new ArrayList<ContingutMovimentEmailEntity>();
				lContingutEmails.add(contingutEmail);
				contingutsEmail.put(contingutEmail.getEmail(), lContingutEmails);
			}
		}
		
		for (String email: contingutsEmail.keySet()) {
			emailHelper.sendEmailBustiaPendentContingut(
					email,
					agrupats,
					contingutsEmail.get(email));
			contingutMovimentEmailRepository.delete(contingutsEmail.get(email));
		}
	}
	
	public static void saveError(Long execucioMassivaContingutId, Throwable error, ExecucioMassivaTipus tipus) {
		StringWriter out = new StringWriter();
		error.printStackTrace(new PrintWriter(out));
		errorsMassiva.put(execucioMassivaContingutId, out.toString());
	}
	
	private static String getError(Long operacioMassivaId) {
		String error = errorsMassiva.get(operacioMassivaId);
		errorsMassiva.remove(operacioMassivaId);
		return error;
	}

	private static final Logger logger = LoggerFactory.getLogger(SegonPlaServiceImpl.class);

}
