/**
 * 
 */
package es.caib.distribucio.persist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {

	public RegistreAnnexEntity findByRegistreAndId(RegistreEntity registre, Long annexId);	
	
	public Long countByRegistreAndArxiuEstat(RegistreEntity registre, AnnexEstat arxiuEstat );
	
//	@Query(	"Select ra from"
//			+ "	RegistreAnnexEntity ra "
//			+ " where "
//			+ " (:esNullNumero = true or lower(ra.registre.numero) like lower('%'||:numero||'%')) and "
//			+ " (:esNullArxiuEstat = true or ra.arxiuEstat = :arxiuEstat) and " 
//			+ " (ra in ("
//			+ "		select raf.annex "
//			+ "		from RegistreAnnexFirmaEntity raf "
//			+ "		where raf.tipus = :tipusFirma"
//			+ "	)) "
//			+ "")
//	public Page<RegistreAnnexEntity> findByFiltrePaginat(
//			@Param("esNullNumero") boolean esNullNumero,
//			@Param("numero") String numero,
//			@Param("esNullArxiuEstat") boolean esNullArxiuEstat,
//			@Param("arxiuEstat") AnnexEstat arxiuEstat,
//			@Param("esNullTipusFirma") boolean esNullTipusFirma,
//			@Param("tipusFirma") String tipusFirma,
//			Pageable pageable);
	
	@Query(	"Select ra from"
			+ "	RegistreAnnexEntity ra "
			+ " join ra.firmes raf"
			+ " where "
			+ " (:esNullNumero = true or lower(ra.registre.numero) like lower('%'||:numero||'%')) and "
			+ " (:esNullArxiuEstat = true or ra.arxiuEstat = :arxiuEstat) and "
			+ " (:esNullTipusFirma = true or raf.tipus = :tipusFirma) and "
			+ " (:esNullTitol = true or lower(ra.titol) like lower('%'||:titol||'%')) and "
			+ " (:esNullFitxerNom = true or lower(ra.fitxerNom) like lower('%'||:fitxerNom||'%')) and "
			+ " (:esNullFitxerTipusMime = true or lower(ra.fitxerTipusMime) like lower('%'||:fitxerTipusMime||'%')) "
			+ "")
	public Page<RegistreAnnexEntity> findByFiltrePaginat(
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullArxiuEstat") boolean esNullArxiuEstat,
			@Param("arxiuEstat") AnnexEstat arxiuEstat,
			@Param("esNullTipusFirma") boolean esNullTipusFirma,
			@Param("tipusFirma") String tipusFirma,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol,
			@Param("esNullFitxerNom") boolean esNullFitxerNom,
			@Param("fitxerNom") String fitxerNom,
			@Param("esNullFitxerTipusMime") boolean esNullFitxerTipusMime,
			@Param("fitxerTipusMime") String fitxerTipusMime,
			Pageable pageable);
	
}
