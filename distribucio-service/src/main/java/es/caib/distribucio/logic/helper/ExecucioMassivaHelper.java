package es.caib.distribucio.logic.helper;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.logic.intf.dto.ClassificacioResultatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaEstatDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.FileNameOption;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ContingutService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaContingutEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaContingutRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;

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
    @Autowired
    private RegistreRepository registreRepository;
    @Autowired
    private RegistreHelper registreHelper;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private EmailHelper emailHelper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
	public ClassificacioResultatDto classificarNewTransaction(Long entitatId, Long elementId, String parametres) {
		String titol = getValorParametre(parametres, "titol", String.class);
        String tipus = getValorParametre(parametres, "tipus", String.class);
		String procedimentCodi = getValorParametre(parametres, "codiProcediment", String.class);
		String serveiCodi = getValorParametre(parametres, "codiServei", String.class);
		
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatId, 
				elementId,
                tipus,
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
				Throwable throwable = registreService.reintentarEnviamentBackofficeAdmin(
						entitatId, 
						elementId);
				correcte = throwable == null;
				missatge = "Anotació reenviada al backoffice " + (correcte ? "correctament" : "amb error: " + throwable.getMessage());
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
		boolean correcte = true;
		String missatge = null;
		
		if (ArrayUtils.contains(estatsReenviablesBackoffices, registre.getProcesEstat())) {
			Throwable throwable = registreService.reintentarEnviamentBackofficeAdmin(
					entitatId, 
					elementId);
			correcte = throwable == null;
			missatge = "Anotació reenviada al backoffice " + (registre.getBackCodi()) + " " + (correcte ? "correctament" : "amb error: " + throwable.getMessage());
		}else {
			correcte = false;
			missatge = messageHelper.getMessage("execucio.massiva.helper.enviament.backoffice.estat.incompatible", new Object[] {elementId}); 
		}

		if (!correcte) {
			throw new RuntimeException(missatge);
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

    
    /** Descarrega tots els annexos d'una anotació i els afegeix en el zip temporal de l'execució massiva. */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void descarregarAnnexos(Long entitatId, ExecucioMassivaEntity em) {
        EntitatEntity entity = entitatRepository.findById(entitatId).get();
        ConfigHelper.setEntitatActualCodi(entity.getCodi());
        this.updateProcessantNewTransaction(em, new Date());

        List<String> nomsArxius = new ArrayList<String>();
        List<String> errors = new ArrayList<>();

        try {
            if (em.getParametres() != null && !em.getParametres().isEmpty()) {
                boolean estructuraCarpetes = getValorParametre(em.getParametres(), "estructuraCarpetes", Boolean.class);
                boolean versioImprimible = getValorParametre(em.getParametres(), "versioImprimible", Boolean.class);
                FileNameOption tipusNomDocument = FileNameOption.valueOf(getValorParametre(em.getParametres(), "nomDocument", String.class));
                String rolActual = getValorParametre(em.getParametres(), "rolActual", String.class);

                String directoriDesti = configHelper.getConfig("es.caib.distribucio.fitxers");
                String documentNom = "/exportZip/annexosRegistres_" + em.getId() + ".zip";
                FileOutputStream fos = new FileOutputStream(directoriDesti + documentNom);
                ZipOutputStream zip = new ZipOutputStream(fos);

                for (ExecucioMassivaContingutEntity emc: em.getContinguts()) {
                    if (this.isEmcDisponibleNewTransaction(emc)) {
                        this.updateProcessantNewTransaction(emc, new Date());
                        try {
                            this.descarregarAnnexos(
                                    emc.getId(),
                                    zip,
                                    estructuraCarpetes,
                                    versioImprimible,
                                    tipusNomDocument,
                                    rolActual,
                                    nomsArxius,
                                    errors);
                        } catch (Exception e) {
                            StringBuilder errMsg = new StringBuilder("Hi ha hagut un error executant el contingut de l'acció massiva [id=" + emc.getId());
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            errMsg.append(", tipus=" + em.getTipus());
                            errMsg.append(", auth=" + (auth != null ? auth.getPrincipal() : "-"));
                            if (auth != null) {
                                errMsg.append(", rols=[");
                                for (GrantedAuthority ga : auth.getAuthorities()) {
                                    errMsg.append(ga.getAuthority()).append(" ");
                                }
                                errMsg.append("]");
                            }
                            errMsg.append("]");
//                            logger.error(errMsg.toString(), e);
                            this.updateErrorNewTransaction(
                                    emc,
                                    new Date(),
                                    e.getMessage());
                            continue;
                        }
                        this.updateFinalitzatNewTransaction(emc, new Date());
                    }
                }

                if (!errors.isEmpty()) {
                    StringBuilder avisosContent = new StringBuilder();
                    for(String error : errors) {
                        avisosContent.append(error).append("\n\n");
                    }
                    ZipEntry entry = new ZipEntry("errors.txt");
                    byte[] contingut = avisosContent.toString().getBytes();
                    entry.setSize(contingut.length);
                    zip.putNextEntry(entry);
                    zip.write(contingut);
                    zip.closeEntry();
                }

                if (this.isFinalitzableNewTransaction(em)) {
                    zip.close();
                    em.setNomDocument(documentNom);
                    this.updateFinalitzatNewTransaction(em, new Date());
                    emailHelper.sendEmailAccioMassiva(em, errors);
                }
            } else {
                throw new Exception("Empty params");
            }
        } catch(Exception e) {
            throw new RuntimeException("Error no controlat executant l'acció massvia: " + e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void descarregarAnnexos(Long emcId, ZipOutputStream zip, boolean estructuraCarpetes, boolean versioImprimible, FileNameOption tipusNomDocument, String rolActual, List<String> nomsArxius, List<String> errors) {
        ExecucioMassivaContingutEntity emc = execucioMassivaContingutRepository.findById(emcId).get();
        try {
            RegistreEntity registre = registreRepository.findById(emc.getElementId()).get();
            List<RegistreAnnexEntity> annexos = registre.getAnnexos().stream()
                    .filter(a -> (!"tothom".equalsIgnoreCase(rolActual) || a.getSicresTipusDocument() == null || !RegistreAnnexSicresTipusDocumentEnum.INTERN.equals(a.getSicresTipusDocument())))
                    .collect(Collectors.toList());

            ZipEntry ze;
            for (RegistreAnnexEntity annex : annexos) {
                FitxerDto fitxer = null;
                if (versioImprimible && registreHelper.potGenerarVersioImprimible(annex)) {
                    try {
                        fitxer = registreHelper.getAnnexFitxerImprimible(annex);
                    } catch (Exception e) {
                        String errMsg = "[" + annex.getRegistre().getNumero() + "] - Error obtenint la versió imprimible del l'annex " + annex.getId() + " \"" + annex.getTitol() + "\", es procedeix a consultar l'original. Error: \\\""
                                + e.getClass() + " " + e.getMessage() + "\"";
                        errors.add(errMsg);
                    }
                }
                if (fitxer == null) {
                    try {
                        fitxer = registreHelper.getAnnexFitxer(annex.getId(), false);
                    } catch (Exception e) {
                        String errMsg = "[" + annex.getRegistre().getNumero() + "] - Error obtenint la versió original del l'annex " + annex.getId() + " \"" + annex.getTitol() + "\". Error: \\\""
                                + e.getClass() + " " + e.getMessage() + "\"";
                        errors.add(errMsg);
                        continue;
                    }
                }

                String nomDoc;
                switch (tipusNomDocument) {
                    case TITLE:
                        nomDoc = annex.getTitol() + "." + fitxer.getExtensio();
                        break;
                    case TITLE_ORIGINAL:
                        nomDoc = annex.getTitol() + " - " + fitxer.getNom();
                        break;
                    default:
                        nomDoc = fitxer.getNom();
                        break;
                }
                nomDoc = nomDoc.replaceAll("/", "_");

                if (estructuraCarpetes) {
                    nomDoc = annex.getRegistre().getNumero().replaceAll("/", "_") + "/" + nomDoc;
                }

                String recursNom = this.getZipRecursNom(nomDoc, nomsArxius);
                ze = new ZipEntry(recursNom);
                zip.putNextEntry(ze);
                if (fitxer.getContingut() != null) {
                    zip.write(fitxer.getContingut());
                    nomsArxius.add(recursNom);
                }
                zip.closeEntry();
            }
        } catch(Exception e) {
        	throw new RuntimeException("Error no controlat executant l'acció massvia: " + e.getMessage(), e);
        }
    }

    private String getZipRecursNom(String nomEntrada, List<String> nomsArxius) {
        int contador = 0;
        String temp = nomEntrada;
        while (nomsArxius.contains(temp)) {
            contador++;
            temp = nomEntrada.substring(0, nomEntrada.lastIndexOf(".")) +
                    " (" + contador + ")" + nomEntrada.substring(nomEntrada.lastIndexOf("."));
        }
        nomsArxius.add(temp);
        return temp;
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
	public boolean isFinalitzableNewTransaction(ExecucioMassivaEntity em) {
        Optional<ExecucioMassivaEntity> emNewTransaction = execucioMassivaRepository.findById(em.getId());

        if (emNewTransaction.isPresent()) {
            em.setEstat(emNewTransaction.get().getEstat());
            return ExecucioMassivaEstatDto.PROCESSANT.equals(emNewTransaction.get().getEstat());
        }
        return false;
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
				&& !(registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && registreDto.isReintentsEsgotat())
				&& !(registreDto.getProcesEstat() == RegistreProcesEstatEnum.BACK_ERROR && registreDto.isReintentsEsgotat())
        ) {
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
