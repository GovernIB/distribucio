/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.persist.repository.EntitatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ServeiEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.ServeiRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.Link;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import es.caib.distribucio.plugin.servei.Servei;

/**
 * Helper per operar amb serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ServeiHelper {

	@Autowired private ServeiRepository serveiRepository;
	@Autowired private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired private EntitatRepository entitatRepository;

	@Resource private PluginHelper pluginHelper;
	@Autowired private ConversioTipusHelper conversioTipusHelper;

	/** Progrés d'acualització actual.*/
	public static Map<Long, UpdateProgressDto> serveisActualitzacio = new HashMap<Long, UpdateProgressDto>();


	/** Consutla la llista de serveis de BBDD i marca com a extingits els que no hagi retornat la consulta a Rolsac.
	 * 
	 * @param serveiMap Map amb els serveis de Distribucio.
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualtizarServeisNoVigents(
			EntitatEntity entitat,
			Map<String, Servei> serveiMap) {		

		String msgInfo;		
		msgInfo = "Actualització de serveis no vigents";
		logger.info(msgInfo);
		
		// Consulta els serveis vigents
		List<ServeiEntity> serveisVigents = serveiRepository.findAllByEntitatAndEstat(entitat, ServeiEstatEnumDto.VIGENT);
		msgInfo = "Actualment a la BBDD hi ha " + serveisVigents.size() + " serveis vigents.";
		logger.info(msgInfo);
		
		int nousExtingits=0;
		List<String> serveisExtingits = new ArrayList<String>();
		for (ServeiEntity serveiEntity : serveisVigents) {
			if (!serveiMap.containsKey(serveiEntity.getCodi())) {
				serveiEntity.setEstat(ServeiEstatEnumDto.EXTINGIT);
				serveisExtingits.add(serveiEntity.getCodiSia() + " - " + serveiEntity.getNom());		
				nousExtingits++;
			}
		}	
		
		if (nousExtingits > 0) {
			msgInfo="S'han marcat com a extingits " + nousExtingits + " serveis";
			logger.info(msgInfo);			
		} else {
			msgInfo="No s'ha marcat cap servei com a extingit";
			logger.info(msgInfo);		
		}
		msgInfo = " dels " + serveisVigents.size() + " serveis que estaven vigents.";
		logger.info(msgInfo);
	}

	/** Mètode per tractar per separat un servei vigent de Distribucio. Es consultarà la seva UO i es determinarà si s'ha d'actualtizar, crear o deixar tal 
	 * com està.
	 * 
	 * @param servei 
	 * 			Servei consultat a Distribucio.	
	 * @param unitatsOrganitzatives
	 * 			Map amb les unitats organitzatives per codi per no haver-les de consultar per cada servei.
	 * @param entitatEntity
	 * 			La entitat per a actualitzar el servei
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public ServeiDto actualitzaServei(
			Servei servei, 
			Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives,
			EntitatEntity entitatEntity) {
		
		String msgInfo;
		msgInfo = servei.getCodigoSIA() + " - " + servei.getNombre();
		logger.info(msgInfo);

		ServeiEntity serveiEntity = null;
		try {
			// Determina la unitat organitzativa
			UnitatOrganitzativaEntity unitatOrganitzativa = this.resoldreUnitatOrganitzativa(
					unitatsOrganitzatives, 
					servei,
					entitatEntity.getCodiDir3());		
			// Consulta el servei a la BBDD
			serveiEntity = serveiRepository.findByCodi(entitatEntity.getId(), servei.getCodigo());
			if (serveiEntity == null) {
				// Crea el nou servei
				serveiEntity = ServeiEntity.getBuilder(
						servei.getCodigo(),
						servei.getNombre(),
						servei.getCodigoSIA(),
						ServeiEstatEnumDto.VIGENT,						
						unitatOrganitzativa,
						entitatEntity,
                        servei.isComun()).built();
				serveiRepository.save(serveiEntity);
			} else {
				// Servei existent. Comprova si s'ha d'actualitzar el servei
				List<String> campsActualtizats = new ArrayList<String>();
				if (!servei.getCodigoSIA().equals(serveiEntity.getCodiSia())) {
					campsActualtizats.add("Codi SIA: \"" + serveiEntity.getCodiSia() + "\" -> \"" +
										servei.getCodigoSIA() + "\"");
				}
				if (!servei.getNombre().equals(serveiEntity.getNom())) {
					campsActualtizats.add("Nom: \"" + serveiEntity.getNom() + "\" -> \"" +
										servei.getNombre() + "\"");
				}
				if (!serveiEntity.getEstat().equals(ServeiEstatEnumDto.VIGENT)) {
					campsActualtizats.add("Estat: \"" + ServeiEstatEnumDto.EXTINGIT + "\" -> \"" + 
										ServeiEstatEnumDto.VIGENT + "\"");
				}
				if (!unitatOrganitzativa.getId().equals(serveiEntity.getUnitatOrganitzativa().getId())) {
					campsActualtizats.add("Unitat organitzativa: \"" + serveiEntity.getUnitatOrganitzativa().getCodiAndNom() + "\" -> \"" +
							unitatOrganitzativa.getCodiAndNom() + "\"");
				}
                if (servei.isComun() != serveiEntity.isComu()) {
                    campsActualtizats.add("Comu: \"" + serveiEntity.isComu() + "\" -> \"" +
                            servei.isComun() + "\"");
                }
				if (campsActualtizats.size() > 0) {
					serveiEntity.update(
							servei.getCodigo(), 
							servei.getNombre(), 
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,							
							unitatOrganitzativa,
							entitatEntity,
                            servei.isComun());
				} 
			}
		} catch(Exception e) {
			logger.error("Error actualitzant el servei: " + e.toString());		
		}
		return conversioTipusHelper.convertir(
				serveiEntity, 
				ServeiDto.class);
	}

	
	/** Troba la unitat organitzativa de la BBDD a partir de les dades del servei de Distribucio. Si no troba
	 * la UO amb codi SIA del serveis afegeix un avís al progrés per a que s'actualitzi l'arbre d'unitats. 
	 * @param servei
	 * @param progres
	 * @return
	 */
	private UnitatOrganitzativaEntity resoldreUnitatOrganitzativa(
			Map<String, UnitatOrganitzativaEntity> unitatsOranitzatives,
			Servei servei,
			String codiUnitatArrel) {
		
		UnitatOrganitzativaEntity uo = null;
		Link unidadAdministrativaLink = servei.getUnidadAdministrativa();
		Link organoInstructorLink = servei.getOrganoInstructor();
		String unidadAdministrativaCodigo = null;
		if (unidadAdministrativaLink!=null) {
			unidadAdministrativaCodigo = unidadAdministrativaLink.getCodigo();
		} else if (organoInstructorLink != null) {
			unidadAdministrativaCodigo = organoInstructorLink.getCodigo();
		}
		if (unidadAdministrativaCodigo != null) {
			if (!unitatsOranitzatives.containsKey(unidadAdministrativaCodigo)) {
				String codiDir3 = null;
				// Cerca a rolsac el codi dir3 de la unitat organitzativa del servei, si no en té va cercant en els pares
				UnitatAdministrativa unitatAdministrativa = null;
				String codi = unidadAdministrativaCodigo;
				boolean error = false;
				do {
					// Consulta de la unitat administrativa per codi a Rolsac amb 5 reintents
					int reintents = 0;
					do {
						try {
							unitatAdministrativa = 
									pluginHelper.procedimentGetUnitatAdministrativa(codi);
						} catch (Exception e) {
							logger.error("Error consultant la unitat organitzativa amb codi " + codi + " a Rolsac: pel servei " +
										servei.getCodigoSia()  + e.toString());
						}
                        error = reintents++ >= 5;
					} while (unitatAdministrativa == null && !error);
					
					if (unitatAdministrativa != null) {
						if (unitatAdministrativa.getCodiDir3() != null ) {
							codiDir3 = unitatAdministrativa.getCodiDir3();
							uo = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(codiUnitatArrel, codiDir3);
							if (uo == null && unitatAdministrativa.getPareCodi() != null) {
								codi = unitatAdministrativa.getPareCodi();
							} else {
								codi = null;
							}
						} else if (unitatAdministrativa.getPareCodi() != null) {
							// Cerca el codi Dir3 en la unitat administrativa pare
							codi = unitatAdministrativa.getPareCodi();
						} else {
							codi = null;
						}
					} else {
						codi = null;
					}
				} 
				while(uo == null 
						&& codi != null);
				
				if (uo == null) {
					try {					
						uo = unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(codiUnitatArrel, codiUnitatArrel);
					}catch(Exception e) {
						logger.debug("No s'ha trobat el paràmetre amb codi "+ codiUnitatArrel);
					}
					
				}
				unitatsOranitzatives.put(unidadAdministrativaCodigo, uo);				
			} else {
				uo = unitatsOranitzatives.get(unidadAdministrativaCodigo);
			}
		}
		return uo;
	}

	@Async
	@Transactional
	public void findAndUpdateServeis(Long entitatId) throws Exception {

		String msgInfo;
		UpdateProgressDto progres = null;
		// Comprova si hi ha una altre instància del procés en execució
		if (isUpdatingServeis(entitatId)) {
			logger.debug("Ja existeix un altre procés que està executant l'actualització de serveis per l'entitat " + entitatId + ".");
			return;    // S'està executant l'actualitzacio
		} else {
			progres = new UpdateProgressDto();
			serveisActualitzacio.put(entitatId, progres);
		}

		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		// Els plugins s'instancien per entitat i llegeixen el codi de l'entitat actual del
		// ThreadLocal de ConfigHelper. A la interfície JSP l'hi deixa LlistaEntitatsInterceptor,
		// però les peticions de la interfície REACT (/api/**) estan excloses dels interceptors
		// (veure INTERCEPTOR_EXCLUSIONS a WebMvcConfig), així que el fixam aquí a partir de
		// l'entitat que rep el mètode -- mateix patró que RegistreServiceImpl i SegonPlaServiceImpl.
		ConfigHelper.setEntitatActualCodi(entitat.getCodi());
		msgInfo = "Inici del procés d'actualització de serveis de l'entitat " + entitat.getCodi() + " " + entitat.getNom();
		progres.setEstat(UpdateProgressDto.Estat.INICIALITZANT);
		logger.info(msgInfo);

		List<Servei> serveiList = null;
		int reintents = 1;
		boolean errorConsultaServeis = false;
		Exception exConsultaServeis = null;
		String errMsg = "-";
		do {
			try {
				msgInfo = "Obtenint el llistat de serveis per a l'entitat " + entitat.getCodiDir3();
				logger.info(msgInfo);
				serveiList = pluginHelper.serveiFindByCodiDir3(entitat.getCodiDir3());
			} catch (Exception e) {
				exConsultaServeis = e;
				errMsg = "Error consultant els serveis per l'entitat: " + entitat.getCodiDir3();
				errorConsultaServeis = reintents++ >= 3;
			}
		}
		while (serveiList == null && !errorConsultaServeis);

		try {
			// Comprova si hi ha hagut errors consultant els serveis
			if (errorConsultaServeis) {
				String errorMessage = exConsultaServeis.getMessage() != null ? exConsultaServeis.getMessage() : errMsg;
				throw new Exception(errorMessage, exConsultaServeis);
			}

			if (serveiList == null || serveiList.isEmpty()) {
				throw new Exception(
						"No s'ha obtingut cap llista o resultat per la consulta de serveis: (llista " + (serveiList == null ? "nul·la" : "buida") + ")"
				);
			}

			// Processa els serveis consultats
			msgInfo = "S'han obtingut " + serveiList.size() + " serveis vigents a Rolsac.";
			logger.info(msgInfo);
			progres.setEstat(UpdateProgressDto.Estat.ACTUALITZANT);
			progres.setTotal(serveiList.size());

			// Crea un Map amb els serveis de Distribucio per codi
			Map<String, Servei> serveiMap = new HashMap<String, Servei>();
			for (Servei servei : serveiList) {
				serveiMap.put(servei.getCodigo(), servei);
			}

			// Deshabilita els serveis que no hagi retornat Distribucio
			actualtizarServeisNoVigents(entitat, serveiMap);

			// Processa tots els serveis, actualitza-ne la informació, donant-los d'alta i revisant la seva UO
			msgInfo = "Es procedeix a processar els " + serveiList.size() + " serveis consultats a Rolsac.";
			logger.info(msgInfo);

			// Map<codi unitat rolsac, unitatOrganitzativa> per no haver de consultar la UO de totes les unitats per codi rolsac
			Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives = new HashMap<String, UnitatOrganitzativaEntity>();
			for (Servei servei : serveiList) {
				// Tracta el servei en una transacció a part.
				actualitzaServei(servei, unitatsOrganitzatives, entitat);
				progres.incProcessats();
			}

			progres.setEstat(UpdateProgressDto.Estat.FINALITZAT);
		} catch (Exception e) {
			progres.setEstat(UpdateProgressDto.Estat.ERROR);
			progres.setErrorMsg(e.getMessage());
		}
	}

	public ServeiDto findAndUpdateServei(Long entitatId, String serveiCodi) throws Exception {
		EntitatEntity entitat = entitatRepository.getReferenceById(entitatId);
		// Els plugins s'instancien per entitat i llegeixen el codi de l'entitat actual del
		// ThreadLocal de ConfigHelper. A la interfície JSP l'hi deixa LlistaEntitatsInterceptor,
		// però les peticions de la interfície REACT (/api/**) estan excloses dels interceptors
		// (veure INTERCEPTOR_EXCLUSIONS a WebMvcConfig), així que el fixam aquí a partir de
		// l'entitat que rep el mètode -- mateix patró que RegistreServiceImpl i SegonPlaServiceImpl.
		ConfigHelper.setEntitatActualCodi(entitat.getCodi());

		Servei servei = null;
		int reintents = 1;
		boolean errorConsultaServeis = false;
		Exception exConsultaServeis = null;
		String errMsg = "-";
		do {
			try {
				servei = pluginHelper.serveiGetByCodi(serveiCodi);
			} catch (Exception e) {
				exConsultaServeis = e;
				errMsg = "Error consultant el servei per codi: " + serveiCodi;
			}
			errorConsultaServeis = reintents++ >= 3;
		}
		while (servei == null && !errorConsultaServeis);

		// Comprova si hi ha hagut errors consultant els serveis
		if (errorConsultaServeis && exConsultaServeis != null) {
			String errorMessage = exConsultaServeis.getMessage() != null ? exConsultaServeis.getMessage() : errMsg;
			throw new Exception(errorMessage, exConsultaServeis);
		}

		if (servei == null) {
			throw new Exception(
					"No s'ha obtingut cap resultat per la consulta de servei: (" + serveiCodi + ")"
			);
		}

		ServeiDto serveiDto = actualitzaServei(servei, new HashMap<String, UnitatOrganitzativaEntity>(), entitat);

		return serveiDto;
	}

	public boolean isUpdatingServeis(Long entitatId) {
		UpdateProgressDto progres = serveisActualitzacio.get(entitatId);
		return progres != null
				&& progres.getEstat() != UpdateProgressDto.Estat.FINALITZAT
				&& progres.getEstat() != UpdateProgressDto.Estat.ERROR;
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiHelper.class);
}