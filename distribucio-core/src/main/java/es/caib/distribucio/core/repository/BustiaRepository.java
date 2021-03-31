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

import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;

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
}
