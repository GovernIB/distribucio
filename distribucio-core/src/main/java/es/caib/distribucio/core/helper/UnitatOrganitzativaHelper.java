/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.TipusTransicioEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;


/**
 * Helper per a operacions amb unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class UnitatOrganitzativaHelper {

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private EntitatRepository entitatRepository;
	
	

	/**
	 * Searches for an return the unitat object with the given codi from the given list of unitats
	 * @param codi
	 * @param allUnitats
	 * @return
	 */
	private UnitatOrganitzativa getUnitatFromCodi(String codi, List<UnitatOrganitzativa> allUnitats){
		
		UnitatOrganitzativa unitatFromCodi = null;
		for (UnitatOrganitzativa unitatWS : allUnitats) {
			if (unitatWS.getCodi().equals(codi)) {
				unitatFromCodi = unitatWS;
			}
		}
		return unitatFromCodi;
	}
	
	
	/**
	 * Starting point, calls recursive method to get last historicos
	 * @param unitat - unitats which historicos we are looking for
	 * @param unitatsFromWebService - unitats used for getting unitat object from codi 
	 * @param addHistoricos - initially empty list to add the last historicos (unitats that dont have historicos)
	 */
	private List<UnitatOrganitzativa> getLastHistoricos(UnitatOrganitzativa unitat, List<UnitatOrganitzativa> unitatsFromWebService){
		
		List<UnitatOrganitzativa> lastHistorcos = new ArrayList<>();
		getLastHistoricosRecursive(unitat, unitatsFromWebService, lastHistorcos);
		return lastHistorcos;
	}
	
	
	
	/**
	 * Method to get last historicos (recursive to cover cumulative synchro case)
	 * @param unitat - unitats which historicos we are looking for
	 * @param unitatsFromWebService - unitats used for getting unitat object from codi 
	 * @param addHistoricos - initially empty list to add the last historicos (unitats that dont have historicos)
	 */
	private void getLastHistoricosRecursive(UnitatOrganitzativa unitat, List<UnitatOrganitzativa> unitatsFromWebService,  List<UnitatOrganitzativa> lastHistorcos){

			if (unitat.getHistoricosUO() == null || unitat.getHistoricosUO().isEmpty()) {
				lastHistorcos.add(unitat);
			} else {
				for (String historicoCodi : unitat.getHistoricosUO()) {
					getLastHistoricosRecursive(getUnitatFromCodi(historicoCodi, unitatsFromWebService),
							unitatsFromWebService, lastHistorcos);
				}
			}
	}
	
	
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) throws SistemaExternException{
		

		EntitatEntity entitat = entitatRepository.getOne(entidadId);
		
		UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getUnitatArrel(),
				null, null);
		
		
		List<UnitatOrganitzativa> unitatsVigentsWS = pluginHelper.findAmbPare(entitat.getUnitatArrel(),
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
	
	public List<UnitatOrganitzativaDto> predictSynchronization(Long entidadId) throws SistemaExternException{
		

		EntitatEntity entitat = entitatRepository.getOne(entidadId);
		
		UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getUnitatArrel(),
				null, null);
		
		// getting list of last changes from webservices
		List<UnitatOrganitzativa> unitatsWS = pluginHelper.findAmbPare(entitat.getUnitatArrel(),
				entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());
		
		
		// getting all vigent unitats from database
		List<UnitatOrganitzativaEntity> vigentUnitats = unitatOrganitzativaRepository
				.findByCodiUnitatArrelAndEstatV(entitat.getUnitatArrel());

		// list of unitats which are vigent now in database but in
		// synchronization list taken from webservices they are marked as
		// obsolete
		List<UnitatOrganitzativa> unitatsVigentObsolete = new ArrayList<>();
		for (UnitatOrganitzativaEntity unitatV : vigentUnitats) { 
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && !unitatWS.getEstat().equals("V")
						&& !unitatV.getCodi().equals(entitat.getUnitatArrel())) {
					unitatsVigentObsolete.add(unitatWS);
				}
			}
		}

		// setting list of last historicos unitats in every vigent unitat
		for (UnitatOrganitzativa vigentObsolete : unitatsVigentObsolete) {
			vigentObsolete.setLastHistoricosUnitats(getLastHistoricos(vigentObsolete, unitatsWS));
		}
		
		// converting form UnitatOrganitzativa to UnitatOrganitzativaDto
		List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = new ArrayList<>();
		for(UnitatOrganitzativa vigentObsolete : unitatsVigentObsolete){
			unitatsVigentObsoleteDto.add(conversioTipusHelper.convertir(
					vigentObsolete, 
					UnitatOrganitzativaDto.class));
		}
		
		return unitatsVigentObsoleteDto;
	}
	
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId){
		
		EntitatEntity entitat = entitatRepository.getOne(entidadId);
		
		// getting list of last changes from webservices
		List<UnitatOrganitzativa> unitatsWS = pluginHelper.findAmbPare(entitat.getUnitatArrel(),
				entitat.getFechaActualizacion(), entitat.getFechaSincronizacion());
		
		
		// getting all vigent unitats from database
		List<UnitatOrganitzativaEntity> vigentUnitats = unitatOrganitzativaRepository
				.findByCodiUnitatArrelAndEstatV(entitat.getUnitatArrel());
		
		 
		// list of vigent unitats from webservice
		List<UnitatOrganitzativa> unitatsVigentsWithChangedAttributes = new ArrayList<>();
		for (UnitatOrganitzativaEntity unitatV : vigentUnitats) {
			for (UnitatOrganitzativa unitatWS : unitatsWS) {
				if (unitatV.getCodi().equals(unitatWS.getCodi()) && unitatV.getEstat().equals("V")
						&& unitatWS.getHistoricosUO() == null
						&& !unitatV.getCodi().equals(entitat.getUnitatArrel())) {
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
	 * Method to get last historicos (recursive to cover cumulative synchro case)
	 * @param unitat
	 * @param lastHistorcos
	 */
	public void getLastHistoricosRecursive(UnitatOrganitzativaDto unitat, List<UnitatOrganitzativaDto> lastHistorcos) {

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

		List<UnitatOrganitzativa> unitats;

		UnitatOrganitzativa unidadPadreWS = pluginHelper.findUnidad(entitat.getUnitatArrel(),
				null, null);


			unitats = pluginHelper.findAmbPare(entitat.getUnitatArrel(), entitat.getFechaActualizacion(),
					entitat.getFechaSincronizacion());


		for (UnitatOrganitzativa unidadWS : unitats) {
			sincronizarUnitat(unidadWS);
		}

		// historicos
		for (UnitatOrganitzativa unidadWS : unitats) {
			UnitatOrganitzativaEntity unitat = unitatOrganitzativaRepository
					.findByCodiUnitatArrelAndCodi(entitat.getUnitatArrel(), unidadWS.getCodi());
			sincronizarHistoricosUnitat(unitat, unidadWS);
		}

		List<UnitatOrganitzativaEntity> obsoleteUnitats = unitatOrganitzativaRepository
				.findByCodiUnitatArrelAndEstatNotV(entitat.getUnitatArrel());

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
	}
	

	public void sincronizarHistoricosUnitat(
			UnitatOrganitzativaEntity unitat, 
			UnitatOrganitzativa unidadWS) {

		if (unidadWS.getHistoricosUO()!=null && !unidadWS.getHistoricosUO().isEmpty()) {
			for (String historicoCodi : unidadWS.getHistoricosUO()) {
				UnitatOrganitzativaEntity nova = unitatOrganitzativaRepository.findByCodi(historicoCodi);
				unitat.addNova(nova);
				nova.addAntiga(unitat);
			}
		}
	}
	


	/**
	 * This method creates new (if it doesnt already exists) or updates existing unidad in database with the given unitatWS  
	 * 
	 * @param unidadWS
	 * @param entidadId
	 * @throws Exception
	 */
	public UnitatOrganitzativaEntity sincronizarUnitat(UnitatOrganitzativa unitatWS) {


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
//						codiUnitatArrel("A04025121").
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
				unitatOrganitzativaRepository.save(unitat);
			} else {
				unitat.update(
						unitatWS.getCodi(),
						unitatWS.getDenominacio(),
						unitatWS.getNifCif(),
						unitatWS.getEstat(),
						unitatWS.getCodiUnitatSuperior(),
//						"A04025121",
						unitatWS.getCodiUnitatArrel(),
						unitatWS.getCodiPais(),
						unitatWS.getCodiComunitat(),
						unitatWS.getCodiProvincia(),
						unitatWS.getCodiPostal(),
						unitatWS.getNomLocalitat(),
						unitatWS.getTipusVia(),
						unitatWS.getNomVia(),
						unitatWS.getNumVia());
			}
			

			
			// Guardamos el Unitat
			//unitat = unitatOrganitzativaRepository.save(unitat);
			//unitatOrganitzativaRepository.flush();
		}
		return unitat;
	}
	
	public static UnitatOrganitzativaDto assignAltresUnitatsFusionades(
			UnitatOrganitzativaEntity unitatOrganitzativaEntity, UnitatOrganitzativaDto unitatOrganitzativaDto) {
		if (unitatOrganitzativaEntity != null && unitatOrganitzativaDto != null) {
			Map<String, String> altresUnitatsFusionades = new HashMap<String, String>();
			if (unitatOrganitzativaEntity.getTipusTransicio() == TipusTransicioEnumDto.FUSIO) {
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
		
		return toUnitatOrganitzativaDto(unitatOrganitzativaRepository.findByCodiUnitatArrelAndCodi(
				entitat.getUnitatArrel(),
				unitatOrganitzativaCodi));
	}

//	public UnitatOrganitzativaDto findPerEntitatAndCodi(
//			String entitatCodi,
//			String unitatOrganitzativaCodi) {
//		ArbreDto<UnitatOrganitzativaDto> arbre = cacheHelper.findUnitatsOrganitzativesPerEntitat(entitatCodi);
//		for (UnitatOrganitzativaDto unitat: arbre.toDadesList()) {
//			if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
//				return unitat;
//			}
//		}
//		return null;
//	}
	
	
//	public UnitatOrganitzativaDto findAmbCodi(
//			String unitatOrganitzativaCodi) {
//		return pluginHelper.unitatsOrganitzativesFindByCodi(
//				unitatOrganitzativaCodi);
//	}

	public UnitatOrganitzativaDto findAmbCodi(
			String unitatOrganitzativaCodi) {
		return toUnitatOrganitzativaDto(unitatOrganitzativaRepository.findByCodi(
				unitatOrganitzativaCodi));
	}

	public List<UnitatOrganitzativaDto> findPath(
			String unitatArrel,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreByPare(unitatArrel);
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
	 * @param unitatArrel
	 * @param unitatCodiPermesos
	 * @return
	 */
	public ArbreDto<UnitatOrganitzativaDto> findPerUnitatArrelAmbCodisPermesos(
			String unitatArrel,
			Set<String> unitatCodiPermesos) {
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreByPare(unitatArrel).clone();
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
		return arbre;
	}
	
//	public List<UnitatOrganitzativaDto> findUnitatsOrganitzativesPerEntitatFromPlugin(String entitatCodi)
//			throws SistemaExternException {
//
//		EntitatEntity entitat = entitatRepository.findByCodi(entitatCodi);
//		return pluginHelper.unitatsOrganitzativesFindListByPare(entitat.getUnitatArrel());
//	}
	

	public UnitatOrganitzativaDto findConselleria(
			String unitatPare,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatsOrganitzativesFindArbreByPare(unitatPare).clone();
		UnitatOrganitzativaDto unitatConselleria = null;
		for (ArbreNodeDto<UnitatOrganitzativaDto> node: arbre.toList()) {
			UnitatOrganitzativaDto uo = node.getDades();
			if (uo.getCodi().equals(unitatOrganitzativaCodi)) {
				ArbreNodeDto<UnitatOrganitzativaDto> nodeActual = node;
				while (nodeActual.getNivell() > 1) {
					nodeActual = nodeActual.getPare();
				}
				if (nodeActual.getNivell() == 1)
					unitatConselleria = nodeActual.getDades();
				break;
			}
		}
		return unitatConselleria;
	}



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
	 * Takes the list of unitats from database and converts it to the tree
	 * 
	 * @param pareCodi 
	 * 				unitatArrel
	 * @return tree of unitats
	 */
	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(String pareCodi) {

		List<UnitatOrganitzativaEntity> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
				.findByCodiUnitatArrel(pareCodi);

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
			resposta.setArrel(getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzatives, null));
			return resposta;

		}
		return null;
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
	
	/**
	 * Takes the list of unitats from database and converts it to the tree
	 * 
	 * @param pareCodi 
	 * 				unitatArrel
	 * @return tree of unitats
	 */
	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPareAndEstatVigent(String pareCodi) {

		List<UnitatOrganitzativaEntity> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
				.findByCodiUnitatArrelAndEstatV(pareCodi);
		
		
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
			resposta.setArrel(getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzatives, null));
			return resposta;

		} else {
			return null;
		}
	}
	
	
	
	
	
	/**
	 * 
	 * @param unitatOrganitzativa - in first call it is unitat arrel, later the children nodes
	 * @param unitatsOrganitzatives
	 * @param pare - in first call it is null, later pare
	 * @return
	 */
	private ArbreNodeDto<UnitatOrganitzativaDto> getNodeArbreUnitatsOrganitzatives(
			UnitatOrganitzativa unitatOrganitzativa,
			List<UnitatOrganitzativa> unitatsOrganitzatives,
			ArbreNodeDto<UnitatOrganitzativaDto> pare) {
		// creating current arbre node and filling it with pare arbre node and dades as current unitat
		ArbreNodeDto<UnitatOrganitzativaDto> currentArbreNode = new ArbreNodeDto<UnitatOrganitzativaDto>(
				pare,
				conversioTipusHelper.convertir(
						unitatOrganitzativa,
						UnitatOrganitzativaDto.class));
		String codiUnitat = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi() : null;
		// for every child of current unitat call recursively getNodeArbreUnitatsOrganitzatives()
		for (UnitatOrganitzativa uo: unitatsOrganitzatives) {
			//searches for children of current unitat
			if (	(codiUnitat == null && uo.getCodiUnitatSuperior() == null) ||
					(uo.getCodiUnitatSuperior() != null && uo.getCodiUnitatSuperior().equals(codiUnitat))) {
				currentArbreNode.addFill(
						getNodeArbreUnitatsOrganitzatives(
								uo,
								unitatsOrganitzatives,
								currentArbreNode));
			}
		}
		return currentArbreNode;
	}
	
	
	

}
