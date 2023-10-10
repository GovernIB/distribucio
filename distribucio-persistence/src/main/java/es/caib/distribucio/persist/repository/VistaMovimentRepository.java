/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.persist.entity.VistaMovimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la vista de moviments
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface VistaMovimentRepository extends JpaRepository<VistaMovimentEntity, Long> {

	/** Consulta pel datatable de moviments */
	@Query(	"select v " +
			"from " +
			"    VistaMovimentEntity v " +
			"where " +
			"    (v.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (v.origen in (:bustiesIds) or v.origen in (:bustiesIds) or (v.desti in (:bustiesIds) or v.origen is null)))" +
			"and (:esNullNumero = true or lower(v.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(v.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNumeroOrigen = true or lower(v.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(v.remitent) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or v.data >= :dataInici) " +
			"and (:esNullDataFi = true or v.data < :dataFi) " +
			"and (:esProcessat = false or v.pendent = false) " +
			"and (:esPendent = false or v.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or v.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullDocumentacioFisicaCodi = true or v.documentacioFisicaCodi = :documentacioFisicaCodi) " +
			"and (:esNullBackCodi = true or lower(v.backCodi) like lower('%'||:backCodi||'%')) " +
			"and (:esNullUnitatOrganitzativa = true or v.bustia in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"and (:esNullProcesEstat = true or v.procesEstat = :procesEstat) " +
			"and (:nomesAmbErrors = false or v.procesError != null ) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from v.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 ) " +
			"and (:isNullBustiaOrigen = true or v.origen = :bustiaOrigen) " +
			"and (:isNullBustiaDesti = true or v.desti = :bustiaDesti)")
	public Page<VistaMovimentEntity> findMovimentsByFiltre(
			@Param("entitat") Long entitat,
			@Param("esBustiesTotes") boolean esBustiesTotes,
			@Param("bustiesIds") List<Long> bustiesIds,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullRemitent") boolean esNullRemitent,
			@Param("remitent") String remitent,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esProcessat") boolean esProcessat,
			@Param("esPendent") boolean esPendent,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullEnviatPerEmail") boolean esNullEnviatPerEmail,
			@Param("enviatPerEmail") Boolean enviatPerEmail,
			@Param("esNullDocumentacioFisicaCodi") boolean esNullDocumentacioFisicaCodi,
			@Param("documentacioFisicaCodi") String documentacioFisicaCodi,
			@Param("esNullBackCodi") boolean esNullBackCodi,
			@Param("backCodi") String backCodi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("isNullBustiaOrigen") boolean isNullBustiaOrigen,
			@Param("bustiaOrigen") Long bustiaOrigen,
			@Param("isNullBustiaDesti") boolean isNullBustiaDesti,
			@Param("bustiaDesti") Long bustiaDesti,
			Pageable pageable);
	
	/** Consulta pel datatable de moviments */
	@Query(	"select v.id " +
			"from " +
			"    VistaMovimentEntity v " +
			"where " +
			"    (v.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (v.origen in (:bustiesIds) or v.origen in (:bustiesIds) or (v.desti in (:bustiesIds) or v.origen is null)))" +
			"and (:esNullNumero = true or lower(v.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(v.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNumeroOrigen = true or lower(v.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(v.remitent) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or v.data >= :dataInici) " +
			"and (:esNullDataFi = true or v.data < :dataFi) " +
			"and (:esProcessat = false or v.pendent = false) " +
			"and (:esPendent = false or v.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or v.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullDocumentacioFisicaCodi = true or v.documentacioFisicaCodi = :documentacioFisicaCodi) " +
			"and (:esNullBackCodi = true or lower(v.backCodi) like lower('%'||:backCodi||'%')) " +
			"and (:esNullUnitatOrganitzativa = true or v.bustia in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"and (:esNullProcesEstat = true or v.procesEstat = :procesEstat) " +
			"and (:nomesAmbErrors = false or v.procesError != null ) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from v.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 ) " +
			"and (:isNullBustiaOrigen = true or v.origen = :bustiaOrigen) " +
			"and (:isNullBustiaDesti = true or v.desti = :bustiaDesti)")
	public List<String> findRegistreIdsByFiltre(
			@Param("entitat") Long entitat,
			@Param("esBustiesTotes") boolean esBustiesTotes,
			@Param("bustiesIds") List<Long> bustiesIds,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullExtracte") boolean esNullExtracte,
			@Param("extracte") String extracte,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullRemitent") boolean esNullRemitent,
			@Param("remitent") String remitent,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esProcessat") boolean esProcessat,
			@Param("esPendent") boolean esPendent,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			@Param("esNullEnviatPerEmail") boolean esNullEnviatPerEmail,
			@Param("enviatPerEmail") Boolean enviatPerEmail,
			@Param("esNullDocumentacioFisicaCodi") boolean esNullDocumentacioFisicaCodi,
			@Param("documentacioFisicaCodi") String documentacioFisicaCodi,
			@Param("esNullBackCodi") boolean esNullBackCodi,
			@Param("backCodi") String backCodi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("isNullBustiaOrigen") boolean isNullBustiaOrigen,
			@Param("bustiaOrigen") Long bustiaOrigen,
			@Param("isNullBustiaDesti") boolean isNullBustiaDesti,
			@Param("bustiaDesti") Long bustiaDesti);
	
	
	
	@Query(	"select distinct v.origen " +
			"from " +
			"    VistaMovimentEntity v " +
			"where " +
			"    (v.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (v.origen in (:bustiesIds) or v.origen in (:bustiesIds) or (v.desti in (:bustiesIds) or v.origen is null)))")
	public List<Long> findBustiesOrigenByFiltre(
			@Param("entitat") Long entitat,
			@Param("esBustiesTotes") boolean esBustiesTotes,
			@Param("bustiesIds") List<Long> bustiesIds);
}
