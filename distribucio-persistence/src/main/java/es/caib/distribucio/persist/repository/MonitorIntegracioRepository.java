/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.persist.entity.MonitorIntegracioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar un monitorIntegracio de base
 * de dades del tipus monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface MonitorIntegracioRepository extends JpaRepository<MonitorIntegracioEntity, Long> {

	@Query( "select count(mon) " +
			"from " + 
			"	MonitorIntegracioEntity mon " +  
			"where " + 
			"lower(mon.codi) like lower(:codi)")
	public Integer countByCodi(
			@Param("codi") String codi);
	
	@Query( "from " + 
			"MonitorIntegracioEntity mon " + 
			"where " + 
			"mon.codi like :codiMonitor " + 
			"and (:isDataNula = true or mon.data >= :data) " +
			"and (:isDataFiNula = true or mon.data < :dataFi) " +
			"and (:isNullDescripcio = true or lower(mon.descripcio) like lower('%'||:descripcio||'%')) " +
			"and (:isNullUsuari = true or lower(mon.codiUsuari) like lower('%'||:usuari||'%'))" + 
			"and (:isNullEstat = true or mon.estat = :estat) " +
			"and (:isNullTipus = true or mon.tipus = :tipus) " +
			"and (:isNullEntitat = true or lower(mon.codiEntitat) like lower('%'||:entitat||'%')) " +
			"and (:isNullNumeroRegistre = true or lower(mon.numeroRegistre) like lower('%'||:numeroRegistre||'%')) "
    )
	Page<MonitorIntegracioEntity> findByFiltrePaginat(
			@Param("codiMonitor") String codiMonitor,
			@Param("isDataNula") boolean isDataNula,
			@Param("data") Date data,
            @Param("isDataFiNula") boolean isDataFiNula,
			@Param("dataFi") Date dataFi,
			@Param("isNullDescripcio") boolean isNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("isNullUsuari") boolean isNullUsuari,
			@Param("usuari") String usuari,
			@Param("isNullEstat") boolean isNullEstat,
			@Param("estat") IntegracioAccioEstatEnumDto estat,
			@Param("isNullTipus") boolean isNullTipus,
			@Param("tipus") IntegracioAccioTipusEnumDto tipus,
            @Param("isNullEntitat") boolean isNullEntitat,
            @Param("entitat") String entitat,
            @Param("isNullNumeroRegistre") boolean isNullNumeroRegistre,
            @Param("numeroRegistre") String numeroRegistre,
            Pageable pageable);

	@Query(	" 	select count(mon) " + 
			"	from MonitorIntegracioEntity mon " +
			"	where mon.data < :data ")
	public Integer countMonitorByDataBefore(
			@Param("data") Date data);

	@Query(	"select mon.codi, count(mon)" +
			"from MonitorIntegracioEntity mon " +
			" where mon.estat = 'ERROR' " +
            " and (:isDataNula = true or mon.data >= :data) " +
            " and (:isDataFiNula = true or mon.data < :dataFi) " +
            " and (:isNullDescripcio = true or lower(mon.descripcio) like lower('%'||:descripcio||'%')) " +
            " and (:isNullUsuari = true or lower(mon.codiUsuari) like lower('%'||:usuari||'%'))" +
            " and (:isNullTipus = true or mon.tipus = :tipus) " +
            " and (:isNullEntitat = true or lower(mon.codiEntitat) like lower('%'||:entitat||'%'))" +
            " and (:isNullNumeroRegistre = true or lower(mon.numeroRegistre) like lower('%'||:numeroRegistre||'%'))" +
			" group by mon.codi ")
	public List<Object[]> countErrorsGroupByCodi(
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
            @Param("entitat") String entitat,
            @Param("isNullNumeroRegistre") boolean isNullNumeroRegistre,
            @Param("numeroRegistre") String numeroRegistre);

	@Query(	"select count(mon)" +
			"from MonitorIntegracioEntity mon " +
			"where mon.codiUsuari = :usuari " +
            "and mon.data >= :dataInici " +
			"and mon.data <= :dataFinal " +
            "and mon.descripcio like '%Canvi d''estat%'" +
			"and mon.codi = 'BACKOFFICE' ")
	public Integer countBackofficeCanvisEstatFromUser(
			@Param("usuari") String usuari,
			@Param("dataInici") Date dataInici,
			@Param("dataFinal") Date dataFinal
    );

	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Modifying
	@Query(	"delete from MonitorIntegracioEntity mon " +
			"where mon.data < :data ")
	public void deleteDataBefore(
			@Param("data") Date data);
	
	/** Esborra les dades filtrant pel codi */
	@Modifying
	@Query("delete from MonitorIntegracioEntity mon " +
			"where mon.codi = :codi")
	public void deleteByCodiMonitor(
			@Param("codi") String codi);
	
	@Modifying
	@Query(value = "update dis_mon_int " +
			"set codi_usuari = :codiNou where codi_usuari = :codiAntic",
			nativeQuery = true)
	int updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
