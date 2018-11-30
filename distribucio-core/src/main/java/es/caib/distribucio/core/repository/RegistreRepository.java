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
	
	/** Consulta les anotacions de registre pendents de processar amb regles que no
	 * siguin de tipus backoffice sistra.
	 * @return
	 */
	@Query("from RegistreEntity r " +
		    "where r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.PENDENT " +
			"	and r.regla is not null " +
		    "	and (r.regla.backofficeTipus is null or r.regla.backofficeTipus <> es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto.SISTRA) " +
		    "order by r.data asc")
	List<RegistreEntity> findAmbReglaPendentProcessar();
	
	/** Consulta les anotacions de registre pendents de distribuïr
	 * que s'han rebut anteriorment via WS
	 * @return
	 */
	@Query("from RegistreEntity r " +
			"where ((r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.PENDENT and r.procesData is null) " +
			" or (r.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.ERROR and (r.procesIntents is null or r.procesIntents <= :maxReintents))) " +
			" and r.procesEstatSistra is null" +
		    " order by r.data asc")
	List<RegistreEntity> findPendentsDistribuir(
			@Param("maxReintents") int maxReintents);
	
	/** Consulta les anotacions de registre pendents de processar amb regles per a tipus backoffice sistra que
	 * estiguin en estat pendent o error de sistra i que no hagin superat el nombre de reintents.
	 * @return
	 */
	@Query("from RegistreEntity r " +
		    "where r.regla.backofficeTipus = es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto.SISTRA " +
		    "	and r.procesEstatSistra in ( es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum.PENDENT, " +
		    "						   es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum.ERROR) " +
			"	and (r.regla.backofficeIntents is null or  r.procesIntents < r.regla.backofficeIntents) " +
		    "order by r.data asc")
	List<RegistreEntity> findAmbReglaPendentProcessarBackofficeSistra();

	
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
			"where r.regla.backofficeTipus = es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto.SISTRA " +
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
			"	and (:esNullUnitatOrganitzativa = true or r.unitatAdministrativa = :unitatOrganitzativa) " +
			"   and (:esNullBustia = true or r.pare.id = :bustia) " +
			"	and (:esNullDataInici = true or r.data >= :dataInici) " +
			"	and (:esNullDataFi = true or r.data <= :dataFi) " +
			"	and (:esNullProcesEstat = true or r.procesEstat = :procesEstat)")
	public Page<RegistreEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullUnitatOrganitzativa") boolean esNullUnitatOrganitzativa,
			@Param("unitatOrganitzativa") String unitatOrganitzativa,
			@Param("esNullBustia") boolean esNullBustia,
			@Param("bustia") Long bustia,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullProcesEstat") boolean esNullProcesEstat, 
			@Param("procesEstat") RegistreProcesEstatEnum procesEstat,
			Pageable pageable);
	
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
	
}
