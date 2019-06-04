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

import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;

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
	
	
	
	@Query(	"from " +
			"    ReglaEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"and (:esNullFiltreUnitat = true or r.unitatOrganitzativa = :unitatOrganitzativa) " +
			"and (:esNullFiltreNom = true or lower(r.nom) like lower('%'||:filtreNom||'%')) " + 
			"and (:esNullFiltreTipus = true or r.tipus = :filtreTipus) " + 			
			"and (:esNullFiltreEstat = true or r.unitatOrganitzativa.estat = 'E' or r.unitatOrganitzativa.estat = 'A' or r.unitatOrganitzativa.estat = 'T')")
	Page<BustiaEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativa") UnitatOrganitzativaEntity unitatOrganitzativa, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esNullFiltreTipus") boolean esNullFiltreTipus,
			@Param("filtreTipus") ReglaTipusEnumDto filtreTipus,			
			@Param("esNullFiltreEstat") boolean esNullFiltreEstat,
			Pageable pageable);
	
	
	List<ReglaEntity> findByBustia(BustiaEntity bustia);

	/** Mètode per trobar les regles aplicables per entitat i per codi procediment i codi assumpte.
	 * 
	 * @param entitat
	 * @param b
	 * @param procedimentCodi
	 * @param c
	 * @param assumpteCodi
	 * @return
	 */
	@Query(	"from " +
			"    ReglaEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"and r.activa = true " + 
			"and (r.procedimentCodi is null or r.procedimentCodi = :procedimentCodi) " +
			"and (r.assumpteCodi is null or r.assumpteCodi = :assumpteCodi) " + 
			"order by r.ordre asc")
	List<ReglaEntity> findAplicables(
			@Param("entitat") EntitatEntity entitat, 
			@Param("procedimentCodi") String procedimentCodi, 
			@Param("assumpteCodi") String assumpteCodi);

}
