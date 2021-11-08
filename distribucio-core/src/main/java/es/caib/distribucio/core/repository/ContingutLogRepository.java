/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
