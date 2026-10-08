package es.caib.distribucio.persist.resourcerepository;

import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.distribucio.persist.base.repository.BaseRepository;
import es.caib.distribucio.persist.resourceentity.ExcepcioLogResourceEntity;

/**
 * Repositori per a la gestió d'entitats de tipus {@link ExcepcioLogResourceEntity}.
 *
 * @author Límit Tecnologies
 */
public interface ExcepcioLogResourceRepository extends BaseRepository<ExcepcioLogResourceEntity, Long> {

	/**
	 * Esborrat massiu en una sola sentència. No es fa amb un delete derivat del nom del mètode
	 * perquè Spring Data el resol carregant totes les entitats (amb el CLOB de la traça) i
	 * esborrant-les una a una.
	 */
	@Modifying
	@Query("delete from ExcepcioLogResourceEntity el where el.data < :data")
	int deleteByDataBefore(@Param("data") Date data);

}
