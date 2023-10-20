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
 * @author Limit Tecnologies
 */
@Configuration
@EnableJpaAuditing
public class AuditingConfig {

	@Autowired
	private UsuariRepository usuariRepository;

	@Bean
	public AuditorAware<UsuariEntity> auditorProvider() {
		return new AuditorAware<UsuariEntity>() {
			@Override
			public java.util.Optional<UsuariEntity> getCurrentAuditor() {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null && authentication.isAuthenticated()) {
					return usuariRepository.findById(authentication.getName());
				} else {
					return Optional.empty();
				}
			}
		};
	}

}
