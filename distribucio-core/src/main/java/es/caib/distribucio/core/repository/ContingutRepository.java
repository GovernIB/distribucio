/**
 * 
 */
package es.caib.distribucio.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;

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
			"and (c.pare is not null or type(c) <> es.caib.distribucio.core.entity.BustiaEntity) " +
			"and (:tipusBustia = true or type(c) <> es.caib.distribucio.core.entity.BustiaEntity) " +
			"and (:tipusRegistre = true or type(c) <> es.caib.distribucio.core.entity.RegistreEntity) " +
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
			"    c " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    (:esPareNull = true or c.pare = :pare) " +
			"and (:esPareNull = false or c.pare in (:pares)) " +
			"and (:esNullContingutDescripcio = true or lower(c.nom) like lower('%'||:contingutDescripcio||'%')) " +
			"and (:esNumeroOrigen = true or lower(c.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(c.darrerMoviment.remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or c.data >= :dataInici) " +
			"and (:esNullDataFi = true or c.data < :dataFi) " +
			"and (:esNullEstatSimple = true " +
			"		or (:isProcessat = false " +
			"				and (c.procesEstat in ('BUSTIA_PENDENT', 'ARXIU_PENDENT', 'REGLA_PENDENT'))) " +
			"		or (:isProcessat = true " +
			"				and (c.procesEstat in ('BUSTIA_PROCESSADA', 'BACK_PENDENT', 'BACK_REBUDA', 'BACK_PROCESSADA', 'BACK_REBUTJADA', 'BACK_ERROR')))) " +
			"and (:esNullInteressat = true " +
			"		or c.id in (" +
			"			select interessat.registre.id " +
			"			from RegistreInteressatEntity interessat " +	
			"			where interessat.representat is null " +
			"				and lower(interessat.documentNum||' '||interessat.nom||' '||interessat.llinatge1||' '||interessat.llinatge2) like lower('%'||:interessat||'%'))) ")
	public Page<ContingutEntity> findRegistreByPareAndFiltre(
			@Param("esPareNull") boolean esPareNull,
			@Param("pare") ContingutEntity pare,
			@Param("pares") List<? extends ContingutEntity> pares,
			@Param("esNullContingutDescripcio") boolean esNullContingutDescripcio,
			@Param("contingutDescripcio") String contingutDescripcio,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullRemitent") boolean esNullRemitent,
			@Param("remitent") String remitent,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstatSimple") boolean esNullEstatSimple,
			@Param("isProcessat") boolean isProcessat,
			@Param("esNullInteressat") boolean esNullInteressat,
			@Param("interessat") String interessat,
			Pageable pageable);
	
	@Query(	"select " +
			"    c.id " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"    (:esPareNull = true or c.pare = :pare) " +
			"and (:esPareNull = false or c.pare in (:pares)) " +
			"and (:esNullContingutDescripcio = true or lower(c.nom) like lower('%'||:contingutDescripcio||'%')) " +
			"and (:esNumeroOrigen = true or lower(c.numeroOrigen) like lower('%'||:numeroOrigen||'%')) " +
			"and (:esNullRemitent = true or lower(c.darrerMoviment.remitent.nom) like lower('%'||:remitent||'%')) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate < :dataFi) " +
			"and ((:esNullEstat = true and (c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT or c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.BUSTIA_PROCESSADA)) or (c.procesEstat = :estat))")
	public List<Long> findRegistreIdsByPareAndFiltre(
			@Param("esPareNull") boolean esPareNull,
			@Param("pare") ContingutEntity pare,
			@Param("pares") List<? extends ContingutEntity> pares,
			@Param("esNullContingutDescripcio") boolean esNullContingutDescripcio,
			@Param("contingutDescripcio") String contingutDescripcio,
			@Param("esNumeroOrigen") boolean esNumeroOrigen,
			@Param("numeroOrigen") String numeroOrigen,
			@Param("esNullRemitent") boolean esNullRemitent,
			@Param("remitent") String remitent,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") RegistreProcesEstatEnum estat);
	
	
	@Query(	"select " +
			"    count(c) " +
			"from " +
			"    ContingutEntity c " +
			"where " +
			"(c.pare in (:pares)) " +
			"and (c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT or c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.ARXIU_PENDENT or c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.REGLA_PENDENT)")
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
			" and (type(c) != es.caib.distribucio.core.entity.RegistreEntity or (c.procesEstat = es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT)) " +
			"group by " +
			"    c.pare")
	List<Object[]> countByPares(
			@Param("entitat") EntitatEntity entitat,
			@Param("pares") List<? extends ContingutEntity> pares);

	@Query("select " +
			"   c " +
			"from " +
			"   ContingutEntity c " +
			"where " +
			"   type(c) = es.caib.distribucio.core.entity.RegistreEntity " +
			"AND " +
			"   c.pare.id = :pareId")
	List<ContingutEntity> findRegistresByPareId(
			@Param("pareId") Long pareId);

}
