/**
 * 
 */
package es.caib.distribucio.persist.repository;

import javax.sql.DataSource;

import es.caib.distribucio.logic.intf.dto.ResultatConsultaDto;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DominiResultRepository {
	void setDataSource(DataSource dataSource,String consulta);
	ResultatConsultaDto findDominisByConsutla();
}
