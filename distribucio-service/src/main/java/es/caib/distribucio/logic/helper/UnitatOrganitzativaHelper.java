/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.ArbreNodeDto;
import es.caib.distribucio.logic.intf.dto.MunicipiDto;
import es.caib.distribucio.logic.intf.dto.ProvinciaDto;
import es.caib.distribucio.logic.intf.dto.TipusTransicioEnumDto;
import es.caib.distribucio.logic.intf.dto.TipusViaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.ReglaRepository;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;


/**
 * Helper per a operacions amb unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UnitatOrganitzativaHelper {

	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private ArbreNodeHelper arbreNodeHelper;

	public List<String> getCodisOfUnitatsDescendants(EntitatEntity entitat, String codi) {
		List<String> codisOfUnitatsDescendants = new ArrayList<String>();
		if (codi != null) {
			
			ArbreDto<UnitatOrganitzativaDto> arbre = this.getArbreOfUnitatsDescendants(entitat, codi);
			// Agafa tots els identificadors
			if (arbre != null && !arbre.toList().isEmpty()) {
				for (UnitatOrganitzativaDto uo : arbre.toDadesList()) {
					codisOfUnitatsDescendants.add(uo.getCodi());
				}			
			}
		}
		return codisOfUnitatsDescendants;
	}

	public ArbreDto<UnitatOrganitzativaDto> getArbreOfUnitatsDescendants(
			EntitatEntity entitat, 
			String codiUnitatOrganitzativa) {
		List<UnitatOrganitzativaEntity> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
				.findByCodiDir3Entitat(entitat.getCodiDir3());
		List<UnitatOrganitzativa> unitatsOrganitzatives = conversioTipusHelper
				.convertirList(unitatsOrganitzativesEntities, UnitatOrganitzativa.class);
		ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(true);
		// Cerca l'unitat organitzativa amb el codi donat
		UnitatOrganitzativa unitatOrganitzativaArrel = null;
		for (UnitatOrganitzativa unitatOrganitzativa : unitatsOrganitzatives) {
			if (codiUnitatOrganitzativa.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
				unitatOrganitzativaArrel = unitatOrganitzativa;
				break;
			}
		}
		if (unitatOrganitzativaArrel != null) {
			// Omple l'arbre d'unitats organitzatives
			resposta.setArrel(
					arbreNodeHelper.getNodeArbreUnitatsOrganitzatives(
							unitatOrganitzativaArrel,
							unitatsOrganitzatives,
							null));
			return resposta;
		}
		return null;
	}

	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) throws SistemaExternException{
		EntitatEntity entitat = entitatRepository.getReferenceById(entidadId);
		/*UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getCodiDir3(),
				null, null);*/
		List<UnitatOrganitzativa> unitatsVigentsWS = pluginHelper.findAmbPare(entitat.getCodiDir3(),
				entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());
		// converting form UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentWSDto = new ArrayList<>();
		for(UnitatOrganitzativa vigentObsolete : unitatsVigentsWS){
			unitatsVigentWSDto.add(conversioTipusHelper.convertir(
					vigentObsolete, 
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentWSDto;
	}

	/**
	 * Getting from WS obsolete unitats (with pointer to vigent unitat)  
	 * @param entidadId
	 * @return
	 * @throws SistemaExternException
	 */
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entidadId) throws SistemaExternException{
		EntitatEntity entitat = entitatRepository.getReferenceById(entidadId);
		/*UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getCodiDir3(),
				null, null);*/
		// getting list of last changes from webservices
		List<UnitatOrganitzativa> unitatsWS = pluginHelper.findAmbPare(entitat.getCodiDir3(),
				entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());

		// getting all vigent unitats from database
		List<UnitatOrganitzativaEntity> vigentUnitats = allUnitatsVigentsFromDB(entitat);	

		// list of obsolete unitats from ws that were vigent after last sincro (are vigent in DB and obsolete in WS)
		// the reason why we don't just return all obsolete unitats from ws is because it is possible to have cumulative changes:
		// If since last sincro unitat A changed to B and then to C then in our DB will be A(Vigent) but from WS we wil get: A(Extinguished) -> B(Extinguished) -> C(Vigent) 
		// we only want to return A (we dont want to return B) because prediction must show this transition (A -> C) [between A(that is now vigent in database) and C (that is now vigent in WS)]     
		List<UnitatOrganitzativa> unitatsVigentObsolete = unitatsVigentsDbObsoleteWs(entitat, unitatsWS, vigentUnitats);	
		
		// setting obsolete unitat to point to the last one it transitioned into 
		// Name of the field historicosUO is totally wrong because this field shows future unitats not historic
		// but that's how it is named in WS and we cant change it 
		// lastHistoricosUnitats field should be pointing to the last unitat it transitioned into.
		// We need to recursively find last one because it is possible there will be cumulative changes:
		// If since last sincro unitat A changed to B and then to C then from WS we will get unitat 
		//A pointing to B (A -> B) and unitat B pointing to C (B -> C)  
		// what we want is to add direct pointer from unitat A to C (A -> C)
		for (UnitatOrganitzativa vigentObsolete : unitatsVigentObsolete) {			
		
			vigentObsolete.setLastHistoricosUnitats(getLastHistoricos(vigentObsolete, unitatsWS));
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = toUnitatOrganitzativaDto(unitatsVigentObsolete);
		return unitatsVigentObsoleteDto;
	}

	private List<UnitatOrganitzativaDto> toUnitatOrganitzativaDto(List<UnitatOrganitzativa> unitatsVigentObsolete) {
		List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = new ArrayList<>();
		for(UnitatOrganitzativa vigentObsolete : unitatsVigentObsolete){
			unitatsVigentObsoleteDto.add(conversioTipusHelper.convertir(
					vigentObsolete, 
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentObsoleteDto;
	}

	private List<UnitatOrganitzativa> unitatsVigentsDbObsoleteWs(EntitatEntity entitat,
			List<UnitatOrganitzativa> unitatsWS, List<UnitatOrganitzativaEntity> vigentUnitats) {
		List<UnitatOrganitzativa> unitatsVigentObsolete = new ArrayList<>();
		for (UnitatOrganitzativaEntity unitatV : vigentUnitats) { 
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && !unitatWS.getEstat().equals("V")
						&& !unitatV.getCodi().equals(entitat.getCodiDir3())) {
					unitatsVigentObsolete.add(unitatWS);
				}
			}
		}
		logger.debug("Consulta unitats obsolete ");
		for (UnitatOrganitzativa vigentObsolete : unitatsVigentObsolete) {
			logger.debug(vigentObsolete.getCodi()+" "+vigentObsolete.getEstat()+" "+vigentObsolete.getHistoricosUO());
		}
		return unitatsVigentObsolete;
	}

	private List<UnitatOrganitzativaEntity> allUnitatsVigentsFromDB(EntitatEntity entitat) {
		List<UnitatOrganitzativaEntity> vigentUnitats = unitatOrganitzativaRepository
				.findByCodiDir3AndEstatV(entitat.getCodiDir3());
		logger.debug("Consulta d'unitats vigents a DB");
		for(UnitatOrganitzativaEntity uv: vigentUnitats){
			logger.debug(ToStringBuilder.reflectionToString(uv));
		}
		return vigentUnitats;
	}

	/**
	 * Getting unitats that didn't transition to any other unitats, they only got some properties changed 
	 * @param entidadId
	 * @return
	 */
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId){
		EntitatEntity entitat = entitatRepository.getReferenceById(entidadId);
		List<UnitatOrganitzativa> unitatsVigentsWithChangedAttributes = new ArrayList<>();

		// getting list of last changes from webservices
		List<UnitatOrganitzativa> unitatsWS = pluginHelper.findAmbPare(entitat.getCodiDir3(), null, null);
		// getting all vigent unitats from database
		List<UnitatOrganitzativaEntity> vigentUnitats = unitatOrganitzativaRepository
				.findByCodiDir3AndEstatV(entitat.getCodiDir3());
		
		// Consultem les unitats vigents per comprovar si la denominació coofical és diferent a l'actual
		// i afegim al llistat de sincronitzables les unitats la denominació de les quals 
		//no coincideixi amb la denominació cooficial que porti el webservice 	
		for (UnitatOrganitzativaEntity unitatV : vigentUnitats) {
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
						&& !(unitatV.getDenominacio().equals(unitatWS.getDenominacio()))
						&& !unitatsVigentsWithChangedAttributes.contains(unitatWS)) {
					unitatsVigentsWithChangedAttributes.add(unitatWS);
				}
			}
		}
		
		// getting list of last changes from webservices
		unitatsWS = pluginHelper.findAmbPare(entitat.getCodiDir3(),
						entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());
		// list of vigent unitats from webservice
		for (UnitatOrganitzativaEntity unitatV : vigentUnitats) {
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
						&& (unitatWS.getHistoricosUO() == null || unitatWS.getHistoricosUO().isEmpty())
						&& !unitatV.getCodi().equals(entitat.getCodiDir3())) {
					unitatsVigentsWithChangedAttributes.add(unitatWS);
				}
			}
		}	
		
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentsWithChangedAttributesDto = new ArrayList<>();
		for(UnitatOrganitzativa vigent : unitatsVigentsWithChangedAttributes){
			unitatsVigentsWithChangedAttributesDto.add(conversioTipusHelper.convertir(
					vigent, 
					UnitatOrganitzativaDto.class));
		}
		return unitatsVigentsWithChangedAttributesDto;
	}

	/**
	 * Getting unitats that are new (not transitioned from any other unitat)
	 * @param entidadId
	 * @return
	 */
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entidadId){
		EntitatEntity entitat = entitatRepository.getReferenceById(entidadId);
		// getting list of syncronization unitats from webservices
		List<UnitatOrganitzativa> unitatsWS = pluginHelper.findAmbPare(entitat.getCodiDir3(),
				entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());
		// getting all vigent unitats from database
		List<UnitatOrganitzativaEntity> vigentUnitatsDB = unitatOrganitzativaRepository
				.findByCodiDir3AndEstatV(entitat.getCodiDir3());
		//List of new unitats that are vigent
		List<UnitatOrganitzativa> vigentUnitatsWS = new ArrayList<>();
		//List of new unitats that are vigent and does not exist in database
		List<UnitatOrganitzativa> vigentNotInDBUnitatsWS = new ArrayList<>();
		//List of new unitats (that are vigent, not pointed by any obsolete unitat and does not exist in database)
		List<UnitatOrganitzativa> newUnitatsWS = new ArrayList<>();
		//Filtering to only obtain vigents 
		for (UnitatOrganitzativa unitatWS : unitatsWS) {
			if (unitatWS.getEstat().equals("V") && !unitatWS.getCodi().equals(entitat.getCodiDir3())) {
				vigentUnitatsWS.add(unitatWS);
			}
		}
		// Filtering to only obtain vigents that does not already exist in
		// database
		for (UnitatOrganitzativa vigentUnitat : vigentUnitatsWS) {
			boolean found = false;
			for (UnitatOrganitzativaEntity vigentUnitatDB : vigentUnitatsDB) {
				if (vigentUnitatDB.getCodi().equals(vigentUnitat.getCodi())) {
					found = true;
				}
			}
			if (found == false) {
				vigentNotInDBUnitatsWS.add(vigentUnitat);
			}
		}
		// Filtering to obtain unitats that are vigent, not pointed by any obsolete unitat and does not already exist in database
		for (UnitatOrganitzativa vigentNotInDBUnitatWS : vigentNotInDBUnitatsWS) {
			boolean pointed = false;
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if(unitatWS.getHistoricosUO()!=null){
					for(String novaCodi: unitatWS.getHistoricosUO()){
						if(novaCodi.equals(vigentNotInDBUnitatWS.getCodi())){
							pointed = true;
						}
					}
				}
			}
			if (pointed == false) {
				newUnitatsWS.add(vigentNotInDBUnitatWS);
			}
		}
		// converting from UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> newUnitatsDto = new ArrayList<>();
		for (UnitatOrganitzativa vigent : newUnitatsWS){
			newUnitatsDto.add(conversioTipusHelper.convertir(
					vigent, 
					UnitatOrganitzativaDto.class));
		}
		return newUnitatsDto;
	}

	/**
	 * Method to get last historicos (recursive to cover cumulative synchro case)
	 * @param unitat
	 * @param lastHistorcos
	 */
	public void getLastHistoricosRecursive(
			UnitatOrganitzativaDto unitat, 
			List<UnitatOrganitzativaDto> lastHistorcos) {

		if (unitat.getNoves() == null || unitat.getNoves().isEmpty()) {
			lastHistorcos.add(unitat);
		} else {
			for (UnitatOrganitzativaDto unitatI : unitat.getNoves()) {
				getLastHistoricosRecursive(unitatI, lastHistorcos);
			}
		}
	}

	/**
	 * Starting point, calls recursive method to get last historicos
	 * and sets last historicos and sets the type of transition
	 * 
	 * @param uo
	 * @return
	 */
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		// if there are no historicos leave them empty and don´t go into recursive method
		if (uo.getNoves() == null || uo.getNoves().isEmpty()) {
			return uo;
		} else {
			List<UnitatOrganitzativaDto> lastHistorcos = new ArrayList<>();
			getLastHistoricosRecursive(uo, lastHistorcos);
			uo.setLastHistoricosUnitats(lastHistorcos);

			return uo;
		}
	}

	public void sincronizarOActualizar(EntitatEntity entitat) {
		List<UnitatOrganitzativa> unitats = pluginHelper.findAmbPare(entitat.getCodiDir3(), entitat.getFechaActualizacion(),
					entitat.getFechaSincronizacion());
		// Takes all the unitats from WS and saves them to database. If unitat did't exist in db it creates new one if it already existed it overrides existing one.  
		// In addition, the related rules will also be updated with the new unit entity
		for (UnitatOrganitzativa unidadWS : unitats) {
			sincronizarUnitat(unidadWS, entitat.getCodiDir3());
		}
		// historicos
		for (UnitatOrganitzativa unidadWS : unitats) {
			UnitatOrganitzativaEntity unitat = unitatOrganitzativaRepository
					.findByCodiDir3EntitatAndCodi(entitat.getCodiDir3(), unidadWS.getCodi());
			sincronizarHistoricosUnitat(unitat, unidadWS);
		}
		List<UnitatOrganitzativaEntity> obsoleteUnitats = unitatOrganitzativaRepository
				.findByCodiDir3EntitatAndEstatNotV(entitat.getCodiDir3());
		// setting type of transition
		for (UnitatOrganitzativaEntity obsoleteUnitat : obsoleteUnitats) {
			if (obsoleteUnitat.getNoves().size() > 1) {
				obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.DIVISIO);
			} else {
				if (obsoleteUnitat.getNoves().size() == 1) {
					if (obsoleteUnitat.getNoves().get(0).getAntigues().size() > 1) {
						obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.FUSIO);
					} else if (obsoleteUnitat.getNoves().get(0).getAntigues().size() == 1) {
						obsoleteUnitat.updateTipusTransicio(TipusTransicioEnumDto.SUBSTITUCIO);						
					}
				}
			} 
		}
		// regles
		for (UnitatOrganitzativa unidadWS : unitats)  {
			UnitatOrganitzativaEntity unitat = unitatOrganitzativaRepository
					.findByCodiDir3EntitatAndCodi(entitat.getCodiDir3(), unidadWS.getCodi());
			if ("E".equals(unitat.getEstat())
					&& unitat.getNoves() != null 
					&& unitat.getNoves().size() == 1) {
				sincronitzarRegles(entitat, unitat, unitat.getNoves().get(0));
			}
		}
		// Repassa les unitats vigents per actualitzar el nom en cas que sigui diferent
		unitats = pluginHelper.findAmbPare(entitat.getCodiDir3(), null, null);
		// Takes all the unitats from WS and saves them to database. If unitat did't exist in db it creates new one if it already existed it overrides existing one.  
		for (UnitatOrganitzativa unidadWS : unitats) {
			comprovarDenominacio(unidadWS);
		}
	}

	public void sincronizarHistoricosUnitat(
			UnitatOrganitzativaEntity unitat, 
			UnitatOrganitzativa unidadWS) {
		if (unidadWS.getHistoricosUO()!=null && !unidadWS.getHistoricosUO().isEmpty()) {
			for (String historicoCodi : unidadWS.getHistoricosUO()) {
				UnitatOrganitzativaEntity nova = unitatOrganitzativaRepository.findByCodi(historicoCodi);
				if (!unitat.getNoves().contains(nova)) {
					unitat.addNova(nova);
				}
				if (!nova.getAntigues().contains(unitat)) {
					nova.addAntiga(unitat);
				}
				unitatOrganitzativaRepository.saveAndFlush(nova);
			}
		}
	}

	/** Mètode per consultar l'anotació de BBDD i actualitzar la denominació en cas que sigui diferent a la de la unitat consultada vigent.
	 * 
	 * @param unitatWS
	 */
	public void comprovarDenominacio( UnitatOrganitzativa unitatWS) {
		UnitatOrganitzativaEntity unitat = null;
		if (unitatWS != null) {
			// checks if unitat already exists in database
			unitat = unitatOrganitzativaRepository.findByCodi(unitatWS.getCodi());
			if (unitat != null) {
				if (unitat.getCodi().equals(unitatWS.getCodi()) && unitatWS.getEstat().equals("V")
						&& !(unitat.getDenominacio().equals(unitatWS.getDenominacio()))) {
					unitat.updateDenominacio(unitatWS.getDenominacio());
				}
			}
		}
	}

	/**
	 * Creating new unitat (if it doesn't exist in db) or overriding existing one (if it exists in db)
	 * 
	 * @param unidadWS
	 * @param entidadId
	 * @throws Exception
	 */
	@Transactional
	public UnitatOrganitzativaEntity sincronizarUnitat(UnitatOrganitzativa unitatWS, String codiEntitat) {
		UnitatOrganitzativaEntity unitat = null;
		if (unitatWS != null) {					
			// checks if unitat already exists in database
			unitat = unitatOrganitzativaRepository.findByCodi(unitatWS.getCodi());
			//if not it creates a new one
			if (unitat == null) {
				unitat = UnitatOrganitzativaEntity.getBuilder(
						unitatWS.getCodi(),
						unitatWS.getDenominacio()).
						nifCif(unitatWS.getNifCif()).
						estat(unitatWS.getEstat()).
						codiUnitatSuperior(unitatWS.getCodiUnitatSuperior()).
						codiUnitatArrel(unitatWS.getCodiUnitatArrel()).
						codiPais(unitatWS.getCodiPais()).
						codiComunitat(unitatWS.getCodiComunitat()).
						codiProvincia(unitatWS.getCodiProvincia()).
						codiPostal(unitatWS.getCodiPostal()).
						nomLocalitat(unitatWS.getNomLocalitat()).
						tipusVia(unitatWS.getTipusVia()).
						nomVia(unitatWS.getNomVia()).
						numVia(unitatWS.getNumVia())
						.build();
				unitat.setCodiDir3Entitat(codiEntitat);
				unitatOrganitzativaRepository.saveAndFlush(unitat);
			} else {
				unitat.update(
						unitatWS.getCodi(),
						unitatWS.getDenominacio(),
						unitatWS.getNifCif(),
						unitatWS.getEstat(),
						unitatWS.getCodiUnitatSuperior(),
						unitatWS.getCodiUnitatArrel(),
						unitatWS.getCodiPais(),
						unitatWS.getCodiComunitat(),
						unitatWS.getCodiProvincia(),
						unitatWS.getCodiPostal(),
						unitatWS.getNomLocalitat(),
						unitatWS.getTipusVia(),
						unitatWS.getNomVia(),
						unitatWS.getNumVia());
				unitat.setCodiDir3Entitat(codiEntitat);
				unitatOrganitzativaRepository.saveAndFlush(unitat);
			}
		}
		return unitat;
	}

	/**
	 * Comprova les regles de les unitats fusionades o substituïdes i actaulitza la UO.
	 * 
	 * @param unidadWS
	 * @param entidadId
	 * @throws Exception
	 * @return List<ReglaEntity>
	 */
	@Transactional
	public void  sincronitzarRegles(
			EntitatEntity entitat, 
			UnitatOrganitzativaEntity obsoleta,
			UnitatOrganitzativaEntity nova) 
	{		
			// busca les regles que tenen la obsoleta com a UO en el filtre		
			for (ReglaEntity regla : reglaRepository.findByEntitatAndUnitatOrganitzativaFiltreCodi(entitat, obsoleta.getCodi())) {
				logger.info("Sincronitzant la regla " + regla.getId() + " " + regla.getNom() + ". Canviant de la UO  filtre obsoleta " + obsoleta.getCodi() + " a la nova " + nova.getCodi() + "." );
					regla.setUnitatOrganitzativaFiltre(nova);
					reglaRepository.saveAndFlush(regla);
			}
			// busca les regles que tenen la obsoleta com a uo en l'acció de moure
			for (ReglaEntity regla : reglaRepository.findByEntitatAndUnitatDestiCodi(entitat, obsoleta.getCodi())) {  
				logger.info("Sincronitzant la regla " + regla.getId() + " " + regla.getNom() + ". Canviant de la UO  destí obsoleta " + obsoleta.getCodi() + " a la nova " + nova.getCodi() + "." );
				regla.setUnitatDesti(nova);
				reglaRepository.saveAndFlush(regla);
		}
	}

	public static UnitatOrganitzativaDto assignAltresUnitatsFusionades(
			UnitatOrganitzativaEntity unitatOrganitzativaEntity, UnitatOrganitzativaDto unitatOrganitzativaDto) {
		if (unitatOrganitzativaEntity != null && unitatOrganitzativaDto != null) {
			Map<String, String> altresUnitatsFusionades = new HashMap<String, String>();
			if (unitatOrganitzativaEntity.getTipusTransicio() == TipusTransicioEnumDto.FUSIO
					&& unitatOrganitzativaEntity.getNoves().size() > 0) {
				List<UnitatOrganitzativaEntity> antigaUnitats = unitatOrganitzativaEntity.getNoves().get(0)
						.getAntigues();
				for (UnitatOrganitzativaEntity unitatEntity : antigaUnitats) {
					if (unitatOrganitzativaEntity.getCodi() != unitatEntity.getCodi()) {
						altresUnitatsFusionades.put(unitatEntity.getCodi(), unitatEntity.getDenominacio());
					}
				}
			}
			unitatOrganitzativaDto.setAltresUnitatsFusionades(altresUnitatsFusionades);
			return unitatOrganitzativaDto;
		} else
			return null;
	}

	public UnitatOrganitzativaDto toUnitatOrganitzativaDto(
			UnitatOrganitzativaEntity source) {
		return conversioTipusHelper.convertir(
				source, 
				UnitatOrganitzativaDto.class);
	}

	public UnitatOrganitzativaDto findPerEntitatAndCodi(
			String entitatCodi,
			String unitatOrganitzativaCodi) {
		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
		return toUnitatOrganitzativaDto(unitatOrganitzativaRepository.findByCodiDir3EntitatAndCodi(
				entitat.getCodiDir3(),
				unitatOrganitzativaCodi));
	}

	public UnitatOrganitzativaDto findAmbCodi(
			String unitatOrganitzativaCodi) {
		return toUnitatOrganitzativaDto(unitatOrganitzativaRepository.findByCodi(
				unitatOrganitzativaCodi));
	}

	/**
	 * Finds path of unitats that starts from given unitat and stops with unitata arrel unitat arrel
	 * @param codiDir3
	 * @param unitatOrganitzativaCodi
	 * @return
	 */
	public List<UnitatOrganitzativaDto> findPath(
			String codiDir3,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreByPare(codiDir3);
		List<UnitatOrganitzativaDto> superiors = new ArrayList<UnitatOrganitzativaDto>();
		String codiActual = unitatOrganitzativaCodi;
		do {
			UnitatOrganitzativaDto unitatActual = cercaDinsArbreAmbCodi(
					arbre,
					codiActual);
			if (unitatActual != null) {
				superiors.add(unitatActual);
				codiActual = unitatActual.getCodiUnitatSuperior();
			} else {
				codiActual = null;
			}
		} while (codiActual != null);
		return superiors;
	}

	/**
	 * Returns the tree of unitats containing unitats specified in @param unitatCodiPermesos and their parent unitats on the path up to the root unitat
	 * @param codiDir3
	 * @param unitatCodiPermesos
	 * @return
	 */
	public ArbreDto<UnitatOrganitzativaDto> findPerCodiDir3EntitatAmbCodisPermesos(
			String codiDir3,
			Set<String> unitatCodiPermesos) {
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreByPare(codiDir3);
		final Timer timer = metricRegistry.timer(MetricRegistry.name(UnitatOrganitzativaHelper.class, "clone"));
		Timer.Context context = timer.time();
		arbre = (arbre != null)? arbre.clone() : new ArbreDto<UnitatOrganitzativaDto>(false);
		context.stop();
		final Timer timerleaveOnlySpecifiedUnitatsInArbre = metricRegistry.timer(MetricRegistry.name(UnitatOrganitzativaHelper.class, "leaveOnlySpecifiedUnitatsInArbre"));
		Timer.Context contextleaveOnlySpecifiedUnitatsInArbre = timerleaveOnlySpecifiedUnitatsInArbre.time();
		if (unitatCodiPermesos != null) {
			// Calcula els nodes a "salvar" afegint els nodes permesos
			// i tots els seus pares.
			List<ArbreNodeDto<UnitatOrganitzativaDto>> nodes = arbre.toList();
			Set<String> unitatCodiSalvats = new HashSet<String>();
			for (ArbreNodeDto<UnitatOrganitzativaDto> node: nodes) {
				if (unitatCodiPermesos.contains(node.dades.getCodi())) {
					unitatCodiSalvats.add(node.dades.getCodi());
					ArbreNodeDto<UnitatOrganitzativaDto> pare = node.getPare();
					while (pare != null) {
						unitatCodiSalvats.add(pare.dades.getCodi());
						pare = pare.getPare();
					}
				}
			}
			// Esborra els nodes no "salvats"
			for (ArbreNodeDto<UnitatOrganitzativaDto> node: nodes) {
				if (!unitatCodiSalvats.contains(node.dades.getCodi())) {
					if (node.getPare() != null)
						node.getPare().removeFill(node);
					else
						arbre.setArrel(null);
				}
			}
		}
		contextleaveOnlySpecifiedUnitatsInArbre.stop();
		return arbre;
	}

	/**
	 * Takes the list of unitats from database and converts it to the tree
	 * 
	 * @param pareCodi 
	 * 				codiDir3
	 * @return tree of unitats
	 */
	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPareAndEstatVigent(String pareCodi) {
		List<UnitatOrganitzativaEntity> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
				.findByCodiDir3AndEstatV(pareCodi);
		List<UnitatOrganitzativa> unitatsOrganitzatives = conversioTipusHelper
				.convertirList(unitatsOrganitzativesEntities, UnitatOrganitzativa.class);
		ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
		// Cerca l'unitat organitzativa arrel
		UnitatOrganitzativa unitatOrganitzativaArrel = null;
		for (UnitatOrganitzativa unitatOrganitzativa : unitatsOrganitzatives) {
			if (pareCodi.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
				unitatOrganitzativaArrel = unitatOrganitzativa;
				break;
			}
		}
		if (unitatOrganitzativaArrel != null) {
			// Omple l'arbre d'unitats organitzatives
			resposta.setArrel(
					arbreNodeHelper.getNodeArbreUnitatsOrganitzatives(
							unitatOrganitzativaArrel,
							unitatsOrganitzatives,
							null));
			return resposta;
		} else {
			return null;
		}
	}

	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(String pareCodi) {
		return cacheHelper.unitatsOrganitzativesFindArbreByPare(pareCodi);
	}

	public UnitatOrganitzativaDto toDto(UnitatOrganitzativaEntity entity) {
		UnitatOrganitzativaDto unitat = conversioTipusHelper.convertir(
				entity,
				UnitatOrganitzativaDto.class);
		if (unitat != null) {
			unitat.setAdressa(
					getAdressa(
							unitat.getTipusVia(), 
							unitat.getNomVia(), 
							unitat.getNumVia()));
			if (unitat.getCodiPais() != null && !"".equals(unitat.getCodiPais())) {
				unitat.setCodiPais(("000" + unitat.getCodiPais()).substring(unitat.getCodiPais().length()));
			}
			if (unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				unitat.setCodiComunitat(("00" + unitat.getCodiComunitat()).substring(unitat.getCodiComunitat().length()));
			}
			if ((unitat.getCodiProvincia() == null || "".equals(unitat.getCodiProvincia())) && 
					unitat.getCodiComunitat() != null && !"".equals(unitat.getCodiComunitat())) {
				List<ProvinciaDto> provincies = cacheHelper.findProvinciesPerComunitat(unitat.getCodiComunitat());
				if (provincies != null && provincies.size() == 1) {
					unitat.setCodiProvincia(provincies.get(0).getCodi());
				}		
			}
			if (unitat.getCodiProvincia() != null && !"".equals(unitat.getCodiProvincia())) {
				unitat.setCodiProvincia(("00" + unitat.getCodiProvincia()).substring(unitat.getCodiProvincia().length()));
				if (unitat.getLocalitat() == null && unitat.getNomLocalitat() != null) {
					MunicipiDto municipi = findMunicipiAmbNom(
							unitat.getCodiProvincia(), 
							unitat.getNomLocalitat());
					if (municipi != null)
						unitat.setLocalitat(municipi.getCodi());
					else
						logger.error("UNITAT ORGANITZATIVA. No s'ha trobat la localitat amb el nom: '" + unitat.getNomLocalitat() + "'");
				}
			}
		}
		return unitat;
	}

