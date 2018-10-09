/**
 * 
 */
package es.caib.distribucio.core.api.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;

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
	 *            Atribut id del l'anotació que es vol consultarcontenidor a on està situada l'anotació.
	 * @return els detalls de l'anotació.
	 * @throws NotFoundException
	 *             Si no s'ha trobat l'objecte amb l'id especificat.
	 */
	@PreAuthorize("hasRole('tothom')")
	public RegistreAnotacioDto findOne(
			Long entitatId,
			Long contingutId,
			Long registreId) throws NotFoundException;

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
	 * Distribueix periòdicament les anotacions de registre que han arribat
	 * al DISTRIBUCIO i s'han quedat pendents de distribuir de forma asíncrona.
	 */
	public void distribuirAnotacionsPendents();

	/**
	 * Processa periòdicament les regles de tipus backoffice Sistra pendents d'aplicar 
	 * a les anotacions de registre. Aquestes regles es revisen un cop cada minut (60000 milisegons).
	 */
	public void reglaAplicarPendentsBackofficeSistra();

	
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
	public boolean reglaReintentarAdmin(
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
	public boolean reglaReintentarUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getArxiuAnnex(
			Long annexId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getJustificant(
			Long registreId) throws NotFoundException;
	
	@PreAuthorize("hasRole('tothom')")
	public FitxerDto getAnnexFirmaContingut(
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
	public List<RegistreAnnexDetallDto> getAnnexosAmbArxiu(
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
	public RegistreAnotacioDto findAmbIdentificador(String identificador);

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
	 *            
	 * @return L'anotació modificada
	 */
	@PreAuthorize("hasRole('tothom')")
	public RegistreAnotacioDto marcarLlegida(
			Long entitatId,
			Long contingutId,
			Long registreId);

	RegistreAnnexDetallDto getRegistreJustificant(Long entitatId, Long contingutId, Long registreId)
			throws NotFoundException;

	RegistreAnnexDetallDto getAnnexAmbArxiu(Long entitatId, Long contingutId, Long registreId, String fitxerArxiuUuid)
			throws NotFoundException;

	RegistreAnnexDetallDto getAnnexFirmesAmbArxiu(Long entitatId, Long contingutId, Long registreId,
			String fitxerArxiuUuid) throws NotFoundException;
	
	List<RegistreAnnexDetallDto> getAnnexos(Long entitatId, Long contingutId, Long registreId) throws NotFoundException;

	void enviarAnotacioRegistreEntrada(String entitat, String unitatAdministrativa, RegistreAnotacio registreEntrada);
}
