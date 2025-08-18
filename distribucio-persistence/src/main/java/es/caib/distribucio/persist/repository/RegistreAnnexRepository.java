/**
 * 
 */
package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.entity.EntitatEntity;
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
			+ " left join ra.firmes raf"
			+ " where "
			+ " (ra.registre.entitat = :entitat) and "
			+ " (:esNullNumero = true or lower(ra.registre.numero) like lower('%'||:numero||'%')) and "
			+ " (:esNullNumeroCopia = true or ra.registre.numeroCopia = :numeroCopia) and "
			+ " (:esNullArxiuEstat = true or ra.arxiuEstat = :arxiuEstat) and "
			+ " (:esNullDataRecepcioInici = true or ra.registre.data >= :dataRecepcioInici) and "
			+ " (:esNullDataRecepcioFi = true or ra.registre.data <= :dataRecepcioFi) and "
			+ " (:esNullTipusFirma = true or (raf is not null and raf.tipus = :tipusFirma)) and "
			+ " (:esNullTitol = true or lower(ra.titol) like lower('%'||:titol||'%')) and "
			+ " (:esNullFitxerNom = true or lower(ra.fitxerNom) like lower('%'||:fitxerNom||'%')) and "
			+ " (:esNullFitxerTipusMime = true or lower(ra.fitxerTipusMime) like lower('%'||:fitxerTipusMime||'%')) "
			+ "")
	public Page<RegistreAnnexEntity> findByFiltrePaginat(
			@Param("entitat") EntitatEntity entitat, 
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullNumeroCopia") boolean esNullNumeroCopia,
			@Param("numeroCopia") Integer numeroCopia,
			@Param("esNullArxiuEstat") boolean esNullArxiuEstat,
			@Param("arxiuEstat") AnnexEstat arxiuEstat,
			@Param("esNullDataRecepcioInici") boolean esNullDataRecepcioInici,
			@Param("dataRecepcioInici") Date dataRecepcioInici,
			@Param("esNullDataRecepcioFi") boolean esNullDataRecepcioFi, 
			@Param("dataRecepcioFi") Date dataRecepcioFi, 
			@Param("esNullTipusFirma") boolean esNullTipusFirma,
			@Param("tipusFirma") String tipusFirma,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol,
			@Param("esNullFitxerNom") boolean esNullFitxerNom,
			@Param("fitxerNom") String fitxerNom,
			@Param("esNullFitxerTipusMime") boolean esNullFitxerTipusMime,
			@Param("fitxerTipusMime") String fitxerTipusMime,
			Pageable pageable);
	
	@Query(	"Select ra.id from"
			+ "	RegistreAnnexEntity ra "
			+ " left join ra.firmes raf"
			+ " where "
			+ " (ra.registre.entitat = :entitat) and "
			+ " (:esNullNumero = true or lower(ra.registre.numero) like lower('%'||:numero||'%')) and "
			+ " (:esNullNumeroCopia = true or ra.registre.numeroCopia = :numeroCopia) and "
			+ " (:esNullArxiuEstat = true or ra.arxiuEstat = :arxiuEstat) and "
			+ " (:esNullDataRecepcioInici = true or ra.registre.data >= :dataRecepcioInici) and "
			+ " (:esNullDataRecepcioFi = true or ra.registre.data <= :dataRecepcioFi) and "
			+ " (:esNullTipusFirma = true or (raf is not null and raf.tipus = :tipusFirma)) and "
			+ " (:esNullTitol = true or lower(ra.titol) like lower('%'||:titol||'%')) and "
			+ " (:esNullFitxerNom = true or lower(ra.fitxerNom) like lower('%'||:fitxerNom||'%')) and "
			+ " (:esNullFitxerTipusMime = true or lower(ra.fitxerTipusMime) like lower('%'||:fitxerTipusMime||'%')) "
			+ "")
	public List<Long> findIdsByFiltre(
			@Param("entitat") EntitatEntity entitat,
			@Param("esNullNumero") boolean esNullNumero,
			@Param("numero") String numero,
			@Param("esNullNumeroCopia") boolean esNullNumeroCopia,
			@Param("numeroCopia") Integer numeroCopia,
			@Param("esNullArxiuEstat") boolean esNullArxiuEstat,
			@Param("arxiuEstat") AnnexEstat arxiuEstat,
			@Param("esNullDataRecepcioInici") boolean esNullDataRecepcioInici,
			@Param("dataRecepcioInici") Date dataRecepcioInici,
			@Param("esNullDataRecepcioFi") boolean esNullDataRecepcioFi, 
			@Param("dataRecepcioFi") Date dataRecepcioFi, 
			@Param("esNullTipusFirma") boolean esNullTipusFirma,
			@Param("tipusFirma") String tipusFirma,
			@Param("esNullTitol") boolean esNullTitol,
			@Param("titol") String titol,
			@Param("esNullFitxerNom") boolean esNullFitxerNom,
			@Param("fitxerNom") String fitxerNom,
			@Param("esNullFitxerTipusMime") boolean esNullFitxerTipusMime,
			@Param("fitxerTipusMime") String fitxerTipusMime);
	
	@Query("Select ra.titol from RegistreAnnexEntity ra where ra.registre = :registre")
	public List<String> findTitolByRegistre(@Param("registre") RegistreEntity registre);

	@Modifying
	@Query(value = "update dis_registre_annex " +
			"set createdby_codi = :codiNou, lastmodifiedby_codi = :codiNou " +
			"where createdby_codi = :codiAntic or lastmodifiedby_codi = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(
			@Param("codiAntic") String codiAntic, 
			@Param("codiNou") String codiNou);

	public List<RegistreAnnexEntity> findByIdIn(List<Long> multipleAnnexosIds);

//	@Query("Select ra from RegistreAnnexEntity ra " + 
//			"where ra.registre.entitat = :entitat " + 
//			"and ra.fitxerArxiuUuid is not null " + 
//			"order by ra.id desc")
//	public RegistreAnnexEntity findTopByEntitatAndFitxerArxiuUuidNotNull(@Param("entitat") EntitatEntity entitat);
	
	public RegistreAnnexEntity findTopByRegistre_EntitatAndFitxerArxiuUuidNotNull(EntitatEntity entitat);
}
