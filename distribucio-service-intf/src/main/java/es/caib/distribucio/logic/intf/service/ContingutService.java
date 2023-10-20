/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.dto.ContingutComentariDto;
import es.caib.distribucio.logic.intf.dto.ContingutDto;
import es.caib.distribucio.logic.intf.dto.ContingutFiltreDto;
import es.caib.distribucio.logic.intf.dto.ContingutLogDetallsDto;
import es.caib.distribucio.logic.intf.dto.ContingutLogDto;
import es.caib.distribucio.logic.intf.dto.ContingutMovimentDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;

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
	 * @param rolActual Rol de l'usuari actual
	 * @return El contingut amb l'id especificat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual,
			boolean isVistaMoviments) throws NotFoundException;

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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(
			Long entitatId,
			Long contingutId);

	/**
	 * Obté els detalls d'una acció realitzada damunt un contingut.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutLogId
	 *            Atribut id del log del qual es volen veure detalls.
	 * @return Els detalls de l'acció.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('DIS_ADMIN_LECTURA')")
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
	@PreAuthorize("isAuthenticated()")
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
	@PreAuthorize("isAuthenticated()")
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
	 * @param rolActual Rol de l'usuari actual
	 * @return boolea per indicar si el procés ha finaltizat correctament
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean marcarProcessat(
			Long entitatId,
			Long contingutId,
			String text, 
			String rolActual) throws NotFoundException;

	/**
	 * Comprova si l'usuari actual disposa de permisos sobre una bústia
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param contingutId
	 *            Atribut id del contingut que es vol exportar.
	 * @return boolea
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean hasPermisSobreBustia(
			Long entitatId,
			Long contingutId) throws NotFoundException;

	/** 
	 * 
	 */
	@PreAuthorize("hasAnyRole('DIS_REPORT','DIS_ADMIN', 'DIS_ADMIN_LECTURA')")
	public List<LogsDadesObertesDto> findLogsPerDadesObertes(
			Date dataInici,
			Date dataFi, 
			LogTipusEnumDto tipus, 
			String usuari, 
			Long anotacioId, 
			String anotacioNumero,
			RegistreProcesEstatEnum anotacioEstat, 
			Boolean error, 
			Boolean pendent, 
			Long bustiaOrigen, 
			Long bustiaDesti, 
			String uoOrigen, 
			String uoSuperior, 
			String uoDesti, 
			String uoDestiSuperior);

}
