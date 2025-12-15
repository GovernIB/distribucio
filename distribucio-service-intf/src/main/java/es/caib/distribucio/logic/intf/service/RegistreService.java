/**
 * 
 */
package es.caib.distribucio.logic.intf.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import es.caib.distribucio.logic.intf.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;

/**
 * Declaració dels mètodes per a gestionar les anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RegistreService {

	/**
	 * Retorna la informació d'una anotació de registre situada dins un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultar.
	 * @param isVistaMoviments
	 * 			  Atribut per detectar si està en la vista de moviments, llavors no comprovar permisos bústia
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) throws NotFoundException;
	
	/**
	 * Retorna la informació d'una anotació de registre situada dins un contenidor.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultar.
	 * @param isVistaMoviments
	 * 			  Atribut per detectar si està en la vista de moviments, llavors no comprovar permisos bústia
	 * @param rolActual
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments, 
			String rolActual) throws NotFoundException;

	/**
	 * Retorna la informació de múltples anotacions de registre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param multipleRegistreIds
	 *            Atributs id del les anotacions que que es volen consultar.
	 * @param isAdmin 
	 * 			  Indica si l'usuari és administrador o no. Si no ho és filtrarà per les seves bústies.
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds,
			boolean isAdmin) throws NotFoundException;
	
	@PreAuthorize("isAuthenticated()")
	public List<RegistreDto> findByEntitatCodiAndNumero(
			Long entitatId,
			String numero) throws NotFoundException;
	
	/**
	 * Consulta el registre
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param isAdmin 
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<ContingutDto> findRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	List<Long> findRegistreIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments, boolean isAdmin);

	/**
	 * Torna a processar una anotació de registre sense bústia assignada. Assigna 
	 * la bústia per defecte, busca regles pendents i afegeix el primer moviment.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param registreId
	 *            Atribut id de l'anotació de registre que es vol tornar a processar.
	 * @return true si s'ha processat sense errors o false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public boolean reintentarBustiaPerDefecte(
			Long entitatId,
			Long registreId) throws NotFoundException;

	/**
	 * Torna a processar una anotació de registre pendent o amb error.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param registreId
	 *            Atribut id de l'anotació de registre que es vol tornar a processar.
	 * @return true si s'ha processat sense errors o false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long registreId) throws NotFoundException;

	/**
	 * Torna a processar un desat de annexos de manera manual.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param registreId
	 *            Atribut id de l'anotació de registre que es vol desar els seus annexos.
	 * @return true si s'ha processat sense errors o false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public boolean processarAnnexosAdmin(
			Long entitatId,
			Long registreId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getAnnexFitxer(
			Long annexId, boolean ambVersioImprimible) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getJustificant(
			Long registreId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public String getNumeroById(
			Long registreId) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public FitxerDto getAnnexFirmaFitxer(
			Long annexId,
			int indexFirma) throws NotFoundException;
	

	/** Retorna un fitxer amb el justificant i annexos comprimits en un .zip o 
	 * llença error en cas de no poder recuperar el contingut.
	 * @param rolActual Rol de l'usuari actual
	 * @return Objecte FitxerDto amb la documentacio en un arxiu .zip.
	 * @throws Error en el cas d'haver error consultant documents o creant un zip.
	 */	
	public FitxerDto getZipDocumentacio(
			Long registreId, 
			String rolActual,
			boolean ambVersioImprimible) throws Exception;

	/**
	 * Marca com a llegida una anotació de registre
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @return L'anotació modificada
	 */
	@PreAuthorize("isAuthenticated()")
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long registreId);
	
	/**
	 * Retorna la informació de l'expedient emmagatzemada a dins l'arxiu,
	 * donada una anotació de registre
	 * @param expedientId
	 *            Atribut id de l'anotacio de registre.
	 * @return la informació de l'expedient emmagatzemada a dins l'arxiu
	 */
	@PreAuthorize("isAuthenticated()")
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId);

	@PreAuthorize("isAuthenticated()")
	public RegistreAnnexDto getRegistreJustificant(Long entitatId, Long registreId, boolean isVistaMoviments)
			throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public RegistreAnnexDto getAnnexSenseFirmes(Long entitatId, Long registreId, Long annexId, boolean isVistaMoviments)
			throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public RegistreAnnexDto getAnnexAmbFirmes(Long entitatId, Long registreId,
			Long annexId, boolean isVistaMoviments) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id);

	public List<AnotacioRegistreEntrada> findForBackoffice(String identificador, Date dataRegistre);
	
	/** Mètode per trobar l'id del registre i les seves còpies a partir de l'identificador clau+número.
	 * 
	 * @param ids Objectes amb la clau + número.
	 * @return Retorna l'id del registre i les seves còpies que coincideixen amb el número i la clau.
	 * 
	 * @throws Excepció si no pot trobar el registre.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<Long> findRegistresPerIdentificador(AnotacioRegistreId id) throws Exception;

	/** Mètode per canviar l'estat a rebuda si el seu estat inicial es comunicada al backoffice en un registre d'anotació.
	 * 
	 * @param registreId
	 * @param observacions
	 */
	@PreAuthorize("isAuthenticated()")
	public void canviEstatComunicadaARebuda(
    		long registreId,
    		String observacions);
	
	/** Mètode per canviar l'estat a un registre d'anotació.
	 * 
	 * @param id
	 * @param estat
	 * @param observacions
	 */
	@PreAuthorize("isAuthenticated()")
	public void canviEstat(
			long id,
			Estat estat,
			String observacions);

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long registreId);

	/** 
	 * Mètode per classificar una anotació de registre pendent de processar amb un codi de procediment.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol classificar.
	 * @param procedimentCodi
	 *            Codi del procediment que es vol assignar a l'anotació.
	 * @param titol
	 *            nou titol pel l'anotació.
	 * @param string 
	 * @return true si l'anotació ha canviat de bústia o d'estat, false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long registreId,
            String tipus,
			String procedimentCodi,
			String serveiCodi,
			String titol) throws NotFoundException;

	/** 
	 * Mètode que retorna la llista de procediments disponibles donada una bústia.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param bustiaId
	 *            Atribut id de la bústia.
	 * @return la llista de procediments.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId);
	
	/** 
	 * Mètode que retorna la llista de serveis disponibles donada una bústia.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param bustiaId
	 *            Atribut id de la bústia.
	 * @return la llista de serveis.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public List<ServeiDto> classificarFindServeis(
			Long entitatId,
			Long bustiaId);

	@PreAuthorize("isAuthenticated()")
	public List<HistogramPendentsEntryDto> getHistogram();

	@PreAuthorize("isAuthenticated()")
	public int getNumberThreads();

	/**
	 * Assigna el registre a l'usuari actual.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el registre.
	 * @param id
	 *            Atribut id del registre/anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	void bloquejar(Long entitatId, Long id);

	/**
	 * Allibera un registre agafat per l'usuari actual.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el registre.
	 * @param id
	 *            Atribut id del registre/anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	void alliberar(Long entitatId, Long id);

	/**
	 * Consulta els moviments d'un registre
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param isAdmin 
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	PaginaDto<ContingutDto> findMovimentsRegistre(
			Long entitatId, 
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre, 
			PaginacioParamsDto paginacioParams);

	/**
	 * Consulta els ids dels moviments d'un registre
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param isAdmin 
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	List<String> findRegistreMovimentsIds(
			Long entitatId, 
			List<BustiaDto> bustiesUsuari, 
			RegistreFiltreDto filtre,
			boolean isAdmin);
	
	/**
	 * Consulta el registre
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @param isAdmin 
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	public PaginaDto<ContingutDto> findMovimentRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) throws NotFoundException;

	@PreAuthorize("isAuthenticated()")
	public List<ContingutDto> getPathContingut(
			Long entitatId,
			Long bustiaId) throws NotFoundException;

	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public void marcarSobreescriure(
			Long entitatId,
			Long registreId);

	@PreAuthorize("isAuthenticated()")
	public boolean marcarPendent(
			Long entitatId,
			Long registreId,
			String text,
			String rolActual);

	/** Invoca la validació de firmes i actualitza l'estat de l'annex. Si té firmes vàlides llavors
	 * es guarda com a definitiu.
	 * 
	 * @param entitatId
	 * @param registreId
	 * @param annexId
	 * @return
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public ValidacioFirmaEnum validarFirmes(
			Long entitatId,
			Long registreId,
			Long annexId);
		
	/**
	 * Cerca les dades (sense detall) de les firmes d'un annex. 
	 *  
	 *  @param registreId
	 **/
	@PreAuthorize("isAuthenticated()")
	public List<RegistreAnnexFirmaDto> getDadesAnnexFirmesSenseDetall(
			Long annexId);

	/**
	 * Retorna l'id d'un registre encriptat
	 * 
	 *  @param registreId
	 **/
	public String obtenirRegistreIdEncriptat (
			Long registreId);

	/**
	 * Retorna l'id d'un registre desencriptat
	 * 
	 *  @param clau
	 **/
	public String obtenirRegistreIdDesencriptat (
			String clau) throws Exception;

	public boolean reintentarProcessamentUser(Long entitatId, Long registreId);

	/**
	 * Modifica els valors de les dades d'un registre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el contingut.
	 * @param registreId
	 *            Atribut id del registre del qual es vol consultar el contingut.
	 * @param valors
	 *            Valors de les dades.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "')")
	public void dadaSave(
			Long entitatId,
			Long registreId,
			Map<String, Object> valors) throws NotFoundException;

//	/**
//	 * Recupera els valors de les dades d'un registre.
//	 * 
//	 * @param entitatId
//	 *            Atribut id de l'entitat a la qual pertany el contingut.
//	 * @param registreId
//	 *            Atribut id del registre del qual es vol consultar el contingut.
//	 * @throws NotFoundException
//	 *             Si no s'ha trobat l'objecte amb l'id especificat.
//	 */
//	List<DadaDto> obtenirDadesByRegistre(Long entitatId, Long registreId) throws NotFoundException;

	/**
	 * Retorna la informació d'una anotació de registre situada dins un contenidor amb les seves meta-dades. 
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultar.
	 * @param isVistaMoviments
	 * 			  Atribut per detectar si està en la vista de moviments, llavors no comprovar permisos bústia
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("isAuthenticated()")
	RegistreDto findOneAmbDades(Long entitatId, Long registreId, boolean isVistaMoviments, String rolActual)
			throws NotFoundException;

	/**
	 * Assigna el registre a l'usuari seleccionat.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat a la qual pertany el registre.
	 * @param registreId
	 *            Atribut id del registre/anotació.
	 * @param usuariCodi
	 *            Atribut codi de l'usuari.
	 * @param comentari
	 *            Comentari assignació.
	 */
	@PreAuthorize("isAuthenticated()")
	void assignar(Long entitatId, Long registreId, String usuariCodi, String comentari);

}
