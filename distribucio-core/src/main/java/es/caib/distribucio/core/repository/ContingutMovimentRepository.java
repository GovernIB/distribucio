/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus ContingutMoviment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutMovimentRepository extends JpaRepository<ContingutMovimentEntity, Long> {

	List<ContingutMovimentEntity> findByContingutOrderByCreatedDateAsc(
			ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenNull(ContingutEntity contingut);
	
	List<ContingutMovimentEntity> findByContingutAndOrigenNotNullOrderByCreatedDateAsc(ContingutEntity contingut);

	List<ContingutMovimentEntity> findByContingutAndOrigenNotNullOrderByCreatedDateDesc(ContingutEntity contingut);
	
	@Query( "select distinct mv.origen.id " + 
			"from ContingutMovimentEntity mv " + 
			"where mv.contingut.id in (:registresIds)")
	List<Long> findBustiesOrigenByRegistres(@Param("registresIds") List<Long> registresIds);
}
