package es.caib.distribucio.logic.intf.service;

import java.util.Date;

import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricFiltreDto;

/**
 * Declaració dels mètodes per a la consulta de dades estadístiques
 * d'anotacions per unitat organitzativa, per estat i per bústies.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricService {

	/** Mètode general de filtr i consulta de dades històriques. Es pot
	 * filtrar per un rang de dates, per UO superior o específic i 
	 * seleccionar les dades a retornar i si fer el càlcul mensual o 
	 * diari.
	 * 
	 * @param entitatId Identificador de la identitat actual per l'usuari.
	 * @param filtre Conté les opcions de filtre i de dades a retornar.
	 * 
	 * @return Segons l'objecte filtre es retornen dades d'anotacions, estats i 
	 * bústies per un rang de dadets per consulta de mes o dia i per unes unitats
	 * organitzatives concretes o per l'entitat en general si no es filtra per UO.
	 * 
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public HistoricDadesDto getDadesHistoriques(
			Long entitatId,
			HistoricFiltreDto filtre);

	/** Mètode per invocar el càlcul de dades històriques per la data sol·licitada. Normalment
	 * s'usa des del formulari per calcular les dates pel mateix dia.
	 * 
	 * @param data
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public void calcularDadesHistoriques(Date data);

	/** Mètode per recalcular els totals d'un dia passat a partir de les dades del dia següent.
	 * 
	 * @param data
	 */
	@PreAuthorize("hasRole('" + BaseConfig.ROLE_ADMIN + "') or hasRole('" + BaseConfig.ROLE_ADMIN_LECTURA + "')")
	public void recalcularTotals(Date data);

}
