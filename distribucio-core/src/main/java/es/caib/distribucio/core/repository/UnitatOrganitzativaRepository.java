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
			"and (:esNullFiltreDenominacio = true or lower(uo.denominacio) like lower('%'||:denominacio||'%')) " + 
			"and (:esNullCodiUnitatSuperior = true or lower(uo.codiUnitatSuperior) like lower('%'||:codiUnitatSuperior||'%')) " + 
			"and (:esNullCodiUnitatArrel = true or lower(uo.codiUnitatArrel) like lower('%'||:codiUnitatArrel||'%')) " + 
			"and (:esNullEstat = true or uo.estat = :estat) ")
	Page<UnitatOrganitzativaEntity> findByCodiDir3AndUnitatDenominacioFiltrePaginat(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("codi") String codi, 
			@Param("esNullFiltreDenominacio") boolean esNullFiltreDenominacio,
			@Param("denominacio") String denominacio,	
			@Param("esNullCodiUnitatSuperior") boolean esNullCodiUnitatSuperior,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullCodiUnitatArrel") boolean esNullCodiUnitatArrel,
			@Param("codiUnitatArrel") String codiUnitatArrel,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,
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


	/** Retorna les unitats orgàniques que són superiors d'una 
	 * unitat orgànica amb bústia filtrat o no per codi o denominació.
	 * 
	 * @param entitatId per filtar per entitat.
	 * @param filtre Per filtrar o no per codi o denominació
	 * @return Llistat ordenat de les unitats orgàniques que són la superior de
	 * 		les unitats orgàniques de les bústies de l'entitat.
	 */
	@Query(	"select distinct b.unitatOrganitzativa " + 
			"from   BustiaEntity b " + 
			"where b.entitat.id = :entitatId " +
			"	and (:esNullFiltre = true or lower(b.unitatOrganitzativa.codi) like lower('%'||:filtre||'%') " +
			"		or lower(b.unitatOrganitzativa.denominacio) like lower('%'||:filtre||'%')) ")
	List<UnitatOrganitzativaEntity> findUnitatsSuperiors(
			@Param("entitatId") Long entitatId,
			@Param("esNullFiltre") boolean esNullFiltre, 
			@Param("filtre") String filtre);
			

	
	
}
