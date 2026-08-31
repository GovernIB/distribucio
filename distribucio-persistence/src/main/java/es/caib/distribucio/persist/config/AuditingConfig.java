/**
 * 
 */
package es.caib.distribucio.persist.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.UsuariRepository;

/**
 * Configuració per a les entitats de base de dades auditables.
 * 
 * L'auditor és el codi de l'usuari i no l'entitat, perquè només hi pot haver
 * un {@link AuditorAware} per a tot el context i les entitats de recurs de la
 * capa REACT ({@link es.caib.distribucio.persist.base.entity.BaseAuditableEntity})
 * guarden l'auditor com a {@link String}.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {

	@Autowired
	private UsuariRepository usuariRepository;

	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				// Si l'usuari no està donat d'alta a dis_usuari no s'informa l'auditor,
				// per a no violar les claus foranes cap a dis_usuari que tenen algunes taules.
				return usuariRepository.findById(authentication.getName()).map(UsuariEntity::getCodi);
			} else {
				return Optional.empty();
			}
		};
	}

}
