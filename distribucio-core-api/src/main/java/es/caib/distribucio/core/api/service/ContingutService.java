/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.ContingutLogDto;
import es.caib.distribucio.core.api.dto.ContingutMovimentDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.core.api.exception.NotFoundException;

/**
 * Declaració dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ContingutService {

	/**
	 * Obté la informació del contingut especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param ambFills
	 *            Indica si la resposta ha d'incloure els fills del contingut.
	 * @param ambVersions
	 *            Indica si la resposta ha d'incloure les versions del contingut.
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions) throws NotFoundException;

	/**
	 * Obté la informació del contingut especificat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el contingut.
	 * @param ambFills
	 *            Indica si la resposta ha d'incloure els fills del contingut.
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista d'accions realitzades damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista d'accions realitzades damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) throws NotFoundException;
	
	
	/** 
	 * Obté el registrte d'accions realitzades damunt un contingut amb tots els detalls com a una llista de detalls.
	 * 
	 * @param entitatId
	 * @param contingutId
	 * @return
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(
			Long entitatId,
			Long contingutId);

	/**
	 * Obté els detalls d'una acció realitzada damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @param contingutLogId
	 *            Atribut id del log del qual es volen veure detalls.
	 * @return Els detalls de l'acció.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista de moviments realitzats damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté el registre d'accions realitzades damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut del qual es vol consultar el registre.
	 * @return La llista de moviments realitzats damunt el contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/**
	 * Obté una llista dels continguts esborrats permetent especificar dades
	 * per al seu filtratge.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param filtre
	 *            El filtre de la consulta.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return Una pàgina amb els continguts trobats.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;

	/**
	 * Retorna els comentaris d'un contingut
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol exportar.
	 * @return Llista de comentaris pel contingut.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<ContingutComentariDto> findComentarisPerContingut(
			Long entitatId,
			Long contingutId) throws NotFoundException;
	
	/**
	 * Publica un comentari per a un contingut
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol exportar.
	 * @param text
	 *            text del comentari a publicar.
	 * @return RespostaPublicacioComentariDto per indicar si s'ha publicat correctament (amb/sense errors).
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public RespostaPublicacioComentariDto publicarComentariPerContingut(
			Long entitatId,
			Long contingutId,
			String text) throws NotFoundException;
	
	
	/**
	 * Marcar com a processat un contingut
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol exportar.
	 * @param text
	 *            text del comentari a publicar.
	 * @return boolea per indicar si el procés ha finaltizat correctament
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean marcarProcessat(
			Long entitatId,
			Long contingutId,
			String text) throws NotFoundException;
}
