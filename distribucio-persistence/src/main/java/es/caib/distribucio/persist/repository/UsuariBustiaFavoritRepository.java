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

import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.UsuariBustiaFavoritEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;

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
	
	@Modifying
	@Query(value = "update dis_bustia_favorit " +
			"set usuari_codi = :codiNou " +
			"where usuari_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariCodi(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);
	
	@Modifying
	@Query(value = "update dis_bustia_favorit " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic ",
			nativeQuery = true)
	int updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

}
