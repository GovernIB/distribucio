/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.BustiaContingutDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariBustiaFavoritDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;

/**
 * Declaració dels mètodes per a gestionar bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BustiaService {

	/**
	 * Crea una nova bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustia
	 *            Informació de la bústia a crear.
	 * @return La bústia creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public BustiaDto create(
			Long entitatId,
			BustiaDto bustia) throws NotFoundException;

	/**
	 * Actualitza la informació d'una bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustia
	 *            Informació de la bústia a modificar.
	 * @return La bústia modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustia) throws NotFoundException;

	/**
	 * Activa o desactiva una bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Id de la bústia a modificar.
	 * @param activa
	 *            Indica si activar o desactivar la bústia.
	 * @return La bústia modificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public BustiaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) throws NotFoundException;

	/**
	 * Esborra la bústia amb l'id especificat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la bústia a esborrar.
	 * @return La bústia esborrada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public BustiaDto delete(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Marca la bústia com a bústia per defecte dins la seva unitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la bústia a esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public BustiaDto marcarPerDefecte(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta una bústia donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *             Atribut id de la bústia a trobar.
	 * @return La bústia amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public BustiaDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

	/**
	 * Consulta una bústia donat el seu id.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *             Atribut id de la bústia a trobar.
	 * @return La bústia amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public BustiaDto findById(
			Long id) throws NotFoundException;
	
	
	/**
	 * Consulta una bústia donat el seu id i amb els permisos ordenats.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *             Atribut id de la bústia a trobar.
	 * @return La bústia amb l'id especificat o null si no s'ha trobat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public BustiaDto findByIdAmbPermisosOrdenats(Long entitatId, Long id, PaginacioParamsDto paginacio);
	
	
	/**
	 * Consulta les bústies donat el codi de la seva unitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param unitatCodi
	 *            Atribut unitatCodi de les bústies a trobar.
	 * @return Les bústies amb la unitat especificada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) throws NotFoundException;
	
	/**
	 * Consulta les bústies segons els valors del filtre.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre
	 *            El filtre per a la consulta.
	 * @return Les bústies que coincideixen amb els criteris del filtre.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams);

	/**
	 * Retorna una llista de les bústies actives de l'entitat.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return La llista de bústies actives de l'entitat.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) throws NotFoundException;
	
	/**
	 * Llistat de les bústies a les quals te accés un usuari.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param mostrarInactives 
	 * 				Indica si incloure les bústies innactives en el resultat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de regles.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId, 
			boolean mostrarInactives);

	/**
	 * Crea l'anotació de registre i la distribueix.
	 * 
	 * @param entitatCodi
	 *            El codi de l'entitat.
	 * @param tipus
	 *            El tipus d'anotació (ENTRADA o SORTIDA).
	 * @param unitatAdministrativa
	 *            La unitat administrativa destinatària.
	 * @param anotacio
	 *            Les dades de l'anotació de registre.
	 * @return el contenidor enviat
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public Throwable registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) throws NotFoundException;

	/**
	 * Crea l'anotació de registre.
	 * 
	 * @param entitatCodi
	 *            El codi de l'entitat.
	 * @param tipus
	 *            El tipus d'anotació (ENTRADA o SORTIDA).
	 * @param unitatAdministrativa
	 *            La unitat administrativa destinatària.
	 * @param anotacio
	 *            Les dades de l'anotació de registre.
	 * @return L'identificador de l'anotació de registre creada.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 *         Exception
	 *         		Si es produeix alguna excepció creant l'anotació.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public long registreAnotacioCrear(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) throws Exception;
	
	/**
	 * Processa immediatament l'anotació de registre aplicant la distribució, regles i desat
	 * a l'arxiu o ho deixa amb l'estat adient pel seu tractament asíncron.
	 * 
	 * @param registreId
	 *            Identificador de l'anotació de registre.
	 * @return Excepció en cas de produir-se
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_BUSTIA_WS + "')")
	public Throwable registreAnotacioProcessar(
			Long registreId);


	/**
	 * Consulta el nombre d'elements pendents (tant contenidors com registres)
	 * a dins totes les bústies accessibles per l'usuari.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @return El nombre d'elementsPendents.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public long contingutPendentBustiesAllCount(
			Long entitatId) throws NotFoundException;

	/**
	 * Reenvia un contingut pendent de la bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustiaDestiId
	 *            Atribut id de la bústia de destí.
	 * @param contingutId
	 *            Atribut id del contingut que es vol reenviar.
	 * @param comentari
	 *            Comentari pel reenviament.
	 * @param destinsUsuari 
	 * 			  Usuari a s'ha d'assignar l'anotació i un comentari per cada usuari
	 * @param destiLogic
	 *            Origen de les anotacions en la pantalla de moviments.
	 * @param long1 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void registreReenviar(
			Long entitatId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari,
			Long[] perConeixement,
			Map<Long, String> destinsUsuari,
			Long destiLogic) throws NotFoundException;

	/**
	 * Consulta l'arbre de les unitats organitzatives per a mostrar les
	 * bústies.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param nomesBusties
	 *            Indica si només han d'aparèixer les únitats que contenen bústies.
	 * @param nomesBustiesPermeses
	 *            Indica si només han d'aparèixer les bústies a les que es tengui permisos d'accés.
	 * @param comptarElementsPendents
	 *            Indica si s'ha de comptar els elements pendets a cada unitat.
	 * @return L'arbre de les unitats organitzatives.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			Long entitatId,
			boolean nomesBusties,
			boolean nomesBustiesPermeses,
			boolean comptarElementsPendents) throws NotFoundException;

	/**
	 * Modifica els permisos d'un usuari o d'un rol per a accedir a una bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la bústia a la qual es vol modificar el permís.
	 * @param permis
	 *            El permís que es vol modificar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) throws NotFoundException;

	/**
	 * Esborra els permisos d'un usuari o d'un rol per a accedir a una bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param id
	 *            Atribut id de la bústia de la qual es vol modificar el permís.
	 * @param permisId
	 *            Atribut id del permís que es vol esborrar.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> findAmbEntitat(Long entitatId);

	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, BustiaFiltreOrganigramaDto bustiaFiltreOrganigramaDto);

	@PreAuthorize("isAuthenticated()")
	public List<BustiaContingutDto> findAmbEntitatAndFiltrePerInput(Long entitatId, ContingutTipusEnumDto tipus, String filtre);

	@PreAuthorize("isAuthenticated()")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId, List<BustiaDto> busties);

	@PreAuthorize("isAuthenticated()")
	public String getApplictionMetrics();

	@PreAuthorize("isAuthenticated()")
	public void registreAnotacioEnviarPerEmail(
			Long entitatId,
			Long registreId,
			String adresses,
			String motiu,
			boolean isVistaMoviments,
			String rolActual) throws MessagingException;

	/**
	 * Mètode per moure les anotacions de registre d'una bústia a una altra bústia destí. 
	 * Enregistre el moviment amb un comentari opcional.
	 * 
	 * @param entitatId
	 * @param bustiaId
	 * @param destiId
	 * @param comentari
	 * @return
	 * 		Retorna el número d'anotacions mogudes.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public int moureAnotacions(
			long entitatId, 
			long bustiaId, 
			long destiId, 
			String comentari);

	@PreAuthorize("isAuthenticated()")
	public List<UsuariPermisDto> getUsuarisPerBustia(Long bustiaId);

	@PreAuthorize("isAuthenticated()")
	public Map<String, UsuariPermisDto> getUsuarisPerBustia(Long bustiaId, boolean directe, boolean perRol);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<BustiaDto> findAmbUnitatId(
			Long entitatId,
			Long unitatId);

	/** Consulta les unitats organitzatives que són superiors per unitats amb bústies.
	 * @param entitatId
	 * @param filtre
	 * @param paginacioParams
	 * @return La pàgina d'unitats organitzatives que compleixen el filtre.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public List<UnitatOrganitzativaDto> findUnitatsSuperiors(Long entitatId, String filtre);

	@PreAuthorize("isAuthenticated()")
	public boolean isBustiaReadPermitted(Long bustiaId);

	@PreAuthorize("isAuthenticated()")
	public void addToFavorits(
			Long entitatId, 
			Long bustiaId);

	@PreAuthorize("isAuthenticated()")
	public PaginaDto<UsuariBustiaFavoritDto> getBustiesFavoritsUsuariActual(Long entitatId, PaginacioParamsDto paginacioParams);

	@PreAuthorize("isAuthenticated()")
	public void removeFromFavorits(
			Long entitatId, 
			Long id);

	@PreAuthorize("isAuthenticated()")
	public boolean checkIfFavoritExists(
			Long entitatId, 
			Long id);

	@PreAuthorize("isAuthenticated()")
	public List<Long> getIdsBustiesFavoritsUsuariActual(Long entitatId);
	
	/**
	 * Llistat de les bústies origen (extretes de la taula de moviments)
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param mostrarInactivesOrigen 
	 * 				Indica si incloure les bústies innactives en el resultat.
	 * @return El llistat de bústies origen.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> consultaBustiesOrigen(
			Long entitatId, 
			List<BustiaDto> bustiesPermesesPerUsuari, 
			boolean mostrarInactivesOrigen);
	
	/**
	 * Llistat de les bústies a les quals te accés un usuari.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param mostrarInactives 
	 * 				Indica si incloure les bústies innactives en el resultat.
	 * @param paginacioParams
	 *            Paràmetres per a dur a terme la paginació del resultats.
	 * @return La pàgina de regles.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<BustiaDto> findBustiesPerUsuari(
			Long entitatId, boolean mostrarInactives);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "') or hasRole('" + BaseConfig.ROLE_REPORT + "')")
	public List<BustiaDadesObertesDto> findBustiesPerDadesObertes(
			Long id, String uo, String uoSuperior);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "') or hasRole('" + BaseConfig.ROLE_REPORT + "')")
	public List<UsuariDadesObertesDto> findBustiesUsuarisPerDadesObertes(
			String usuari, 
			Long id, 
			String uoDir3Entitat, 
			String uo, 
			String uoSuperior,
			Boolean rol, 
			Boolean permis);

}
