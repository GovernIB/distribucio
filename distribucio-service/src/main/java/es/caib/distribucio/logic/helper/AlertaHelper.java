/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.persist.entity.AlertaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.repository.AlertaRepository;
import es.caib.distribucio.persist.repository.ContingutRepository;

/**
 * Mètodes comuns per a gestionar les alertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AlertaHelper {

	@Autowired
	private AlertaRepository alertaRepository;
	@Autowired
	private ContingutRepository contingutRepository;

	public AlertaEntity crearAlerta(
			String text,
			String error,
			boolean llegida,
			Long contingutId) {
		Optional<ContingutEntity> contingut = contingutRepository.findById(contingutId);
		AlertaEntity entity = AlertaEntity.getBuilder(
				text,
				error,
				llegida,
				contingut.orElse(null)).build();
		return alertaRepository.save(entity);
	}

	public AlertaEntity crearAlerta(
			String text,
			Exception ex,
			Long contingutId) {
		String error = null;
		if (ex != null) {
			error = ExceptionUtils.getStackTrace(ex).substring(0, 2048);
		}
		return crearAlerta(
				text,
				error,
				false,
				contingutId);
	}

}
