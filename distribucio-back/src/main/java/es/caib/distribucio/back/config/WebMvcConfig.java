/**
 * 
 */
package es.caib.distribucio.back.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.opensymphony.module.sitemesh.filter.PageFilter;

/**
 * Configuració de Spring MVC.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@SuppressWarnings("deprecation")
public class WebMvcConfig implements WebMvcConfigurer {

//	@Autowired
//	private AplicacioInterceptor aplicacioInterceptor;
//	@Autowired
//	private SessioInterceptor sessioInterceptor;
//	@Autowired
//	private LlistaEntitatsInterceptor llistaEntitatsInterceptor;
//	@Autowired
//	private LlistaRolsInterceptor llistaRolsInterceptor;
//	@Autowired
//	private ModalInterceptor modalInterceptor;
//	@Autowired
//	private NodecoInterceptor nodecoInterceptor;
//	@Autowired
//	private AjaxInterceptor ajaxInterceptor;
//	@Autowired
//	private ElementsPendentsBustiaInterceptor elementsPendentsBustiaInterceptor;
//	@Autowired
//	private AvisosInterceptor avisosInterceptor;
//	@Autowired
//	private AccesAdminInterceptor accesAdminInterceptor;
//	@Autowired
//	private AccesMetadadaInterceptor accesMetadadaInterceptor;
//	@Autowired
//	private AccesSuperInterceptor accesSuperInterceptor;

	@Bean
	public FilterRegistrationBean<PageFilter> sitemeshFilter() {
		FilterRegistrationBean<PageFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new PageFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(2);
		return registrationBean;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
	}

//	@Override
//	public void addInterceptors(InterceptorRegistry registry) {
//		String[] excludedPathPatterns = new String [] {
//				"/js/**",
//				"/css/**",
//				"/fonts/**",
//				"/img/**",
//				"/images/**",
//				"/extensions/**",
//				"/webjars/**",
//				"/**/datatable/**",
//				"/**/selection/**",
//				"/**/rest/notib**",
//				"/**/rest/notib/**",
//				"/api/rest**",
//				"/api/rest/**",
//				"/api-docs/**",
//				"/**/api-docs/",
//				"/public/**"
//		};
//		registry.addInterceptor(aplicacioInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(sessioInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(llistaEntitatsInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(llistaRolsInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(modalInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(nodecoInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(ajaxInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(elementsPendentsBustiaInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(avisosInterceptor).excludePathPatterns(excludedPathPatterns);
//		registry.addInterceptor(accesAdminInterceptor).addPathPatterns(new String[] {
//				"/bustiaAdminOrganigrama**",
//				"/bustiaAdminOrganigrama/**",
//				"/unitatOrganitzativa**",
//				"/unitatOrganitzativa/**",
//				"/regla**",
//				"/regla/**",
//				"/backoffice**",
//				"/backoffice/**",
//				"/permis**",
//				"/permis/**",
//				"/contingutAdmin**",
//				"/contingutAdmin/**",
//				"/registreAdmin**",
//				"/registreAdmin/**",
//				"/procediment/**",
//				"/procediment**",
//		});
//		registry.addInterceptor(accesMetadadaInterceptor).addPathPatterns(new String[] {
//				"/metaDada**",
//				"/metaDada/**",
//				"/domini**",
//				"/domini/**",
//		});
//		registry.addInterceptor(accesSuperInterceptor).addPathPatterns(new String[] {
//				"/entitat**",
//				"/entitat/**",
//				"/integracio**",
//				"/integracio/**",
//				"/excepcio**",
//				"/excepcio/**",
//				"/registreUser/metriquesView**",
//				"/registreUser/metriquesView/**",
//				"/registreUser/anotacionsPendentArxiu**",
//				"/registreUser/anotacionsPendentArxiu/**",
//				"/monitor**",
//				"/monitor/**",
//				"/config**",
//				"/config/**",
//				"/avis**",
//				"/avis/**",
//		}).excludePathPatterns(new String[] {
//				"/entitat/logo",
//				"/entitat/**/logo"
//		});
//	}

}
