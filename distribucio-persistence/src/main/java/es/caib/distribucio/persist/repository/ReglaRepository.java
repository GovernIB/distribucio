/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ReglaRepository extends JpaRepository<ReglaEntity, Long> {
	
	
	List<ReglaEntity> findByEntitatAndUnitatOrganitzativaFiltreCodi(EntitatEntity entitat,String unitatOrganitzativaFiltreFiltreCodi);

	List<ReglaEntity> findByEntitatAndUnitatDestiCodi(EntitatEntity entitat,String unitatDestiCodi);

	
	List<ReglaEntity> findByEntitatOrderByOrdreAsc(EntitatEntity entitat);

	@Query(	"from " +
			"    ReglaEntity reg " +
			"where " +
			"    reg.entitat = :entitat " +
			"and (:esNullFiltre = true or lower(reg.nom) like lower('%'||:filtre||'%') or lower(reg.assumpteCodiFiltre) like lower('%'||:filtre||'%') or lower(reg.unitatOrganitzativaFiltre.codi) like lower('%'||:filtre||'%')) ")
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
			"and (:esNullFiltreUnitat = true or r.unitatOrganitzativaFiltre = :unitatOrganitzativaFiltre) " +
			"and (:esNullFiltreNom = true or lower(r.nom) like lower('%'||:filtreNom||'%')) " + 
			"and (:esNullCodiAssumpte = true or lower(r.assumpteCodiFiltre) like lower('%'||:codiAssumpte||'%')) " +
			"and (:esNullFiltreCodiSIA = true or lower(r.procedimentCodiFiltre) like lower('%'||:filtreCodiSIA||'%')) " + 
			"and (:esNullFiltreTipus = true or r.tipus = :filtreTipus) " +
			"and (:esNullPresencial = true or r.presencial = :isPresencial) " +
			"and (:esNullBustia = true or r.bustiaFiltre = :bustia) " + 
			"and (:esNullBackoffice = true or r.backofficeDesti = :backoffice) " + 
//			"and (r.activa = :isActiva) ") +
			"and (:esNullActiva = true or r.activa = :activa)")
	Page<ReglaEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativaFiltre") UnitatOrganitzativaEntity unitatOrganitzativaFiltre, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte, 
			@Param("codiAssumpte") String codiAssumpte, 
			@Param("esNullFiltreCodiSIA") boolean esNullFiltreCodiSIA,
			@Param("filtreCodiSIA") String filtreCodiSIA,
			@Param("esNullFiltreTipus") boolean esNullFiltreTipus,
			@Param("filtreTipus") ReglaTipusEnumDto filtreTipus,
			@Param("esNullPresencial") boolean esNullPresencial, 
			@Param("isPresencial") ReglaPresencialEnumDto isPresencial, 
			@Param("esNullBustia") boolean esNullBustia, 
			@Param("bustia") BustiaEntity bustia, 
			@Param("esNullBackoffice") boolean esNullBackoffice,
			@Param("backoffice") BackofficeEntity backoffice,
