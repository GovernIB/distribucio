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

import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ProcedimentEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.ProcedimentRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.Link;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;

/**
 * Helper per operar amb procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentHelper {

	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	@Resource
	private PluginHelper pluginHelper;
	
	/** Consutla la llista de procediments de BBDD i marca com a extingits els que no hagi retornat la consulta a Distribucio.
	 * 
	 * @param procedimentMap Map amb els procediments de Distribucio.
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualtizarProcedimentsNoVigents(
			EntitatEntity entitatActual,
			Map<String, Procediment> procedimentMap) {		

		String msgInfo;		
		msgInfo = "Actualització de procediments no vigents";
		logger.info(msgInfo);
		
		// Consulta els procediments vigents
		List<ProcedimentEntity> procedimentsVigents = procedimentRepository.findAllByEntitatAndEstat(entitatActual, ProcedimentEstatEnumDto.VIGENT);
		msgInfo = "Actualment a la BBDD hi ha " + procedimentsVigents.size() + " procediments vigents.";
		logger.info(msgInfo);
		
		int nousExtingits=0;
		List<String> procedimentsExtingits = new ArrayList<String>();
		for (ProcedimentEntity procedimentEntity : procedimentsVigents) {
			if (!procedimentMap.containsKey(procedimentEntity.getCodi())) {
				procedimentEntity.setEstat(ProcedimentEstatEnumDto.EXTINGIT);
				procedimentsExtingits.add(procedimentEntity.getCodiSia() + " - " + procedimentEntity.getNom());		
				nousExtingits++;
			}
		}	
		
		if (nousExtingits > 0) {
			msgInfo="S'han marcat com a extingits " + nousExtingits + " procediments";
			logger.info(msgInfo);			
		} else {
			msgInfo="No s'ha marcat cap procediment com a extingit";
			logger.info(msgInfo);		
		}
		msgInfo = " dels " + procedimentsVigents.size() + " procediments que estaven vigents.";
		logger.info(msgInfo);
	}

	/** Mètode per tractar per separat un procediment vigent de Distribucio. Es consultarà la seva UO i es determinarà si s'ha d'actualtizar, crear o deixar tal 
	 * com està.
	 * 
	 * @param procediment 
	 * 			Procediment consultat a Distribucio.	
	 * @param unitatsOrganitzatives
	 * 			Map amb les unitats organitzatives per codi per no haver-les de consultar per cada procediment.
	 * @param entitatEntity
	 * 			La entitat per a actualitzar el procediment
	 */
	@Transactional( propagation = Propagation.REQUIRES_NEW)
	public void actualitzaProcediment(
			Procediment procediment, 
			Map<String, UnitatOrganitzativaEntity> unitatsOrganitzatives,
			EntitatEntity entitatEntity) {
		
		String msgInfo;
		msgInfo = procediment.getCodigoSIA() + " - " + procediment.getNombre();
		logger.info(msgInfo);

		try {
			// Determina la unitat organitzativa
			UnitatOrganitzativaEntity unitatOrganitzativa = this.resoldreUnitatOrganitzativa(
					unitatsOrganitzatives, 
					procediment,
					entitatEntity.getCodiDir3());
			// Consulta el procediment a la BBDD
			ProcedimentEntity procedimentEntity = procedimentRepository.findByCodi(entitatEntity.getId(), procediment.getCodigo());
			if (procedimentEntity == null) {
				// Crea el nou procediment
				procedimentEntity = ProcedimentEntity.getBuilder(
						procediment.getCodigo(),
						procediment.getNombre(),
						procediment.getCodigoSIA(),
						ProcedimentEstatEnumDto.VIGENT,						
						unitatOrganitzativa,
						entitatEntity).built();
				procedimentRepository.save(procedimentEntity);
			} else {
				// Procediment existent. Comprova si s'ha d'actualitzar el procediment
				List<String> campsActualtizats = new ArrayList<String>();
				if (!procediment.getCodigoSIA().equals(procedimentEntity.getCodiSia())) {
					campsActualtizats.add("Codi SIA: \"" + procedimentEntity.getCodiSia() + "\" -> \"" +
										procediment.getCodigoSIA() + "\"");
				}
				if (!procediment.getNombre().equals(procedimentEntity.getNom())) {
					campsActualtizats.add("Nom: \"" + procedimentEntity.getNom() + "\" -> \"" +
										procediment.getNombre() + "\"");
				}
				if (!procedimentEntity.getEstat().equals(ProcedimentEstatEnumDto.VIGENT)) {
					campsActualtizats.add("Estat: \"" + ProcedimentEstatEnumDto.EXTINGIT + "\" -> \"" + 
										ProcedimentEstatEnumDto.VIGENT + "\"");
				}			
				if (!unitatOrganitzativa.getId().equals(procedimentEntity.getUnitatOrganitzativa().getId())) {
					campsActualtizats.add("Unitat organitzativa: \"" + procedimentEntity.getUnitatOrganitzativa().getCodiAndNom() + "\" -> \"" +
							unitatOrganitzativa.getCodiAndNom() + "\"");
				}
				if (campsActualtizats.size() > 0) {
					procedimentEntity.update(
							procediment.getCodigo(), 
							procediment.getNombre(), 
							procediment.getCodigoSIA(),
							ProcedimentEstatEnumDto.VIGENT,							
							unitatOrganitzativa,
							entitatEntity);					
				} 
			}
		} catch(Exception e) {
			logger.error("Error actualitzant el procediment: " + e.toString());		
		}		
	}

	
	/** Troba la unitat organitzativa de la BBDD a partir de les dades del procediment de Distribucio. Si no troba
	 * la UO amb codi SIA del procediments afegeix un avís al progrés per a que s'actualitzi l'arbre d'unitats. 
	 * @param procediment
	 * @param progres 
	 * @return
	 */
	private UnitatOrganitzativaEntity resoldreUnitatOrganitzativa(
			Map<String, UnitatOrganitzativaEntity> unitatsOranitzatives,
			Procediment procediment,
			String codiUnitatArrel) {
		
		UnitatOrganitzativaEntity uo = null;
		Link unidadAdministrativaLink = procediment.getUnidadAdministrativa();
		String unidadAdministrativaCodigo = null;
		if (unidadAdministrativaLink!=null) {
			unidadAdministrativaCodigo = unidadAdministrativaLink.getCodigo();
		}		
		if (unidadAdministrativaCodigo != null) {
			if (!unitatsOranitzatives.containsKey(unidadAdministrativaCodigo)) {
				String codiDir3 = null;
				// Cerca a rolsac el codi dir3 de la unitat organitzativa del procediment, si no en té va cercant en els pares
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
							logger.error("Error consultant la unitat organitzativa amb codi " + codi + " a Rolsac: pel procediment " +
										procediment.getCodigoSia()  + ex.toString());
							error = reintents++ >= 5;
						}
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
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentHelper.class);
}