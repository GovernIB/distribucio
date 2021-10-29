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
import es.caib.distribucio.core.entity.HistoricAnotacioEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus d'històric d'anotacions per unitat orgànica.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricAnotacioRepository extends JpaRepository<HistoricAnotacioEntity, Long> {

	/** Esborra les dades pel dia indicat. */
	
	@Query( "delete from HistoricAnotacioEntity ha " +
			"where ha.data = :data " +
			"		and ha.tipus = :tipus ")
	@Modifying
	public void deleteByDataAndTipus(
			@Param("data") Date data,
			@Param("tipus") HistoricTipusEnumDto tipus);

	@Query(	
			"select bustia.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(anotacions) " +
			"from BustiaEntity bustia " +
			"		left join bustia.fills as anotacions " +
			"where (anotacions is null or trunc(anotacions.data) = trunc(:data)) " +
			"group by bustia.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getAnotacions(
			@Param("data") Date data);
	
	@Query(	
			"select entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(anotacionsTotal) " +
			"from EntitatEntity entitat, " +
			" 	  BustiaEntity bustia " +
			"		left join bustia.fills as anotacionsTotal " +
			"where bustia.entitat = entitat " +
			"group by entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getAnotacionsTotal();

	@Query(
			"select moviment.contingut.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(moviment) " +
			"from ContingutMovimentEntity moviment, " +
			"		BustiaEntity bustia " +	
			"where trunc(moviment.createdDate) = :data " +
			"		and moviment.origenId is not null " +
			" 		and moviment.contingut.pare = bustia.id " +
			"group by moviment.contingut.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getReenviaments(
			@Param("data") Date data);
	
	

	@Query(
			"select email.contingut.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(email) " +
			"from ContingutLogEntity email, " +
			"		BustiaEntity bustia " +
			"where email.tipus = 'ENVIAMENT_EMAIL' " + 
			"		and trunc(email.createdDate) = :data " +
			" 		and email.contingut.pare = bustia.id " +
			"group by email.contingut.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getEmails(
			@Param("data") Date data);
	
	@Query(	
			"select bustia.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(bustia) " +
			"from BustiaEntity bustia " +
			"where bustia.activa = true " +
			"group by bustia.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getBusties();

	@Query(	
			"select registre.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(distinct registre.justificantArxiuUuid) " +
			"from RegistreEntity registre, " +
			"		BustiaEntity bustia " +
			"where trunc(registre.justificant.createdDate) = :data " +
			"		and registre.pare.id = bustia.id " +
			"group by registre.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getJustificants(
			@Param("data") Date data);

	@Query(	
			"select annex.registre.entitat.id, " +
			"		bustia.unitatOrganitzativa.id, " + 
			"		count(distinct annex.fitxerArxiuUuid) " +
			"from RegistreAnnexEntity annex, " +
			"		BustiaEntity bustia " +
			"where trunc(annex.createdDate) = :data " +
			"		and annex.registre.pare.id = bustia.id " +
			"group by annex.registre.entitat.id, bustia.unitatOrganitzativa.id"
	)
	public List<Object[]> getAnnexos(
			@Param("data") Date data);

	@Query(	
			"select entitat.id, " +
			"		sum(anotacions), " +
			"		avg(anotacionsTotal), " +
			"		sum(reenviaments), " +
			"		sum(emails), " +
			"		sum(justificants), " +
			"		sum(annexos), " +
			"		avg(busties) " +
			"from HistoricAnotacioEntity " +
			"where data = :data " +
			"group by entitat.id"
	)
	public List<Object[]> getDadesPerEntitat(
			@Param("data") Date data);

	@Query(	
			"select entitat.id, " +
			"		unitat.id, " + 
			"		sum(anotacions), " +
			"		max(anotacionsTotal), " +
			"		sum(reenviaments), " +
			"		sum(emails), " +
			"		sum(justificants), " +
			"		sum(annexos), " +
			"		max(busties), " +
			"		max(usuaris) " +
			"from HistoricAnotacioEntity " +
			"where data >= :mesInici " +
			"		and  data < :mesFi " +
			"group by entitat.id, unitat.id"
	)
	public List<Object[]> getDadesPerMes(
			@Param("mesInici") Date mesInici,
			@Param("mesFi") Date mesFi);

	@Query(	"from HistoricAnotacioEntity " +
			"where entitat.id = :entitatId " +
			"		and ((:dadesEntitat = true and unitat is null) " +
			"            or (unitat.id in (:unitatsIds))) " +
			"		and tipus = :tipus " +
			"		and (:esNullDataInici = true or data >= :dataInici) " +
			"		and (:esNullDataFi = true or data <= :dataFi) " +
			"order by data asc ")
	public List<HistoricAnotacioEntity> findByFiltre (
			@Param("entitatId") Long entitatId, 
			@Param("dadesEntitat") boolean dadesEntitat, 
			@Param("unitatsIds") List<Long> unitatsIds, 
			@Param("tipus") HistoricTipusEnumDto tipus, 
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

}
