package es.caib.distribucio.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import es.caib.distribucio.persist.entity.ExecucioMassivaContingutEntity;
import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;

@Component
public interface ExecucioMassivaContingutRepository extends JpaRepository<ExecucioMassivaContingutEntity, Long> {

	
	@Query( "from ExecucioMassivaContingutEntity emc " +
			"where emc.elementId in (:continguts) " +
			"and emc.estat in (:estats)")
	public List<ExecucioMassivaContingutEntity> findByContingutsAndEstatIn(
			@Param("continguts") List<Long> continguts,
			@Param("estats") List<ExecucioMassivaContingutEstatDto> estats);

	@Query( "select emc.elementNom from ExecucioMassivaContingutEntity emc " +
			"where emc.elementId in (:continguts) " +
			"and emc.estat in (:estats)")
	public List<String> findElementNomByContingutsAndEstatIn(
			@Param("continguts") List<Long> continguts,
			@Param("estats") List<ExecucioMassivaContingutEstatDto> estats);
	
	public List<ExecucioMassivaContingutEntity> findByExecucioMassivaOrderByOrdreAsc(ExecucioMassivaEntity execucioMassiva);

}
