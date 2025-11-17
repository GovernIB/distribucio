/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreRepository extends JpaRepository<RegistreEntity, Long> {

	List<RegistreEntity> findByReglaNotNullAndProcesEstatOrderByCreatedDateAsc(
			RegistreProcesEstatEnum procesEstat);

	long countByExpedientArxiuUuidAndEsborrat(String expedientArxiuUuid, int esborrat);

	List<RegistreEntity> findByRegla(
			ReglaEntity regla);
	
	List<RegistreEntity> findByNumero(String numero);

	@Query(
			"select r.numeroCopia " + 
			"from RegistreEntity r " +
			"where r.numero = :numero " +
			"order by " +
		    "    r.numeroCopia asc")
	List<Integer> findCopiesByNumero(String numero);
	
	List<RegistreEntity> findByNumeroAndData(String numero, Date data);
	
	@Query(
			"from" +
			"    RegistreEntity r " +
			"where r.numero = :numero" )
	List<RegistreEntity> findRegistresByNumero(
			@Param("numero") String numero);
	
	
	@Query(
			"select count(r) " + 
			"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.ARXIU_PENDENT " +
			"and r.procesIntents < :maxReintents " +
			"and r.entitat = :entitat ")
	long countGuardarAnnexPendents(
			@Param("entitat") EntitatEntity entitat, 
			@Param("maxReintents") int maxReintents);

	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.ARXIU_PENDENT " +
			"and r.procesIntents < :maxReintents " +
			"and r.entitat = :entitat ")
	Page<RegistreEntity> findGuardarAnnexPendentsPaged(
			@Param("entitat") EntitatEntity entitat, 
			@Param("maxReintents") int maxReintents,
			Pageable pageable);

	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BACK_ERROR " +
			"and r.procesIntents >= :maxReintents " +
			"and r.entitat = :entitat ")
	List<RegistreEntity> findEstatErrorProcesament(
			@Param("entitat") EntitatEntity entitat,
			@Param("maxReintents") int maxReintents);

	
	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.regla is not null " +
			"and r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.REGLA_PENDENT " +
			"and r.procesIntents <= :maxReintents " +
			"and r.entitat = :entitat " + 
		    "order by " +
		    "    r.data desc")
	List<RegistreEntity> findAmbReglaPendentAplicar(
			@Param("entitat") EntitatEntity entitat, 
			@Param("maxReintents") int maxReintents);
	
	
	
	/** Consulta de les anotacions pendents d'enviar ordenades per regla. */
	@Query(
			"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.regla.activa = true " +
			"and r.entitat = :entitat " +
			"and r.regla.tipus = es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto.BACKOFFICE " +
			"and r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BACK_PENDENT " +
			"and (r.backRetryEnviarData is null or r.backRetryEnviarData < :currentDate) " +
			"and r.procesIntents < :maxReintents " +
		    "order by r.regla.id asc ")
	List<RegistreEntity> findAmbEstatPendentEnviarBackoffice(
			@Param("entitat") EntitatEntity entitat,
			@Param("currentDate") Date currentDate,
			@Param("maxReintents") int maxReintents);


	/** Consulta de les anotacions comunicades que han sobrepassat el limit. */
    @Query(	"from " +
            "    RegistreEntity r " +
            "where " +
			"    r.regla.activa = true " +
			"and r.regla.tipus = es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto.BACKOFFICE " +
			"and r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BACK_COMUNICADA " +
			"and r.backComunicadaData <= :dataLimit")
	List<RegistreEntity> findAmbLimitEstatComunicadaBackoffice(
			@Param("dataLimit") Date dataLimit);


	/*
	RegistreEntity findByPareAndId(
			ContingutEntity pare,
			Long id);
	*/
	RegistreEntity findByEntitatAndId(
			EntitatEntity entitat,
			Long id);
	
	@Query(	"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.pare.id = :pareId ")
	List<RegistreEntity> findByPareId(
			@Param("pareId") Long pareId);

	/*List<RegistreEntity> findByPareAndMotiuRebuigNullOrderByDataAsc(
			ContingutEntity pare);*/

	@Query(	"select " +
			"    pare.id, " +
			"    count(*) " +
			"from " +
			"    RegistreEntity " +
			"where " +
			"    motiuRebuig is null " +
			"and (pare is not null and pare in (:pares)) " +
			"group by " +
			"    pare.id")
	List<Object[]> countByParesAndNotRebutjat(
			@Param("pares") List<? extends ContingutEntity> pares);

	

	RegistreEntity findByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndData(
			String entitatCodi,
			String llibreCodi,
			String registreTipus,
			String numero,
			Date data);

	List<RegistreEntity> findByEntitatCodiAndNumero(
			String entitatCodi,			
			String numero);
	
	/** Registres duplicats */
	List<RegistreEntity> findRegistresByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndDataAndEsborrat(
			String entitatCodi,
			String llibreCodi,
			String registreTipus,
			String numero,
			Date data,
			int esborrat);
		
	/** Consulta les anotacions de registre que tenen l'expedient a l'arxiu pendents
	 * de tancar i a les quals ja s'ha excedit el temps d'espera establert
	 * 
	 * @return
	 */
	@Query("from RegistreEntity r " +
			"where r.dataTancament is not null " +
			" and r.dataTancament <= :ara " +
			" and r.arxiuTancat = false" +
			" and r.arxiuTancatError = false " +
			" and r.entitat = :entitat " +
		    " order by r.dataTancament asc")
	List<RegistreEntity> findPendentsTancarArxiuByEntitat(
			@Param("ara") Date ara,
			@Param("entitat") EntitatEntity entitat);

	/** Mètode per consultar el número màxim pel valor de la columna numero_copia. Serveix per crear una nova còpia 
	 * incrementant el valor a partir del resultat d'aquesta consulta.
	 * @param entitatCodi
	 * @param llibreCodi
	 * @param data
	 * @return
	 */
	@Query( "select coalesce(max(r.numeroCopia), 0) " + 
			"from RegistreEntity r " +
			"where  r.entitatCodi = :entitatCodi " +
			"		and r.llibreCodi = :llibreCodi " +
			"		and r.data = :data ")
	Integer findMaxNumeroCopia(
			@Param("entitatCodi") String entitatCodi,
			@Param("llibreCodi") String llibreCodi,
			@Param("data") Date data);

	List<RegistreEntity> findByPareInAndIdIn(
			List<? extends ContingutEntity> pares,
			List<Long> ids);

	List<RegistreEntity> findByIdIn(
			List<Long> ids);

	/** Consulta pel datatable del registre user */
	@Query(	"select r " +
			"from " +
			"    RegistreEntity r " +
			"		left outer join r.darrerMoviment.remitent as remitent "	+
			"where " +
			"    (r.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (r.pare.id in (:bustiesIds))) " +
			"and (:esNullNumero = true or lower(r.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(r.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or r.data >= :dataInici) " +
			"and (:esNullDataFi = true or r.data < :dataFi) " +
			"and (:esProcessat = false or r.pendent = false) " +
			"and (:esPendent = false or r.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or r.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullDocumentacioFisicaCodi = true or r.documentacioFisicaCodi = :documentacioFisicaCodi) " +
			"and (:esNullBackCodi = true or lower(r.backCodi) like lower('%'||:backCodi||'%')) " +
			"and (:esNullUnitatOrganitzativa = true or r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"and (:esNullProcesEstat = true or r.procesEstat = :procesEstat) " +
			"and (:esNullReintentsPendents = true " +
			"		or (:reintentsPendents = true and r.procesIntents < :maxReintents) " + 
			"		or (:reintentsPendents = false and r.procesIntents >= :maxReintents)) " +
			//"and (:ambIntentsPendents = true or r.procesIntents < :maxReintents) " +
			"and (:nomesAmbErrors = false or r.procesError != null ) " +
			"and (:nomesAmbEsborranys = false or r.annexosEstatEsborrany > 0 ) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from r.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 ) " + 
			"and (:esNullSobreescriure = true or r.sobreescriure = :sobreescriure) " + 
			"and (:esNullProcedimentCodi = true or r.procedimentCodi = :procedimentCodi)")
	public Page<RegistreEntity> findRegistreByPareAndFiltre(
			@Param("entitat") EntitatEntity entitat,
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
			@Param("esNullReintentsPendents") boolean esNullReintentsPendents,
			@Param("reintentsPendents") Boolean reintentsPendents,
			@Param("maxReintents") int maxReintents, 
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesAmbEsborranys") boolean nomesAmbEsborranys,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("esNullSobreescriure") boolean esNullSobreescriure,
			@Param("sobreescriure") Boolean sobreescriure,
			@Param("esNullProcedimentCodi") boolean esNullProcedimentCodi, 
			@Param("procedimentCodi") String procedimentCodi, 
			Pageable pageable);
	/** Consulta pel datatable del registre user */
	@Query(	"select r " +
			"from " +
			"    RegistreEntity r " +
			"		left outer join r.darrerMoviment.remitent as remitent "	+
			"where " +
			"    (r.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (r.pare.id in (:bustiesIds))) " +
			"and (:esNullNumero = true or lower(r.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(r.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or r.data >= :dataInici) " +
			"and (:esNullDataFi = true or r.data < :dataFi) " +
			"and (:esProcessat = false or r.pendent = false) " +
			"and (:esPendent = false or r.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or r.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullDocumentacioFisicaCodi = true or r.documentacioFisicaCodi = :documentacioFisicaCodi) " +
			"and (:esNullBackCodi = true " +
			"		or (:senseBackOffice = true and r.backCodi = null) " +
			"		or (lower(r.backCodi) like lower('%'||:backCodi||'%'))) " +
			"and (:esNullUnitatOrganitzativa = true or r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"and (:esNullProcesEstat = true or r.procesEstat = :procesEstat) " +
			"and (:esNullReintentsPendents = true " +
			"		or (:reintentsPendents = true and r.procesIntents < :maxReintents) " + 
			"		or (:reintentsPendents = false and r.procesIntents >= :maxReintents)) " +
			//"and (:ambIntentsPendents = true or r.procesIntents < :maxReintents) " +
			"and (:nomesAmbErrors = false or r.procesError != null ) " +
			"and (:nomesAmbEsborranys = false or r.annexosEstatEsborrany > 0 ) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from r.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 ) " + 
			"and (:esNullSobreescriure = true or r.sobreescriure = :sobreescriure) " + 
			"and (:esNullProcedimentCodi = true or r.procedimentCodi = :procedimentCodi) " +
			"and (:esNullLlistaIdRegistresAnnex = true or "
			+ "										(r.id in (:llista1) or r.id in (:llista2) or r.id in (:llista3) or r.id in (:llista4) or r.id in (:llista5) or r.id in (:llista6) or r.id in (:llista7) ) )")
