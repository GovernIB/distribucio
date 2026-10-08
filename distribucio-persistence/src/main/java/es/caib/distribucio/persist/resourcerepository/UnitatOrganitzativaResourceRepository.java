package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.UnitatOrganitzativaResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositori per a la gestió d'entitats de tipus {@link UnitatOrganitzativaResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface UnitatOrganitzativaResourceRepository extends BaseRepository<UnitatOrganitzativaResourceEntity, Long> {

    @Query(value = "SELECT id FROM dis_unitat_organitzativa " +
            "START WITH id = :unitatId " +
            "CONNECT BY PRIOR codi = codi_unitat_superior",
            nativeQuery = true)
    List<Long> findUnitatAndAllDescendentsIds(@Param("unitatId") Long unitatId);

}
