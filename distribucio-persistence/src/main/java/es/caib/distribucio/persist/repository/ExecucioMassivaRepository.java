package es.caib.distribucio.persist.repository;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.logic.intf.dto.ExecucioMassivaTipusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import es.caib.distribucio.persist.entity.ExecucioMassivaEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;

@Component
public interface ExecucioMassivaRepository extends JpaRepository<ExecucioMassivaEntity, Long> {

    @Query("select e " +
        "from 	ExecucioMassivaEntity e " +
        "where 	e.entitat.id = :entitatId " +
        "	and (:isNullUsuari = true or e.usuari.codi = :usuariCodi) " +
        "	and (:isNullTipus  = true or e.tipus = :tipus) " +
        "	order by e.createdDate desc")
    Page<ExecucioMassivaEntity> findExecucionsFiltrades(
        @Param("entitatId") Long entitatId,
        @Param("isNullUsuari") boolean isNullUsuari,
        @Param("usuariCodi") String usuariCodi,
        @Param("isNullTipus") boolean isNullTipus,
        @Param("tipus") ExecucioMassivaTipusDto tipus,
        Pageable pageable);

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

    @Query("select e " +
        "from ExecucioMassivaEntity e " +
        "where e.dataFi < :dataLimit " +
        "	and e.nomDocument is not null")
	List<ExecucioMassivaEntity> findZipByDataLimit(
			@Param("dataLimit") Date dataLimit);

    @Query("select COUNT(e) " +
        "from ExecucioMassivaEntity e " +
        "where e.dataCreacio >= :data " +
        "   and e.tipus = 'DESCARREGAR'" +
        "   and e.usuari.codi = :user")
	Integer countNombreAccionsMassives(
            @Param("user") String user,
			@Param("data") Date data);
}
