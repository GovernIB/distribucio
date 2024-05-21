/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.ContingutMovimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenIdNull(ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenIdNotNullOrderByCreatedDateAsc(ContingutEntity contingut);

	List<ContingutMovimentEntity> findByContingutAndOrigenIdNotNullOrderByCreatedDateDesc(ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndDestiId(ContingutEntity contingut, Long destiId);
}