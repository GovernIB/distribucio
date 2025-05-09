/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaRepository extends JpaRepository<BustiaEntity, Long> {

	List<BustiaEntity> findByEntitatAndPareNotNullOrderByNomAsc(EntitatEntity entitat);
		
	List<BustiaEntity> findByEntitatAndActivaTrueAndPareNotNullOrderByNomAsc(EntitatEntity entitat);
	
	/**
	 * Finds all the busties of given unitat except root bustia
	 * @param entitat
	 * @param unitatOrganitzativa
	 * @return
	 */
	List<BustiaEntity> findByEntitatAndUnitatOrganitzativaAndPareNotNull(
	EntitatEntity entitat,
	UnitatOrganitzativaEntity unitatOrganitzativa);
	
	/**
	 * Finds all the enabled busties of given unitat except root bustia
	 * @param entitat
	 * @param unitatOrganitzativa
	 * @return
	 */
	List<BustiaEntity> findByEntitatAndUnitatOrganitzativaAndActivaTrueAndPareNotNull(
	EntitatEntity entitat,
	UnitatOrganitzativaEntity unitatOrganitzativa);
	
	
	Long countByEntitatAndUnitatOrganitzativaAndPareNotNull(
		    EntitatEntity entitat,
		    UnitatOrganitzativaEntity unitatOrganitzativa);

	
	/**
	 * Finds root bustia of unitat
	 * @param entitat
	 * @param unitatOrganitzativa
	 * @return
	 */
	BustiaEntity findByEntitatAndUnitatOrganitzativaAndPareNull(
			EntitatEntity entitat,
			UnitatOrganitzativaEntity unitatOrganitzativa);

	/** Busca bústies per defecte per una entitat i una unitat organitzativa
	 * 
	 * @param entitat
	 * @param unitatOrganitzativa
	 * @return
	 */
	@Query("from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and b.unitatOrganitzativa = :unitatOrganitzativa " +
			"and b.activa = true " +
			"and b.perDefecte = true ")
	List<BustiaEntity> findPerDefecte(
			@Param("entitat") EntitatEntity entitat,
			@Param("unitatOrganitzativa")  UnitatOrganitzativaEntity unitatOrganitzativa);

		
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " +
			"and (:esCodisUnitatsSuperiorsBuida = true or b.unitatOrganitzativa.codi in (:codisUnitatsSuperiors)) " + 
			"and (:esNullFiltreEstat = true or (b.unitatOrganitzativa.estat = 'E') or (b.unitatOrganitzativa.estat = 'A') or (b.unitatOrganitzativa.estat = 'T')) " +
			"and (:esNullPerDefecte = true or b.perDefecte = true) " + 
			"and (:esNullActiva = true or b.activa = true) " +
			"order by b.nom asc "
			)
	List<BustiaEntity> findAmbEntitatAndFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esCodisUnitatsSuperiorsBuida") boolean esCodisUnitatsSuperiorsBuida,
			@Param("codisUnitatsSuperiors") List<String> codisUnitatsSuperiors,
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("esNullPerDefecte") boolean esNullPerDefecte,
			@Param("esNullActiva") boolean esNullActiva);

		
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " +
			"and (:esCodisUnitatsSuperiorsBuida = true or " + 
				"(b.unitatOrganitzativa.codi in (:llista1) or b.unitatOrganitzativa.codi in (:llista2) or b.unitatOrganitzativa.codi in (:llista3) or b.unitatOrganitzativa.codi in (:llista4) or b.unitatOrganitzativa.codi in (:llista5)) )" +
			"and (:esNullFiltreEstat = true or (b.unitatOrganitzativa.estat = 'E') or (b.unitatOrganitzativa.estat = 'A') or (b.unitatOrganitzativa.estat = 'T')) " +
			"and (:esNullPerDefecte = true or b.perDefecte = true) " + 
			"and (:esNullActiva = true or b.activa = true) " +
			"order by b.nom asc "
			)
	List<BustiaEntity> findAmbEntitatAndFiltreAmbLlistes(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esCodisUnitatsSuperiorsBuida") boolean esCodisUnitatsSuperiorsBuida,
			@Param("llista1") List<String> llista1,
			@Param("llista2") List<String> llista2,
			@Param("llista3") List<String> llista3,
			@Param("llista4") List<String> llista4,
			@Param("llista5") List<String> llista5,
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("esNullPerDefecte") boolean esNullPerDefecte,
			@Param("esNullActiva") boolean esNullActiva);
	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " +
			"and (:esCodisUnitatsSuperiorsBuida = true or b.unitatOrganitzativa.codi in (:codisUnitatsSuperiors)) " +
			"and (:esNullFiltreEstat = true or (b.unitatOrganitzativa.estat like 'E') or (b.unitatOrganitzativa.estat = 'A') or (b.unitatOrganitzativa.estat = 'T'))" +
			"and (:perDefecte = false or b.perDefecte = true) " +
			"and (:activa = false or b.activa = true)")
	Page<BustiaEntity> findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esCodisUnitatsSuperiorsBuida") boolean esCodisUnitatsSuperiorsBuida,
			@Param("codisUnitatsSuperiors") List<String> codisUnitatsSuperiors,
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("perDefecte") boolean perDefecte,
			@Param("activa") boolean activa,
			Pageable pageable);
	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " +
			"and (:esCodisUnitatsSuperiorsBuida = true or " +
				"(b.unitatOrganitzativa.codi in (:llista1) or b.unitatOrganitzativa.codi in (:llista2) or b.unitatOrganitzativa.codi in (:llista3) or b.unitatOrganitzativa.codi in (:llista4) or b.unitatOrganitzativa.codi in (:llista5)) )" +
			"and (:esNullFiltreEstat = true or (b.unitatOrganitzativa.estat like 'E') or (b.unitatOrganitzativa.estat = 'A') or (b.unitatOrganitzativa.estat = 'T'))" +
			"and (:perDefecte = false or b.perDefecte = true) " +
			"and (:activa = false or b.activa = true)")
	Page<BustiaEntity> findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltrePaginatAmbLlistes(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esCodisUnitatsSuperiorsBuida") boolean esCodisUnitatsSuperiorsBuida,
			@Param("llista1") List<String> llista1,
			@Param("llista2") List<String> llista2,
			@Param("llista3") List<String> llista3,
			@Param("llista4") List<String> llista4,
			@Param("llista5") List<String> llista5,
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("perDefecte") boolean perDefecte,
			@Param("activa") boolean activa,
			Pageable pageable);

	@Query("from BustiaEntity b " +
			"where " +
			" b.activa = true " +
			"and b.pare != null " +
			"and (:isNullBustiaId = true or b.id like :bustiaId) " + 
			"and (:isNullUo = true or b.unitatOrganitzativa = :uo) " +
			"and (:esCodisUnitatsSuperiorsBuida = true or b.unitatOrganitzativa.codi in (:codisUnitatsSuperiors)) "
			)
	List<BustiaEntity> findBustiesPerDadesObertes(
			@Param("isNullBustiaId") boolean isNullBustiaId, 
			@Param("bustiaId") long bustiaId, 
			@Param("isNullUo") boolean isNullUo,
			@Param("uo") UnitatOrganitzativaEntity uo, 
			@Param("esCodisUnitatsSuperiorsBuida") boolean esCodisUnitatsSuperiorsBuida,
			@Param("codisUnitatsSuperiors") List<String> codisUnitatsSuperiors);	


	@Query("from BustiaEntity b "
			+ "where " + 
			"(:isNullBustiaId = true or b.id like :bustiaId) " + 
			"and (:isNullUo = true or b.unitatOrganitzativa.codi like :uo) " + 
			"and (:isNullUoSuperior = true or b.unitatOrganitzativa.codiUnitatSuperior like :uoSuperior) "
			)
	List<BustiaEntity> findBustiesUsuarisPerDadesObertes(
			@Param("isNullBustiaId") boolean isNullBustiaId, 
			@Param("bustiaId") long bustiaId, 
			@Param("isNullUo") boolean isNullUo, 
			@Param("uo") String uo, 
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior);


	@Query("select b from BustiaEntity b, "
			+ "UnitatOrganitzativaEntity uo "
			+ "where "
			+ "(b.unitatOrganitzativa.id = uo.id) " + 
			"and (:isNullUoSuperior = true or uo.codi like :uoSuperior) "
			)
	List<BustiaEntity> findBustiesPerUnitatSuperior(
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior);


	@Query("select b from BustiaEntity b, "
			+ "UnitatOrganitzativaEntity uo "
			+ "where "
			+ "(b.unitatOrganitzativa.id = uo.id) " + 
			"and (:isNullUoSuperior = true or uo.codiUnitatSuperior like :uoSuperior) "
			)
	List<BustiaEntity> findBustiesPerCodiUnitatSuperior(
			@Param("isNullUoSuperior") boolean isNullUoSuperior, 
			@Param("uoSuperior") String uoSuperior);


	@Query("from BustiaEntity b "
			+ "where " 
			+ ":isNullIdUnitatOrganitzativa = true or b.unitatOrganitzativa.id like :idUnitatOrganitzativa"
			)
	List<BustiaEntity> findBustiesPerIdUnitatOrganitzativa(
			@Param("isNullIdUnitatOrganitzativa") boolean isNullIdUnitatOrganitzativa, 
			@Param("idUnitatOrganitzativa") Long idUnitatOrganitzativa);
	
	
	@Query(" select c from ContingutEntity c, "
			+ "BustiaEntity b "
			+ "where "
			+ "b.id = c.id "
			+ "and b.tipus = :tipus "
			+ "and c.entitat.id like :entitatId "
			+ "and (:isNullFiltre = true or lower(c.nom) like lower('%'||:filtre||'%')) "
			)
	List<ContingutEntity> findAmbEntitatAndFiltreInput(
			@Param("entitatId") Long entitatId, 
			@Param("tipus") ContingutTipusEnumDto tipus, 
			@Param("isNullFiltre") boolean isNullFiltre, 
			@Param("filtre") String filtre);

}
