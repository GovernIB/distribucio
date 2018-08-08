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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
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
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentMetadades;
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
		registre.updateRebuig(motiu);
		// Refrescam cache usuaris bústia
		bustiaHelper.evictElementsPendentsBustia(
				entitat,
				bustia);
	}

	@Override
	@Transactional
	@Async
	@Scheduled(fixedRateString = "60000")
	public void reglaAplicarPendentsBackofficeSistra() {
		logger.debug("Aplicant regles a les anotacions pendents per a regles de backoffice tipus Sistra");
		List<RegistreEntity> pendents = registreRepository.findAmbReglaPendentProcessarBackofficeSistra();
		logger.debug("Aplicant regles a " + pendents.size() + " registres pendents");
		if (!pendents.isEmpty()) {
			Date ara;
			Date darrerProcessament;
			Integer minutsEntreReintents;
			Calendar properProcessamentCal = Calendar.getInstance();
			for (RegistreEntity pendent: pendents) {
				try {
					// Comprova si ha passat el temps entre reintents o ha d'esperar
					boolean esperar = false;
					darrerProcessament = pendent.getProcesData();
					minutsEntreReintents = pendent.getRegla().getBackofficeTempsEntreIntents();
					if (darrerProcessament != null && minutsEntreReintents != null) {
						// Calcula el temps pel proper intent
						properProcessamentCal.setTime(darrerProcessament);
						properProcessamentCal.add(Calendar.MINUTE, minutsEntreReintents);
						ara  = new Date();
						esperar = ara.before(properProcessamentCal.getTime());
					}
					if (!esperar) {
						try {
							registreHelper.distribuirAnotacioPendent(pendent.getId());
						} catch (Exception e) {
							registreHelper.actualitzarEstatError(
									pendent.getId(), 
									e);
						}
					}
				} catch (Exception e) {
					alertaHelper.crearAlerta(
							messageHelper.getMessage(
									"alertes.segon.pla.aplicar.regles.backoffice.sistra.error",
									new Object[] {pendent.getId()}),
							e,
							pendent.getId());
				}
			}
		} else {
			logger.debug("No hi ha registres pendents de processar");
		}
	}
	
	@Value("${config:es.caib.distribucio.tasca.dist.anotacio.asincrona}")
    private boolean isDistAsincEnabled;
	
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.dist.anotacio.pendent.periode.execucio}")
	public void distribuirAnotacionsPendents() {
		
		if (isDistAsincEnabled) {
		
			logger.debug("Distribuïnt anotacions de registere pendents");
			try {
				String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.dist.anotacio.pendent.max.reintents");
				List<RegistreEntity> pendents = registreRepository.findPendentsDistribuir(Integer.parseInt(maxReintents));
				
				logger.debug("Distribuïnt " + pendents.size() + " anotacion pendents");
				if (!pendents.isEmpty()) {
					for (RegistreEntity pendent: pendents) {
						try {
							registreHelper.distribuirAnotacioPendent(pendent.getId());
						} catch (Exception e) {
							registreHelper.actualitzarEstatError(
									pendent.getId(), 
									e);
						}
					}
				} else {
					logger.debug("No hi ha anotacions pendents de distribuïr");
				}
			} catch (Exception e) {
				logger.error("Error distribuïnt anotacions pendents", e);
				e.printStackTrace();
			}
		}
	}
	
	@Override
	@Transactional
	public boolean reglaReintentarAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant aplicació de regla a anotació pendent (" +
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
		if (	RegistreProcesEstatEnum.PENDENT.equals(anotacio.getProcesEstat()) ||
				RegistreProcesEstatEnum.ERROR.equals(anotacio.getProcesEstat())) {
			
			try {
				registreHelper.distribuirAnotacioPendent(anotacio.getId());
				return true;
			} catch (Exception e) {
				registreHelper.actualitzarEstatError(
						anotacio.getId(), 
						e);
				return false;
			}
			
		} else {
			throw new ValidationException(
					anotacio.getId(),
					RegistreEntity.class,
					"L'estat de l'anotació no és PENDENT o ERROR");
		}
	}

	@Override
	@Transactional
	public boolean reglaReintentarUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant aplicació de regla a anotació pendent (" +
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
		if (RegistreProcesEstatEnum.PENDENT.equals(anotacio.getProcesEstat()) ||
			RegistreProcesEstatEnum.ERROR.equals(anotacio.getProcesEstat())) {
			try {
				registreHelper.distribuirAnotacioPendent(anotacio.getId());
				return true;
			} catch (Exception e) {
				registreHelper.actualitzarEstatError(
						anotacio.getId(), 
						e);
				return false;
			}
		} else {
			throw new ValidationException(
					anotacio.getId(),
					RegistreEntity.class,
					"L'estat de l'anotació no és PENDENT o ERROR");
		}
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
		document = pluginHelper.arxiuDocumentConsultar(registre, registre.getJustificantArxiuUuid(), null, true);
		
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
				new Date(), 
				procesEstat, 
				resultadoProcesamiento);
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
	

	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
