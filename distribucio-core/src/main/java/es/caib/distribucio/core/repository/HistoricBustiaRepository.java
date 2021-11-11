/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.core.entity.HistoricBustiaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus d'històric de bústies per unitat orgànica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricBustiaRepository extends JpaRepository<HistoricBustiaEntity, Long> {

	/** Esborra les dades pel dia indicat. */
	
	@Query( "delete from HistoricBustiaEntity hb " +
			"where hb.data = :data " +
			"		and hb.tipus = :tipus ")
	@Modifying
	public void deleteByDataAndTipus(
			@Param("data") Date data,
			@Param("tipus") HistoricTipusEnumDto tipus);
	
	@Query("select  bustia.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " +
			"		bustia.id, " +
			"		bustia.nom " +
			"from BustiaEntity bustia " +
			"where bustia.activa = true and bustia.pare is not null"
	)
	public List<Object[]> getBusties();

	
	@Query(	
			"select entitat.id, " +
			"		unitat.id, " + 
			"		bustiaId, " + 
			"		nom, " + 
			"		avg(usuaris), " +
			"		avg(usuarisPermis), " +
			"		avg(usuarisRol) " +
			"from HistoricBustiaEntity " +
			"where data >= :mesInici " +
			"		and  data < :mesFi " +
			"group by entitat.id, unitat.id, bustiaId, nom"
	)
	public List<Object[]> getDadesPerMes(
			@Param("mesInici") Date mesInici,
			@Param("mesFi") Date mesFi);

	@Query(	
			"from HistoricBustiaEntity " +
			"where entitat.id = :entitatId " +
			"		and ((:dadesEntitat = true and entitat.id = :entitatId) " +
			"            or (unitat.id in (:unitatsIds))) " +
			"		and tipus = :tipus " +
			"		and (:esNullDataInici = true or data >= :dataInici) " +
			"		and (:esNullDataFi = true or data <= :dataFi) " +
			"order by data asc ")
	public List<HistoricBustiaEntity> findByFiltre (
			@Param("entitatId") Long entitatId, 
			@Param("dadesEntitat") boolean dadesEntitat, 
			@Param("unitatsIds") List<Long> unitatsIds, 
			@Param("tipus") HistoricTipusEnumDto tipus, 
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

}
