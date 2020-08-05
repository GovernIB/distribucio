/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;

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
	 * @param contingutId
	 *            Atribut id del contingut pare a on està situada l'anotació.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultar.
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public RegistreDto findOne(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException;

	/**
	 * Retorna la informació de múltples anotacions de registre.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param multipleRegistreIds
	 *            Atributs id del les anotacions que que es volen consultar.
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds) throws NotFoundException;

	/**
	 * Rebutja un registre situat dins una bústia.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustia
	 *            Atribut id de la bústia.
	 * @param registreId
	 *            Atribut id de l'anotació de registre a afegir.
	 * @param motiu
	 *            Motiu del rebuig.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) throws NotFoundException;
	
	/**
	 * Consulta el contingut pendent a dins múltiples bústies.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param filtre del datatable
	 * @return El contingut pendent.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ContingutDto> findRegistreUser(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;
	
	/**
	 * Obté una llista d'anotacions de registre donades d'alta dins DISTRIBUCIO
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
	public PaginaDto<RegistreDto> findRegistreAdmin(
			Long entitatId,
			AnotacioRegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException;	
	
	/**
	 * Torna a processar una anotació de registre pendent o amb error.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustiaId
	 *            Atribut id de la bústia de la qual es vol modificar el permís.
	 * @param registreId
	 *            Atribut id de l'anotació de registre que es vol tornar a processar.
	 * @return true si s'ha processat sense errors o false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException;

	/**
	 * Torna a processar una anotació de registre pendent o amb error.
	 * 
	 * @param entitatId
	 *            Id de l'entitat.
	 * @param bustiaId
	 *            Atribut id de la bústia de la qual es vol modificar el permís.
	 * @param registreId
	 *            Atribut id de l'anotació de registre que es vol tornar a processar.
	 * @return true si s'ha processat sense errors o false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getAnnexFitxer(
			Long annexId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getJustificant(
			Long registreId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getAnnexFirmaFitxer(
			Long annexId,
			int indexFirma) throws NotFoundException;
	
	/**
	 * Retorna una llista d'annexos amb els les rutes dels fitxers asiciats
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param contingutId
	 *            Atribut id del contingut pare a on està situada l'anotació.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @return Llista d'annexos
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public List<RegistreAnnexDto> getAnnexosAmbArxiu(
			Long entitatId,
			Long contingutId,
			Long registreId) throws NotFoundException;
	
	/**
	 * Retorna un justificant amb contingut o sense
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param contingutId
	 *            Atribut id del contingut pare a on està situada l'anotació.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @param ambContingut
	 *            Atribut booleà per indicar si es vol recuperar al contingut del justificant
	 * @return annex justificant
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
//	@PreAuthorize("hasRole('tothom')")
//	public RegistreAnnexDetallDto getJustificant(
//			Long entitatId,
//			Long contingutId,
//			Long registreId,
//			boolean ambContingut) throws NotFoundException;

	/**
	 * Retorna la informació d'una anotació de registre segons el seu identificador.
	 * 
	 * @param identificador
	 *            Atribut identificador l'entitat.
	 * @return els detalls de l'anotació o null si no es troba.
	 */
	//@PreAuthorize("hasRole('DIS_BSTWS')")
	public RegistreDto findAmbIdentificador(String identificador);

	/**
	 * Mètode per actualitzar l'estat d'una anotació de registre.
	 * @param procesEstat
	 * 				Estat del procés per a l'anotació
	 * @param procesEstatSistra
	 * 				Estat del procés SISTRA per l'anotació
	 * @param resultat
	 * 				Descripció del resultat d'error o del processament SISTRA.
	 */
	//@PreAuthorize("hasRole('DIS_BSTWS')")
	public void updateProces(
			Long registreId,
			RegistreProcesEstatEnum procesEstat, 
			RegistreProcesEstatSistraEnum procesEstatSistra,
			String resultadoProcesamiento);

	/** Mètode per consultar les anotacions de registre per a les consultes de backoffices
	 * tipus Sistra
	 * @param identificadorProcediment
	 * @param identificadorTramit
	 * @param procesEstatSistra
	 * @param desdeDate
	 * @param finsDate
	 * @return La llista de números d'entrada de registres (identificadors) segons els paràmetres 
	 * de filtre.
	 */
	//@PreAuthorize("hasRole('DIS_BSTWS')")
	public List<String> findPerBackofficeSistra(
			String identificadorProcediment, 
			String identificadorTramit,
			RegistreProcesEstatSistraEnum procesEstatSistra, 
			Date desdeDate, 
			Date finsDate);
	
	/**
	 * Marca com a llegida una anotació de registre
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param contingutId
	 *            Atribut id del contingut pare a on està situada l'anotació.
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @return L'anotació modificada
	 */
	@PreAuthorize("hasRole('tothom')")
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long contingutId,
			Long registreId);
	
	/**
	 * Retorna la informació de l'expedient emmagatzemada a dins l'arxiu,
	 * donada una anotació de registre
	 * @param expedientId
	 *            Atribut id de l'anotacio de registre.
	 * @return la informació de l'expedient emmagatzemada a dins l'arxiu
	 */
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId);

	RegistreAnnexDto getRegistreJustificant(Long entitatId, Long contingutId, Long registreId)
			throws NotFoundException;

	RegistreAnnexDto getAnnexSenseFirmes(Long entitatId, Long contingutId, Long registreId, Long annexId)
			throws NotFoundException;

	RegistreAnnexDto getAnnexAmbFirmes(Long entitatId, Long contingutId, Long registreId,
			Long annexId) throws NotFoundException;
	
	List<RegistreAnnexDto> getAnnexos(Long entitatId, Long contingutId, Long registreId) throws NotFoundException;

	AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id);

	void canviEstat(AnotacioRegistreId id,
			Estat estat,
			String observacions);

	boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId);

	/** 
	 * Mètode per classificar una anotació de registre pendent de processar amb un codi de procediment.
	 * 
	 * @param entitatId
	 *            Atribut id de l'entitat.
	 * @param contingutId
	 *            Atribut id del contingut pare a on està situada l'anotació (bústia).
	 * @param registreId
	 *            Atribut id del l'anotació que es vol classificar.
	 * @param procedimentCodi
	 *            Codi del procediment que es vol assignar a l'anotació.
	 * @return true si l'anotació ha canviat de bústia o d'estat, false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String procedimentCodi) throws NotFoundException;

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
	@PreAuthorize("hasRole('tothom')")
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId);

	@PreAuthorize("hasRole('tothom')")
	public List<Long> findRegistreAdminIdsAmbFiltre(Long entitatId, AnotacioRegistreFiltreDto filtre);

}
