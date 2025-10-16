/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

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
			"and (:isDataNula = true or mon.data between :data and :dataFi) " + 
			"and (:isNullDescripcio = true or lower(mon.descripcio) like lower('%'||:descripcio||'%')) " + 
			"and (:isNullUsuari = true or lower(mon.codiUsuari) like lower('%'||:usuari||'%'))" + 
			"and (:isNullEstat = true or mon.estat = :estat) " +
			"and (:isNullEntitat = true or lower(mon.codiEntitat) like lower('%'||:entitat||'%'))")
	Page<MonitorIntegracioEntity> findByFiltrePaginat(
			@Param("codiMonitor") String codiMonitor, 
			@Param("isDataNula") boolean isDataNula, 
			@Param("data") Date data, 
			@Param("dataFi") Date dataFi, 
			@Param("isNullDescripcio") boolean isNullDescripcio, 
			@Param("descripcio") String descripcio, 
			@Param("isNullUsuari") boolean isNullUsuari, 
			@Param("usuari") String usuari, 
			@Param("isNullEstat") boolean isNullEstat, 
			@Param("estat") IntegracioAccioEstatEnumDto estat,
            @Param("isNullEntitat") boolean isNullEntitat,
            @Param("entitat") String entitat,
            Pageable pageable);

	@Query(	" 	select count(mon) " + 
			"	from MonitorIntegracioEntity mon " +
			"	where mon.data < :data ")
	public Integer countMonitorByDataBefore(
			@Param("data") Date data);

	@Query(	"select mon.codi, count(mon)" +
			"from MonitorIntegracioEntity mon " +
			"where mon.estat = 'ERROR' " + 
			"and mon.data >= :dataInici " + 
			"group by mon.codi ")
	public List<Object[]> countErrorsGroupByCodi(
			@Param("dataInici") Date dataInici);

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
