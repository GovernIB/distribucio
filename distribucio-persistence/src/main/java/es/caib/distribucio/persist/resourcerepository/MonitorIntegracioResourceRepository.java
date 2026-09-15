package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.MonitorIntegracioResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repositori per a la gestió d'entitats de tipus {@link MonitorIntegracioResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface MonitorIntegracioResourceRepository extends BaseRepository<MonitorIntegracioResourceEntity, Long> {

	@Query("select mon.codi, count(mon) " +
			"from MonitorIntegracioResourceEntity mon " +
			"left join mon.entitat ent " +
			"where mon.estat = 'ERROR' " +
			"and (:isDataNula = true or mon.data >= :data) " +
			"and (:isDataFiNula = true or mon.data < :dataFi) " +
			"and (:isNullDescripcio = true or lower(mon.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isNullUsuari = true or lower(mon.codiUsuari) like lower('%'||:usuari||'%')) " +
			"and (:isNullTipus = true or mon.tipus = :tipus) " +
			"and (:isNullEntitat = true or ent.id = :entitatId) " +
			"and (:isNullNumeroRegistre = true or lower(mon.numeroRegistre) like lower('%'||:numeroRegistre||'%')) " +
			"group by mon.codi")
	List<Object[]> countErrorsGroupByCodi(
			@Param("isDataNula") boolean isDataNula,
			@Param("data") Date data,
			@Param("isDataFiNula") boolean isDataFiNula,
			@Param("dataFi") Date dataFi,
			@Param("isNullDescripcio") boolean isNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("isNullUsuari") boolean isNullUsuari,
			@Param("usuari") String usuari,
			@Param("isNullTipus") boolean isNullTipus,
			@Param("tipus") IntegracioAccioTipusEnumDto tipus,
			@Param("isNullEntitat") boolean isNullEntitat,
			@Param("entitatId") Long entitatId,
			@Param("isNullNumeroRegistre") boolean isNullNumeroRegistre,
			@Param("numeroRegistre") String numeroRegistre);

}
