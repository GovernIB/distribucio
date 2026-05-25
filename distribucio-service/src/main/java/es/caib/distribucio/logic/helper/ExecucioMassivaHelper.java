package es.caib.distribucio.logic.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.MessagingException;

import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.registre.FileNameOption;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.service.*;
import es.caib.distribucio.persist.entity.*;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.repository.ExecucioMassivaContingutRepository;
import es.caib.distribucio.persist.repository.ExecucioMassivaRepository;
import org.springframework.ui.Model;

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
    private ConversioTipusHelper conversioTipusHelper;
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void descarregarAnnexos(Long entitatId, Long elementId) {
        EntitatEntity entity = entitatRepository.findById(entitatId).get();
        ConfigHelper.setEntitat(conversioTipusHelper.convertir(entity, EntitatDto.class));
        ExecucioMassivaEntity em = execucioMassivaRepository.findById(elementId).get();
        try {
            if (em.getParametres() != null && !em.getParametres().isEmpty()) {
                this.updateProcessantNewTransaction(em, new Date());
                em.updateProcessant(new Date());

                boolean estructuraCarpetes = getValorParametre(em.getParametres(), "estructuraCarpetes", Boolean.class);
                boolean versioImprimible = getValorParametre(em.getParametres(), "versioImprimible", Boolean.class);
                FileNameOption nomDocument = FileNameOption.valueOf(getValorParametre(em.getParametres(), "nomDocument", String.class));
                String rolActual = getValorParametre(em.getParametres(), "rolActual", String.class);

                List<RegistreEntity> registres = registreRepository.findAllById(
                        em.getContinguts().stream()
                                .map(ExecucioMassivaContingutEntity::getElementId)
                                .collect(Collectors.toList())
                );

                List<RegistreAnnexEntity> annexos = registres.stream()
                        .map(RegistreEntity::getAnnexos)
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .filter(a -> (!"tothom".equalsIgnoreCase(rolActual) || a.getSicresTipusDocument() == null || !RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor().equals(a.getSicresTipusDocument())))
                        .collect(Collectors.toList());

                for (ExecucioMassivaContingutEntity contingut : em.getContinguts()) {
                    this.updateProcessantNewTransaction(contingut, new Date());
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream out = new ZipOutputStream(baos);
                ZipEntry ze;
                List<String> nomsArxius = new ArrayList<String>();
                int numFiles = 0;
                List<String> errors = new ArrayList<>();

                for (RegistreAnnexEntity annex : annexos) {
                    FitxerDto fitxer = null;
                    if (versioImprimible && registreHelper.potGenerarVersioImprimible(annex)) {
                        try {
                            fitxer = registreHelper.getAnnexFitxerImprimible(annex.getId());
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
                        }
                    }

                    String nomDoc;
                    switch (nomDocument) {
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
                        nomDoc = annex.getRegistre().getNom().replaceAll("/", "_") + "/" + nomDoc;
                    }

                    String recursNom = this.getZipRecursNom(nomDoc, nomsArxius);
                    ze = new ZipEntry(recursNom);
                    out.putNextEntry(ze);
                    if (fitxer.getContingut() != null) {
                        out.write(fitxer.getContingut());
                    }
                    out.closeEntry();
                    numFiles++;
                }

                if (!errors.isEmpty()) {
                    StringBuilder avisosContent = new StringBuilder();
                    for(String error : errors) {
                        avisosContent.append(error + "\n");
                    }
                    ZipEntry entry = new ZipEntry("errors.txt");
                    byte[] contingut = avisosContent.toString().getBytes();
                    entry.setSize(contingut.length);
                    out.putNextEntry(entry);
                    out.write(contingut);
                    out.closeEntry();
                }

                out.close();

//                int maxSize = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.mida.max", "10"));
//                if (baos.size() > maxSize * 1024 * 1024) {
//                    this.marcarExecucioAmbError(em, "Superada mida máxima ("+maxSize+")");
//                    return;
//                }

                if (numFiles > 0) {
                    String directoriDesti = configHelper.getConfig("es.caib.distribucio.fitxers");
                    String documentNom = "/exportZip/annexosRegistres_" + em.getId() + ".zip";
                    File fContent = new File(directoriDesti + documentNom);
                    fContent.getParentFile().mkdirs();
                    FileOutputStream outContent = new FileOutputStream(fContent);
                    outContent.write(baos.toByteArray());
                    outContent.close();
                    em.setNomDocument(documentNom);
                    for (ExecucioMassivaContingutEntity contingut : em.getContinguts()) {
                        this.updateFinalitzatNewTransaction(contingut, new Date());
                    }
                    this.updateFinalitzatNewTransaction(em, new Date());

                    emailHelper.sendEmailAccioMassiva(em, errors);
                } else {
                    this.marcarExecucioAmbError(em, "Empty file");
                }
            } else {
                this.marcarExecucioAmbError(em, "Empty params");
            }
        } catch (Exception e) {
            this.marcarExecucioAmbError(em, e.getMessage());
        }
    }

    private void marcarExecucioAmbError(ExecucioMassivaEntity em, String reason) {
        for (ExecucioMassivaContingutEntity contingut : em.getContinguts()) {
            this.updateErrorNewTransaction(contingut, new Date(), reason);
        }
        this.updateFinalitzatNewTransaction(em, new Date());
    }

    public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execucioId) {
        FitxerDto resultat = new FitxerDto();

        try {
            String directoriDesti = configHelper.getConfig("es.caib.distribucio.fitxers");
            ExecucioMassivaEntity execucioMassiva = execucioMassivaRepository.findById(execucioId).get();
            String nomDocument = execucioMassiva.getNomDocument();

            byte[] bytes = Files.readAllBytes(Paths.get(directoriDesti + nomDocument));
            resultat.setContingut(bytes);
            if (nomDocument.contains("/")) {
                resultat.setNom(
                        nomDocument.substring(nomDocument.lastIndexOf("/") + 1));
            } else {
                resultat.setNom(nomDocument);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultat;
    }

    private String getZipRecursNom(String nomEntrada, List<String> nomsArxius) {
        int contador = 0;
        for (String nom : nomsArxius) {
            if (nom!=null && nom.equals(nomEntrada)) {
                contador++;
            }
        }
        if (contador > 0) {
            nomEntrada = nomEntrada.substring(0, nomEntrada.lastIndexOf(".")) +
                    " (" + contador + ")" + nomEntrada.substring(nomEntrada.lastIndexOf("."));
            return getZipRecursNom(nomEntrada, nomsArxius);
        }
        nomsArxius.add(nomEntrada);
        return nomEntrada;
    }

    public boolean chechFormDescargaMassiva(List<RegistreDto> registres, Model model) {
        List<String> errors = new ArrayList<>();
        int maxExec = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.exec.max", "5"));
        int maxSize = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.mida.max", "10"));

        boolean enabled = "true".equals(configHelper.getConfig("es.caib.distribucio.exportar.annex.zip.enabled"));
        int exec = this.getNumeroExecucioMasiva();
        double midaAproximada = this.getMidaAproximada(registres);

        if (exec >= maxExec) {
            errors.add(messageHelper.getMessage("registre.annex.descarregar.zip.exec.max", new Object[] {maxExec}));
        }
        if (midaAproximada > (maxSize * 1024 * 1024)) {
            errors.add(messageHelper.getMessage("registre.annex.descarregar.zip.size.max", new Object[] {String.format("%.2f", midaAproximada / 1024 / 1024), maxSize}));
        } else {
            model.addAttribute("midaMaxima", maxSize);
        }
        if (!enabled) {
            errors.add("La acción masiva no està habilitada");
        }

        model.addAttribute("errors", errors);
        model.addAttribute("disabled", !errors.isEmpty());
        return errors.isEmpty();
    }

    private double getMidaAproximada(List<RegistreDto> registresSeleccionats) {
        double tamany = 0;
        for (RegistreDto registre : registresSeleccionats) {
            for (RegistreAnnexDto annexos : registre.getAnnexos()) {
                tamany += annexos.getFitxerTamany() * obtenirRatioCompresio(annexos.getFitxerTipusMime());
            }
        }
        return tamany;
    }

    private double obtenirRatioCompresio(String mime) {
        if (mime == null) return 0.80; // Fallback seguro
        String m = mime.toLowerCase();

        //  Textos y datos (alta compresión)
        if (m.startsWith("text/") || m.contains("json") || m.contains("xml") || m.contains("csv") || m.contains("sql")) {
            return 0.30; // ~70% reducción
        }
        // 📑 PDF
        if (m.equals("application/pdf")) return 0.70;
        // 🖼️ Imágenes (ya están comprimidas)
        if (m.startsWith("image/")) return 0.95;
        //  Office legacy (.doc, .xls, .ppt)
        if (m.contains("msword") || m.contains("ms-excel") || m.contains("ms-powerpoint")) return 0.40;
        // 📦 Office moderno (.docx, .xlsx, .pptx) → ya son ZIP internamente
        if (m.contains("openxmlformats")) return 0.95;
        // ️ Archivos comprimidos / Multimedia
        if (m.contains("zip") || m.contains("rar") || m.contains("7z") ||
                m.startsWith("video/") || m.startsWith("audio/")) {
            return 0.99; // Prácticamente 0% compresión
        }
        // 🔹 Fallback genérico
        return 0.80;
    }

    private int getNumeroExecucioMasiva() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        Date date = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        return execucioMassivaRepository.countNombreAccionsMassives(user, date);
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
