/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.HistogramPendentsEntryDto;
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
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultar.
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public RegistreDto findOne(
			Long entitatId,
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
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<ContingutDto> findRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) throws NotFoundException;
	

	@PreAuthorize("hasRole('tothom')")
	List<Long> findRegistreIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean isAdmin);
	

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
	@PreAuthorize("hasRole('DIS_ADMIN')")
	public boolean reintentarProcessamentAdmin(
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
	@PreAuthorize("hasRole('tothom')")
	public boolean reintentarProcessamentUser(
			Long entitatId,
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
	

	/** Retorna un fitxer amb el justificant i annexos comprimits en un .zip o 
	 * llença error en cas de no poder recuperar el contingut.
	 * @return Objecte FitxerDto amb la documentacio en un arxiu .zip.
	 * @throws Error en el cas d'haver error consultant documents o creant un zip.
	 */
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getZipDocumentacio(
			Long registreId) throws Exception;
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
	 * @param registreId
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @return L'anotació modificada
	 */
	@PreAuthorize("hasRole('tothom')")
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
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId);

	@PreAuthorize("hasRole('tothom')")
	public RegistreAnnexDto getRegistreJustificant(Long entitatId, Long registreId)
			throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public RegistreAnnexDto getAnnexSenseFirmes(Long entitatId, Long registreId, Long annexId)
			throws NotFoundException;

	@PreAuthorize("hasRole('tothom')")
	public RegistreAnnexDto getAnnexAmbFirmes(Long entitatId, Long registreId,
			Long annexId) throws NotFoundException;
	
	public AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id);

	public void canviEstat(AnotacioRegistreId id,
			Estat estat,
			String observacions);

	@PreAuthorize("hasRole('DIS_ADMIN')")
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
	 * @return true si l'anotació ha canviat de bústia o d'estat, false en cas contrari.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public ClassificacioResultatDto classificar(
			Long entitatId,
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
	public List<HistogramPendentsEntryDto> getHistogram();

	@PreAuthorize("hasRole('tothom')")
	public int getNumberThreads();


}
