package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ContingutMovimentResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

/**
 * Repositori per a la gestió d'entitats de tipus {@link ContingutMovimentResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ContingutMovimentResourceRepository extends BaseRepository<ContingutMovimentResourceEntity, Long> {

	/** Indica si el contingut ha tengut algun moviment amb origen o destí en alguna de les bústies indicades. */
	@Query("select case when count(m) > 0 then true else false end from ContingutMovimentResourceEntity m " +
			"where m.contingut.id = :contingutId " +
			"and (m.origenId in :busties or m.destiId in :busties)")
	boolean existsMovimentAmbBusties(
			@Param("contingutId") Long contingutId,
			@Param("busties") Collection<Long> busties);

}
