/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

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

}