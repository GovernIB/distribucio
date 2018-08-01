/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReglaRepository extends JpaRepository<ReglaEntity, Long> {
	
	
	List<ReglaEntity> findByEntitatAndUnitatCodi(EntitatEntity entitat,String unitatCodi);

	List<ReglaEntity> findByEntitatOrderByOrdreAsc(EntitatEntity entitat);
	List<ReglaEntity> findByEntitatAndActivaTrueOrderByOrdreAsc(EntitatEntity entitat);

	@Query(	"from " +
			"    ReglaEntity reg " +
			"where " +
			"    reg.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(reg.nom) like lower('%'||:filtre||'%') or lower(reg.assumpteCodi) like lower('%'||:filtre||'%') or lower(reg.unitatCodi) like lower('%'||:filtre||'%')) ")
	Page<ReglaEntity> findByEntitatAndFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre,		
			Pageable pageable);

	int countByEntitat(EntitatEntity entitat);

}
