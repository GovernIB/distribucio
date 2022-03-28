/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	

	/** Consulta per treure la informació d'un registre filtrant per data, tipus i usuari */
	@Query("select cl from "
			+ "ContingutLogEntity cl, "
			+ "RegistreEntity r, " 
			+ "BustiaEntity b, " 
			+ "UnitatOrganitzativaEntity uo "
			+ "where "
			+ "(cl.contingut.id = r.id) "
			+ "and (cl.contingutMoviment.origenId = b.id) "
			+ "and (b.unitatOrganitzativa.id = uo.id) "
			+ "and (cl.createdDate between :dataInici and :dataFi) " 
			+ "and (:isNullTipus = true or cl.contingut.tipus like :tipus) "
			+ "and (:isNullUsuari = true or cl.contingutMoviment.remitent.codi like :usuari) "
			+ "and (:isNullAnotacioId = true or r.id like :anotacioId) "
			+ "and (:isNullAnotacioEstat = true or r.procesEstat like :anotacioEstat) "
			+ "and (:isNullAnotacioError = true or r.arxiuTancatError like :anotacioError) "
			+ "and (:isNullPendent = true or r.pendent like :pendent) "
			+ "and (:isNullBustiaOrigen = true or cl.contingutMoviment.origenId like :bustiaOrigen) "
			+ "and (:isNullBustiaDesti = true or cl.contingutMoviment.destiId like :bustiaDesti) "
			+ "and (:isNullUoOrigen = true or uo.codi like :uoOrigen) "
			+ "and (:isNullUoSuperior = true or uo.codiUnitatSuperior like :uoSuperior) "
			+ "and (:isNullUoDesti = true or uo.codi like :uoDesti) "
			+ "and (:isNullUoDestiSuperior = true or uo.codiUnitatSuperior like :uoDestiSuperior) "
			)
	List<ContingutLogEntity> findLogsPerDadesObertes(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi, 
			@Param("isNullTipus") boolean isNullTipus,
			@Param("tipus") Object tipus, 
			@Param("isNullUsuari") boolean isNullUsuari, 
			@Param("usuari") String usuari, 
			@Param("isNullAnotacioId") boolean isNullAnotacioId, 
			@Param("anotacioId") long anotacioId, 
			@Param("isNullAnotacioEstat") boolean isNullAnotacioEstat, 
			@Param("anotacioEstat") Object anotacioEstat, 
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
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior, 
			@Param("isNullUoDesti") boolean isNullUoDesti, 
			@Param("uoDesti") String uoDesti, 
			@Param("isNullUoDestiSuperior") boolean isNullUoDestiSuperior, 
			@Param("uoDestiSuperior") String uoDestiSuperior
			);
	
}