//			@Param("isActiva") boolean isActiva,
			@Param("esNullActiva") boolean esNullActiva, 
			@Param("activa") boolean activa, 
			Pageable pageable);
	
	@Query(	"Select r.id from " +
			"    ReglaEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"and (:esNullFiltreUnitat = true or r.unitatOrganitzativaFiltre = :unitatOrganitzativaFiltre) " +
			"and (:esNullFiltreNom = true or lower(r.nom) like lower('%'||:filtreNom||'%')) " + 
			"and (:esNullCodiAssumpte = true or lower(r.assumpteCodiFiltre) like lower('%'||:codiAssumpte||'%')) " +
			"and (:esNullFiltreCodiSIA = true or lower(r.procedimentCodiFiltre) like lower('%'||:filtreCodiSIA||'%')) " + 
			"and (:esNullFiltreTipus = true or r.tipus = :filtreTipus) " +
			"and (:esNullPresencial = true or r.presencial = :isPresencial) " +
			"and (:esNullBustia = true or r.bustiaFiltre = :bustia) " + 
			"and (:esNullBackoffice = true or r.backofficeDesti = :backoffice) " + 
			"and (:esNullActiva = true or r.activa = :activa)")
	public List<Long> findIdsByFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullFiltreUnitat") boolean esNullFiltreUnitat,
			@Param("unitatOrganitzativaFiltre") UnitatOrganitzativaEntity unitatOrganitzativaFiltre, 
			@Param("esNullFiltreNom") boolean esNullFiltreNom,
			@Param("filtreNom") String filtreNom,
			@Param("esNullCodiAssumpte") boolean esNullCodiAssumpte, 
			@Param("codiAssumpte") String codiAssumpte, 
			@Param("esNullFiltreCodiSIA") boolean esNullFiltreCodiSIA,
			@Param("filtreCodiSIA") String filtreCodiSIA,
			@Param("esNullFiltreTipus") boolean esNullFiltreTipus,
			@Param("filtreTipus") ReglaTipusEnumDto filtreTipus,
			@Param("esNullPresencial") boolean esNullPresencial, 
			@Param("isPresencial") ReglaPresencialEnumDto isPresencial, 
			@Param("esNullBustia") boolean esNullBustia, 
			@Param("bustia") BustiaEntity bustia, 
			@Param("esNullBackoffice") boolean esNullBackoffice,
			@Param("backoffice") BackofficeEntity backoffice,
			@Param("esNullActiva") boolean esNullActiva, 
			@Param("activa") boolean activa);
	
	
	List<ReglaEntity> findByBustiaDesti(BustiaEntity bustiaDesti);
	
	List<ReglaEntity> findByBustiaFiltre(BustiaEntity bustiaFiltre);

	/** Mètode per trobar les regles aplicables per entitat i per codi procediment i codi assumpte.
	 * 
	 * @param entitat
	 * @param b
	 * @param procedimentCodiFiltre
	 * @param c
	 * @param assumpteCodiFiltre
	 * @param string 
	 * @return
	 */
	@Query(	"from " +
			"    ReglaEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"and r.activa = true " + 
			"and (r.unitatOrganitzativaFiltre is null or r.unitatOrganitzativaFiltre.id = :unitatOrganitzativaFiltreId) " + 
			"and (r.bustiaFiltre is null or r.bustiaFiltre.id = :bustiaId) " + 
			"and (r.procedimentCodiFiltre is null or (r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre = :procedimentCodiFiltre or r.procedimentCodiFiltre like (:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre))) " +
			"and (r.assumpteCodiFiltre is null or r.assumpteCodiFiltre = :assumpteCodiFiltre) " +
			"and ((r.presencial is null) or (:isPresencialNull is true or r.presencial = :presencial)) " +
			"order by r.ordre asc")
	List<ReglaEntity> findAplicables(
			@Param("entitat") EntitatEntity entitat, 
			@Param("unitatOrganitzativaFiltreId") Long unitatOrganitzativaFiltreId, 
			@Param("bustiaId") Long bustiaId,
			@Param("procedimentCodiFiltre") String procedimentCodiFiltre, 
			@Param("assumpteCodiFiltre") String assumpteCodiFiltre, 
			@Param("isPresencialNull") boolean isPresencialNull, 
			@Param("presencial") ReglaPresencialEnumDto presencial);

	/** Mètode per trobar els registres als quals se'ls pot aplicar la regla manualment
	 * a l'acció de l'administrador d'aplicar la regla manualment.
	 * 
	 * @param entitat
	 * @param b
	 * @param procedimentCodiFiltre
	 * @param c
	 * @param assumpteCodiFiltre
	 * @param string 
	 * @return
	 */
	@Query(	"from " +
			"    RegistreEntity r " +
			"where " +
			"    r.entitat = :entitat " +
			"and r.procesEstat = es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum.BUSTIA_PENDENT " +
			"and (:unitatOrganitzativaFiltreIsNull = true or r.pare.id in (:bustiesUnitatOrganitzativaIds)) " +
			"and (:isRegistrePresencialNull = true or r.presencial = :registrePresencial) " + 
			"and (:bustiaFiltreIsNull = true or r.pare.id = :bustiaFiltreId) " + 
			"and (:procedimentsCodisFiltreIsEmpty = true or r.procedimentCodi in (:procedimentsCodisFiltre)) " +
			"and (:assumpteCodiFiltreIsNull = true or r.assumpteCodi = :assumpteCodiFiltre) " + 
			"order by r.identificador asc")
	List<RegistreEntity> findRegistres(
			@Param("entitat") EntitatEntity entitat,
			@Param("unitatOrganitzativaFiltreIsNull") boolean unitatOrganitzativaFiltreIsNull,
			@Param("bustiesUnitatOrganitzativaIds") List<Long> bustiesUnitatOrganitzativaIds,
			@Param("isRegistrePresencialNull") boolean isRegistrePresencialNull, 
			@Param("registrePresencial") boolean registrePresencial,
			@Param("bustiaFiltreIsNull") boolean bustiaFiltreIsNull,
			@Param("bustiaFiltreId") Long bustiaFiltreId,
			@Param("procedimentsCodisFiltreIsEmpty") boolean procedimentsCodisFiltreIsEmpty,
			@Param("procedimentsCodisFiltre") List<String> procedimentsCodisFiltre,
			@Param("assumpteCodiFiltreIsNull") boolean assumpteCodiFiltreIsNull,
			@Param("assumpteCodiFiltre") String assumpteCodiFiltre);
	
	/** Consulta las reglas de tipo BACKOFFICE para el codi procediment dado */
	@Query(	"from " +
			"    ReglaEntity r " +
			"where " +
			"     r.tipus = 'BACKOFFICE'" +
			" and (r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre = :procedimentCodiFiltre or r.procedimentCodiFiltre like (:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre))")
	List<ReglaEntity> findReglaBackofficeByCodiProcediment(
			@Param("procedimentCodiFiltre") String procedimentCodiFiltre);
	
	/** Consulta les regles donat un codi de procediment. */
	@Query(	"from " +
			"    ReglaEntity r " +
			"where " +
			" (r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre = :procedimentCodiFiltre or r.procedimentCodiFiltre like (:procedimentCodiFiltre||' %') or r.procedimentCodiFiltre like ('% '||:procedimentCodiFiltre))")
	List<ReglaEntity> findReglaByCodiProcediment(
			@Param("procedimentCodiFiltre") String procedimentCodiFiltre);

}
