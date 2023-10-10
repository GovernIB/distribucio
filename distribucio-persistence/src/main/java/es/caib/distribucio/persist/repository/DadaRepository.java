/**
 * 
 */
package es.caib.distribucio.persist.repository;

import es.caib.distribucio.persist.entity.DadaEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus dada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadaRepository extends JpaRepository<DadaEntity, Long> {

	List<DadaEntity> findByRegistreId(Long registreId);
	List<DadaEntity> findByRegistre(RegistreEntity registre);
	List<DadaEntity> findByRegistreAndMetaDadaOrderByOrdreAsc(RegistreEntity registre, MetaDadaEntity metaDada);
	List<DadaEntity> findByRegistreIdInOrderByRegistreIdAscMetaDadaCodiAsc(Collection<Long> registreIds);
	
	@Query(	"select" +
			"    distinct md " +
			"from" +
			"    DadaEntity d inner join d.metaDada md " +
			"where " +
			"    d.registre.id in (:registreIds) " +
			"order by " +
			"    md.codi asc ")
	List<MetaDadaEntity> findDistinctMetaDadaByRegistreIdInOrderByMetaDadaCodiAsc(
			@Param("registreIds") Collection<Long> registreIds);

}
