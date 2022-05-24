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
import es.caib.distribucio.core.entity.UsuariBustiaFavoritEntity;
import es.caib.distribucio.core.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus usuari-bústia-favorit
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariBustiaFavoritRepository extends JpaRepository<UsuariBustiaFavoritEntity, Long> {
	public UsuariBustiaFavoritEntity findByBustiaAndUsuari(BustiaEntity bustia, UsuariEntity usuari);
	public Page<UsuariBustiaFavoritEntity> findByUsuari(UsuariEntity usuari, Pageable pageable);
	
	@Query(	"select " +
			"    ub.id " +
			"from UsuariBustiaFavoritEntity ub " +
			"where ub.bustia = :bustia " +
			"and ub.usuari = :usuari")
	public Long findIdByBustiaAndUsuari(
			@Param("bustia") BustiaEntity bustia,
			@Param("usuari") UsuariEntity usuari);
	
	public List<UsuariBustiaFavoritEntity> findByUsuari(UsuariEntity usuari);
	
	
	@Query("from UsuariBustiaFavoritEntity ub " + 
			"where ub.bustia.id = :bustia")
	public List<UsuariBustiaFavoritEntity> findByBustia (
			@Param("bustia") long bustia);
}
