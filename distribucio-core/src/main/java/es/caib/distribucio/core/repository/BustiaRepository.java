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
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaRepository extends JpaRepository<BustiaEntity, Long> {

	List<BustiaEntity> findByEntitatAndPareNotNull(EntitatEntity entitat);
		
	List<BustiaEntity> findByEntitatAndActivaTrueAndPareNotNull(EntitatEntity entitat);
	
	/**
	 * Finds all the busties of given unitat except root bustia
	 * @param entitat
	 * @param unitatOrganitzativa
	 * @return
	 */
	List<BustiaEntity> findByEntitatAndUnitatOrganitzativaAndPareNotNull(
	EntitatEntity entitat,
	UnitatOrganitzativaEntity unitatOrganitzativa);
	
	List<BustiaEntity> findByEntitatAndUnitatOrganitzativa(
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

	List<BustiaEntity> findByEntitatAndUnitatOrganitzativaAndPerDefecteTrue(
			EntitatEntity entitat,
			UnitatOrganitzativaEntity unitatOrganitzativa);
	
	List<BustiaEntity> findByEntitatAndPerDefecteTrue(
			EntitatEntity entitat);

	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('% '||:filtreNom||' %')) ")
	List<BustiaEntity> findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom);
	
	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " +
			"and (:esNullFiltreEstat = true or b.unitatOrganitzativa.estat = 'E' or b.unitatOrganitzativa.estat = 'A' or b.unitatOrganitzativa.estat = 'T')" + 
			"and (:esNullPerDefecte = true or b.perDefecte = true)" + 
			"and (:esNullActiva = true or b.activa = true)")
	List<BustiaEntity> findByEntitatAndUnitatAndBustiaNomAndUnitatObsoletaAndPareNotNullFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("esNullPerDefecte") boolean esNullPerDefecte,
			@Param("esNullActiva") boolean esNullActiva);
	

	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) "
			+ "and (:esNullFiltreEstat = true or b.unitatOrganitzativa.estat = 'E' or b.unitatOrganitzativa.estat = 'A' or b.unitatOrganitzativa.estat = 'T')")
	Page<BustiaEntity> findByEntitatAndUnitatAndBustiaNomFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,	
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			Pageable pageable);
	
	@Query(	"from " +
			"    BustiaEntity b " +
			"where " +
			"    b.entitat = :entitat " +
			"and b.pare != null "+
			"and (:esNullFiltreUnitat = true or b.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(b.nom) like lower('%'||:filtreNom||'%')) " + 
			"and (:esNullFiltreEstat = true or b.unitatOrganitzativa.estat = 'E' or b.unitatOrganitzativa.estat = 'A' or b.unitatOrganitzativa.estat = 'T')" + 
			"and (:esNullPerDefecte = true or b.perDefecte = true)" + 
			"and (:esNullActiva = true or b.activa = true)")
	Page<BustiaEntity> findByEntitatAndUnitatAndBustiaNomAndPareNotNullFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,	
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			@Param("esNullPerDefecte") boolean esNullPerDefecte,
			@Param("esNullActiva") boolean esNullActiva,
			Pageable pageable);
}
