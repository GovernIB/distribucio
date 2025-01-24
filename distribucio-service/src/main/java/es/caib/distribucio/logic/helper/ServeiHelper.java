/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	@Resource
	private PluginHelper pluginHelper;
	
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
	public void actualitzaServei(
			Servei servei, 
			Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives,
			EntitatEntity entitatEntity) {
		
		String msgInfo;
		msgInfo = servei.getCodigoSIA() + " - " + servei.getNombre();
		logger.info(msgInfo);

		try {
			// Determina la unitat organitzativa
			UnitatOrganitzativaEntity unitatOrganitzativa = this.resoldreUnitatOrganitzativa(
					unitatsOrganitzatives, 
					servei,
					entitatEntity.getCodiDir3());		
			// Consulta el servei a la BBDD
			ServeiEntity serveiEntity = serveiRepository.findByCodi(entitatEntity.getId(), servei.getCodigo());
			if (serveiEntity == null) {
				// Crea el nou servei
				serveiEntity = ServeiEntity.getBuilder(
						servei.getCodigo(),
						servei.getNombre(),
						servei.getCodigoSIA(),
						ServeiEstatEnumDto.VIGENT,						
						unitatOrganitzativa,
						entitatEntity).built();
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
				if (campsActualtizats.size() > 0) {
					serveiEntity.update(
							servei.getCodigo(), 
							servei.getNombre(), 
							servei.getCodigoSIA(),
							ServeiEstatEnumDto.VIGENT,							
							unitatOrganitzativa,
							entitatEntity);					
				} 
			}
		} catch(Exception e) {
			logger.error("Error actualitzant el servei: " + e.toString());		
		}		
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
				Exception ex = null;				
				do {
					// Consulta de la unitat administrativa per codi a Rolsac amb 5 reintents
					int reintents = 0;
					do {
						try {
							unitatAdministrativa = 
									pluginHelper.procedimentGetUnitatAdministrativa(codi);
						} catch (Exception e) {
							reintents++;
							ex = e;
							logger.error("Error consultant la unitat organitzativa amb codi " + codi + " a Rolsac: pel servei " +
										servei.getCodigoSia()  + ex.toString());
							error = reintents++ >= 5;
						}
					} while (unitatAdministrativa == null && !error);
					
					if (unitatAdministrativa != null) {
						if (unitatAdministrativa.getCodiDir3() != null ) {
							codiDir3 = unitatAdministrativa.getCodiDir3();
							uo = unitatOrganitzativaRepository.findByCodi(codiDir3);
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
						uo = unitatOrganitzativaRepository.findByCodi(codiUnitatArrel);						
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
	
	private static final Logger logger = LoggerFactory.getLogger(ServeiHelper.class);
}