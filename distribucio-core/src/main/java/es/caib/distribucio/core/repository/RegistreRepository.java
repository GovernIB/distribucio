/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;

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
	
	RegistreEntity findByNumero(String numero);
	
	
	@Query(
			"from" +
			"    RegistreEntity r " +
			"where r.numero = :numero" )
	List<RegistreEntity> findRegistresByNumero(
			@Param("numero") String numero);
	
	
	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.ARXIU_PENDENT " +
			"and r.procesIntents <= :maxReintents " +
		    "order by " +
		    "    r.data asc")
	List<RegistreEntity> findGuardarAnnexPendents(
			@Param("maxReintents") int maxReintents);

	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.regla is not null " +
			"and r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.REGLA_PENDENT " +
			"and r.procesIntents <= :maxReintents " +
		    "order by " +
		    "    r.data desc")
	List<RegistreEntity> findAmbReglaPendentAplicar(
			@Param("maxReintents") int maxReintents);
	
	
	
	
	@Query(
			"from" +
			"    RegistreEntity r " +
			"where " +
			"    r.regla is not null and r.regla.activa = true " +
			"and r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.BACK_PENDENT " +
			"and (r.backRetryEnviarData is null or r.backRetryEnviarData < :currentDate) " +
		    "order by " +
		    "    r.data desc "
		    + "group by r.regla.id")
	List<RegistreEntity> findAmbEstatPendentEnviarBackoffice(
			@Param("currentDate") Date currentDate);
	


	/*@Query("from RegistreEntity r " +
		    "where r.regla.backofficeTipus = es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto.SISTRA " +
		    "	and r.procesEstatSistra in ( es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum.PENDENT, " +
		    "						   es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum.ERROR) " +
			"	and (r.regla.backofficeIntents is null or  r.procesIntents < r.regla.backofficeIntents) " +
		    "order by r.data asc")
	List<RegistreEntity> findAmbReglaPendentProcessarBackofficeSistra();*/

	RegistreEntity findByPareAndId(
			ContingutEntity pare,
			Long id);
	
	List<RegistreEntity> findByPareId(
			Long pareId);

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

	/** Troba l'anotació de registre per identificador. */
	RegistreEntity findByIdentificador(String identificador);

	/** Consulta els identificadors pel backoffice sistra segons els paràmetres de filtre. 
	 * @param b 
	 * @param procesEstatSistra */
	@Query("select r.identificador " +
			"from RegistreEntity r " +
			"where r.regla.backofficeDesti.tipus = es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto.SISTRA " +
			"	and r.identificadorProcedimentSistra = :identificadorProcediment " +
			"	and r.identificadorTramitSistra = :identificadorTramit " +
			"	and (:esNullProcesEstatSistra = true or r.procesEstatSistra = :estatSistra) " +
			"	and (:esNullDesde = true or r.data >= :desde) " +
			"	and (:esNullFins = true  or r.data <= :fins) " +
		    "order by r.data asc")
	List<String> findPerBackofficeSistra(
			@Param("identificadorProcediment") String identificadorProcediment,
			@Param("identificadorTramit") String identificadorTramit,
			@Param("esNullProcesEstatSistra") boolean esNullProcesEstatSistra, 
			@Param("estatSistra") RegistreProcesEstatSistraEnum estatSistra,
			@Param("esNullDesde") boolean esNullDesde,
			@Param("desde") Date desde,
			@Param("esNullFins") boolean esNullFins,
			@Param("fins") Date fins
		);

	@Query(	"select " +
			"    r " +
			"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"	and (:esNullNom = true or lower(r.nom) like lower('%'||:nom||'%')) " +
			"	and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"	and (:esNullUnitatOrganitzativa = true or r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"   and (:esNullBustia = true or r.pare.id = :bustia) " +
			"	and (:esNullDataInici = true or r.data >= :dataInici) " +
			"	and (:esNullDataFi = true or r.data <= :dataFi) " +
			"	and (:esNullProcesEstat = true or r.procesEstat = :procesEstat)" +
			"	and (:nomesAmbErrors = false or r.procesError != null ) " +
			"	and (:esNullBackCodi = true or lower(r.backCodi) like lower('%'||:backCodi||'%')) ")
	public Page<RegistreEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("esNullBustia") boolean esNullBustia,
			@Param("bustia") Long bustia,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			@Param("nomesAmbErrors") boolean nomesAmbErrors, 			
			@Param("esNullBackCodi") boolean esNullBackCodi,
			@Param("backCodi") String backCodi,
			Pageable pageable);
	
	
	@Query(	"select " +
			"    r.id " +
			"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"	and (:esNullNom = true or lower(r.nom) like lower('%'||:nom||'%')) " +
			"	and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"	and (:esNullUnitatOrganitzativa = true or r.pare.id in (select b.id from BustiaEntity b where b.unitatOrganitzativa = :unitatOrganitzativa)) " +
			"   and (:esNullBustia = true or r.pare.id = :bustia) " +
			"	and (:esNullDataInici = true or r.data >= :dataInici) " +
			"	and (:esNullDataFi = true or r.data <= :dataFi) " +
			"	and (:esNullProcesEstat = true or r.procesEstat = :procesEstat)" +
			"	and (:nomesAmbErrors = false or r.procesError != null ) " +
			"	and (:esNullBackCodi = true or lower(r.backCodi) like lower('%'||:backCodi||'%')) ")
	public List<Long> findIdsByFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa,
			@Param("esNullBustia") boolean esNullBustia,
			@Param("bustia") Long bustia,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			@Param("nomesAmbErrors") boolean nomesAmbErrors, 			
			@Param("esNullBackCodi") boolean esNullBackCodi,
			@Param("backCodi") String backCodi);
	

	/** Consulta les anotacions de registre que tenen 
	 * l'expedient a l'arxiu pendents de tancar i a les quals
	 * ja s'ha excedit el temps d'espera establert
	 * @return
	 */
	@Query("from RegistreEntity r " +
			"where r.dataTancament is not null " +
			" and r.dataTancament <= :ara " +
			" and r.arxiuTancat = false" +
			" and r.arxiuTancatError = false " +
		    " order by r.dataTancament asc")
	List<RegistreEntity> findPendentsTancarArxiu(
			@Param("ara") Date ara);

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
	
	/** Consulta pel datatable del registreuser */
	@Query(	"select r " +
			"from " +
			"    RegistreEntity r " +
			"		left outer join r.darrerMoviment.remitent as remitent "	+
			"where " +
			"	 (r.pare.id in (:bustiesIds)) " +
			"and (:esNullContingutDescripcio = true or lower(r.nom) like lower('%'||:contingutDescripcio||'%')) " +
			"and (:esNumeroOrigen = true or lower(r.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or r.data >= :dataInici) " +
			"and (:esNullDataFi = true or r.data < :dataFi) " +
			"and (:esProcessat = false or r.pendent = false) " +
			"and (:esPendent = false or r.pendent = true) " +
			"and (:esNullEnviatPerEmail = true or r.enviatPerEmail = :enviatPerEmail) " +
			"and (:esNullInteressat = true " +
			"		or (select count(interessat) " +
			"			from r.interessats as interessat" +
			"			where " +
			"				(lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%') " + 
			"					or lower(interessat.raoSocial) like lower('%'||:interessat||'%'))" +
			"			) > 0 )")
	public Page<RegistreEntity> findRegistreByPareAndFiltre(
			@Param("bustiesIds") List<Long> bustiesIds,
			@Param("esNullContingutDescripcio") boolean esNullContingutDescripcio,
			@Param("contingutDescripcio") String contingutDescripcio,
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
			Pageable pageable);

}
