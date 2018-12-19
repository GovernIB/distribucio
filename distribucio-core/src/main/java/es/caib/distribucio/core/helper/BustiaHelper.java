/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
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
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			EntitatEntity entitat,
			boolean nomesAmbBusties,
			boolean nomesAmbBustiesPermeses,
			boolean ambContadorElementsPendents) {
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndPareNotNull(entitat);
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
		// Consulta l'arbre
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		if (ambContadorElementsPendents && !busties.isEmpty()) {
			// Consulta els contadors d'elements pendents per a totes les bústies
			long[] countContenidors = contingutHelper.countFillsAmbPermisReadByContinguts(
					entitat,
					busties,
					nomesAmbBustiesPermeses);
			// Calcula els acumulats de pendents per a cada unitat
			Map<String, Long> acumulats = new HashMap<String, Long>();
			for (int i = 0; i < busties.size(); i++) {
				BustiaEntity bustia = busties.get(i);
				Long acumulat = acumulats.get(bustia.getUnitatCodi());
				if (acumulat == null) {
					acumulats.put(
							bustia.getUnitatCodi(),
							countContenidors[i]);
				} else {
					acumulats.put(
							bustia.getUnitatCodi(),
							acumulat + countContenidors[i]);
				}
			}
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
		}
		return arbre;
	}

	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(
			EntitatEntity entitat,
			String bustiaNomFiltre,
			Long unitatIdFiltre,
			Boolean unitatObsoleta) {
		UnitatOrganitzativaEntity unitat = unitatIdFiltre != null ? unitatRepository.findOne(unitatIdFiltre): null;
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre(entitat,
				unitatIdFiltre == null, 
				unitat,
				bustiaNomFiltre == null || bustiaNomFiltre.isEmpty(), 
				bustiaNomFiltre,
				unitatObsoleta == null || unitatObsoleta == false);
		Set<String> bustiaUnitatCodis = new HashSet<String>();
		for (BustiaEntity bustia: busties) {
			bustiaUnitatCodis.add(bustia.getUnitatOrganitzativa().getCodi());
		}
		ArbreDto<UnitatOrganitzativaDto> arbre = unitatOrganitzativaHelper.findPerCodiDir3EntitatAmbCodisPermesos(
				entitat.getCodiDir3(),
				bustiaUnitatCodis);
		return arbre;
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
			BustiaEntity bustia = bustiaRepository.findByEntitatAndUnitatCodiAndPerDefecteTrue(
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

	public void evictElementsPendentsBustia(
			EntitatEntity entitat,
			BustiaEntity bustia) {
		Set<String> usuaris = contingutHelper.findUsuarisAmbPermisReadPerContenidor(bustia);
		if (usuaris != null) {
			for (String usuari: usuaris)
				cacheHelper.evictElementsPendentsBustiesUsuari(
						entitat,
						usuari);
		}
	}

	public boolean isProcessamentAsincronProperty() {
		String value = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.dist.anotacio.asincrona");
		if (value != null) {
			return new Boolean(value);
		}
		value = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.anotacio.processament.asincron");
		if (value != null) {
			return new Boolean(value);
		}
		return false;
	}

}
