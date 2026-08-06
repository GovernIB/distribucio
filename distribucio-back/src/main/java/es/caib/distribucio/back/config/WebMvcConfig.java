/**
 * 
 */
package es.caib.distribucio.back.config;

import es.caib.distribucio.back.base.config.BaseWebMvcConfig;
import es.caib.distribucio.back.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.opensymphony.module.sitemesh.filter.PageFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Configuració de Spring MVC.
 *
 * @author Limit Tecnologies
 */
@Configuration
@DependsOn("ejbClientConfig")
@SuppressWarnings("deprecation")
public class WebMvcConfig extends BaseWebMvcConfig {

	@Autowired
	private AplicacioInterceptor aplicacioInterceptor;
	@Autowired
	private SessioInterceptor sessioInterceptor;
	@Autowired
	private LlistaEntitatsInterceptor llistaEntitatsInterceptor;
	@Autowired
	private LlistaRolsInterceptor llistaRolsInterceptor;
	@Autowired
	private ModalInterceptor modalInterceptor;
	@Autowired
	private NodecoInterceptor nodecoInterceptor;
	@Autowired
	private AjaxInterceptor ajaxInterceptor;
	@Autowired
	private ElementsPendentsBustiaInterceptor elementsPendentsBustiaInterceptor;
	@Autowired
	private AvisosInterceptor avisosInterceptor;
	@Autowired
	private AccesUserInterceptor accesUserInterceptor;
	@Autowired
	private AccesAdminInterceptor accesAdminInterceptor;
	@Autowired
	private AccesMetadadaInterceptor accesMetadadaInterceptor;
	@Autowired
	private AccesSuperInterceptor accesSuperInterceptor;

	@Bean
	public FilterRegistrationBean<Filter> sitemeshFilter() {
		FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new SkippingSiteMeshFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	/**
	 * SiteMesh (decoració de pàgines JSP) no té sentit -- i no s'ha d'invocar -- per respostes
	 * JSON del motor genèric HAL-FORMS (/api/**) ni pels recursos estàtics del SPA React
	 * (/reactapp/**). A més d'innecessari, inicialitzar el {@link PageFilter} per aquestes rutes
	 * pot fallar carregant "/WEB-INF/decorators.xml" segons com s'exposi src/main/webapp al
	 * ServletContext en mode standalone (Spring Boot embedded, sense WAR real desplegat).
	 */
	private static class SkippingSiteMeshFilter implements Filter {
		private final PageFilter delegate = new PageFilter();

		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			delegate.init(filterConfig);
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
				throws IOException, ServletException {
			String path = ((HttpServletRequest) request).getRequestURI()
					.replaceFirst(((HttpServletRequest) request).getContextPath(), "");
			if (path.startsWith("/api/") || path.equals("/api")
					|| path.startsWith("/reactapp/") || path.equals("/reactapp")) {
				chain.doFilter(request, response);
			} else {
				delegate.doFilter(request, response, chain);
			}
		}

		@Override
		public void destroy() {
			delegate.destroy();
		}
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// Origens explícits (no "*") + allowCredentials perquè el SPA React, quan es corre amb
		// `npm run dev` a :5173, pugui cridar l'API real a :8080 (fetch amb credentials:'include'
		// -- el navegador rebutja Access-Control-Allow-Origin:"*" combinat amb credencials).
		registry.addMapping("/**").
				allowedOrigins("http://localhost:5173", "http://localhost:8080").
				allowCredentials(true).
				allowedHeaders("*").
				allowedMethods("*");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String[] excludedPathPatterns = new String [] {
				"/js/**",
				"/css/**",
				"/fonts/**",
				"/img/**",
				"/images/**",
				"/extensions/**",
				"/webjars/**",
				"/**/datatable/**",
				"/**/selection/**",
				"/**/rest/notib**",
				"/**/rest/notib/**",
				"/api/rest**",
				"/api/rest/**",
				"/api-docs/**",
				"/**/api-docs/",
				"/public/**",
				"/reactapp/**",
				"/api/**"
		};
		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(sessioInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(llistaEntitatsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(llistaRolsInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(modalInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(elementsPendentsBustiaInterceptor).excludePathPatterns(excludedPathPatterns);
		registry.addInterceptor(avisosInterceptor).excludePathPatterns(excludedPathPatterns);
        registry.addInterceptor(accesUserInterceptor).addPathPatterns(new String[] {
                "/registreUser**",
        });
		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(new String[] {
				"/bustiaAdminOrganigrama**",
				"/bustiaAdminOrganigrama/**",
				"/unitatOrganitzativa**",
				"/unitatOrganitzativa/**",
				"/regla**",
				"/regla/**",
				"/backoffice**",
				"/backoffice/**",
				"/permis**",
				"/permis/**",
				"/contingutAdmin**",
				"/contingutAdmin/**",
				"/registreAdmin**",
				"/registreAdmin/**",
				"/procediment/**",
				"/procediment**",
		});
		registry.addInterceptor(accesMetadadaInterceptor).addPathPatterns(new String[] {
				"/metaDada**",
				"/metaDada/**",
				"/domini**",
				"/domini/**",
		});
		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(new String[] {
				"/entitat**",
				"/entitat/**",
				"/integracio**",
				"/integracio/**",
				"/excepcio**",
				"/excepcio/**",
				"/registreUser/metriquesView**",
				"/registreUser/metriquesView/**",
				"/registreUser/anotacionsPendentArxiu**",
				"/registreUser/anotacionsPendentArxiu/**",
				"/monitor**",
				"/monitor/**",
				"/config**",
				"/config/**",
				"/avis**",
				"/avis/**",
		}).excludePathPatterns(new String[] {
				"/entitat/logo",
				"/entitat/**/logo"
		});
	}
	
	/** Configura el firewall per permetre caràcters codificats com el % ja que aquests s'usen en la codificació
	 * dels identificadors en els enllaços públics de descàrrega de documents.
	 *
	 * @return
	 */
	@Bean
    public HttpFirewall getHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        firewall.setAllowUrlEncodedSlash(true);
        firewall.setAllowBackSlash(true);
        firewall.setAllowUrlEncodedPercent(true);
        firewall.setAllowUrlEncodedPeriod(true);
        return firewall;
    }

	/**
	 * El SPA React es serveix mitjançant {@code ReactAppStaticController}/{@code DevProxyController}
	 * (amb reenviament al servidor de desenvolupament de Vite), no mitjançant el mecanisme genèric
	 * de fallback a "index.html" via ResourceHandler que ofereix {@link BaseWebMvcConfig}.
	 */
	@Override
	protected boolean isJsAppResourceHandlerEnabled() {
		return false;
	}

}
