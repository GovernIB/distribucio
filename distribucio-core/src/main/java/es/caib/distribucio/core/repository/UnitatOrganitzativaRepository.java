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

import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UnitatOrganitzativaRepository extends JpaRepository<UnitatOrganitzativaEntity, Long> {

	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:esNullFiltreCodi = true or lower(uo.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullFiltreDenominacio = true or lower(uo.denominacio) like lower('%'||:denominacio||'%')) ")
	Page<UnitatOrganitzativaEntity> findByCodiDir3AndUnitatDenominacioFiltrePaginat(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("codi") String codi, 
			@Param("esNullFiltreDenominacio") boolean esNullFiltreDenominacio,
			@Param("denominacio") String denominacio,		
			Pageable pageable);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) ")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAndCodiAndDenominacioFiltre(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre);
	
	
	UnitatOrganitzativaEntity findByCodi(String codi);
	
	List<UnitatOrganitzativaEntity> findByCodiUnitatArrel(String codiDir3Entitat);
	
	List<UnitatOrganitzativaEntity> findByCodiDir3Entitat(String codiDir3Entitat);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and uo.estat!='V') ")
	List<UnitatOrganitzativaEntity> findByCodiDir3EntitatAndEstatNotV(
			@Param("codiDir3Entitat") String codiDir3Entitat);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and uo.estat='V') ")
	List<UnitatOrganitzativaEntity> findByCodiDir3AndEstatV(
			@Param("codiDir3Entitat") String codiDir3Entitat);
	
	UnitatOrganitzativaEntity findByCodiDir3EntitatAndCodi(String codiDir3Entitat, String codi);
	
	
}
