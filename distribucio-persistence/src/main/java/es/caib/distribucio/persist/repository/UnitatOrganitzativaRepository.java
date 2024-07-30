/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

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
			"and (:esFiltreUnitatSuperiorBuit = true or uo.codi in (:codisUnitatsDecendants)) " + 
			"and (:esNullEstat = true or uo.estat = :estat) ")
	Page<UnitatOrganitzativaEntity> findByFiltrePaginat(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("codi") String codi, 
			@Param("esNullFiltreDenominacio") boolean esNullFiltreDenominacio,
			@Param("denominacio") String denominacio,	
			@Param("esFiltreUnitatSuperiorBuit") boolean esFiltreUnitatSuperiorBuit,
			@Param("codisUnitatsDecendants") List<String> codisUnitatsDecendants,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,
			Pageable pageable);

	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:esNullFiltreCodi = true or lower(uo.codi) like lower('%'||:codi||'%')) " +
			"and (:esNullFiltreDenominacio = true or lower(uo.denominacio) like lower('%'||:denominacio||'%')) " + 
			"and (:esFiltreUnitatSuperiorBuit = true or " + 
			"		(uo.codi in (:llista1) or uo.codi in (:llista2) or uo.codi in (:llista3) or uo.codi in (:llista4) or uo.codi in (:llista5)) )" +
			"and (:esNullEstat = true or uo.estat = :estat) ")
	Page<UnitatOrganitzativaEntity> findByFiltrePaginatAmbLlistes(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("codi") String codi, 
			@Param("esNullFiltreDenominacio") boolean esNullFiltreDenominacio,
			@Param("denominacio") String denominacio,	
			@Param("esFiltreUnitatSuperiorBuit") boolean esFiltreUnitatSuperiorBuit,
			@Param("llista1") List<String> llista1,
			@Param("llista2") List<String> llista2,
			@Param("llista3") List<String> llista3,
			@Param("llista4") List<String> llista4,
			@Param("llista5") List<String> llista5,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") String estat,
			Pageable pageable);

	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) ")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAndCodiAndDenominacioFiltre(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);

	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"(:esNullFiltreCodi = true or lower(uo.codi) like lower('%'||:filtre||'%') " +
			"or (:esNullFiltreCodi = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) ")
	List<UnitatOrganitzativaEntity> findByCodiAndDenominacioFiltre(
			@Param("esNullFiltreCodi") boolean esNullFiltreCodi,
			@Param("filtre") String filtre);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			"and codiUnitatSuperior = :codiUnitatSuperior")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltre(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			 "and uo.id in (select distinct b.unitatOrganitzativa.id from BustiaEntity b)")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAndCodiAndDenominacioFiltreNomesAmbBusties(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			"and uo.id in (select distinct b.unitatOrganitzativa.id from BustiaEntity b)" +
			"and codiUnitatSuperior = :codiUnitatSuperior")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAmbCodiUnitatSuperiorAndCodiAndDenominacioFiltreNomesAmbBusties(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("codiUnitatSuperior") String codiUnitatSuperior,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel);
	
	
	
	UnitatOrganitzativaEntity findByCodi(String codi);
	
	List<UnitatOrganitzativaEntity> findByCodiUnitatArrel(String codiDir3Entitat);
	
	List<UnitatOrganitzativaEntity> findByCodiDir3Entitat(String codiDir3Entitat);
	
	List<UnitatOrganitzativaEntity> findByCodiDir3EntitatOrderByDenominacioAsc(String codiDir3Entitat);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and uo.estat!='V' ")
	List<UnitatOrganitzativaEntity> findByCodiDir3EntitatAndEstatNotV(
			@Param("codiDir3Entitat") String codiDir3Entitat);
	
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and uo.estat='V' ")
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
			"	and b.pare != null " +
			"	and (:esNullFiltre = true or lower(b.unitatOrganitzativa.codi) like lower('%'||:filtre||'%') " +
			"		or lower(b.unitatOrganitzativa.denominacio) like lower('%'||:filtre||'%')) ")
	List<UnitatOrganitzativaEntity> findUnitatsSuperiors(
			@Param("entitatId") Long entitatId,
			@Param("esNullFiltre") boolean esNullFiltre, 
			@Param("filtre") String filtre);
	
	/** Cerca les unitats de la llista de codis que tenen bústia. Serveix per obtenir
	 * la llista d'unitats descendents amb bústies.
	 * 
	 * @param codiDir3Entitat
	 * @param codisUnitatsDecendants
	 * @return
	 */
	@Query(	"select bustia.unitatOrganitzativa.id " +
			"from " +
			"    BustiaEntity bustia " +
			"where " +
			" 	bustia.entitat = :entitat " +
			" 	and bustia.pare is not null " +
			"   and bustia.unitatOrganitzativa.codi in (:codisUO)")
	List<Long> findUnitatsIdsAmbBustiaPerCodis(
			@Param("entitat") EntitatEntity entitat,
			@Param("codisUO") List<String> codisUO);

	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			":isNullUoSuperior = true or uo.codiUnitatSuperior like :uoSuperior ")
	List<UnitatOrganitzativaEntity> findByCodiUnitatSuperior(
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior);

	@Query("from UnitatOrganitzativaEntity uo "
			+ "where "
			+ "(:isNullUoSuperior = true or uo.codiUnitatSuperior like :uoSuperior) "
			)
	List<UnitatOrganitzativaEntity> findPerCodiUnitatSuperior(
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior);
	
	@Query(	"from " +
			"    UnitatOrganitzativaEntity uo " +
			"where " +
			"    uo.codiDir3Entitat = :codiDir3Entitat " +
			"and (:ambArrel = true or uo.codi != :codiDir3Entitat) " +
			"and ((:esNullFiltre = true or lower(uo.codi) like lower('%'||:filtre||'%')) " +
			"or (:esNullFiltre = true or lower(uo.denominacio) like lower('%'||:filtre||'%'))) " +
			"and uo.id in (select distinct b.unitatOrganitzativa.id from BustiaEntity b" + 
			"				where b.id in (:bustiesPermesesIds))")
	List<UnitatOrganitzativaEntity> findByCodiDir3UnitatAndCodiAndDenominacioAndBustiesPermesesFiltre(
			@Param("codiDir3Entitat") String codiDir3Entitat,
			@Param("esNullFiltre") boolean esNullFiltreCodi,
			@Param("filtre") String filtre,
			@Param("ambArrel") boolean ambArrel,
			@Param("bustiesPermesesIds") List<Long> bustiesPermesesIds);

}
