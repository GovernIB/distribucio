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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariBustiaFavoritDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.entity.UsuariBustiaFavoritEntity;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;


/**
 * Mètodes comuns per a gestionar bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class BustiaHelper {

	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private UnitatOrganitzativaRepository unitatRepository;
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private ConfigHelper configHelper;
	
	
	
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			EntitatEntity entitat,
			boolean nomesAmbBusties,
			boolean nomesAmbBustiesPermeses,
			boolean ambContadorElementsPendents) {
		
		final Timer timerbustiesPermeses = metricRegistry.timer(MetricRegistry.name(BustiaHelper.class, "findArbreUnitatsOrganitzatives.bustiesPermeses"));
		Timer.Context contextbustiesPermeses = timerbustiesPermeses.time();
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndPareNotNullOrderByNomAsc(entitat);
		Set<String> bustiaUnitatCodis = null;
		if (nomesAmbBusties) {
			bustiaUnitatCodis = new HashSet<String>();
			if (nomesAmbBustiesPermeses) {
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				permisosHelper.filterGrantedAll(
						busties,
						new ObjectIdentifierExtractor<BustiaEntity>() {
							@Override
							public Long getObjectIdentifier(BustiaEntity bustia) {
								return bustia.getId();
							}
						},
						BustiaEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth);
			}
			for (BustiaEntity bustia: busties)
				bustiaUnitatCodis.add(bustia.getUnitatOrganitzativa().getCodi());
		}
		contextbustiesPermeses.stop();
		
		final Timer timerfindPerCodiDir3EntitatAmbCodisPermesos = metricRegistry.timer(MetricRegistry.name(BustiaHelper.class, "findArbreUnitatsOrganitzatives.findPerCodiDir3EntitatAmbCodisPermesos"));
		Timer.Context contextfindPerCodiDir3EntitatAmbCodisPermesos = timerfindPerCodiDir3EntitatAmbCodisPermesos.time();
		// Consulta l'arbre
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		contextfindPerCodiDir3EntitatAmbCodisPermesos.stop();
		
		
		if (ambContadorElementsPendents && !busties.isEmpty()) {
			
			final Timer timerAcumulats = metricRegistry.timer(MetricRegistry.name(BustiaHelper.class, "findArbreUnitatsOrganitzatives.Acumulats"));
			Timer.Context contextAcumulats = timerAcumulats.time();
			// Consulta els contadors d'elements pendents per a totes les bústies
			long[] countContenidors = contingutHelper.countFillsAmbPermisReadByContinguts(
					entitat,
					busties,
					nomesAmbBustiesPermeses);
			// Calcula els acumulats de pendents per a cada unitat
			Map<String, Long> acumulats = new HashMap<String, Long>();
			for (int i = 0; i < busties.size(); i++) {
				BustiaEntity bustia = busties.get(i);
				Long acumulat = acumulats.get(bustia.getUnitatOrganitzativa().getCodi());
				if (acumulat == null) {
					acumulats.put(
							bustia.getUnitatOrganitzativa().getCodi(),
							countContenidors[i]);
				} else {
					acumulats.put(
							bustia.getUnitatOrganitzativa().getCodi(),
							acumulat + countContenidors[i]);
				}
			}
			contextAcumulats.stop();
			
			final Timer timerCalculaRecorr = metricRegistry.timer(MetricRegistry.name(BustiaHelper.class, "findArbreUnitatsOrganitzatives.CalculaRecorr"));
			Timer.Context contextCalculaRecorr = timerCalculaRecorr.time();
			// Calcula el nombre de nivells de l'arbre
			int nivellsCount = 0;
			for (ArbreNodeDto<UnitatOrganitzativaDto> node: arbre.toList()) {
				if (node.getNivell() > nivellsCount) {
					nivellsCount = node.getNivell();
				}
			}
			// Recorr l'arbre per nivells en ordre invers per a actualitzar
			// el contador d'elements pendents de cada node i dels seus pares
			for (int nivell = nivellsCount; nivell > 0; nivell--) {
				for (ArbreNodeDto<UnitatOrganitzativaDto> node: arbre.toList()) {
					if (node.getNivell() == nivell) {
						// Actualitza el contador del node actual
						String unitatCodi = node.getDades().getCodi();
						Long acumulat = acumulats.get(unitatCodi);
						if (acumulat != null) {
							node.addCount(acumulat);
							// Actualitza els contadors dels pares
							ArbreNodeDto<UnitatOrganitzativaDto> pare = node.getPare();
							while (pare != null) {
								pare.addCount(node.getCount());
								pare = pare.getPare();
							}
						}
					}
				}
			}
			contextCalculaRecorr.stop();
		}
		return arbre;
	}

	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(
			EntitatEntity entitat,
			List<BustiaDto> busties) {

		Set<String> bustiaUnitatCodis = new HashSet<String>();
		for (BustiaDto bustia: busties) {
			bustiaUnitatCodis.add(bustia.getUnitatOrganitzativa().getCodi());
		}
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		return arbre;
	}

	
	
	public BustiaDto toBustiaDto(
			BustiaEntity bustia,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambUnitatOrganitzativa) {
		return (BustiaDto)contingutHelper.toContingutDto(
				bustia,
				false,
				ambFills,
				filtrarFillsSegonsPermisRead,
				false,
				true,
				false,
				ambUnitatOrganitzativa);
	}
	public List<BustiaDto> toBustiaDto(
			List<BustiaEntity> busties,
			boolean ambFills,
			boolean filtrarFillsSegonsPermisRead,
			boolean ambUnitatOrganitzativa) {
		List<BustiaDto> resposta = new ArrayList<BustiaDto>();
		for (BustiaEntity bustia: busties) {
			resposta.add(
					toBustiaDto(
							bustia,
							ambFills,
							filtrarFillsSegonsPermisRead,
							ambUnitatOrganitzativa));
		}
		return resposta;
	}
	
	public UsuariBustiaFavoritDto toUsuariBustiaFavoritDto(UsuariBustiaFavoritEntity source) {
		final Timer timerTotal = metricRegistry.timer(MetricRegistry.name(BustiaHelper.class, "toUsuariBustiaFavoritDto"));
		Timer.Context contextTotal = timerTotal.time();
		UsuariBustiaFavoritEntity sourceDeproxied = HibernateHelper.deproxy(source);
		
		UsuariBustiaFavoritDto usuariBustiaFavoritDto = new UsuariBustiaFavoritDto();
		BustiaDto bustia = new BustiaDto();
		UsuariDto usuari = new UsuariDto();
		
		usuari.setCodi(sourceDeproxied.getUsuari().getCodi());
		bustia.setNom(sourceDeproxied.getBustia().getNom());
		bustia.setId(sourceDeproxied.getBustia().getId());
		
		usuariBustiaFavoritDto.setId(sourceDeproxied.getId());
		usuariBustiaFavoritDto.setBustia(bustia);
		usuariBustiaFavoritDto.setCreatedDate(sourceDeproxied.getCreatedDate().toDate());
		
		contextTotal.stop();
		return usuariBustiaFavoritDto;
	}
	
	public BustiaEntity findBustiaDesti(
			EntitatEntity entitat,
			String unitatOrganitzativaCodi) {
		// Cerca la bústia per defecte de la unitat organitzativa especificada. Si
		// la unitat no te bústia per defecte va pujant a l'arbre fins a trobar la
		// primera unitat que en tengui.
		List<UnitatOrganitzativaDto> path = unitatOrganitzativaHelper.findPath(
				entitat.getCodiDir3(),
				unitatOrganitzativaCodi);
		if (path == null || path.isEmpty()) {
			throw new NotFoundException(
					unitatOrganitzativaCodi,
					UnitatOrganitzativaDto.class);
		}
		BustiaEntity bustiaDesti = null;
		for (UnitatOrganitzativaDto unitat: path) {
			BustiaEntity bustia = this.findBustiaPerDefecte(
					entitat,
					unitat.getCodi());
			if (bustia != null) {
				bustiaDesti = bustia;
				break;
			}
		}
		if (bustiaDesti == null) {
			throw new ValidationException(
					unitatOrganitzativaCodi,
					UnitatOrganitzativaDto.class,
					"No s'ha trobat cap bústia destí per a la unitat organitzativa (" +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
		}
		return bustiaDesti;
	}

	public void evictCountElementsPendentsBustiesUsuari(
			EntitatEntity entitat,
			BustiaEntity bustia) {
		Set<String> usuaris = contingutHelper.findUsuarisCodisAmbPermisReadPerContenidor(bustia);
		if (usuaris != null) {
			for (String usuari: usuaris)
				cacheHelper.evictCountElementsPendentsBustiesUsuari(
						entitat,
						usuari);
		}
	}

	public boolean isProcessamentAsincronProperty() {
		String value = configHelper.getConfig("es.caib.distribucio.tasca.dist.anotacio.asincrona");
		if (value != null) {
			return new Boolean(value);
		}
		value = configHelper.getConfig("es.caib.distribucio.anotacio.processament.asincron");
		if (value != null) {
			return new Boolean(value);
		}
		return false;
	}

	/** Mètode per trobar la bústia per defecte tenint en compte si la query retorna més d'un resultat.
	 * Troba la bústia per entitat i codi d'unitat orgànica.
	 * @param entitat
	 * @param codi
	 * @return
	 */
	public BustiaEntity findBustiaPerDefecte(EntitatEntity entitat, String codiUnitat) {
		
		UnitatOrganitzativaEntity unitatOrganitzativaEntity = unitatRepository.findByCodi(codiUnitat);
		
		BustiaEntity bustaPerDefecte = null;
		List<BustiaEntity> bustiesPerDefecte = bustiaRepository.findPerDefecte(
				entitat,
				unitatOrganitzativaEntity);
		if (bustiesPerDefecte.size() == 1) {
			bustaPerDefecte = bustiesPerDefecte.get(0);
		} else if (bustiesPerDefecte.size() > 1) {
			// és un error que es recuperi més d'una, es llença excepció
			StringBuilder errMsg = new StringBuilder("Error. S'ha recuperat més d'una bústia per defecte per la entitat ")
										.append(entitat.getId() + " \"" + entitat.getCodi())
										.append("\" i codi d'unitat ").append(codiUnitat).append(": ");
			for (BustiaEntity b : bustiesPerDefecte)
				errMsg.append("[" + b.getId() + " " + b.getUnitatOrganitzativa().getCodi() + " \"" + b.getNom() + "\"]");
			
			logger.error(errMsg.toString());
			throw new RuntimeException(errMsg.toString());
		}
		return bustaPerDefecte;
	}
	
	
	
	/** Mètode privat per retornnar un arbre amb les unitats organitzatives superiors
	 * a les bústies de l'entitat. D'aquesta forma s'obté només l'arbre amb bústies.
	 * 
	 * @param entitat Entitat amb les bústies per buscar les unititats orgàniques.
	 * @param filtre Filtre per codi o nom de les unitats orgàniques superiors de les bústies.
	 * @param codiUnitatOrganitzativa Codi del node superior. Només es retornarà l'arbre a partir
	 * del node que coincideixi amb aquest codi.
	 * @return
	 */
	public ArbreDto<UnitatOrganitzativaDto> getArbreUnitatsSuperiors(
			EntitatEntity entitat, 
			String filtre,
			String codiUnitatOrganitzativa) {
		// Recupera les diferents unitats organitzatives de les bústies de l'entorn
		List<UnitatOrganitzativaEntity> unitatsSuperiors = 
				unitatOrganitzativaRepository.findUnitatsSuperiors(
						entitat.getId(),
						filtre == null || filtre.isEmpty(),
						filtre != null ? filtre : "");
		
		// Crea una llista de codis d'UO amb bústia
		Set<String> bustiaUnitatCodis = new HashSet<String>();
		for (UnitatOrganitzativaEntity us : unitatsSuperiors) {
			bustiaUnitatCodis.add(us.getCodi());
		}
		// Consulta tot l'arbre de l'entitat filtrant per codis permesos
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		// Si s'ha passat un codi d'unitat orgànica superior llavors retorna l'arbre a partir del node amb codi igual
		if (codiUnitatOrganitzativa != null && !codiUnitatOrganitzativa.isEmpty()) {
			// Busca el node amb el codi seleccionat
			for (ArbreNodeDto<UnitatOrganitzativaDto> node : arbre.toList()) {
				if (node.getDades().getCodi().equals(codiUnitatOrganitzativa)) {
					arbre = new ArbreDto<UnitatOrganitzativaDto>(false);
					arbre.setArrel(node);
					break;
				}
			}
		}
		return arbre;
	}
	
	/** Mètode per obtenir els codis d'unitats orgàniques de l'arbre que penja a partir de l'unitat
	 * orgànica superior per filtrar per unitat orgànica superior. 
	 * 
	 * @param entitat
	 * @param codiUnitatSuperior
	 * @return Els codis de les UO de l'arbe a partir del node amb codi igual a codiUnitatSuperior.
	 */
	public List<String> getCodisUnitatsSuperiors(EntitatEntity entitat, String codiUnitatSuperior) {
		List<String> codisUnitatsSuperiors = new ArrayList<String>();
		if (codiUnitatSuperior != null) {
			
			ArbreDto<UnitatOrganitzativaDto> arbre = this.getArbreUnitatsSuperiors(entitat, null, codiUnitatSuperior);
			// Agafa tots els identificadors
			for (UnitatOrganitzativaDto uo : arbre.toDadesList()) {
				codisUnitatsSuperiors.add(uo.getCodi());
			}			
		}
		return codisUnitatsSuperiors;
	}


	private static final Logger logger = LoggerFactory.getLogger(BustiaHelper.class);

}
