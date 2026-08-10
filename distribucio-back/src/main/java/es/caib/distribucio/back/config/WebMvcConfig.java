/**
 * 
 */
package es.caib.distribucio.back.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.module.sitemesh.filter.PageFilter;
import es.caib.distribucio.back.base.config.BaseWebMvcConfig;
import es.caib.distribucio.back.interceptor.*;
import es.caib.distribucio.logic.intf.base.util.RequestSessionUtil;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.UserSession;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Configuració de Spring MVC.
 *
 * @author Limit Tecnologies
 */
@Configuration
@Order
@DependsOn("ejbClientConfig")
public class WebMvcConfig extends BaseWebMvcConfig {


	@Value("${" + BaseConfig.PROP_USER_SESSION_HTTP_HEADER + ":X-App-Session}")
	private String userSessionHttpHeader;

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

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UsuariResourceService usuariResourceService;

	private static final long MAX_UPLOAD_SIZE = 52428800;

//	@Bean
//	public FilterRegistrationBean<SiteMeshFilter> sitemeshFilter() {
//
//		FilterRegistrationBean<SiteMeshFilter> registrationBean = new FilterRegistrationBean<>();
//		registrationBean.setFilter(new SiteMeshFilter());
//		registrationBean.addUrlPatterns("/*");
//		registrationBean.setOrder(2);
//		return registrationBean;
//	}

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
	protected boolean isJsAppResourceHandlerEnabled() {
		return true;
	}

	@Override
	protected String getJsAppStaticFolder() {
		return "/reactapp";
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// ResourceHandler per a que totes les peticions desconegudes passin per l'index.html
		registry.
				addResourceHandler(getJsAppStaticFolder() + "/**").
				addResourceLocations(getJsAppStaticFolder() + "/").
				resourceChain(true).
				addResolver(new PathResourceResolver() {
					@Override
					protected Resource getResource(String resourcePath, Resource location) throws IOException {

						var requestedResource = location.createRelative(resourcePath);
						return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : location.createRelative("index.html");
					}
				});
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/jsp/", ".jsp");
	}


	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").
				allowedOrigins("http://localhost:5173", "http://localhost:8080").
				allowCredentials(true).
				allowedHeaders("*").
				allowedMethods("*");
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(MAX_UPLOAD_SIZE);
		return multipartResolver;
	}

	@Bean
	public LocaleResolver localeResolver() {

		var localeResolver = new CustomLocaleResolver(Arrays.asList(Locale.forLanguageTag("ca"), Locale.forLanguageTag("es")));
		localeResolver.setDefaultLocale(Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE));
		return localeResolver;
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {

		var lci = new LocaleChangeInterceptor();
		lci.setParamName("lang");
		return lci;
	}

	@Bean
	public HandlerInterceptor userInterceptor() {

		return new AsyncHandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				usuariResourceService.refresh();
				return true;
			}
		};
	}

	@Bean
	public HandlerInterceptor userSessionInterceptor() {

		return new AsyncHandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws JsonProcessingException {
				String json = request.getHeader(userSessionHttpHeader);
				if (json != null) {
					var parsedJson = objectMapper.readValue(json, java.util.Map.class);
					var entitatId = (Integer)parsedJson.get("e");
					var entitatLong = entitatId != null ? entitatId.longValue() : null;
					RequestSessionUtil.setRequestSession(new UserSession(entitatLong));
				}
				return true;
			}
		};
	}

	private static final String[] INTERCEPTOR_EXCLUSIONS = new String [] {
			BaseConfig.API_PATH + "/**",
			BaseConfig.PING_PATH,
			BaseConfig.SYSENV_PATH,
			BaseConfig.MANIFEST_PATH,
			BaseConfig.AUTH_TOKEN_PATH,
			BaseConfig.REACT_APP_PATH + "/**",
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

	private static final String[] USER_PATHS = { "/registreUser**" };
	private static final String[] ADMIN_PATHS = {
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
			"/procediment**"
	};
	private static final String[] SUPER_PATHS = {
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
			"/avis/**"
	};

	private static final String[] SUPER_EXCLUSIONS = {
			"/entitat/logo",
			"/entitat/**/logo"
	};

	private static final String[] META_PATHS = {
			"/metaDada**",
			"/metaDada/**",
			"/domini**",
			"/domini/**"
	};

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(userSessionInterceptor());
		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(sessioInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(llistaEntitatsInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(llistaRolsInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(modalInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(elementsPendentsBustiaInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
		registry.addInterceptor(avisosInterceptor).excludePathPatterns(INTERCEPTOR_EXCLUSIONS);
        registry.addInterceptor(accesUserInterceptor).addPathPatterns(USER_PATHS);
		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(ADMIN_PATHS);
		registry.addInterceptor(accesMetadadaInterceptor).addPathPatterns(META_PATHS);
		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(SUPER_PATHS).excludePathPatterns(SUPER_EXCLUSIONS);
	}

	public static class CustomLocaleResolver extends SessionLocaleResolver {

		private final AcceptHeaderLocaleResolver acceptHeaderLocaleResolver;

		public CustomLocaleResolver(List<Locale> supportedLocales) {

			acceptHeaderLocaleResolver = new AcceptHeaderLocaleResolver();
			acceptHeaderLocaleResolver.setSupportedLocales(supportedLocales);
		}

		@Override
		@NotNull
		protected Locale determineDefaultLocale(@NotNull HttpServletRequest request) {

			var acceptHeaderLocale = acceptHeaderLocaleResolver.resolveLocale(request);
			if (acceptHeaderLocale != null) {
				return acceptHeaderLocale;
			}
			Locale defaultLocale = getDefaultLocale();
			if (defaultLocale == null) {
				defaultLocale = request.getLocale();
			}
			return defaultLocale;
		}
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

}
