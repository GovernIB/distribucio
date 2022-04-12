/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.List;
import java.util.Properties;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ExcepcioLogDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.exception.NotFoundException;

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
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public String getVersioActual();

	/**
	 * Obté la propietat de la data de la versió actual de l'aplicació.
	 * 
	 * @return La versió actual.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public String getVersioData();

	
	/**
	 * Processa l'autenticació d'un usuari.
	 * 
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'usuari amb el codi de l'usuari autenticat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public void processarAutenticacioUsuari() throws NotFoundException;
	
	/**
	 * Retorna el valor de la propietat plugin.passarelafirma.ignorar.modal.ids.
	 * 
	 * @return el valor del paràmetre.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public String propertyPluginPassarelaFirmaIgnorarModalIds();

	/**
	 * Obté l'usuari actual.
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public UsuariDto getUsuariActual();
	
	/**
	 * Modifica la configuració de l'usuari actual
	 * 
	 * @return L'usuari actual.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public UsuariDto updateUsuariActual(UsuariDto asDto, Long entitatId);

	/**
	 * Obté un usuari donat el seu codi.
	 * 
	 * @param codi
	 *            Codi de l'usuari a cercar.
	 * @return L'usuari obtingut o null si no s'ha trobat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public UsuariDto findUsuariAmbCodi(String codi);

	/**
	 * Consulta els usuaris donat un text.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public List<UsuariDto> findUsuariAmbText(String text);

	/**
	 * Consulta els usuaris per codi o nom que coincideixin amb el text donat.
	 * 
	 * @param text
	 *            Text per a fer la consulta.
	 * @return La llista d'usuaris.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public List<UsuariDto> findUsuariAmbCodiAndNom(String text);
	
	/**
	 * Emmagatzema una excepció llençada per un servei.
	 * 
	 * @param exception
	 *             L'excepció a emmagatzemar.
	 */
	public void excepcioSave(Throwable exception);

	/**
	 * Consulta la informació d'una excepció donat el seu índex.
	 * 
	 * @param index
	 *             L'index de l'excepció.
	 * @return L'excepció.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public ExcepcioLogDto excepcioFindOne(Long index);

	/**
	 * Retorna una llista amb les darreres excepcions emmagatzemades.
	 * 
	 * @return La llista amb les darreres excepcions.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public List<ExcepcioLogDto> excepcioFindAll();

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
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public String propertyBaseUrl();

	/**
	 * Retorna si el plugin d'arxiu està actiu.
	 * 
	 * @return true si està actiu o false si no ho està.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public boolean isPluginArxiuActiu();


	/**
	 * Retorna els valors dels paràmetres de configuració de l'aplicació
	 * que tenen un determinat prefix.
	 * 
	 * @return els valors com a un objecte Properties.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public Properties propertyFindByPrefix(String prefix);

	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	String propertyFindByNom(String nom);
	
	/**
	 * Obté la bústia per defecte de l'usuari i entitat actuals.
	 * 
	 * @param usuari Usuari actual.
	 * 
	 * @param entitatId Id entitat acutual.
	 * 
	 * @return La bústia per defecte.
	 */
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public BustiaDto getBustiaPerDefecte(UsuariDto usuari, Long entitatId);
	
	/**
	 * Actualitza el rol de l'usuari actual a la taula de dades d'usuaris.
	 * 
	 * @param rolActual Rol de l'usuari actual.
	 * 
	 */	
	@PreAuthorize("hasRole('DIS_SUPER') or hasRole('DIS_ADMIN') or hasRole('tothom')")
	public void setRolUsuariActual(String rolActual);

}