//			"and (:esNullNombreAnnexes = true or r.id in (select rae.registre.id from RegistreAnnexEntity rae "
//			+ "										group by rae.registre.id "
//			+ "										having count(*) = :nombreAnnexes))")
	public Page<RegistreEntity> findRegistreByPareAndFiltre(
			@Param("entitat") EntitatEntity entitat,
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
			@Param("senseBackOffice") boolean senseBackOffice, 
			@Param("backCodi") String backCodi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			@Param("esNullReintentsPendents") boolean esNullReintentsPendents,
			@Param("reintentsPendents") Boolean reintentsPendents,
			@Param("maxReintents") int maxReintents, 
			@Param("nomesAmbErrors") boolean nomesAmbErrors,
			@Param("nomesAmbEsborranys") boolean nomesAmbEsborranys,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("esNullSobreescriure") boolean esNullSobreescriure,
			@Param("sobreescriure") Boolean sobreescriure,
			@Param("esNullProcedimentCodi") boolean esNullProcedimentCodi, 
			@Param("procedimentCodi") String procedimentCodi, 
			@Param("esNullLlistaIdRegistresAnnex") boolean esNullLlistaIdRegistresAnnex, 
			@Param("llista1") List<Long> llista1,
			@Param("llista2") List<Long> llista2,
			@Param("llista3") List<Long> llista3,
			@Param("llista4") List<Long> llista4,
			@Param("llista5") List<Long> llista5,
			@Param("llista6") List<Long> llista6,
			@Param("llista7") List<Long> llista7,
			Pageable pageable);
	
	
	
	/** Consulta dels identificadors de registre per a la selecció en registre user */
	@Query(	"select r.id " +
			"from " +
			"    RegistreEntity r " +
			"		left outer join r.darrerMoviment.remitent as remitent "	+
			"where " +
			"    (r.entitat = :entitat) " +
			"and ((:esBustiesTotes = true) or (r.pare.id in (:bustiesIds))) " +
			"and (:esNullNumero = true or lower(r.numero) like lower('%'||:numero||'%')) " +
			"and (:esNullExtracte = true or lower(r.extracte) like lower('%'||:extracte||'%')) " +
			"and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or r.data >= :dataInici) " +
			"and (:esNullDataFi = true or r.data < :dataFi) " +
			"and (:esProcessat = false or r.pendent = false) " +
			"and (:esPendent = false or r.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or r.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullDocumentacioFisicaCodi = true or r.documentacioFisicaCodi = :documentacioFisicaCodi) " +
			"and (:esNullBackCodi = true or lower(r.backCodi) like lower('%'||:backCodi||'%')) " +
			"and (:esNullUnitatOrganitzativa = true or r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"and (:esNullProcesEstat = true or r.procesEstat = :procesEstat)" +
			"and (:nomesAmbErrors = false or r.procesError != null ) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from r.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 )") //el primer destí és l'origen
	public List<Long> findRegistreIdsByPareAndFiltre(
			@Param("entitat") EntitatEntity entitat,
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
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa);
	
	
	/**
	 * Consulta per retornar un llistat amb els registres processats al backoffice
	 * amb errors i regla pendent de tipus enviar a backoffice
	 **/
	@Query("from RegistreEntity r " +
			"where r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BACK_ERROR " +
			"	and r.procesIntents <= :maxReintents " +
			"	and (r.backRetryEnviarData is null or r.backRetryEnviarData < :currentDate) " +
			"	and r.regla.tipus = es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto.BACKOFFICE " +
			"order by r.data DESC")
	public List<RegistreEntity> findRegistresBackError(
			@Param("currentDate") Date currentDate,
			@Param("maxReintents") int maxReintents);
	
	
	/**
	 *  Consulta per retornar les dades dels annexos dels registres
	 **/
	@Query("from RegistreAnnexEntity ra " + 
			"where ra.registre.id = :registreId")
	public List<RegistreAnnexEntity> getDadesRegistreAnnex(
			@Param("registreId") Long registreId);

	@Query(
			"select case when (count(r) > 0) then true else false end " + 
			"from " +
			"    RegistreEntity r JOIN r.annexos a " +
			"where r.id = :registreId " +
			"	and (r.justificantArxiuUuid is null or a.fitxerArxiuUuid is null) " +
			"	and r.entitat = :entitat")
	public Boolean isRegistreArxiuPendentByUuid(@Param("registreId") Long registreId, @Param("entitat") EntitatEntity entitat);
	
	/**
	 * Consulta per retornar un llistat de registres filtrat pel seu
	 * codi de backoffice (BACK_CODI) 
	 **
	 */
	@Query("from RegistreEntity r " + 
			"where r.backCodi = :backCodi")
	public List<RegistreEntity> findRegistreBackCodi(
			@Param("backCodi") String backCodi);

	/** Per consultar l'expedient tipus amb bloqueig de BBDD per actualitzar les seqüències de números
	 * @param expedientTipusId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from RegistreEntity where id = :registreId")
	public RegistreEntity findOneAmbBloqueig(@Param("registreId") Long registreId);

	/** Per consultar l'expedient tipus amb bloqueig de BBDD per actualitzar les seqüències de números
	 * @param expedientTipusId
	 * @return
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("from RegistreEntity r where r.id = :registreId and r.entitat.id = :entitatId ")
	public RegistreEntity findOneAmbBloqueig(
			@Param("entitatId") Long entitatId, 
			@Param("registreId") Long registreId);

	@Modifying
	@Query(value = "update dis_registre " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
	@Modifying
	@Query(value = "update dis_registre " +
			"set agafat_per = :codiNou where agafat_per = :codiAntic",
			nativeQuery = true)
	int updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

	public RegistreEntity findTopByEntitatAndExpedientArxiuUuidNotNull(EntitatEntity entitat);
}
