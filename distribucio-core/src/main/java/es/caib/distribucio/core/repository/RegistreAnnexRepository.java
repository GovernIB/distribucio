/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {

	public RegistreAnnexEntity findByRegistreAndId(RegistreEntity registre, Long annexId);
	
	@Query("select distinct rae.registre.id from RegistreAnnexEntity rae " + 
			"where rae.registre.id in (select rae2.registre.id " + 
			"							from RegistreAnnexEntity rae2 " + 
			"							where rae2.titol <> 'justificant' " + 	
			"							group by rae2.registre.id " + 
			"							having count(*) between :nombreAnnexes and :nombreAnnexesTope) " )
	public List<Long> findByNombreAnnexes(
			@Param("nombreAnnexes") Long nombreAnnexes, 
			@Param("nombreAnnexesTope") Long nombreAnnexesTope);
}
