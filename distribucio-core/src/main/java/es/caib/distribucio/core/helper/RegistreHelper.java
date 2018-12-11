/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.AplicarReglaException;
import es.caib.distribucio.core.api.registre.Firma;
import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreInteressatCanalEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreInteressatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.plugins.arxiu.api.Document;

/**
 * Mètodes comuns per a aplicar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreHelper {

	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;

	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ReglaHelper reglaHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;

	public RegistreAnotacio fromRegistreEntity(
			RegistreEntity entity) {
		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setNumero(entity.getNumero());
		anotacio.setData(entity.getData());
		anotacio.setIdentificador(entity.getIdentificador());
		anotacio.setEntitatCodi(entity.getEntitatCodi());
		anotacio.setEntitatDescripcio(entity.getEntitatDescripcio());
		anotacio.setOficinaCodi(entity.getOficinaCodi());
		anotacio.setOficinaDescripcio(entity.getOficinaDescripcio());
		anotacio.setDataOrigen(entity.getDataOrigen());
		anotacio.setOficinaOrigenCodi(entity.getOficinaOrigenCodi());
		anotacio.setOficinaOrigenDescripcio(entity.getOficinaOrigenDescripcio());
		anotacio.setLlibreCodi(entity.getLlibreCodi());
		anotacio.setLlibreDescripcio(entity.getLlibreDescripcio());
		anotacio.setExtracte(entity.getExtracte());
		anotacio.setAssumpteTipusCodi(entity.getAssumpteTipusCodi());
		anotacio.setAssumpteTipusDescripcio(entity.getAssumpteTipusDescripcio());
		anotacio.setAssumpteCodi(entity.getAssumpteCodi());
		anotacio.setAssumpteDescripcio(entity.getAssumpteDescripcio());
		anotacio.setReferencia(entity.getReferencia());
		anotacio.setExpedientNumero(entity.getExpedientNumero());
		anotacio.setIdiomaCodi(entity.getIdiomaCodi());
		anotacio.setIdiomaDescripcio(entity.getIdiomaDescripcio());
		anotacio.setTransportTipusCodi(entity.getTransportTipusCodi());
		anotacio.setTransportTipusDescripcio(entity.getTransportTipusDescripcio());
		anotacio.setTransportNumero(entity.getTransportNumero());
		anotacio.setUsuariCodi(entity.getUsuariCodi());
		anotacio.setUsuariNom(entity.getUsuariNom());
		anotacio.setUsuariContacte(entity.getUsuariContacte());
		anotacio.setAplicacioCodi(entity.getAplicacioCodi());
		anotacio.setAplicacioVersio(entity.getAplicacioVersio());
		anotacio.setDocumentacioFisicaCodi(entity.getDocumentacioFisicaCodi());
		anotacio.setDocumentacioFisicaDescripcio(entity.getDocumentacioFisicaDescripcio());
		anotacio.setObservacions(entity.getObservacions());
		anotacio.setExposa(entity.getExposa());
		anotacio.setSolicita(entity.getSolicita());
		if (!entity.getInteressats().isEmpty()) {
			List<RegistreInteressat> interessats = new ArrayList<RegistreInteressat>();
			for (RegistreInteressatEntity interessat: entity.getInteressats()) {
				interessats.add(
						fromInteressatEntity(
								interessat));
			}
			anotacio.setInteressats(interessats);
		}
		if (!entity.getAnnexos().isEmpty()) {
			List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
			for (RegistreAnnexEntity annex: entity.getAnnexos()) {
				annexos.add(
						fromAnnexEntity(
								annex,
								anotacio));
			}
			anotacio.setAnnexos(annexos);
		}
		return anotacio;
	}

	public RegistreEntity crearRegistreEntity(
			EntitatEntity entitat,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio,
			ReglaEntity regla) {
		UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findPerEntitatAndCodi(
				entitat.getCodi(),
				unitatAdministrativa);
		String justificantArxiuUuid = null;
		if (anotacio.getJustificant() != null) {
			justificantArxiuUuid = anotacio.getJustificant().getFitxerArxiuUuid();
		}
		RegistreEntity entity = RegistreEntity.getBuilder(
				entitat,
				tipus,
				unitatAdministrativa,
				unitat != null? unitat.getDenominacio() : null,
				anotacio.getNumero(),
				anotacio.getData(),
				anotacio.getIdentificador(),
				anotacio.getExtracte(),
				anotacio.getOficinaCodi(),
				anotacio.getLlibreCodi(),
				anotacio.getAssumpteTipusCodi(),
				anotacio.getIdiomaCodi(),
				(regla != null || (anotacio.getAnnexos() != null && !anotacio.getAnnexos().isEmpty())) ? RegistreProcesEstatEnum.PENDENT : RegistreProcesEstatEnum.NO_PROCES,
				null).
		entitatCodi(anotacio.getEntitatCodi()).
		entitatDescripcio(anotacio.getEntitatDescripcio()).
		oficinaDescripcio(anotacio.getOficinaDescripcio()).
		llibreDescripcio(anotacio.getLlibreDescripcio()).
		assumpteTipusDescripcio(anotacio.getAssumpteTipusDescripcio()).
		assumpteCodi(anotacio.getAssumpteCodi()).
		assumpteDescripcio(anotacio.getAssumpteDescripcio()).
		referencia(anotacio.getReferencia()).
		expedientNumero(anotacio.getExpedientNumero()).
		numeroOrigen(anotacio.getNumeroOrigen()).
		idiomaDescripcio(anotacio.getIdiomaDescripcio()).
		transportTipusCodi(anotacio.getTransportTipusCodi()).
		transportTipusDescripcio(anotacio.getTransportTipusDescripcio()).
		transportNumero(anotacio.getTransportNumero()).
		usuariCodi(anotacio.getUsuariCodi()).
		usuariNom(anotacio.getUsuariNom()).
		usuariContacte(anotacio.getUsuariContacte()).
		aplicacioCodi(anotacio.getAplicacioCodi()).
		aplicacioVersio(anotacio.getAplicacioVersio()).
		documentacioFisicaCodi(anotacio.getDocumentacioFisicaCodi()).
		documentacioFisicaDescripcio(anotacio.getDocumentacioFisicaDescripcio()).
		observacions(anotacio.getObservacions()).
		exposa(anotacio.getExposa()).
		solicita(anotacio.getSolicita()).
		regla(regla).
		oficinaOrigen(
				anotacio.getDataOrigen(),
				anotacio.getOficinaOrigenCodi(),
				anotacio.getOficinaOrigenDescripcio()).
		justificantArxiuUuid(justificantArxiuUuid).
		build();
		if (entity.getProcesEstat() == RegistreProcesEstatEnum.NO_PROCES) {
			entity.updateProces(
					entity.getData(),
					RegistreProcesEstatEnum.PROCESSAT,
					null);
		}
		registreRepository.saveAndFlush(entity);
		if (anotacio.getInteressats() != null) {
			for (RegistreInteressat registreInteressat: anotacio.getInteressats()) {
				entity.getInteressats().add(
						crearInteressatEntity(
								registreInteressat,
								entity));
			}
		}
		if (anotacio.getAnnexos() != null) {
			for (RegistreAnnex registreAnnex: anotacio.getAnnexos()) {
				entity.getAnnexos().add(
						crearAnnexEntity(
								registreAnnex,
								entity));
			}
		}
		return entity;
	}
	
	public byte[] getAnnexArxiuContingut(String nomArxiu) {
		String pathName = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.bustia.contingut.documents.dir");
		
		Path path = Paths.get(pathName + "/" + nomArxiu);
		try {
			byte[] data = Files.readAllBytes(path);
			return data;
		} catch (IOException e) {
			return null;
		}
	}

	public List<ArxiuFirmaDto> convertirFirmesAnnexToArxiuFirmaDto(
			RegistreAnnexEntity annex,
			byte[] firmaDistribucioContingut) {
		List<ArxiuFirmaDto> firmes = null;
		if (annex.getFirmes() != null) {
			firmes = new ArrayList<ArxiuFirmaDto>();
			for (RegistreAnnexFirmaEntity annexFirma: annex.getFirmes()) {
				byte[] firmaContingut = null;
				
				if (annexFirma.getGesdocFirmaId() != null) {
					ByteArrayOutputStream baos_fir = new ByteArrayOutputStream();
					pluginHelper.gestioDocumentalGet(
							annexFirma.getGesdocFirmaId(), 
						PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
						baos_fir);
					firmaContingut = baos_fir.toByteArray();
				} else if(firmaDistribucioContingut != null) {
					firmaContingut = firmaDistribucioContingut;
				}
				
				ArxiuFirmaDto firma = new ArxiuFirmaDto();
				if ("TF01".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CSV);
				} else if ("TF02".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
				} else if ("TF03".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
				} else if ("TF04".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
				} else if ("TF05".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
				} else if ("TF06".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.PADES);
				} else if ("TF07".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
				} else if ("TF08".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.ODT);
				} else if ("TF09".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
				}
				firma.setPerfil(
						ArxiuFirmaPerfilEnumDto.valueOf(annexFirma.getPerfil()));
				firma.setFitxerNom(annexFirma.getFitxerNom());
				firma.setTipusMime(annexFirma.getTipusMime());
				firma.setCsvRegulacio(annexFirma.getCsvRegulacio());
				firma.setAutofirma(annexFirma.isAutofirma());
				firma.setContingut(firmaContingut);
				firmes.add(firma);
			}
		}
		return firmes;
	}

	public void distribuirAnotacioPendent(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		// Guarda els annexos a dins l'arxiu
		BustiaEntity bustia = bustiaHelper.findBustiaDesti(
				anotacio.getEntitat(),
				anotacio.getUnitatAdministrativa());
		String uuidContenidor = guardarAnnexosAmbPluginDistribucio(
				anotacio,
				bustia,
				true);
		// Processa els annexos provinents de SISTRA
		for (RegistreAnnexEntity annex: anotacio.getAnnexos()) {
			if (anotacio.getRegla() != null &&
				anotacio.getRegla().getTipus() == ReglaTipusEnumDto.BACKOFFICE &&
				anotacio.getRegla().getBackofficeTipus() == BackofficeTipusEnumDto.SISTRA) {
				if (annex.getFitxerNom().equals("DatosPropios.xml") || annex.getFitxerNom().equals("Asiento.xml"))
					processarAnnexSistra(anotacio, annex);
			}
		}
		// Aplica la regla a l'anotació
		if (anotacio.getRegla() != null) {
			boolean errorAlAplicarRegla = !reglaHelper.reglaAplicar(anotacio);
			if (errorAlAplicarRegla) {
				if (uuidContenidor != null) {
					pluginHelper.arxiuExpedientEliminar(uuidContenidor);
				}
				throw new AplicarReglaException("Error aplicant regla en segon pla per a l'anotació " + anotacio.getId());
			}
		}
		if (uuidContenidor != null) {
			// si s'ha utilitzat el plugin de gestió documental, s'intentaran esborrar els fitxers guardats
			esborrarDocsTemporals(anotacio);
		}
		// aquí haurem de actualtizar estat OK
		Date dataProcesOk = new Date();
		anotacio.updateProces(
				dataProcesOk,
				RegistreProcesEstatEnum.PROCESSAT, 
				null);
		registreRepository.saveAndFlush(anotacio);
	}

	public String guardarAnnexosAmbPluginDistribucio(
			RegistreEntity anotacio,
			BustiaEntity bustia,
			boolean crearAutofirma) {
		String uuidContenidor = null;
		if (anotacio.getAnnexos() != null && anotacio.getAnnexos().size() > 0) {
			DistribucioRegistreAnotacio distribucioRegistreAnotacio = conversioTipusHelper.convertir(
					anotacio,
					DistribucioRegistreAnotacio.class);
			if (anotacio.getExpedientArxiuUuid() == null) {
				// Cream el contenidor per als annexos de l'anotació de registre
				// només si no s'ha creat anteriorment
				logger.debug("Creant contenidor pels annexos de l'anotació (" +
						"anotacioIdentificador=" + anotacio.getIdentificador() + ", " +
						"unitatOrganitzativaCodi=" + bustia.getEntitat().getCodiDir3() + ")");
				uuidContenidor = pluginHelper.distribucioContenidorCrear(
						anotacio.getNumero(),
						distribucioRegistreAnotacio,
						bustia.getEntitat().getCodiDir3());
			} else {
				// Si el contenidor ja està creat agafam el seu UUID
				uuidContenidor = anotacio.getExpedientArxiuUuid();
			}
			if (uuidContenidor != null) {
				// Emmagatzemam cada un dels annexos de l'anotació de registre
				for (int i = 0; i < anotacio.getAnnexos().size(); i++) {
					RegistreAnnexEntity annex = anotacio.getAnnexos().get(i);
					// Només crea l'annex a dins el contenidor si encara
					// no s'ha creat
					if (annex.getFitxerArxiuUuid() == null) {
						logger.debug("Creant annex a dins el contenidor de l'anotació (" +
								"anotacioIdentificador=" + anotacio.getIdentificador() + ", " +
								"annexTitol=" + annex.getTitol() + ", " +
								"unitatOrganitzativaCodi=" + bustia.getEntitat().getCodiDir3() + ")");
						DistribucioRegistreAnnex distribucioAnnex = distribucioRegistreAnotacio.getAnnexos().get(i);
						String uuidDocument = pluginHelper.distribucioDocumentCrear(
								anotacio.getNumero(),
								distribucioAnnex,
								bustia.getEntitat().getCodiDir3(),
								uuidContenidor);
						annex.updateFitxerArxiuUuid(uuidDocument);
						if (annex.getFitxerTamany() <= 0) {
							Document document = pluginHelper.arxiuDocumentConsultar(
									annex.getRegistre(), 
									annex.getFitxerArxiuUuid(), 
									null, 
									true);
							if (document.getContingut() != null) {
								annex.updateFitxerTamany(
										(int)document.getContingut().getTamany());
							}
						}
						if (distribucioAnnex.getFirmes() != null) {
							for (DistribucioRegistreFirma distribucioFirma: distribucioAnnex.getFirmes()) {
								if (distribucioFirma.isAutofirma() && crearAutofirma) {
									RegistreAnnexFirmaEntity novaFirma = new RegistreAnnexFirmaEntity();
									novaFirma.updatePerNovaFirma(
											distribucioFirma.getTipus(), 
											distribucioFirma.getPerfil(), 
											distribucioFirma.getFitxerNom(), 
											distribucioFirma.getTipusMime(), 
											distribucioFirma.getCsvRegulacio(), 
											distribucioFirma.isAutofirma(), 
											distribucioFirma.getGesdocFirmaId(), 
											annex);
									annex.getFirmes().add(novaFirma);
								}
							}
						}
					}
				}
			}
		}
		logger.debug("Creació del contenidor i dels annexos finalitzada correctament (" +
				"anotacioIdentificador=" + anotacio.getIdentificador() + ", " +
				"unitatOrganitzativaCodi=" + bustia.getEntitat().getCodiDir3() + ")");
		contingutLogHelper.log(
				anotacio,
				LogTipusEnumDto.DISTRIBUCIO,
				anotacio.getNom(),
				null,
				false,
				false);
		return uuidContenidor;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualitzarEstatError(
			Long pendentId,
			Exception ex) {
		String error = ex.getMessage() + ": " + ExceptionUtils.getRootCauseMessage(ex) ;
		RegistreEntity pendent = registreRepository.findOne(pendentId);
		ReglaEntity regla = pendent.getRegla();
		if (regla != null && ReglaTipusEnumDto.BACKOFFICE.equals(regla.getTipus())) {
			Integer intentsRegla = regla.getBackofficeIntents();
			if (intentsRegla == null) {
				pendent.updateProces(
						new Date(),
						RegistreProcesEstatEnum.ERROR,
						error);
			} else {
				Integer intentsPendent = pendent.getProcesIntents();
				if (intentsPendent != null && intentsPendent.intValue() >= intentsRegla.intValue() - 1) {
					pendent.updateProces(
							new Date(),
							RegistreProcesEstatEnum.ERROR,
							error);
				} else {
					pendent.updateProces(
							new Date(),
							RegistreProcesEstatEnum.PENDENT,
							error);
				}
			}
		} else {
			pendent.updateProces(
					new Date(),
					RegistreProcesEstatEnum.ERROR,
					error);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void actualitzarEstatErrorTancament(
			Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		registre.updateArxiuTancatError(true);
	}

	//procés i distribució d'anotacions
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void tancarExpedientArxiu(Long anotacioId) throws Exception {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		pluginHelper.distribucioContenidorMarcarProcessat(anotacio);
		anotacio.updateArxiuTancat(true);
		registreRepository.saveAndFlush(anotacio);
	}



	private RegistreInteressat fromInteressatEntity(
			RegistreInteressatEntity interessatEntity) {
		RegistreInteressat interessat = new RegistreInteressat();
		if (interessatEntity.getTipus() != null)
			interessat.setTipus(interessatEntity.getTipus().getValor());
		if (interessatEntity.getDocumentTipus() != null)
			interessat.setDocumentTipus(interessatEntity.getDocumentTipus().getValor());
		interessat.setDocumentNum(interessatEntity.getDocumentNum());
		interessat.setNom(interessatEntity.getNom());
		interessat.setLlinatge1(interessatEntity.getLlinatge1());
		interessat.setLlinatge2(interessatEntity.getLlinatge2());
		interessat.setRaoSocial(interessatEntity.getRaoSocial());
		interessat.setPais(interessatEntity.getPais());
		interessat.setProvincia(interessatEntity.getProvincia());
		interessat.setMunicipi(interessatEntity.getMunicipi());
		interessat.setAdresa(interessatEntity.getAdresa());
		interessat.setCodiPostal(interessatEntity.getCodiPostal());
		interessat.setEmail(interessatEntity.getEmail());
		interessat.setTelefon(interessatEntity.getTelefon());
		interessat.setEmailHabilitat(interessatEntity.getEmailHabilitat());
		if (interessatEntity.getCanalPreferent() != null)
			interessat.setCanalPreferent(interessatEntity.getCanalPreferent().getValor());
		interessat.setObservacions(interessatEntity.getObservacions());
		if (interessatEntity.getRepresentant() != null) {
			interessat.setRepresentant(
					fromInteressatEntity(interessatEntity.getRepresentant()));
		}
		return interessat;
	}

	private RegistreAnnex fromAnnexEntity(
			RegistreAnnexEntity annexEntity,
			RegistreAnotacio registre) {
		RegistreAnnex annex = new RegistreAnnex();
		annex.setId(annexEntity.getId());
		annex.setTitol(annexEntity.getTitol());
		annex.setFitxerNom(annexEntity.getFitxerNom());
		annex.setFitxerTamany(annexEntity.getFitxerTamany());
		annex.setFitxerTipusMime(annexEntity.getFitxerTipusMime());
		annex.setFitxerArxiuUuid(annexEntity.getFitxerArxiuUuid());
		annex.setEniDataCaptura(annexEntity.getDataCaptura());
		annex.setLocalitzacio(annexEntity.getLocalitzacio());
		if (annexEntity.getOrigenCiutadaAdmin() != null)
			annex.setEniOrigen(annexEntity.getOrigenCiutadaAdmin().getValor());
		if (annexEntity.getNtiTipusDocument() != null)
			annex.setEniTipusDocumental(annexEntity.getNtiTipusDocument().getValor());
		if (annexEntity.getSicresTipusDocument() != null)
			annex.setSicresTipusDocument(annexEntity.getSicresTipusDocument().getValor());
		if (annexEntity.getNtiElaboracioEstat() != null)
			annex.setEniEstatElaboracio(annexEntity.getNtiElaboracioEstat().getValor());
		annex.setObservacions(annexEntity.getObservacions());
		annex.setTimestamp(annexEntity.getTimestamp());
		annex.setValidacioOCSP(annexEntity.getValidacioOCSP());
		return annex;
	}

	private RegistreInteressatEntity crearInteressatEntity(
			RegistreInteressat registreInteressat,
			RegistreEntity registre) {
		RegistreInteressatTipusEnum interessatTipus = RegistreInteressatTipusEnum.valorAsEnum(registreInteressat.getTipus());
		RegistreInteressatEntity.Builder interessatBuilder;
		switch (interessatTipus) {
		case PERSONA_FIS:
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					RegistreInteressatDocumentTipusEnum.valorAsEnum(registreInteressat.getDocumentTipus()),
					registreInteressat.getDocumentNum(),
					registreInteressat.getNom(),
					registreInteressat.getLlinatge1(),
					registreInteressat.getLlinatge2(),
					registre);
			break;
		default: // PERSONA_JUR o ADMINISTRACIO
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					RegistreInteressatDocumentTipusEnum.valorAsEnum(registreInteressat.getDocumentTipus()),
					registreInteressat.getDocumentNum(),
					registreInteressat.getRaoSocial(),
					registre);
			break;
		}
		RegistreInteressatEntity interessatEntity = interessatBuilder.
		pais(registreInteressat.getPais()).
		provincia(registreInteressat.getProvincia()).
		municipi(registreInteressat.getMunicipi()).
		adresa(registreInteressat.getAdresa()).
		codiPostal(registreInteressat.getCodiPostal()).
		email(registreInteressat.getEmail()).
		telefon(registreInteressat.getTelefon()).
		emailHabilitat(registreInteressat.getEmailHabilitat()).
		canalPreferent(
				RegistreInteressatCanalEnum.valorAsEnum(
						registreInteressat.getCanalPreferent())).
		observacions(registreInteressat.getObservacions()).
		build();
		if (registreInteressat.getRepresentant() != null) {
			RegistreInteressat representant = registreInteressat.getRepresentant();
			interessatEntity.updateRepresentant(
					RegistreInteressatTipusEnum.valorAsEnum(representant.getTipus()),
					RegistreInteressatDocumentTipusEnum.valorAsEnum(representant.getDocumentTipus()),
					representant.getDocumentNum(),
					representant.getNom(),
					representant.getLlinatge1(),
					representant.getLlinatge2(),
					representant.getRaoSocial(),
					representant.getPais(),
					representant.getProvincia(),
					representant.getMunicipi(),
					representant.getAdresa(),
					representant.getCodiPostal(),
					representant.getEmail(),
					representant.getTelefon(),
					representant.getEmailHabilitat(),
					RegistreInteressatCanalEnum.valorAsEnum(representant.getCanalPreferent()));
		}
		registreInteressatRepository.save(interessatEntity);
		return interessatEntity;
	}

	private RegistreAnnexEntity crearAnnexEntity(
			RegistreAnnex registreAnnex,
			RegistreEntity registre) {
		String gestioDocumentalId = null;
		if (registreAnnex.getFitxerContingut() != null) {
			gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
					registreAnnex.getFitxerContingut());
		}
		RegistreAnnexEntity annexEntity = RegistreAnnexEntity.getBuilder(
				registreAnnex.getTitol(),
				registreAnnex.getFitxerNom(),
				registreAnnex.getFitxerTamany(),
				registreAnnex.getFitxerArxiuUuid(),
				registreAnnex.getEniDataCaptura(),
				RegistreAnnexOrigenEnum.valorAsEnum(registreAnnex.getEniOrigen()),
				RegistreAnnexNtiTipusDocumentEnum.valorAsEnum(registreAnnex.getEniTipusDocumental()),
				RegistreAnnexSicresTipusDocumentEnum.valorAsEnum(registreAnnex.getSicresTipusDocument()),
				registre).
				fitxerTipusMime(registreAnnex.getFitxerTipusMime()).
				localitzacio(registreAnnex.getLocalitzacio()).
				ntiElaboracioEstat(RegistreAnnexElaboracioEstatEnum.valorAsEnum(registreAnnex.getEniEstatElaboracio())).
				observacions(registreAnnex.getObservacions()).
				build();
		annexEntity.updateGesdocDocumentId(gestioDocumentalId);
		registreAnnexRepository.saveAndFlush(annexEntity);
		if (registreAnnex.getFirmes() != null && registreAnnex.getFirmes().size() > 0) {
			for (Firma firma: registreAnnex.getFirmes()) {
				annexEntity.getFirmes().add(
						crearFirmaEntity(
								firma,
								annexEntity));
			}
		}
		return annexEntity;
	}

	private RegistreAnnexFirmaEntity crearFirmaEntity(
			Firma firma,
			RegistreAnnexEntity annex) {
		String gestioDocumentalId = null;
		if (firma.getContingut() != null) {
			gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP,
					firma.getContingut());
		}
		RegistreAnnexFirmaEntity firmaEntity = RegistreAnnexFirmaEntity.getBuilder(
				firma.getTipus(),
				firma.getPerfil(),
				firma.getFitxerNom(),
				firma.getTipusMime(),
				firma.getCsvRegulacio(),
				false,
				annex).build();
		firmaEntity.updateGesdocFirmaId(gestioDocumentalId);
		registreAnnexFirmaRepository.save(firmaEntity);
		return firmaEntity;
	}

	private void esborrarDocsTemporals(RegistreEntity anotacioEntity) {
		if (anotacioEntity.getAnnexos() != null && anotacioEntity.getAnnexos().size() > 0) {
			for (RegistreAnnexEntity annex: anotacioEntity.getAnnexos()) {
				if (annex.getGesdocDocumentId() != null) {
					pluginHelper.gestioDocumentalDelete(
							annex.getGesdocDocumentId(), 
							PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP);
					annex.updateGesdocDocumentId(null);
				}
				
				for(RegistreAnnexFirmaEntity firma: annex.getFirmes()) {
					if (firma.getGesdocFirmaId() != null) {
						pluginHelper.gestioDocumentalDelete(
								firma.getGesdocFirmaId(), 
								PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP);
						firma.updateGesdocFirmaId(null);
					}
				}
			}
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
	private void processarAnnexSistra(
			RegistreEntity anotacio,
			RegistreAnnexEntity annex) {
		try {
			byte[] annexContingut = null;
			if (annex.getGesdocDocumentId() != null) {
				ByteArrayOutputStream baos_doc = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
					annex.getGesdocDocumentId(), 
					PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
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

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);

}
