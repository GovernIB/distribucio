/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.MunicipiDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.ProvinciaDto;
import es.caib.distribucio.core.api.dto.TipusViaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Utilitat per a accedir a les caches. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class CacheHelper {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private BustiaRepository bustiaRepository;
	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private PermisosEntitatHelper permisosEntitatHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Resource
	private EntitatHelper entitatHelper;
	
	@Cacheable(value = "entitatsUsuari", key="#usuariCodi")
	public List<EntitatDto> findEntitatsAccessiblesUsuari(String usuariCodi) {
		logger.trace("Consulta entitats accessibles (usuariCodi=" + usuariCodi + ")");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<EntitatEntity> entitats = entitatRepository.findByActiva(true);
		permisosHelper.filterGrantedAny(
				entitats,
				new ObjectIdentifierExtractor<EntitatEntity>() {
					public Long getObjectIdentifier(EntitatEntity entitat) {
						return entitat.getId();
					}
				},
				EntitatEntity.class,
				new Permission[] {
					ExtendedPermission.READ,
					ExtendedPermission.ADMINISTRATION,
					ExtendedPermission.ADMIN_LECTURA}, //
				auth);
		List<EntitatDto> resposta = conversioTipusHelper.convertirList(
				entitats,
				EntitatDto.class);
		for (EntitatDto entitat: resposta) {
			try {
				entitat.setLogoCapBytes(entitatHelper.getLogo(entitat.getCodiDir3()));
			} catch (Exception ex) {
				logger.error("No s'ha pogut definir el logo per l'entitat (codiDir3=" + entitat.getCodiDir3() + ")");
			}
		}
		permisosEntitatHelper.omplirPermisosPerEntitats(
				resposta,
				false);
		return resposta;
	}
	@CacheEvict(value = "entitatsUsuari", key="#usuariCodi")
	public void evictEntitatsAccessiblesUsuari(String usuariCodi) {
	}
	
	
	/**
	 * Takes the list of unitats from database and converts it to the tree
	 * 
	 * @param pareCodi 
	 * 				codiDir3
	 * @return tree of unitats
	 */
	@Cacheable(value = "unitatsOrganitzativesArbreByPare", key="{#pareCodi}")
	public ArbreDto<UnitatOrganitzativaDto> unitatsOrganitzativesFindArbreByPare(String pareCodi) {

		final Timer timerunitatsOrganitzativesFindArbreByPare = metricRegistry.timer(MetricRegistry.name(UnitatOrganitzativaHelper.class, "unitatsOrganitzativesFindArbreByPare"));
		Timer.Context contextunitatsOrganitzativesFindArbreByPare = timerunitatsOrganitzativesFindArbreByPare.time();
		
		List<UnitatOrganitzativaEntity> unitatsOrganitzativesEntities = unitatOrganitzativaRepository
				.findByCodiDir3EntitatOrderByDenominacioAsc(pareCodi);
		
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
			resposta.setArrel(unitatOrganitzativaHelper.getNodeArbreUnitatsOrganitzatives(unitatOrganitzativaArrel, unitatsOrganitzatives, null));
			contextunitatsOrganitzativesFindArbreByPare.stop();
			return resposta;

		}
		contextunitatsOrganitzativesFindArbreByPare.stop();
		return null;
		
	}
	
	
	@CacheEvict(value = "unitatsOrganitzativesArbreByPare", key="{#pareCodi}")
	public void evictUnitatsOrganitzativesFindArbreByPare(
			String pareCodi) {
		System.out.println();
	}	
	
	

	@Cacheable(value = "usuariAmbCodi", key="#usuariCodi")
	public DadesUsuari findUsuariAmbCodi(
			String usuariCodi) {
		return pluginHelper.dadesUsuariFindAmbCodi(
				usuariCodi);
	}

	@Cacheable(value = "provinciesPerComunitat", key="#comunitatCodi")
	public List<ProvinciaDto> findProvinciesPerComunitat(String comunitatCodi) {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi),
				ProvinciaDto.class);
	}
	
	@Cacheable(value = "tipusVia")
	public List<TipusViaDto	> findTipusVia() {
		return pluginHelper.dadesExternesTipusViaAll();
	}
	
	@Cacheable(value = "municipisPerProvincia", key="#provinciaCodi")
	public List<MunicipiDto> findMunicipisPerProvincia(String provinciaCodi) {
		return conversioTipusHelper.convertirList(
				pluginHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi),
				MunicipiDto.class);
	}

	@Cacheable(value = "elementsPendentsBustiesUsuari", key="{#entitat.id, #usuariCodi}")
	public long countElementsPendentsBustiesUsuari(
			EntitatEntity entitat,
			String usuariCodi) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		// Obté la llista d'id's amb permisos per a l'usuari
		List<BustiaEntity> busties = bustiaRepository.findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(entitat);
		// Filtra la llista de bústies segons els permisos
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
		
		long count;
		if (!busties.isEmpty()) {
			count = contingutRepository.countPendentsByPares(
					busties);
		}else {
			count = 0;
		}
		return count;
	}
	
	
	
	
	@CacheEvict(value = "elementsPendentsBustiesUsuari", key="{#entitat.id, #usuariCodi}")
	public void evictCountElementsPendentsBustiesUsuari(
			EntitatEntity entitat,
			String usuariCodi) {
	}

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);


}
