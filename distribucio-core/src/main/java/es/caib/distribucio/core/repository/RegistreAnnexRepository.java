/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.api.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreAnnexRepository extends JpaRepository<RegistreAnnexEntity, Long> {

	public RegistreAnnexEntity findByRegistreAndId(RegistreEntity registre, Long annexId);	
	
	public Long countByRegistreAndArxiuEstat(RegistreEntity registre, AnnexEstat arxiuEstat );
}