//	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(
//			String pareCodi) {
//		String accioDescripcio = "Consulta de l'arbre d'unitats donat un pare";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("unitatPare", pareCodi);
//		long t0 = System.currentTimeMillis();
//		try {
//			List<UnitatOrganitzativa> unitatsOrganitzatives = getUnitatsOrganitzativesPlugin().findAmbPare(
//					pareCodi);
//			ArbreDto<UnitatOrganitzativaDto> resposta = new ArbreDto<UnitatOrganitzativaDto>(false);
//			// Cerca l'unitat organitzativa arrel
//			UnitatOrganitzativa unitatOrganitzativaArrel = null;
//			for (UnitatOrganitzativa unitatOrganitzativa: unitatsOrganitzatives) {
//				if (pareCodi.equalsIgnoreCase(unitatOrganitzativa.getCodi())) {
//					unitatOrganitzativaArrel = unitatOrganitzativa;
//					break;
//				}
//			}
//			if (unitatOrganitzativaArrel != null) {
//				// Omple l'arbre d'unitats organitzatives
//				resposta.setArrel(
//						getNodeArbreUnitatsOrganitzatives(
//								unitatOrganitzativaArrel,
//								unitatsOrganitzatives,
//								null));
//				integracioHelper.addAccioOk(
//						IntegracioHelper.INTCODI_UNITATS,
//						accioDescripcio,
//						accioParams,
//						IntegracioAccioTipusEnumDto.ENVIAMENT,
//						System.currentTimeMillis() - t0);
//				return resposta;
//			} else {
//				String errorMissatge = "No s'ha trobat la unitat organitzativa arrel (codi=" + pareCodi + ")";
//				integracioHelper.addAccioError(
//						IntegracioHelper.INTCODI_UNITATS,
//						accioDescripcio,
//						accioParams,
//						IntegracioAccioTipusEnumDto.ENVIAMENT,
//						System.currentTimeMillis() - t0,
//						errorMissatge);
//				throw new SistemaExternException(
//						IntegracioHelper.INTCODI_UNITATS,
//						errorMissatge);
//			}
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_UNITATS,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_UNITATS,
//					errorDescripcio,
//					ex);
//		}
//	}

	private UnitatOrganitzativaDto cercaDinsArbreAmbCodi(
			ArbreDto<UnitatOrganitzativaDto> arbre,
			String unitatOrganitzativaCodi) {
		UnitatOrganitzativaDto trobada = null;
		for (UnitatOrganitzativaDto unitat: arbre.toDadesList()) {
			if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
				trobada = unitat;
				break;
			}
		}
		return trobada;
	}

	/**
	 * Searches for and returns the unitat object with the given codi from the given list of unitats
	 * @param codi
	 * @param allUnitats
	 * @return
	 */
	private UnitatOrganitzativa getUnitatFromCodi(
			String codi, 
			List<UnitatOrganitzativa> allUnitats){
		
		UnitatOrganitzativa unitatFromCodi = null;
		for (UnitatOrganitzativa unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				unitatFromCodi = unitatWS;
			}
		}
		return unitatFromCodi;
	}

	/**
	 * It gives the last (most current) unitat/unitats to which given obsolete unitat transitioned
	 * Starting point of recursivie method
	 * @param unitat - unitats which historicos we are looking for
	 * @param unitatsFromWebService - unitats used for getting unitat object from codi 
	 * @param addHistoricos - initially empty list to add the last historicos (unitats that dont have historicos)
	 */
	private List<UnitatOrganitzativa> getLastHistoricos(
			UnitatOrganitzativa unitat, 
			List<UnitatOrganitzativa> unitatsFromWebService){
		
		List<UnitatOrganitzativa> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(
				unitat, 
				unitatsFromWebService, 
				lastHistorcos);
		return lastHistorcos;
	}

	private void getLastHistoricosRecursive(
			UnitatOrganitzativa unitat,
			List<UnitatOrganitzativa> unitatsFromWebService,
			List<UnitatOrganitzativa> lastHistorcos) {
		logger.debug("Coloca historicos recursiu(" + "unitatCodi=" + unitat.getCodi() + ")");
		if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
			lastHistorcos.add(
					unitat);
		} else {
			for (String historicoCodi : unitat.getHistoricosUO()) {
				UnitatOrganitzativa unitatFromCodi = getUnitatFromCodi(
						historicoCodi,
						unitatsFromWebService);
				if (unitatFromCodi == null) {
					// Looks for historico in database
					UnitatOrganitzativaEntity entity = unitatOrganitzativaRepository.findByCodi(historicoCodi);
					if (entity != null) {
						UnitatOrganitzativa uo = conversioTipusHelper.convertir(entity, UnitatOrganitzativa.class);
						lastHistorcos.add(uo);						
					} else {
						String errorMissatge = "Error en la sincronització amb DIR3. La unitat orgánica (" + unitat.getCodi()
								+ ") té l'estat (" + unitat.getEstat() + ") i l'històrica (" + historicoCodi
								+ ") però no s'ha retornat la unitat orgánica (" + historicoCodi
								+ ") en el resultat de la consulta del WS ni en la BBDD.";
						throw new SistemaExternException(IntegracioHelper.INTCODI_UNITATS, errorMissatge);
					}
				} else {
					getLastHistoricosRecursive(
							unitatFromCodi,
							unitatsFromWebService,
							lastHistorcos);
				}
			}
		}
	}

	

	private String getAdressa(
			Long tipusVia,
			String nomVia,
			String numVia) {
		String adressa = "";
		if (tipusVia != null) {
			List<TipusViaDto> tipus = cacheHelper.findTipusVia();
			for (TipusViaDto tvia: tipus) {
				if (tvia.getCodi().equals(tipusVia)) {
					adressa = tvia.getDescripcio() + " ";
					break;
				}
			}
		}
		adressa += nomVia;
		if (numVia != null) {
			adressa += ", " + numVia;
		}
		return adressa;
	}

	private MunicipiDto findMunicipiAmbNom(
			String provinciaCodi,
			String municipiNom) {
		MunicipiDto municipi = null;
		List<MunicipiDto> municipis = cacheHelper.findMunicipisPerProvincia(provinciaCodi);
		if (municipis != null) {
			for (MunicipiDto mun: municipis) {
				if (mun.getNom().equalsIgnoreCase(municipiNom)) { 
					municipi = mun;
					break;
				}
			}
		}
		return municipi;
	}

	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaHelper.class);

}
