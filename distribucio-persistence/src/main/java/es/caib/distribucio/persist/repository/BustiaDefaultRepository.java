/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.BustiaDefaultEntity;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus bustia default
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaDefaultRepository extends JpaRepository<BustiaDefaultEntity, Long> {

	BustiaDefaultEntity findByEntitatAndUsuari(EntitatEntity entitat, UsuariEntity usuari);
	
	List<BustiaDefaultEntity> findByBustia(BustiaEntity bustai);

	@Modifying
	@Query(value = "update dis_bustia_default " +
			"set usuari = :codiNou where usuari = :codiAntic",
			nativeQuery = true)
	int updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
}
