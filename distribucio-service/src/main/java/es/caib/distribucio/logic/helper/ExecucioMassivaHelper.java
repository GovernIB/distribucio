package es.caib.distribucio.logic.helper;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;

import es.caib.distribucio.logic.intf.dto.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ContingutService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.persist.entity.ExecucioMassivaContingutEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import es.caib.distribucio.persist.repository.ExecucioMassivaContingutRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaRepository;

@Component
public class ExecucioMassivaHelper {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private RegistreService registreService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private AnnexosService annexosService;
	
	@Autowired
	private MessageHelper messageHelper;

	@Autowired
	private ExecucioMassivaRepository execucioMassivaRepository;
	@Autowired
	private ExecucioMassivaContingutRepository execucioMassivaContingutRepository;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ClassificacioResultatDto classificarNewTransaction(Long entitatId, Long elementId, String parametres) {
		String titol = getValorParametre(parametres, "titol", String.class);
		String procedimentCodi = getValorParametre(parametres, "codiProcediment", String.class);
		String serveiCodi = getValorParametre(parametres, "codiServei", String.class);
		
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatId, 
				elementId, 
				procedimentCodi, 
				serveiCodi, 
				titol);

		return resultat;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void reenviarNewTransaction(Long entitatId, Long elementId, String parametres) throws InterruptedException {
		Long[] destins = getValorParametre(parametres, "destins", Long[].class);
		Map<Long, String> destinsUsuari = getValorParametre(parametres, "destinsUsuari", new TypeReference<Map<Long, String>>() {});
		Boolean deixarCopia = getValorParametre(parametres, "deixarCopia", Boolean.class);
		String comentari= getValorParametre(parametres, "comentari", String.class);
		Long[] perConeixement = getValorParametre(parametres, "perConeixement", Long[].class);
		
		bustiaService.registreReenviar(
				entitatId,
				destins,
				elementId,
				deixarCopia,
				comentari,
				perConeixement,
				destinsUsuari,
				null);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void marcarProcessatNewTransaction(Long entitatId, Long elementId, String parametres) {
		String motiu = getValorParametre(parametres, "motiu", String.class);
		RegistreDto registre = registreService.findOne(entitatId, elementId, false);
		
		revisarEstatPerMarcarProcessat(registre);
		
		contingutService.marcarProcessat(
				entitatId, 
				elementId,
				"<span class='label label-default'>" + 
						messageHelper.getMessage("execucio.massiva.helper.accio.marcat.processat") + 
				"</span> " + motiu, 
				"DIS_ADMIN");
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void marcarPendentNewTransaction(Long entitatId, Long elementId, String parametres) {
		String motiu = getValorParametre(parametres, "motiu", String.class);
		
		registreService.marcarPendent(
				entitatId, 
				elementId,
				"<span class='label label-default'>" + 
						messageHelper.getMessage("execucio.massiva.helper.accio.marcat.pendent") + 
				"</span> " + motiu, 
				"DIS_ADMIN");
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void enviarViaEmailNewTransaction(Long entitatId, Long elementId, String parametres) throws MessagingException {
		boolean isVistaMoviments = getValorParametre(parametres, "isVistaMoviments", Boolean.class);
		String adreces = getValorParametre(parametres, "destinataris", String.class);
		String motiu = getValorParametre(parametres, "motiu", String.class);
		
		adreces = revisarAdreces(adreces);
		
		RegistreDto registre = registreService.findOne(entitatId, elementId, false);
		
		revisarEstatPerEnviarViaEmail(registre);
		
		bustiaService.registreAnotacioEnviarPerEmail(
				entitatId,
				elementId,
				adreces, 
				motiu,
				isVistaMoviments,
				"DIS_ADMIN");
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String custodiarAnnexosNewTransaction(Long elementId) {
		try {
			String missatge = null;
			ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = annexosService.guardarComADefinitiu(elementId);
			
			String annexTitol = resultatAnnexDefinitiu.getAnnexTitol();
			String anotacioNumero = resultatAnnexDefinitiu.getAnotacioNumero();
			
			if (resultatAnnexDefinitiu.isOk()) {
				missatge = messageHelper.getMessage(
						resultatAnnexDefinitiu.getKeyMessage(),
						new Object[] {annexTitol, anotacioNumero});
				
				return missatge;
			} else if (resultatAnnexDefinitiu.getThrowable() != null) {	
				missatge = messageHelper.getMessage(
						resultatAnnexDefinitiu.getKeyMessage(),
						new Object[] {annexTitol, anotacioNumero, resultatAnnexDefinitiu.getThrowable()});
				
				throw new RuntimeException(missatge);
			} else {
				missatge = messageHelper.getMessage(
						resultatAnnexDefinitiu.getKeyMessage(),
						new Object[] {annexTitol, anotacioNumero});
				
				throw new RuntimeException(missatge);
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String reintentarProcessamentNewTransaction(Long entitatId, Long elementId) {
		RegistreDto registre = registreService.findOne(entitatId, elementId, false);
		boolean correcte = false;
		String missatge = null;
		
		if (registre.getPare() == null) {
			// Restaura la bústia per defecte i la la regla aplicable si s'escau
			correcte = registreService.reintentarBustiaPerDefecte(
					entitatId,
					elementId);
			registre = registreService.findOne(entitatId, elementId, false);
			missatge = messageHelper.getMessage("execucio.massiva.helper.reintentar.processament.pare.restaurat");
		} else if ( ArrayUtils.contains(estatsReprocessables, registre.getProcesEstat())) {
			if (registre.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT 
				|| registre.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT) 
			{
				// Pendent de processament d'arxiu o regla
				correcte = registreService.reintentarProcessamentAdmin(
						entitatId, 
						elementId);
				missatge = "Anotació reprocessada " + (correcte ? "correctament" : "amb error");
			} else {
				// Pendent d'enviar a backoffice
				correcte = registreService.reintentarEnviamentBackofficeAdmin(
						entitatId, 
						elementId);
				missatge = "Anotació reenviada al backoffice " + (correcte ? "correctament" : "amb error");
			}
		} else if (this.isPendentArxiu(registre)||registre.getAnnexosEstatEsborrany()>0) {
			correcte = registreService.reintentarProcessamentAdmin(
					entitatId, 
					elementId);
			missatge = messageHelper.getMessage("execucio.massiva.helper.registre.desat.arxiu." + (correcte ? "ok" : "error"), null);
		} else {
			missatge = messageHelper.getMessage("execucio.massiva.helper.reintentar.processament.reprocessables.no.detectat");
			correcte = true;
		}
		
		if (correcte) {
			return missatge;
		} else {
			String error = messageHelper.getMessage(
					"execucio.massiva.helper.reintentar.processament.error",
					new Object[] {
							elementId, 
							missatge});
			throw new RuntimeException(error);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String reintentarBackofficeNewTransaction(Long entitatId, Long elementId) {
		RegistreDto registre = registreService.findOne(entitatId, elementId, false);
		String missatge = null;
		
		if (ArrayUtils.contains(estatsReenviablesBackoffices, registre.getProcesEstat())) {
			boolean correcte = registreService.reintentarEnviamentBackofficeAdmin(
					entitatId, 
					elementId);
			missatge = "Anotació reenviada al backoffice " + (registre.getBackCodi()) + " " + (correcte ? "correctament" : "amb error");
		}else {
			missatge = messageHelper.getMessage("execucio.massiva.helper.enviament.backoffice.estat.incompatible", new Object[] {elementId}); 
		}
		
		return missatge;
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String sobreescriureNewTransaction(Long entitatId, Long elementId) {
		RegistreDto registre = registreService.findOne(entitatId, elementId, false);
		boolean correcte = false;
		String missatge = null;
		
		if (RegistreProcesEstatEnum.isPendent(registre.getProcesEstat()) && !registre.isArxiuTancat()) {
			registreService.marcarSobreescriure(
					entitatId, 
					elementId);
			missatge = messageHelper.getMessage(
					"execucio.massiva.helper.marcar.sobreescriure.ok", 
					new Object[] {elementId});
			correcte = true;
		} else {
			missatge = messageHelper.getMessage(
					"execucio.massiva.helper.marcar.sobreescriure.estat.error", 
					new Object[] {
							elementId, 
							registre.getProcesEstat()});
			correcte = false;
		}
		
		if (correcte) {
			return missatge;
		} else {
			throw new RuntimeException(missatge);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateFinalitzatNewTransaction(ExecucioMassivaEntity em, Date dataFi) {
		em.updateFinalitzat(dataFi);
		execucioMassivaRepository.saveAndFlush(em);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateProcessantNewTransaction(ExecucioMassivaEntity em, Date dataInici) {
		em.updateProcessant(dataInici);
		execucioMassivaRepository.saveAndFlush(em);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateProcessantNewTransaction(ExecucioMassivaContingutEntity emc, Date dataInici) {
		emc.updateProcessant(dataInici);
		execucioMassivaContingutRepository.saveAndFlush(emc);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateFinalitzatNewTransaction(ExecucioMassivaContingutEntity emc, Date dataFi) {
		emc.updateFinalitzat(dataFi);
		execucioMassivaContingutRepository.saveAndFlush(emc);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateErrorNewTransaction(ExecucioMassivaContingutEntity emc, Date dataFi, String error) {
		emc.updateError(error, dataFi);
		execucioMassivaContingutRepository.saveAndFlush(emc);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateMissatgeNewTransaction(ExecucioMassivaContingutEntity emc, String missatge) {
		emc.updateMissatge(missatge);
		execucioMassivaContingutRepository.saveAndFlush(emc);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateCancelatNewTransaction(ExecucioMassivaEntity em) {
		em.updateCancelat();
		execucioMassivaRepository.saveAndFlush(em);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updatePausatNewTransaction(ExecucioMassivaEntity em) {
		em.updatePausat();
		execucioMassivaRepository.saveAndFlush(em);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updatePendentNewTransaction(ExecucioMassivaEntity em) {
		em.updatePendent();
		execucioMassivaRepository.saveAndFlush(em);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public boolean isEmcDisponibleNewTransaction(ExecucioMassivaContingutEntity emc) {
		Optional<ExecucioMassivaContingutEntity> emcNewTransaction = execucioMassivaContingutRepository.findById(emc.getId());
		return emcNewTransaction.isPresent() && ExecucioMassivaContingutEstatDto.PENDENT.equals(emcNewTransaction.get().getEstat());
	}
	
	/** Realitza les següents accions:
	 * - Revisa que no es repeteixin les adreces.
	 * - Substitueix els espais en blanc per comes.
	 * - Revisa que les adreces siguin correctes, en cas contrari afegeix un error.
	 * - Revisa que com a mínim hi hagi una adreça.
	 * @param request 
	 * @param adreces
	 * @param bindingResult
	 * @return
	 */
	private String revisarAdreces(String adreces) {
		Set<String> adrecesRevisades = new HashSet<>();
		Set<String> adrecesErronies = new HashSet<>();
		if (adreces != null && !adreces.isEmpty() ) {
			// substitueix els espais per comes
			adreces = adreces.replaceAll("\\s*,\\s*|\\s+", ",");
			for(String adr : adreces.split(",")) {
				if (!adrecesRevisades.contains(adr) && !adrecesErronies.contains(adr)) {
					if (adr.matches("\\S+@\\S+[.\\S+]+")) {
						adrecesRevisades.add(adr);
					} else {
						adrecesErronies.add(adr);
					}
				}
			}
			if (adrecesErronies.size() > 0) 
				throw new ValidationException(
						messageHelper.getMessage(
								"execucio.massiva.helper.pendent.contingut.enviar.email.validacio.adreces",
								new Object[] {StringUtils.join(adrecesErronies.toArray(), ", ")}));
			}
		return StringUtils.join(adrecesRevisades,",");
	}
	
	/** Valida que l'anotació no estigui pendent d'arxiu o si ho està que hagi esgotat els reintents. */
	private void revisarEstatPerEnviarViaEmail(RegistreDto registreDto) {
		if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && !registreDto.isReintentsEsgotat()) {
			throw new ValidationException(
					messageHelper.getMessage(
							"execucio.massiva.helper.pendent.contingut.enviar.email.validacio.estat",
							new Object[] {registreDto.getProcesEstat()}));
		}	
	}

	/** Valida que l'anotació estigui pendent de bústia o que estigui pendent d'Arxiu i hagi esgotat els reintents. */
	private void revisarEstatPerMarcarProcessat(RegistreDto registreDto) {
		if (registreDto.getProcesEstat() != RegistreProcesEstatEnum.BUSTIA_PENDENT
				&& !(registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && registreDto.isReintentsEsgotat())) {
			throw new ValidationException(
					messageHelper.getMessage(
							"execucio.massiva.helper.marcar.processat.validacio.estat",
							new Object[] {registreDto.getProcesEstat()}));
		}	
	}
	
    private <T> T getValorParametre(String json, String clau, Class<T> clazz) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode node = root.get(clau);
            return node != null ? mapper.treeToValue(node, clazz) : (clazz == Boolean.class ? clazz.cast(Boolean.FALSE) : null);
        } catch (Exception e) {
            throw new RuntimeException("Error llegint '" + clau + "' com a " + clazz.getSimpleName(), e);
        }
    }

    private <T> T getValorParametre(String json, String clau, TypeReference<T> typeRef) {
        try {
            JsonNode root = mapper.readTree(json);
            JsonNode node = root.get(clau);
            return node != null ? mapper.readValue(node.toString(), typeRef) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error llegint '" + clau + "' amb TypeReference", e);
        }
    }

	private boolean isPendentArxiu(RegistreDto registreDto) {
		boolean isPendentArxiu = false;
		if (registreDto.getExpedientArxiuUuid() == null) {
			isPendentArxiu = true;
		} else {
			for (RegistreAnnexDto registreAnnex : registreDto.getAnnexos()) {
				if (registreAnnex.getFitxerArxiuUuid() == null) {
					isPendentArxiu = true;
				}
			}
		}
		List<RegistreAnnexDto> llistatAnnexes = registreDto.getAnnexos();
		for (RegistreAnnexDto registreAnnex : llistatAnnexes) {
			if (registreAnnex.getFitxerArxiuUuid() == null) {
				isPendentArxiu = true;
			}
		}
		return isPendentArxiu;
	}
	
	/** Estats que permeten el reprocessament */
	private static RegistreProcesEstatEnum[] estatsReprocessables = {
			RegistreProcesEstatEnum.ARXIU_PENDENT,
			RegistreProcesEstatEnum.REGLA_PENDENT,
			RegistreProcesEstatEnum.BACK_PENDENT,
			RegistreProcesEstatEnum.BACK_COMUNICADA,
			RegistreProcesEstatEnum.BACK_REBUDA,
			RegistreProcesEstatEnum.BACK_ERROR,
			RegistreProcesEstatEnum.BACK_PROCESSADA,
			RegistreProcesEstatEnum.BACK_REBUTJADA,
	};

	/** Estats que permeten el renviament al backoffice */
	private static RegistreProcesEstatEnum[] estatsReenviablesBackoffices = {
			RegistreProcesEstatEnum.BACK_PENDENT,
			RegistreProcesEstatEnum.BACK_COMUNICADA,
			RegistreProcesEstatEnum.BACK_REBUDA,
			RegistreProcesEstatEnum.BACK_ERROR,
			RegistreProcesEstatEnum.BACK_PROCESSADA,
			RegistreProcesEstatEnum.BACK_REBUTJADA,
	};
    
}
