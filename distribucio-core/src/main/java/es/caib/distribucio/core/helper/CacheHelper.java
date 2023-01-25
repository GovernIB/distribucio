/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.MunicipiDto;
import es.caib.distribucio.core.api.dto.ProvinciaDto;
import es.caib.distribucio.core.api.dto.ResultatConsultaDto;
import es.caib.distribucio.core.api.dto.ResultatDominiDto;
import es.caib.distribucio.core.api.dto.TipusViaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.DominiException;
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
	
	@Cacheable(value = "resultatConsultaDominis", key="#consulta.concat(#filtre).concat(#iniciCerca).concat(#finalCerca)")
	public ResultatDominiDto findDominisByConsutla(
			JdbcTemplate jdbcTemplate,
			String consulta,
			String filtre,
			int iniciCerca,
			int finalCerca) {
		ResultatDominiDto resultat = new ResultatDominiDto();
		try {
			if (jdbcTemplate.getDataSource().getConnection() == null)
				throw new DominiException("La hi ha cap connexió establerta amb el domini configurat.");
			if (jdbcTemplate.getDataSource().getConnection().isClosed())
				throw new DominiException("La connexió amb el domini està tancada.");

   			boolean isMySql = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
			jdbcTemplate.setMaxRows(finalCerca);
			boolean compatible = true;
			
//   		### SI ÉS UNA BBDD MYSQL I HI HA FILTRE
   			if (isMySql && !filtre.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") RESULT WHERE RESULT.VALOR LIKE '%"+ filtre + "%'";
   			}
   			
//			### NOMÉS PAGINACIÓ SENSE FILTRE
   			if (isMySql && filtre.isEmpty()) {
   				consulta += " LIMIT " + iniciCerca + "," + finalCerca;
   			}
   			
//   		### SI ÉS UNA BBDD ORACLE I HI HA FILTRE
   			if (!isMySql && !filtre.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") WHERE VALOR LIKE '%"+ filtre + "%'";
   			}
   			
   			if (!isMySql && filtre.isEmpty()) {
//   			### ORACLE > 12C OR ORACLE < 12C
   				if (isOracleVersionGtOrEq12c(jdbcTemplate)) {
					consulta += " OFFSET " + iniciCerca + " ROWS FETCH NEXT " + finalCerca + " ROWS ONLY";
				} else if (!isOracleVersionGtOrEq12c(jdbcTemplate)) {
					StringBuilder consultaBuilder = new StringBuilder(consulta);
					consultaBuilder.insert(consulta.toLowerCase().indexOf("valor") + 5, ", ROWNUM AS RW ");
					consulta = "SELECT * FROM (" + consultaBuilder + ") WHERE RW  >= " + iniciCerca + " AND RW <= " + finalCerca;
				}
   			}
   			
   			if (!compatible)
   				throw new DominiException("La versió actual de la BBDD no és compatible amb la cerca de dominis. Consulti el seu administrador.");
   			
			if (jdbcTemplate != null) {
				List<ResultatConsultaDto> resultatConsulta = jdbcTemplate.query(consulta, new DominiRowMapperHelper());
				
				resultat.setTotalElements(resultatConsulta.size());
				resultat.setResultat(resultatConsulta);
			}
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut recuperar el resultat de consulta: " + e.getMessage(),
					e.getCause());
		} finally {
			try {
				jdbcTemplate.getDataSource().getConnection().close();
			} catch (SQLException ex) {
				logger.error("Hi ha hagut un error tancant la connexió JDBC", ex);
			}
		}
		return resultat;
	}
	
	@Cacheable(value = "resultatConsultaDominis", key="#consulta.concat(#dadaValor)")
	public ResultatConsultaDto getValueSelectedDomini(
			JdbcTemplate jdbcTemplate,
			String consulta,
			String dadaValor) {
		List<ResultatConsultaDto> resultat = new ArrayList<ResultatConsultaDto>();
		try {
			if (jdbcTemplate.getDataSource().getConnection() == null)
				throw new DominiException("La hi ha cap connexió establerta amb el domini configurat.");
			if (jdbcTemplate.getDataSource().getConnection().isClosed())
				throw new DominiException("La connexió amb el domini està tancada.");

   			boolean isMySql = jdbcTemplate.getDataSource().getConnection().getMetaData().getDatabaseProductName().toLowerCase().contains("mysql");
			boolean compatible = true;
			
//   		### SI ÉS UNA BBDD MYSQL I HI HA FILTRE
   			if (isMySql && !dadaValor.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") RESULT WHERE RESULT.ID = '"+ dadaValor + "'";
   			}
   			
//   		### SI ÉS UNA BBDD ORACLE I HI HA FILTRE
   			if (!isMySql && !dadaValor.isEmpty()) {
   				jdbcTemplate.setMaxRows(0);
   				consulta = "SELECT * FROM (" + consulta + ") WHERE ID = '"+ dadaValor + "'";
   			}
   			
   			if (!compatible)
   				throw new DominiException("La versió actual de la BBDD no és compatible amb la cerca de dominis. Consulti el seu administrador.");
   			
			if (jdbcTemplate != null) {
				resultat = jdbcTemplate.query(consulta, new DominiRowMapperHelper());
			}
		} catch (Exception e) {
			throw new DominiException(
					"No s'ha pogut recuperar el resultat de consulta: " + e.getMessage(),
					e.getCause());
		} finally {
			try {
				jdbcTemplate.getDataSource().getConnection().close();
			} catch (SQLException ex) {
				logger.error("Hi ha hagut un error tancant la connexió JDBC", ex);
			}
		}
		return resultat.get(0);
	}
	
	@CacheEvict(value = "resultatConsultaDominis", allEntries=true)
	public void evictFindDominisByConsutla() {
	}
	
	private boolean isOracleVersionGtOrEq12c(JdbcTemplate jdbcTemplate) throws SQLException {
		DatabaseMetaData databaseMetaData = jdbcTemplate.getDataSource().getConnection().getMetaData();
		int versionMajor = databaseMetaData.getDatabaseMajorVersion();
		int versionMinnor = databaseMetaData.getDatabaseMinorVersion();
		
		if (versionMajor < 12) {
			return false;
		} else if (versionMajor == 12) {
			if (versionMinnor >= 1) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CacheHelper.class);


}
