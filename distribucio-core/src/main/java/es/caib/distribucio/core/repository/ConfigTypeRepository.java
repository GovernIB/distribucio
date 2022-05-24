/**
 * 
 */
package es.caib.distribucio.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.core.entity.ConfigTypeEntity;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigTypeRepository extends JpaRepository<ConfigTypeEntity, String> {
	
	public ConfigTypeEntity findByCode(String code);	

}