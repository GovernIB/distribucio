/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatAccionEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.exception.AplicarReglaException;
import es.caib.distribucio.logic.intf.exception.ScheduledTaskException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.ContingutMovimentEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.repository.ReglaRepository;

/**
 * Mètodes comuns per a aplicar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ReglaHelper {

	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private AlertaHelper alertaHelper;
	@Autowired
	private GestioDocumentalHelper gestioDocumentalHelper;	
	@Autowired
	private ConfigHelper configHelper;

	private final static String CLAU_XIFRAT = "3çS)ZX!3a94_*?S2";

	/** Troba la primera regla aplicable després de la regla actual en cas que no sigui nul·la en la llista
	 * de regles. Si itera i no en troba cap retorna null.
	 * 
	 * @param reglaActual Regla a partir de la qual se cercarà dins la llista. Li la regla està marcada per aturar avaluació no
	 * se cercarà cap regla.
	 * @param regles Conté el llistat de regles per avaluar si són aplicables o no.
	 * @return
	 */
	private ReglaEntity findSeguent(
			EntitatEntity entitat,
			ReglaEntity reglaActual,
			Long unitatId,
			Long bustiaId,
			String procedimentCodi,
			String assumpteCodi,
			Boolean presencial) {
		if (reglaActual != null && reglaActual.isAturarAvaluacio()) {
			return null;
		}
		ReglaPresencialEnumDto esPresencial = null;
		if (presencial != null) {
			esPresencial = presencial == true ? ReglaPresencialEnumDto.SI : ReglaPresencialEnumDto.NO;
		}
		List<ReglaEntity> regles = reglaRepository.findAplicables(
				entitat,
				unitatId,
				bustiaId,
				procedimentCodi != null ? procedimentCodi : "",
				assumpteCodi != null ? assumpteCodi : "", 
				esPresencial == null,
				esPresencial);
		if (regles == null || regles.isEmpty()) {
			return null;
		}
		if (reglaActual == null) {
			return regles.get(0);
		} 
		// Cerca la següent a partir del número d'ordre actual
		int iSeguent = 0;
		ReglaEntity reglaSeguent = null;
		while (iSeguent < regles.size()
				&& regles.get(iSeguent).getOrdre() <= reglaActual.getOrdre()) {
			iSeguent++;
		}
		if (iSeguent < regles.size() ) {
			// seguent regla després de l'ordre de l'actual
			reglaSeguent = regles.get(iSeguent);
		} else {
			// La primera regla coincident
			reglaSeguent = regles.get(0);
		}
		return reglaSeguent;
	}

	/** Troba la primera regla aplicable segons les dades del registre. */
	public ReglaEntity findAplicable(
			EntitatEntity entitat,
			Long unitatId,
			Long bustiaId,
			String procedimentCodi,
			String assumpteCodi,
			Boolean presencial) {
		ReglaEntity reglaAplicable = null;
		
		ReglaPresencialEnumDto esPresencial = null;
		if (presencial != null) {
			esPresencial = presencial == true ? ReglaPresencialEnumDto.SI : ReglaPresencialEnumDto.NO;
		}
		List<ReglaEntity> regles = reglaRepository.findAplicables(
					entitat,
					unitatId,
					bustiaId,
					procedimentCodi != null ? procedimentCodi : "",
					assumpteCodi != null ? assumpteCodi : "", 
					esPresencial == null,
					esPresencial);
		if (regles.size() > 0) {
			reglaAplicable = regles.get(0);
		}
		return reglaAplicable;
	}

	public static String encrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] crypted = null;
		SecretKeySpec skey = new SecretKeySpec(CLAU_XIFRAT.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skey);
		crypted = cipher.doFinal(input.getBytes());
		return new String(Base64.getEncoder().encode(crypted));
	}

	public Exception aplicarControlantException(
			RegistreEntity registre,
			List<ReglaEntity> reglesApplied) {
		logger.debug("Aplicant regla a anotació de registre (" +
				"registreId=" + registre.getId() + ", " +
				"registreNumero=" + registre.getNumero() + ", " +
				"reglaId=" + registre.getRegla().getId() + ", " +
				"reglaTipus=" + registre.getRegla().getTipus().name() + ", " +
				(registre.getRegla().getBackofficeDesti() != null ? "reglaBackoffice=" + registre.getRegla().getBackofficeDesti().getNom() + ", "  : "") +
				"bustia=" + registre.getRegla().getBustiaDesti() + ")");
		try {
			boolean throwException = false;
			if (throwException) {
				throw new RuntimeException("Exception when aplying rule!!!!!!");
			}
			aplicar(
					registre,
					reglesApplied);
			ReglaEntity lastRegla = reglesApplied.get(reglesApplied.size() - 1);
			if (lastRegla.getTipus() != ReglaTipusEnumDto.BACKOFFICE || (lastRegla.getTipus() == ReglaTipusEnumDto.BACKOFFICE && isAnotacioAlreadySavedInArxiu(registre))) {
				ContingutEntity pare = registre.getPare();
				if (pare != null) {
					if (HibernateHelper.isProxy(pare))
						pare = HibernateHelper.deproxy(pare);
					emailHelper.createEmailsPendingToSend(
							(BustiaEntity)pare,
							registre,
							registre.getDarrerMoviment());
				}
			}
			
			
			logger.debug("Processament anotació OK (id=" + registre.getId() + ", núm.=" + registre.getNumero() + ")");
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.aplicar.regles",
							new Object[] {registre.getId()}),
					null,
					registre.getId());
			return null;
		} catch (Exception ex) {
			String procesError;
			if (ex instanceof ScheduledTaskException) {
				procesError = ex.getMessage();
			} else {
				Throwable rootExc = ExceptionUtils.getRootCause(ex);
				if (rootExc != null) {
					procesError = ExceptionUtils.getStackTrace(rootExc);
				} else {
					procesError = ExceptionUtils.getStackTrace(ex);
				}
			}
			logger.debug("Processament anotació ERROR (" +
					"id=" + registre.getId() + ", " +
					"núm.=" + registre.getIdentificador() + "): " +
					procesError);
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.aplicar.regles.error",
							new Object[] {registre.getId()}),
					ex,
					registre.getId());
			return ex;
		}
	}

	public boolean isAnotacioAlreadySavedInArxiu(RegistreEntity registre) {
		boolean alreadySavedInArxiu = true;
		if (registre.getAnnexos() != null && !registre.getAnnexos().isEmpty()) {
			for (RegistreAnnexEntity registreAnnexEntity : registre.getAnnexos()) {
				if (registreAnnexEntity.getFitxerArxiuUuid() == null || registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
					alreadySavedInArxiu = false;
				}
			}
		}
		return alreadySavedInArxiu;
	}

	public boolean isAnotacioReactivada(RegistreEntity registre) {
		return registre.isReactivat();
	}

	public boolean isAnotacioDuplicada(RegistreEntity registre) {
		boolean anotacioDuplicada = false;
		ContingutMovimentEntity darrerMoviment = registre.getDarrerMoviment();
		if (darrerMoviment != null) {
			anotacioDuplicada = darrerMoviment.getNumDuplicat() > 1;
		}
		return anotacioDuplicada;
	}

	public boolean isAnotacioPerConeixement(RegistreEntity registre) {
		boolean isPerConeixement = false;
		ContingutMovimentEntity darrerMoviment = registre.getDarrerMoviment();
		if (darrerMoviment != null) {
			isPerConeixement = darrerMoviment.isPerConeixement();
		}
		return isPerConeixement;
	}

	public void aplicarSimulation(
			EntitatEntity entitatEntity,
			RegistreSimulatDto registreSimulatDto,
			List<ReglaEntity> reglasApplied,
			List<RegistreSimulatAccionDto> simulatAccions, 
			Boolean presencial) {
		// Consulta la  propietat per avaluar totes les regles o només la 1a coincident com fins ara
		boolean avaluarTotesLesRegles = 
				configHelper.getAsBoolean("es.caib.distribucio.tasca.aplicar.regles.avaluar.totes", false);

		ReglaEntity reglaToApply = null;
		boolean reglaAplicada = false;
		boolean reglaBackoffice = false;
		boolean reglaJaAplicada = false;
		boolean aturarAvaluacio = false;
		List<Long> reglesAplicadesIds = new ArrayList<Long>();
		for (ReglaEntity ra : reglasApplied) {
			reglesAplicadesIds.add(ra.getId());
		}
		// Màxim de seguretat per evitar bucles infinits
		int max_bucle_regles = 50;
		logger.debug("Inici de la simulació (avaluarTotesLesRegles=" + avaluarTotesLesRegles + ")");		
		do {
			reglaAplicada = false;
			if (avaluarTotesLesRegles) {
				// #655 S'avaluen totes les regles per veure si apliquen, en comptes de consultar la 1a consulta la següent de la llista
				// o torna a l'inici de la llista
				reglaToApply = findSeguent(
						entitatEntity,
						reglaToApply, 
						registreSimulatDto.getUnitatId(),
						registreSimulatDto.getBustiaId(),
						registreSimulatDto.getProcedimentCodi(),
						registreSimulatDto.getAssumpteCodi(), 
						presencial);
			} else {
				// Consulta la següent regla
				reglaToApply = findAplicable(
						entitatEntity,
						registreSimulatDto.getUnitatId(),
						registreSimulatDto.getBustiaId(),
						registreSimulatDto.getProcedimentCodi(),
						registreSimulatDto.getAssumpteCodi(), 
						presencial);
			}
			if (reglaToApply != null) {
				// Comprova si ja s'ha aplicat anteriorment.
				reglaJaAplicada = reglesAplicadesIds.contains(reglaToApply.getId());
				if (reglaToApply.getId() == null) {
					logger.error("La regla aplicada no té identificador. Regla: {nom: " + reglaToApply.getNom() + ", descripcio: " + reglaToApply.getDescripcio() + "}" );
				}
				if (reglaJaAplicada) {
					simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.LOOP_DETECTED, null, reglaToApply.getNom()));
				} else {
					logger.debug("Simular aplicació regla=" + reglaToApply.getNom() + ", tipus=" + reglaToApply.getTipus());
					// Aplica la regla a la simulació
					switch (reglaToApply.getTipus()) {
					case BACKOFFICE: // ############################### BACKOFFICE ###############################
						simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.BACKOFFICE, reglaToApply.getBackofficeDesti().getNom(), reglaToApply.getNom()));
						reglaBackoffice = true;
						break;
					case BUSTIA:
					case UNITAT: // ############################### BUSTIA / UNITAT ###############################
						if (reglaToApply.getTipus() == ReglaTipusEnumDto.UNITAT) {
							BustiaEntity bustiaDesti = bustiaHelper.findBustiaDesti(
									reglaToApply.getEntitat(),
									reglaToApply.getUnitatDesti().getCodi());
							simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.UNITAT, bustiaDesti.getUnitatOrganitzativa().getCodi() + " - "+ bustiaDesti.getUnitatOrganitzativa().getDenominacio(), reglaToApply.getNom()));
							registreSimulatDto.setUnitatId(bustiaDesti.getUnitatOrganitzativa().getId());

							simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.BUSTIA_PER_DEFECTE, bustiaDesti.getNom(), null));
							registreSimulatDto.setBustiaId(bustiaDesti.getId());
						} else if (reglaToApply.getTipus() == ReglaTipusEnumDto.BUSTIA) {
							simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.BUSTIA, reglaToApply.getBustiaDesti().getNom(), reglaToApply.getNom()));
							registreSimulatDto.setBustiaId(reglaToApply.getBustiaDesti().getId());
							registreSimulatDto.setUnitatId(reglaToApply.getBustiaDesti().getUnitatOrganitzativa().getId());
						}
						break;
					}
				}
				reglaAplicada = true;
				reglasApplied.add(reglaToApply);
				reglesAplicadesIds.add(reglaToApply.getId());
				if (reglaToApply.getId() == null) {
					logger.error("La regla aplicada no té identificador. Regla: {nom: " + reglaToApply.getNom() + ", descripcio: " + reglaToApply.getDescripcio() + "}" );
				}
				if (reglaToApply.isAturarAvaluacio()) {
					simulatAccions.add(new RegistreSimulatAccionDto(RegistreSimulatAccionEnumDto.ATURAR_EVALUACIO, null, reglaToApply.getNom()));
					aturarAvaluacio = true;
				}
			} else {
				reglaAplicada = false;
			}
		} while (reglaAplicada 
				&& !reglaBackoffice 
				&& !reglaJaAplicada
				&& !aturarAvaluacio
				&& max_bucle_regles-- > 0);
		if (max_bucle_regles < 0) {
			logger.error("S'ha detectat un bucle simulant regles.");
		}
	}

	public void aplicar(
			RegistreEntity registre,
			List<ReglaEntity> reglesApplied) {
		boolean avaluarTotesLesRegles = true;
		boolean reglaAplicada = false;
		boolean reglaBackoffice = false;
		boolean reglaJaAplicada = false;
		boolean aturarAvaluacio = false;
		List<Long> reglesAplicadesIds = new ArrayList<Long>();
		for (ReglaEntity ra : reglesApplied) {
			reglesAplicadesIds.add(ra.getId());
		}
		// Màxim de seguretat per evitar bucles infinits
		int max_bucle_regles = 50;
		logger.debug("Inici del processament de regles per l'anotació " + registre.getNumero() + " (avaluarTotesLesRegles=" + avaluarTotesLesRegles + ")");		
		do {
			reglaAplicada = false;
			ReglaEntity regla = registre.getRegla();
			logger.debug("Aplicant regla =" + regla.getNom() + " de tipus " + regla.getTipus() + " al registre " + registre.getNumero() + ". Regla=" + ToStringBuilder.reflectionToString(regla));
			boolean alreadySavedInArxiu = isAnotacioAlreadySavedInArxiu(registre);
			boolean reactivada = isAnotacioReactivada(registre);
			boolean isPerConeixement = isAnotacioPerConeixement(registre);
			boolean anotacioDuplicada = isAnotacioDuplicada(registre);
			String error = null;
			try {
				switch (regla.getTipus()) {
				case BACKOFFICE: // ############################### BACKOFFICE ###############################
					if (alreadySavedInArxiu) {
						if ((isPermesSobreescriureAnotacions() && !reactivada && !isPerConeixement && !anotacioDuplicada) || !isPermesSobreescriureAnotacions()) {
							if (regla.getBackofficeDesti() == null) {
								throw new RuntimeException("La regla és del tipus backoffice però no té un backoffice específic assignat, s'ha de corregir la regla.");
							}
							registre.updateProcesBackPendent();
							registre.updateBackPendentData(new Date());
							// Informa del codi del backoffice que processarà l'anotació
							registre.updateBackCodi(regla.getBackofficeDesti().getCodi());
							// there is @Scheduled method that sends periodically anotacios with state: BACK_PENDENT to backoffice  
							// ------ log and evict -----------
							List<String> params = new ArrayList<>();
							params.add(regla.getNom());
							params.add(regla.getTipus().toString());
							contingutLogHelper.log(
									registre,
									LogTipusEnumDto.REGLA_APLICAR,
									params,
									false);	
						}
					} else {
						registre.setNewProcesEstat(RegistreProcesEstatEnum.ARXIU_PENDENT);
					}
					reglaBackoffice = true;
					break;
				case BUSTIA:
				case UNITAT: // ############################### BUSTIA / UNITAT ###############################
					BustiaEntity bustiaDesti = null;
					if (regla.getTipus() == ReglaTipusEnumDto.UNITAT) {
						bustiaDesti = bustiaHelper.findBustiaDesti(
								registre.getEntitat(),
								regla.getUnitatDesti().getCodi());

					} else if (regla.getTipus() == ReglaTipusEnumDto.BUSTIA) {
						bustiaDesti = regla.getBustiaDesti();
					}
					// ------ log -----------
					ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
							registre,
							bustiaDesti,
							null,
							false,
							null);
					List<String> params = new ArrayList<>();
					params.add(regla.getNom());
					params.add(regla.getTipus().toString());
					contingutLogHelper.logAccioWithMovimentAndParams(
							registre,
							LogTipusEnumDto.REGLA_APLICAR,
							contingutMoviment,
							true,
							params);
					// ------- change anotacio to next state -------- 
					RegistreProcesEstatEnum estat;
					if (!alreadySavedInArxiu) {
						estat = RegistreProcesEstatEnum.ARXIU_PENDENT;
					} else {
						estat = RegistreProcesEstatEnum.BUSTIA_PENDENT;
					}
					registre.setNewProcesEstat(estat);
					break;
				}
				// ------ FIND AND APPLY NEXT RELGA IF EXISTS -----------
				reglaAplicada = true;
				reglesApplied.add(regla);
				reglesAplicadesIds.add(regla.getId());
				if (regla.getId() == null) {
					logger.error("La regla aplicada no té identificador. Regla: {nom: " + regla.getNom() + ", descripcio: " + regla.getDescripcio() + "}" );
				}
				if (regla.isAturarAvaluacio()) {
					aturarAvaluacio = true;
				}
				ContingutEntity pare = registre.getPare();
				if (HibernateHelper.isProxy(pare))
					pare = HibernateHelper.deproxy(pare);
				BustiaEntity bustia = (BustiaEntity)pare;
				Boolean presencial = null;
				if (registre.getPresencial() != null) {
					presencial = registre.getPresencial();
				}
				ReglaEntity nextReglaToApply = null;
				if (avaluarTotesLesRegles
						 && !aturarAvaluacio
						 && !reglaBackoffice) {
					// #655 S'avaluen totes les regles per veure si apliquen, en comptes de consultar la 1a consulta la següent de la llista
					// o torna a l'inici de la llista
					nextReglaToApply = this.findSeguent(
							registre.getEntitat(),
							regla, 
							bustia.getUnitatOrganitzativa().getId(),
							bustia.getId(),
							registre.getProcedimentCodi(),
							registre.getAssumpteCodi(), 
							presencial);
				} else {
					// Consulta la següent regla
					nextReglaToApply = this.findAplicable(
							registre.getEntitat(),
							bustia.getUnitatOrganitzativa().getId(),
							bustia.getId(),
							registre.getProcedimentCodi(),
							registre.getAssumpteCodi(), 
							presencial);
				}
				if (nextReglaToApply != null) {
					// Comprova si ja s'ha aplicat anteriorment.
					reglaJaAplicada = reglesAplicadesIds.contains(nextReglaToApply.getId());
					if (nextReglaToApply.getId() == null) {
						logger.error("La regla aplicada no té identificador. Regla: {nom: " + nextReglaToApply.getNom() + ", descripcio: " + nextReglaToApply.getDescripcio() + "}" );
					}
					if (!reglaJaAplicada) {
						registre.updateRegla(nextReglaToApply);
						regla = nextReglaToApply;
					}
				}
			} catch (Exception ex) {
				Throwable t = ExceptionUtils.getRootCause(ex);
				if (t == null)
					t = ex.getCause();
				if (t == null)
					t = ex;
				error = ExceptionUtils.getStackTrace(t);
				throw new AplicarReglaException(error);
			}
		} while (reglaAplicada 
				&& !reglaBackoffice 
				&& !reglaJaAplicada
				&& !aturarAvaluacio
				&& max_bucle_regles-- > 0);
		if (max_bucle_regles < 0) {
			logger.error("S'ha detectat un bucle aplicant regles.");
		}
	}

	/*
	 * Mètode privat per obrir el document annex de tipus sistra i extreure'n
	 * informació per a l'anotació de registre. La informació que es pot extreure
	 * depén del document:
	 * - Asiento.xml: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDENTIFICADOR_TRAMITE (VARCHAR2(20))
	 * - DatosPropios.xml: DATOS_PROPIOS.INSTRUCCIONES.IDENTIFICADOR_PROCEDIMIENTO (VARCHAR2(100))
	 * 
	 * @param anotacio 
	 * 			Anotació del registre
	 * @param annex
	 * 			Document annex amb el contingut per a llegir.
	 */
	public void processarAnnexSistra(
			RegistreEntity anotacio,
			RegistreAnnexEntity annex) {
		try {
			byte[] annexContingut = null;
			if (annex.getGesdocDocumentId() != null) {
				ByteArrayOutputStream baos_doc = new ByteArrayOutputStream();
				gestioDocumentalHelper.gestioDocumentalGet(
					annex.getGesdocDocumentId(), 
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
					baos_doc);
				annexContingut = baos_doc.toByteArray();
				annex.updateGesdocDocumentId(null);
			}
			org.w3c.dom.Document doc = XmlHelper.getDocumentFromContent(annexContingut);
			if (annex.getFitxerNom().equals("DatosPropios.xml")) {
				String identificadorProcediment = XmlHelper.getNodeValue(
						doc.getDocumentElement(), "INSTRUCCIONES.IDENTIFICADOR_PROCEDIMIENTO");
				anotacio.updateIdentificadorProcedimentSistra(identificadorProcediment);
			} else if (annex.getFitxerNom().equals("Asiento.xml")) {
				String identificadorTramit = XmlHelper.getNodeValue(
						doc.getDocumentElement(), "DATOS_ASUNTO.IDENTIFICADOR_TRAMITE");
				anotacio.updateIdentificadorTramitSistra(identificadorTramit);
			}		
		} catch (Exception e) {
			logger.error(
					"Error processant l'annex per l'anotació amb regla backoffice SISTRA " + annex.getFitxerNom(),
					e);
		}
	}
	
	private boolean isPermesSobreescriureAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.sobreescriure.anotacions.duplicades");
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaHelper.class);

}