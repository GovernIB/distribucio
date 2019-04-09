/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArxiuContingutDto;
import es.caib.distribucio.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.helper.AlertaHelper;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;

/**
 * Implementació dels mètodes per a gestionar anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RegistreServiceImpl implements RegistreService {

	@Resource
	private RegistreRepository registreRepository;
	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private RegistreAnnexRepository registreAnnexRepository;

	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private ReglaHelper reglaHelper;
	@Resource
	private BustiaHelper bustiaHelper;
	@Resource
	private RegistreHelper registreHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private AlertaHelper alertaHelper;

	@Transactional(readOnly = true)
	@Override
	public RegistreAnotacioDto findOne(
			Long entitatId,
			Long contingutId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		

		RegistreAnotacioDto registreAnotacio = (RegistreAnotacioDto)contingutHelper.toContingutDto(
				registre);
		

		contingutHelper.tractarInteressats(registreAnotacio.getInteressats());		
		
		return registreAnotacio;
	}
	
	@Transactional
	@Override
	public RegistreAnnexDetallDto getRegistreJustificant(
			Long entitatId,
			Long contingutId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		

		RegistreAnnexDetallDto justificant = getJustificantPerRegistre(
					entitat, 
					contingut, 
					registre.getJustificantArxiuUuid(), 
					true);
		
		justificant.setRegistreId(registreId);
		
		
		return justificant;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<RegistreAnnexDetallDto> getAnnexos(
			Long entitatId,
			Long contingutId,
			Long registreId	
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		
		

		List<RegistreAnnexDetallDto> anexosDto = new ArrayList<>();
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			
			RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
					annexEntity,
					RegistreAnnexDetallDto.class);
			annex.setAmbDocument(true);
			anexosDto.add(annex);
		}
			
		return anexosDto;
	}

	@Transactional
	@Override
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		logger.debug("Rebutjar anotació de registre a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(
				registreId,
				null);
		if (!registre.getPare().equals(bustia)) {
			logger.error("No s'ha trobat el registre a dins la bústia (" +
					"bustiaId=" + bustiaId + "," +
					"registreId=" + registreId + ")");
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"La bústia especificada (id=" + bustiaId + ") no coincideix amb la bústia de l'anotació de registre");
		}
		registre.updateMotiuRebuig(motiu);
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
	}

	@Override
	@Scheduled(
			fixedDelayString = "${config:es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio}")
	public void guardarAnnexosArxiuPendents() {
		if (bustiaHelper.isProcessamentAsincronProperty()) {
			logger.debug("Execució de tasca programada: guardar annexos pendents a l'arxiu");
			int maxReintents = getGuardarAnnexosMaxReintentsProperty();
			List<RegistreEntity> pendents = registreRepository.findGuardarAnnexPendents(maxReintents);
			if (pendents != null && !pendents.isEmpty()) {
				logger.debug("Processant annexos pendents de guardar a l'arxiu de " + pendents.size() + " anotacions de registre");
				Exception excepcio = null;
				for (RegistreEntity pendent: pendents)
					try {
						logger.debug("Processant anotacio pendent de guardar a l'arxiu (pendentId=" + pendent.getId() +", pendentNom=" + pendent.getNom() + ")");
						excepcio = registreHelper.processarAnotacioPendentArxiu(pendent.getId());
					} catch (Exception e) {
						excepcio = e;
					} finally {
						if (excepcio != null)
							logger.error("Error processant l'anotacio pendent de l'arxiu (pendentId=" + pendent.getId() + ", pendentNom=" + pendent.getNom() + "): " + excepcio.getMessage(), excepcio);
					}
			} else {
				logger.debug("No hi ha anotacions amb annexos pendents de guardar a l'arxiu");
			}
		}
	}

	@Override
	@Scheduled(
			fixedDelayString = "${config:es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio}")
	public void aplicarReglesPendents() {
		logger.debug("Execució de tasca programada: aplicar regles pendents");
		int maxReintents = getAplicarReglesMaxReintentsProperty();
		List<RegistreEntity> pendents = registreRepository.findAmbReglaPendentAplicar(maxReintents);
		logger.debug("Aplicant regles a " + pendents.size() + " anotacions de registre pendents");
		if (pendents != null && !pendents.isEmpty()) {
			Calendar properProcessamentCal = Calendar.getInstance();
			for (RegistreEntity pendent: pendents) {
				// Comprova si ha passat el temps entre reintents o ha d'esperar
				boolean esperar = false;
				Date darrerProcessament = pendent.getProcesData();
				Integer minutsEntreReintents = pendent.getRegla().getBackofficeTempsEntreIntents();
				if (darrerProcessament != null && minutsEntreReintents != null) {
					// Calcula el temps pel proper intent
					properProcessamentCal.setTime(darrerProcessament);
					properProcessamentCal.add(Calendar.MINUTE, minutsEntreReintents);
					esperar = new Date().before(properProcessamentCal.getTime());
				}
				if (!esperar) {
					registreHelper.processarAnotacioPendentRegla(pendent.getId());
				}
			}
		} else {
			logger.debug("No hi ha anotacions de registre amb regles pendents de processar");
		}
	}

	@Override
	@Scheduled(
			fixedDelayString = "${config:es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio}")
	//@Scheduled(fixedRate = 120000)
	public void tancarContenidorsArxiuPendents() {
		logger.debug("Execució de tasca programada: tancar contenidors arxiu pendents");
		List<RegistreEntity> pendents = registreRepository.findPendentsTancarArxiu(new Date());
		if (pendents != null && !pendents.isEmpty()) {
			logger.debug("Tancant contenidors d'arxiu de " + pendents.size() + " anotacions de registre pendents");
			for (RegistreEntity registre: pendents) {
				registreHelper.tancarExpedientArxiu(registre.getId());
			}
		} else {
			logger.debug("No hi ha anotacions de registre amb contenidors d'arxiu pendents de tancar");
		}
	}
	


	@Override
	@Transactional
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				false);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}

	@Override
	@Transactional
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per usuaris (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto getArxiuAnnex(
			Long annexId) {
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto arxiu = new FitxerDto();
		
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(annex.getRegistre(), annex.getFitxerArxiuUuid(), null, true, true);
		
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(annex.getFitxerNom());
				arxiu.setContentType(documentContingut.getTipusMime());
				arxiu.setContingut(documentContingut.getContingut());
				arxiu.setTamany(documentContingut.getContingut().length);
			}
		}
		return arxiu;
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto getJustificant(
			Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		FitxerDto arxiu = new FitxerDto();
		
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(registre, registre.getJustificantArxiuUuid(), null, true, true);
		
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(obtenirJustificantNom(document));
				arxiu.setContentType(documentContingut.getTipusMime());
				arxiu.setContingut(documentContingut.getContingut());
				arxiu.setTamany(documentContingut.getContingut().length);
			}
		}
		return arxiu;
	}
	
	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFirmaContingut(
			Long annexId,
			int indexFirma) {
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto arxiu = new FitxerDto();
		
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(annex.getRegistre(), annex.getFitxerArxiuUuid(), null, true);
		
		if (document != null) {
			List<Firma> firmes = document.getFirmes();
			if (firmes != null && firmes.size() > 0) {
				
				Iterator<Firma> it = firmes.iterator();
				while (it.hasNext()) {
					Firma firma = it.next();
					if (firma.getTipus() == FirmaTipus.CSV) {
						it.remove();
					}
				}
				
				Firma firma = firmes.get(indexFirma);
				RegistreAnnexFirmaEntity firmaEntity = annex.getFirmes().get(indexFirma);
				if (firma != null && firmaEntity != null) {
					arxiu.setNom(firmaEntity.getFitxerNom());
					arxiu.setContentType(firmaEntity.getTipusMime());
					arxiu.setContingut(firma.getContingut());
					arxiu.setTamany(firma.getContingut().length);
				}
			}
		}
		return arxiu;
	}

	@Transactional(readOnly = true)
	@Override
	public List<RegistreAnnexDetallDto> getAnnexosAmbArxiu(
			Long entitatId,
			Long contingutId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		List<RegistreAnnexDetallDto> annexos = new ArrayList<RegistreAnnexDetallDto>();
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
					annexEntity,
					RegistreAnnexDetallDto.class);
			if (annex.getFitxerArxiuUuid() != null && !annex.getFitxerArxiuUuid().isEmpty()) {
				Document document = pluginHelper.arxiuDocumentConsultar(
						contingut,
						annex.getFitxerArxiuUuid(),
						null,
						true);
				DocumentMetadades metadades = document.getMetadades();
				if (metadades != null) {
					annex.setFirmaCsv(metadades.getMetadadaAddicional("eni:csv") != null ? String.valueOf(metadades.getMetadadaAddicional("eni:csv")) : null);
					
				}
				annex.setAmbDocument(true);
				
				if (document.getFirmes() != null && document.getFirmes().size() > 0) {
					List<ArxiuFirmaDto> firmes = registreHelper.convertirFirmesAnnexToArxiuFirmaDto(annexEntity, null);
					Iterator<Firma> it = document.getFirmes().iterator();
					
					int firmaIndex = 0;
					while (it.hasNext()) {
						Firma arxiuFirma = it.next();
						if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {
							ArxiuFirmaDto firma = firmes.get(firmaIndex);
							if (pluginHelper.isValidaSignaturaPluginActiu()) {
								byte[] documentContingut = document.getContingut().getContingut();
								byte[] firmaContingut = arxiuFirma.getContingut();
								if (	ArxiuFirmaTipusEnumDto.XADES_DET.equals(firma.getTipus()) ||
										ArxiuFirmaTipusEnumDto.CADES_DET.equals(firma.getTipus())) {
									firmaContingut = arxiuFirma.getContingut();
								}
								firma.setDetalls(
										pluginHelper.validaSignaturaObtenirDetalls(
												documentContingut,
												firmaContingut));
							}
							firmaIndex++;
						} else {
							it.remove();
						}
					}
					annex.setFirmes(firmes);
					annex.setAmbFirma(true);
 				}
				
			}
			annexos.add(annex);
		}
		return annexos;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDetallDto getAnnexAmbArxiu(
			Long entitatId,
			Long contingutId,
			Long registreId,
			String fitxerArxiuUuid
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		
		
		RegistreAnnexEntity chosenAnnexEntity=null;
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			if (annexEntity.getFitxerArxiuUuid().equals(fitxerArxiuUuid)) {
				chosenAnnexEntity = annexEntity;
			}
		}
			
		RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
				chosenAnnexEntity,
				RegistreAnnexDetallDto.class);
		annex.setAmbDocument(true);
			
			
		return annex;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDetallDto getAnnexFirmesAmbArxiu(
			Long entitatId,
			Long contingutId,
			Long registreId,
			String fitxerArxiuUuid
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		RegistreAnnexEntity chosenAnnexEntity=null;
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			if (annexEntity.getFitxerArxiuUuid().equals(fitxerArxiuUuid)) {
				chosenAnnexEntity = annexEntity;
			}
		}
		RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
				chosenAnnexEntity,
				RegistreAnnexDetallDto.class);
		if (annex.getFitxerArxiuUuid() != null && !annex.getFitxerArxiuUuid().isEmpty()) {
			Document document = pluginHelper.arxiuDocumentConsultar(
					contingut,
					annex.getFitxerArxiuUuid(),
					null,
					true);
			annex.setAmbDocument(true);
			if (document.getFirmes() != null && document.getFirmes().size() > 0) {
				List<ArxiuFirmaDto> firmes = registreHelper.convertirFirmesAnnexToArxiuFirmaDto(chosenAnnexEntity, null);
				Iterator<Firma> it = document.getFirmes().iterator();
				int firmaIndex = 0;
				while (it.hasNext()) {
					Firma arxiuFirma = it.next();
					if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {
						ArxiuFirmaDto firma = firmes.get(firmaIndex);
						if (pluginHelper.isValidaSignaturaPluginActiu()) {
							byte[] documentContingut = document.getContingut().getContingut();
							byte[] firmaContingut = arxiuFirma.getContingut();
							if (	ArxiuFirmaTipusEnumDto.XADES_DET.equals(firma.getTipus()) ||
									ArxiuFirmaTipusEnumDto.CADES_DET.equals(firma.getTipus())) {
								firmaContingut = arxiuFirma.getContingut();
							}
							firma.setDetalls(
									pluginHelper.validaSignaturaObtenirDetalls(
											documentContingut,
											firmaContingut));
						}
						firmaIndex++;
					} else {
						it.remove();
					}
				}
				annex.setFirmes(firmes);
				annex.setAmbFirma(true);
			}
		}
		return annex;
	}

	@Transactional(readOnly = true)
	@Override
	public RegistreAnotacioDto findAmbIdentificador(String identificador) {
		RegistreAnotacioDto registreAnotacioDto;
		RegistreEntity registre = registreRepository.findByIdentificador(identificador);
		if (registre != null)
			registreAnotacioDto = (RegistreAnotacioDto) contingutHelper.toContingutDto(registre);
		else
			registreAnotacioDto = null;
		return registreAnotacioDto;
	}

	@Transactional
	@Override
	public void updateProces(
			Long registreId, 
			RegistreProcesEstatEnum procesEstat, 
			RegistreProcesEstatSistraEnum procesEstatSistra,
			String resultadoProcesamiento) {
		logger.debug("Actualitzar estat procés anotació de registre ("
				+ "registreId=" + registreId + ", "
				+ "procesEstat=" + procesEstat + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "resultadoProcesamiento=" + resultadoProcesamiento + ")");
		RegistreEntity registre = registreRepository.findOne(registreId);
		registre.updateProces(
				procesEstat,
				resultadoProcesamiento != null ? new Exception(resultadoProcesamiento) : null);
		registre.updateProcesSistra(procesEstatSistra);
	}

	@Transactional (readOnly = true)
	@Override
	public List<String> findPerBackofficeSistra(
			String identificadorProcediment, 
			String identificadorTramit,
			RegistreProcesEstatSistraEnum procesEstatSistra, 
			Date desdeDate, 
			Date finsDate) {
		logger.debug("Consultant els numeros d'entrada del registre pel backoffice Sistra ("
				+ "identificadorProcediment=" + identificadorProcediment + ", "
				+ "identificadorTramit=" + identificadorTramit + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "desdeDate=" + desdeDate + ", "
				+ "finsDate=" + finsDate + ")");
		
		return registreRepository.findPerBackofficeSistra(
				identificadorProcediment,
				identificadorTramit,
				procesEstatSistra == null,
				procesEstatSistra,
				desdeDate == null,
				desdeDate,
				finsDate == null,
				finsDate);
	}

	@Transactional
	@Override
	public RegistreAnotacioDto marcarLlegida(
			Long entitatId,
			Long contingutId,
			Long registreId) {
		logger.debug("Marcan com a llegida l'anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					contingut,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
					contingut,
					registreId);
		registre.updateLlegida(true);
		
		return (RegistreAnotacioDto) contingutHelper.toContingutDto(
				registre);		
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		logger.debug("Obtenint informació de l'arxiu per l'anotacio ("
				+ "registreAnotacioId=" + registreAnotacioId + ")");
		RegistreEntity registre = registreRepository.findOne(registreAnotacioId);
		
		ArxiuDetallDto arxiuDetall = null;
		if (registre.getExpedientArxiuUuid() != null) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientInfo(registre.getExpedientArxiuUuid());
			List<ContingutArxiu> continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.INDEX_REMISSIO);
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
			if (continguts != null) {
				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
				for (ContingutArxiu cont: continguts) {
					ArxiuContingutDto detallFill = new ArxiuContingutDto();
					detallFill.setIdentificador(
							cont.getIdentificador());
					detallFill.setNom(
							cont.getNom());
					if (cont.getTipus() != null) {
						switch (cont.getTipus()) {
						case EXPEDIENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
							break;
						case DOCUMENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
							break;
						case CARPETA:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
							break;
						}
					}
					detallFills.add(detallFill);
				}
				arxiuDetall.setFills(detallFills);
			}
		}
		return arxiuDetall;
	}


	private Exception processarAnotacioPendent(RegistreEntity anotacio) {
		boolean pendentArxiu = RegistreProcesEstatEnum.ARXIU_PENDENT.equals(
				anotacio.getProcesEstat());
		boolean pendentRegla = RegistreProcesEstatEnum.REGLA_PENDENT.equals(
				anotacio.getProcesEstat());
		Exception exceptionProcessant = null;
		if (pendentArxiu) {
			exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(
					anotacio.getId());
			if (exceptionProcessant != null) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
						anotacio.getId());
			}
			return exceptionProcessant;
		} else if (pendentRegla) {
			exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
					anotacio.getId());
			return exceptionProcessant;
		} else {
			throw new ValidationException(
					anotacio.getId(),
					RegistreEntity.class,
					"L'anotació de registre no es troba en estat pendent");
		}
	}

	private RegistreAnnexDetallDto getJustificantPerRegistre(
			EntitatEntity entitat,
			ContingutEntity contingut,
			String justificantUuid,
			boolean ambContingut) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitat.getId() + ", "
				+ "contingutId=" + contingut.getId() + ", "
				+ "justificantUuid=" + justificantUuid + ")");
		

		RegistreAnnexDetallDto annex = new RegistreAnnexDetallDto();
		Document document = pluginHelper.arxiuDocumentConsultar(contingut, justificantUuid, null, ambContingut);
		
		annex.setFitxerNom(obtenirJustificantNom(document));
		annex.setFitxerTamany(document.getContingut().getContingut().length);
		annex.setFitxerTipusMime(document.getContingut().getTipusMime());
		annex.setTitol(document.getNom());
		DocumentMetadades metadades = document.getMetadades();
		if (metadades != null) {
			annex.setDataCaptura(metadades.getDataCaptura());
			annex.setOrigenCiutadaAdmin(metadades.getOrigen().name());
			annex.setNtiElaboracioEstat(metadades.getEstatElaboracio().name());
			annex.setNtiTipusDocument(metadades.getTipusDocumental().name());
			annex.setFirmaCsv(metadades.getMetadadaAddicional("eni:csv") != null ? String.valueOf(metadades.getMetadadaAddicional("eni:csv")) : null);
		}
		
		annex.setAmbDocument(true);
		
		return annex;
	}

	private String obtenirJustificantNom(Document document) {
		String fileName = "";
		String fileExtension = "";
		
		if (document.getContingut() != null) { 
			if (document.getContingut().getTipusMime() != null)
				fileExtension = document.getContingut().getTipusMime();
			
			if (document.getContingut().getArxiuNom() != null && !document.getContingut().getArxiuNom().isEmpty()) {
				fileName = document.getContingut().getArxiuNom();
				fileExtension = document.getContingut().getTipusMime();
			} else {
				fileName = document.getNom();
			}
		} else {
			fileName = document.getNom();
		}
		
		String fragment = "";
    	if (fileName.length() > 4)
    		fragment = fileName.substring(fileName.length() -5);
    	
    	if (fragment.contains("."))
    		return fileName;
    	
    	if (!fileExtension.isEmpty()) {
    		if (fileExtension.contains("/")) {
    			fileName += ("." + fileExtension.split("/")[1]);
    		} else if (fileExtension.contains(".")) {
    			fileName += fileExtension;
    		} else {
    			fileName += "." + fileExtension;
    		}
    	}
    	
		return fileName;
	}

	private int getGuardarAnnexosMaxReintentsProperty() {
		//String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.dist.anotacio.pendent.max.reintents");
		String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}
	private int getAplicarReglesMaxReintentsProperty() {
		String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.aplicar.regles.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
