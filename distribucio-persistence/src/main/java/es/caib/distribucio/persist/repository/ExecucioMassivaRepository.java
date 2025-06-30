package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;

@Component
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassivaEntity, Long> {

	List<ExecucioMassivaEntity> findByUsuariAndEntitatIdOrderByCreatedDateDesc(UsuariEntity usuari, Long entitatId, Pageable pageable);
	
	List<ExecucioMassivaEntity> findByEntitatIdOrderByCreatedDateDesc(Long entitatId, Pageable pageable);

		@Query("select e " +
			"from 	ExecucioMassivaEntity e " +
			"where 	e.dataCreacio <= :ara " +
			"	and e.dataFi is null " +
			"	and e.entitat.id = :entitatId " +
			"   and exists (select 1 from e.continguts c where c.estat = es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto.PENDENT) " +
			"	order by e.id asc")
	List<ExecucioMassivaEntity> findMassivesAmbPendentsByEntitatPerProcessar(
			@Param("ara") Date ara, 
			@Param("entitatId") Long entitatId);
	
}
