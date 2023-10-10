/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutRepository extends JpaRepository<ContingutEntity, Long> {

	List<ContingutEntity> findByPareAndEsborrat(
			ContingutEntity pare,
			int esborrat,
			Sort sort);

	List<ContingutEntity> findByPareAndNomOrderByEsborratAsc(
			ContingutEntity pare,
			String nom);

	ContingutEntity findByPareAndNomAndEsborrat(
			ContingutEntity pare,
			String nom,
			int esborrat);
	
	List<ContingutEntity> findByPare(
			ContingutEntity pare);

	@Query(	"select " +
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and (c.pare is not null or type(c) <> es.caib.distribucio.persist.entity.BustiaEntity) " +
			"and (:tipusBustia = true or type(c) <> es.caib.distribucio.persist.entity.BustiaEntity) " +
			"and (:tipusRegistre = true or type(c) <> es.caib.distribucio.persist.entity.RegistreEntity) " +
			"and (:esNullNom = true or lower(c.nom) like lower('%'||:nom||'%')) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and ((:mostrarEsborrats = true and c.esborrat > 0) or (:mostrarNoEsborrats = true and c.esborrat = 0)) ")
	public Page<ContingutEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("tipusBustia") boolean tipusBustia,
			@Param("tipusRegistre") boolean tipusRegistre,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("mostrarEsborrats") boolean mostrarEsborrats,
			@Param("mostrarNoEsborrats") boolean mostrarNoEsborrats,
			Pageable pageable);
	
	@Query(	"select " +
			"    count(c) " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"(c.pare in (:pares)) " +
			"and (c.pendent=true)")
	public long countPendentsByPares(
			@Param("pares") List<? extends ContingutEntity> pares);
	
	
	@Query(	"select " +
			"    c.pare.id, " +
			"    count(*) " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.pare in (:pares) " +
			"and c.esborrat = 0 " +
			" and (type(c) != es.caib.distribucio.persist.entity.RegistreEntity or (c.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT)) " +
			"group by " +
			"    c.pare")
	List<Object[]> countByPares(
			@Param("entitat") EntitatEntity entitat,
			@Param("pares") List<? extends ContingutEntity> pares);
	
	
	@Query(	"select " +
			"    c.pare.id, " +
			"    count(*) " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    c.entitat = :entitat " +
			"and (c.pare in (:llista1) or c.pare in (:llista2) or c.pare in (:llista3) or c.pare in (:llista4) or c.pare in (:llista5) )" +
			"and c.esborrat = 0 " +
			" and (type(c) != es.caib.distribucio.persist.entity.RegistreEntity or (c.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT)) " +
			"group by " +
			"    c.pare")
	List<Object[]> countByParesAmbLlistes(
			@Param("entitat") EntitatEntity entitat,
			@Param("llista1") List<? extends ContingutEntity> llista1,
			@Param("llista2") List<? extends ContingutEntity> llista2,
			@Param("llista3") List<? extends ContingutEntity> llista3,
			@Param("llista4") List<? extends ContingutEntity> llista4,
			@Param("llista5") List<? extends ContingutEntity> llista5);

	@Query("select " +
			"   c " +
			"from " +
			"   ContingutEntity c " +
			"where " +
			"   type(c) = es.caib.distribucio.persist.entity.RegistreEntity " +
			"AND " +
			"   c.pare.id = :pareId")
	List<ContingutEntity> findRegistresByPareId(
			@Param("pareId") Long pareId);
	
}