/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutLogEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutLog.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutLogRepository extends JpaRepository<ContingutLogEntity, Long> {

	List<ContingutLogEntity> findByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	List<ContingutLogEntity> findByPareInOrderByCreatedDateAsc(
			List<ContingutLogEntity> pares);

	List<ContingutLogEntity> findByContingutMovimentInOrderByCreatedDateAsc(
			List<ContingutMovimentEntity> moviments);
	

	/** Consulta per treure la informació d'un registre filtrant per data, tipus i usuari. Per cada 
	 * resultat retorna un Ojbect[] {ContingutLogEntity log, RegistreEntity registre, BustiaEntity origen, BustiaEntity destí } */
	@Query(	"select l, r, bOrigen, bDesti " + 
			"from  	ContingutLogEntity l, " +
			"		RegistreEntity r, " +
			"		BustiaEntity bOrigen, " +
			"		BustiaEntity bDesti " +
			"where " +
			"		(l.contingut.id = r.id) " +
			"	and (l.contingut.tipus = es.caib.distribucio.core.api.dto.ContingutTipusEnumDto.REGISTRE) " +
			"	and ((l.contingutMoviment is null and bOrigen.id = r.pare.id) " + 
			"			or (bOrigen.id = l.contingutMoviment.origenId)) " + // bústia origen
			"	and ((l.contingutMoviment is null and bDesti.id = r.pare.id) " + 
			" 			or (bDesti.id = l.contingutMoviment.destiId)) " + // bústia destí
			" 	and (l.createdDate between :dataInici and :dataFi) " +
			" and (:isNullTipus = true or l.tipus = :tipus) " +
			" and (:isNullUsuari = true or l.createdBy.codi like :usuari) " +
			" and (:isNullAnotacioId = true or r.id = :anotacioId) " +
			" and (:isNullAnotacioEstat = true or r.procesEstat like :anotacioEstat) " +
			" and (:isNullAnotacioError = true " + 
			"			or (:anotacioError = true and r.procesError != null) " + 
			"			or (:anotacioError = false and r.procesError is null)) " +
			" and (:isNullPendent = true or r.pendent = :pendent ) " +
			" and (:isNullBustiaOrigen = true or bOrigen.id = :bustiaOrigen) " +
			" and (:isNullBustiaDesti = true or bDesti.id = :bustiaDesti) " +
			" and (:isNullUoOrigen = true or bOrigen.unitatOrganitzativa.codi like :uoOrigen) " +
			" and (:isCodisUoSuperiorsOrigenEmpty = true or bOrigen.unitatOrganitzativa.codi in (:codisUoSuperiorsOrigen)) " +
			" and (:isNullUoDesti = true or bDesti.unitatOrganitzativa.codi like :uoDesti) " +
			" and (:isCodisUoSuperiorsDestiEmpty = true or bDesti.unitatOrganitzativa.codi in (:codisUoSuperiorsDesti)) " +
			"order by l.createdDate asc "
			)
	List<Object[]> findLogsPerDadesObertes(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi,
			@Param("isNullTipus") boolean isNullTipus,
			@Param("tipus") LogTipusEnumDto tipus, 
			@Param("isNullUsuari") boolean isNullUsuari, 
			@Param("usuari") String usuari, 
			@Param("isNullAnotacioId") boolean isNullAnotacioId, 
			@Param("anotacioId") long anotacioId, 
			@Param("isNullAnotacioEstat") boolean isNullAnotacioEstat, 
			@Param("anotacioEstat") RegistreProcesEstatEnum anotacioEstat,
			@Param("isNullAnotacioError") boolean isNullAnotacioError, 
 			@Param("anotacioError") Boolean anotacioError,
			@Param("isNullPendent") boolean isNullPendent,
			@Param("pendent") Boolean pendent, 
			@Param("isNullBustiaOrigen") boolean isNullBustiaOrigen, 
			@Param("bustiaOrigen") long bustiaOrigen, 
			@Param("isNullBustiaDesti") boolean isNullBustiaDesti, 
			@Param("bustiaDesti") long bustiaDesti,
			@Param("isNullUoOrigen") boolean isNullUoOrigen, 
			@Param("uoOrigen") String uoOrigen,
			@Param("isCodisUoSuperiorsOrigenEmpty") boolean isCodisUoSuperiorsOrigenEmpty, 
			@Param("codisUoSuperiorsOrigen") List<String> codisUoSuperiorsOrigen, 
			@Param("isNullUoDesti") boolean isNullUoDesti, 
			@Param("uoDesti") String uoDesti, 
			@Param("isCodisUoSuperiorsDestiEmpty") boolean isCodisUoSuperiorsDestiEmpty, 
			@Param("codisUoSuperiorsDesti") List<String> codisUoSuperiorsDesti
			);
	
}
