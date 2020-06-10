/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.List;

import javax.mail.MessagingException;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;

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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('tothom')")
	public BustiaDto findById(
			Long entitatId,
			Long id) throws NotFoundException;

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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN') or hasRole('tothom')")
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
	@PreAuthorize("hasRole('tothom')")
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId, boolean mostrarInactives);

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
	@PreAuthorize("hasRole('DIS_BSTWS')")
	public Exception registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) throws NotFoundException;



	/**
	 * Consulta els identificadors del contingut pendent d'una bústia per filtre. Serveix per obtenir els ID's quan
	 * se seleccionin tots els elements.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param paginacioParamsDto 
	 * 
	 * @return Els identificadors del contingut pendent.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<Long> findIdsAmbFiltre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre);



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
	@PreAuthorize("hasRole('tothom')")
	public long contingutPendentBustiesAllCount(
			Long entitatId) throws NotFoundException;

	/**
	 * Reenvia un contingut pendent de la bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustiaOrigenId
	 *            Atribut id de la bústia d'origen.
	 * @param bustiaDestiId
	 *            Atribut id de la bústia de destí.
	 * @param contingutId
	 *            Atribut id del contingut que es vol reenviar.
	 * @param comentari
	 *            Comentari pel reenviament.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void registreReenviar(
			Long entitatId,
			Long bustiaOrigenId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari) throws NotFoundException;

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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public List<BustiaDto> findAmbEntitat(Long entitatId);

	@PreAuthorize("hasRole('tothom')")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, String bustiaNomFiltre, Long unitatIdFiltre,
			Boolean unitatObsoleta);

	@PreAuthorize("hasRole('tothom')")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId, String bustiaNomFiltre,
			Long unitatIdFiltre, Boolean unitatObsoleta);

	@PreAuthorize("hasRole('tothom')")
	public String getApplictionMetrics();

	@PreAuthorize("hasRole('tothom')")
	public void registreAnotacioEnviarPerEmail(Long entitatId, Long contingutId, Long registreId, String adresses)
			throws MessagingException;

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
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public int moureAnotacions(
			long entitatId, 
			long bustiaId, 
			long destiId, 
			String comentari);

	List<UsuariPermisDto> getUsersPermittedForBustia(Long bustiaId);

	List<BustiaDto> findAmbUnitatId(Long entitatId,
			Long unitatId);	

}
