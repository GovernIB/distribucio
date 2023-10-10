package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.distribucio.persist.entity.ConfigGroupEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigGroupRepository extends JpaRepository<ConfigGroupEntity, String> {

    List<ConfigGroupEntity> findByParentCodeIsNull(Sort sort);
}