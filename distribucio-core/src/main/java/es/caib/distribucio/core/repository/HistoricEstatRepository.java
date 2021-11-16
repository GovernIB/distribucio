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

import es.caib.distribucio.core.api.dto.historic.HistoricEstatDto;
import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.core.entity.HistoricEstatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus d'històric d'estats per unitat orgànica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricEstatRepository extends JpaRepository<HistoricEstatEntity, Long> {

	/** Esborra les dades pel dia indicat. */
	
	@Query( "delete from HistoricEstatEntity he " +
			"where he.data = :data " +
			"		and he.tipus = :tipus ")
	@Modifying
	public void deleteByDataAndTipus(
			@Param("data") Date data,
			@Param("tipus") HistoricTipusEnumDto tipus);
	
	@Query(	
			"select registre.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		registre.procesEstat, " + 
			"		sum(case when registre.procesError is null " + 
			"						and trunc(registre.procesData) = :data then 1 else 0 end) as correcte, " +
			"		sum(case when registre.procesError is null then 1 else 0 end) as correcteTotal, " +
			"		sum(case when registre.procesError is not null " + 
			"						and trunc(registre.procesData) = :data then 1 else 0 end) as error, " +
			"		sum(case when registre.procesError is not null then 1 else 0 end) as errorTotal, " +
			"		count(registre) as total " +
			"from RegistreEntity registre, " +
			"		BustiaEntity bustia " +
			"where registre.pare.id = bustia.id " +
			"		and bustia.pare is not null "	+
			"group by registre.entitat.id, bustia.unitatOrganitzativa.id, registre.procesEstat"
	)
	public List<Object[]> getEstats(
			@Param("data") Date data);
	
	@Query(	
			"select entitat.id, " +
			"		estat, " + 
			"		sum(correcte), " +
			"		sum(correcteTotal), " +
			"		sum(error), " +
			"		sum(errorTotal), " +
			"		sum(total) " +
			"from HistoricEstatEntity " +
			"where data = :data " +
			"		and tipus = 'DIARI' " +
			"group by entitat.id, estat"
	)
	public List<Object[]> getDadesPerEntitat(
			@Param("data") Date data);
	
	@Query(	
			"select entitat.id, " +
			"		unitat.id, " + 
			"		estat, " + 
			"		sum(correcte), " +
			"		max(correcteTotal), " +
			"		sum(error), " +
			"		max(errorTotal), " +
			"		max(total) " +
			"from HistoricEstatEntity " +
			"where data >= :mesInici " +
			"		and  data < :mesFi " +
			"		and tipus = 'DIARI' " +
			"group by entitat.id, unitat.id, estat"
	)
	public List<Object[]> getDadesPerMes(
			@Param("mesInici") Date mesInici,
			@Param("mesFi") Date mesFi);

	@Query(	
			"from HistoricEstatEntity " +
			"where entitat.id = :entitatId " +
			"		and ((:dadesEntitat = true and unitat is null) " +
			"            or (unitat.id in (:unitatsIds))) " +
			"		and tipus = :tipus " +
			"		and (:esNullDataInici = true or data >= :dataInici) " +
			"		and (:esNullDataFi = true or data <= :dataFi) " +
			"order by data asc ")
	public List<HistoricEstatEntity> findByFiltre (
			@Param("entitatId") Long entitatId, 
			@Param("dadesEntitat") boolean dadesEntitat, 
			@Param("unitatsIds") List<Long> unitatsIds, 
			@Param("tipus") HistoricTipusEnumDto tipus, 
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	/** Consulta per retornar les dades agregades per estat sense tenir
	 * en compte la UO.	 */
	@Query(	"select new es.caib.distribucio.core.api.dto.historic.HistoricEstatDto( " +
			"			data, " + 
			"			tipus, " + 
			"			estat, " + 
			"			sum(correcte), " + 
			"			sum(correcteTotal), " + 
			"			sum(error), " + 
			"			sum(errorTotal), " + 
			"			sum(total)) " +
			"from HistoricEstatEntity " +
			"where entitat.id = :entitatId " +
			"		and ((:dadesEntitat = true and unitat is null) " +
			"            or (unitat.id in (:unitatsIds))) " +
			"		and tipus = :tipus " +
			"		and (:esNullDataInici = true or data >= :dataInici) " +
			"		and (:esNullDataFi = true or data <= :dataFi) " +
			"group by data, tipus, estat " +
			 "order by data asc ")
	public List<HistoricEstatDto> findAgregatsByFiltre (
			@Param("entitatId") Long entitatId, 
			@Param("dadesEntitat") boolean dadesEntitat, 
			@Param("unitatsIds") List<Long> unitatsIds, 
			@Param("tipus") HistoricTipusEnumDto tipus, 
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	
	/** Posa el total com la resta del total del dia o mes seguent menys els nous.*/
	@Query("update HistoricEstatEntity e " +
			"set e.correcteTotal = coalesce (" +
			"	(	select eSeguent.correcteTotal - eSeguent.correcte" +
			"		from HistoricEstatEntity eSeguent " +
			"		where e.entitat = eSeguent.entitat " +
			"				and ((e.unitat is null and eSeguent.unitat is null) or (e.unitat = eSeguent.unitat)) " +
			"				and e.estat = eSeguent.estat " +
			"				and e.tipus = eSeguent.tipus" +
			"				and ((e.tipus = 'DIARI' and eSeguent.data = :diaSeguent) " +
			"						or (e.tipus = 'MENSUAL' and eSeguent.data = :mesSeguent))" +
			"	), correcteTotal), " +
			"e.errorTotal = coalesce (" +
			"	(	select eSeguent.errorTotal - eSeguent.error " +
			"		from HistoricEstatEntity eSeguent " +
			"		where e.entitat = eSeguent.entitat " +
			"				and ((e.unitat is null and eSeguent.unitat is null) or (e.unitat = eSeguent.unitat)) " +
			"				and e.estat = eSeguent.estat " +
			"				and e.tipus = eSeguent.tipus " +
			"				and ((e.tipus = 'DIARI' and eSeguent.data = :diaSeguent) " +
			"						or (e.tipus = 'MENSUAL' and eSeguent.data = :mesSeguent))" +
			"	), errorTotal), " +
			"e.total = coalesce (" +
			"	(	select eSeguent.total - eSeguent.error - eSeguent.correcte " +
			"		from HistoricEstatEntity eSeguent " +
			"		where e.entitat = eSeguent.entitat " +
			"				and ((e.unitat is null and eSeguent.unitat is null) or (e.unitat = eSeguent.unitat)) " +
			"				and e.estat = eSeguent.estat " +
			"				and e.tipus = eSeguent.tipus " +
			"				and ((e.tipus = 'DIARI' and eSeguent.data = :diaSeguent) " +
			"						or (e.tipus = 'MENSUAL' and eSeguent.data = :mesSeguent))" +
			"	), total) " +
			"where e.data = :data ")
	@Modifying
	public void recalcularTotals(
			@Param("data") Date data,
			@Param("diaSeguent") Date diaSeguent,
			@Param("mesSeguent") Date mesSeguent);

}
