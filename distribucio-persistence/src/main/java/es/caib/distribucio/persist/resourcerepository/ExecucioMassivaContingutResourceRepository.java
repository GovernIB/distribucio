package es.caib.distribucio.persist.resourcerepository;

import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ExecucioMassivaContingutResourceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExecucioMassivaContingutResourceRepository extends BaseRepository<ExecucioMassivaContingutResourceEntity, Long> {

	/**
	 * Usat per comprovar si ja hi ha una execució massiva pendent/en curs pels
	 * mateixos elements abans de crear-ne una de nova.
	 */
	@Query("select emc.elementNom from ExecucioMassivaContingutResourceEntity emc "
			+ "where emc.elementId in (:elementIds) "
			+ "and emc.estat in (:estats)")
	List<String> findElementNomByElementIdInAndEstatIn(
			@Param("elementIds") List<Long> elementIds,
			@Param("estats") List<ExecucioMassivaContingutEstatDto> estats);

}