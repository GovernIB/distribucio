/**
 * 
 */
package es.caib.distribucio.logic.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Configuració de cache.
 * 
 * @author Limit Tecnologies
 */
@Configuration
@EnableCaching
public class CacheConfig {

	public static final String ACL_CACHE_NAME = "aclCache";

	/*@Bean
	public EhCacheCacheManager ehCacheCacheManager() {
		CacheManager cacheManager = ehCacheManagerFactoryBean().getObject();
		if (cacheManager.getCache(ACL_CACHE_NAME) == null) {
			cacheManager.addCache(ACL_CACHE_NAME);
		}
		return new EhCacheCacheManager(cacheManager);
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
		bean.setShared(true);
		return bean;
	}*/

}
