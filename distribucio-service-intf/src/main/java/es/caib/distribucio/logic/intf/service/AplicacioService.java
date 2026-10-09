/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.List;
import java.util.Properties;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Declaració dels mètodes comuns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AplicacioService {

	/**
	 * Obté la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public String getVersioActual();

	/**
	 * Obté la propietat de la data de la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public String getVersioData();

	
	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PreAuthorize("isAuthenticated()")
	public void processarAutenticacioUsuari() throws NotFoundException;
	
	/**
	 * Retorna el valor de la propietat plugin.passarelafirma.ignorar.modal.ids.
	 * 
	 * @return el valor del paràmetre.
	 */
	@PreAuthorize("isAuthenticated()")
	public String propertyPluginPassarelaFirmaIgnorarModalIds();

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto getUsuariActual();
	
	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto updateUsuariActual(UsuariDto asDto, Long entitatId);

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("isAuthenticated()")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Consulta els usuaris per codi o nom que coincideixin amb el text donat.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<UsuariDto> findUsuariAmbCodiAndNom(String text);
	
	/**
	 * Emmagatzema una excepció al log d'excepcions. No llança mai cap excepció: si no es
	 * pot guardar, només ho deixa escrit al log de l'aplicació.
	 *
	 * @param uri
	 *             URI de la petició on s'ha produït l'excepció (pot ser null).
	 * @param exception
	 *             L'excepció a emmagatzemar.
	 * @param origen
	 *             Descripció del punt on s'ha capturat l'excepció.
	 */
	public void excepcioSave(String uri, Throwable exception, String origen);

	/**
	 * Consulta la informació d'una excepció donat el seu identificador.
	 *
	 * @param id
	 *             L'identificador de l'excepció.
	 * @return L'excepció.
	 * @throws NotFoundException
	 *             Si no existeix l'excepció (p. ex. perquè ja s'ha esborrat per antiga).
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public ExcepcioLogDto excepcioFindOne(Long id) throws NotFoundException;

	/**
	 * Retorna una pàgina de les excepcions emmagatzemades.
	 *
	 * @param paginacioParams
	 *             Paràmetres de paginació i ordenació.
	 * @return La pàgina d'excepcions.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public PaginaDto<ExcepcioLogDto> excepcioFindPage(PaginacioParamsDto paginacioParams);

	/**
	 * Retorna una llista amb els diferents rols els quals
	 * tenen assignat algun permis.
	 * 
	 * @return La llista amb els rols.
	 */
	public List<String> permisosFindRolsDistinctAll();

	/**
	 * Retorna el valor de la propietat es.caib.distribucio.base.url.
	 * 
	 * @return el valor del paràmetre.
	 */
	@PreAuthorize("isAuthenticated()")
	public String propertyBaseUrl();

	/**
	 * Retorna si el plugin d'arxiu està actiu.
	 * 
	 * @return true si està actiu o false si no ho està.
	 */
	@PreAuthorize("isAuthenticated()")
	public boolean isPluginArxiuActiu();


	/**
	 * Retorna els valors dels paràmetres de configuració de l'aplicació
	 * que tenen un determinat prefix.
	 * 
	 * @return els valors com a un objecte Properties.
	 */
	@PreAuthorize("isAuthenticated()")
	public Properties propertyFindByPrefix(String prefix);

	@PreAuthorize("isAuthenticated()")
	public String propertyFindByNom(String nom);

	/**
	 * Obté la bústia per defecte de l'usuari i entitat actuals.
	 * 
	 * @param usuari Usuari actual.
	 * 
	 * @param entitatId Id entitat acutual.
	 * 
	 * @return La bústia per defecte.
	 */
	@PreAuthorize("isAuthenticated()")
	public BustiaDto getBustiaPerDefecte(UsuariDto usuari, Long entitatId);

	/**
	 * Actualitza el rol de l'usuari actual a la taula de dades d'usuaris.
	 * 
	 * @param rolActual Rol de l'usuari actual.
	 * 
	 */
	@PreAuthorize("isAuthenticated()")
	public void setRolUsuariActual(String rolActual);

	/**
	 * Retorna l'objecte amb informació d'autenticació obtingut del
	 * SecurityContext de Spring.
	 * 
	 * @return l'objecte amb informació d'autenticació.
	 */
	public Authentication getAuthentication();

	/** Consulta les dades de l'usuari i actualitza les dades bàsiques a la taula d'usuaris. Aquesta
	 * funció es crida en entrar a la pàgina de configuració del perfil.
	 * 
	 * @param codi Codi de l'usuari per actualitzar.
	 * @return Retorna el DTO de l'usuari actualitzat.
	 */
	public UsuariDto updateUsuari(String codi);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_SUPER + "')")
	public Long updateUsuariCodi(String codiAntic, String codiNou);

	/** Consulta els rols de l'usuari actual amb el plugin de dades d'usuari. */
	@PreAuthorize("isAuthenticated()")
	public List<String> getRolsUsuariActual();
	
}
