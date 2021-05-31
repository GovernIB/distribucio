/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.List;
import java.util.Properties;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.ExcepcioLogDto;
import es.caib.distribucio.core.api.dto.IntegracioAccioDto;
import es.caib.distribucio.core.api.dto.IntegracioDto;
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
	public UsuariDto updateUsuariActual(UsuariDto asDto);

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
	 * Obté les integracions disponibles.
	 * 
	 * @return La llista d'integracions.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public List<IntegracioDto> integracioFindAll();

	/**
	 * Obté la llista de les darreres accions realitzades a una integració.
	 * 
	 * @param codi
	 *             Codi de la integració.
	 * @return La llista amb les darreres accions.
	 * @throws NotFoundException
	 *             Si no s'ha trobat la integració amb el codi especificat.
	 */
	@PreAuthorize("hasRole('DIS_SUPER')")
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(
			String codi) throws NotFoundException;

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

}
