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

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.helper.AlertaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.UsuariRepository;

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
	@Resource
	private ContingutMovimentRepository contingutMovimentRepository;
	@Resource
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Resource
	private UsuariRepository usuariRepository;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat}")
	public void comprovarEnviamentEmailsNoAgrupatsBustia() {
		comprovarEnviamentEmailsBustia(false);
	}
	
	@Override
	@Transactional
	@Scheduled(cron = "${config:es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat}")
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
	
	public static void saveError(Long execucioMassivaContingutId, Throwable error) {
		StringWriter out = new StringWriter();
		error.printStackTrace(new PrintWriter(out));
		errorsMassiva.put(execucioMassivaContingutId, out.toString());
	}
	
}
