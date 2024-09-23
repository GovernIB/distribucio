/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;

/**
 * Mètodes per a la gestió del log d'excepcions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ExcepcioLogHelper {

	public static final int DEFAULT_MAX_EXCEPCIONS = 20;

	private LinkedList<ExcepcioLogDto> excepcions = new LinkedList<ExcepcioLogDto>();

	public List<ExcepcioLogDto> findAll() {
		synchronized(excepcions) {
			int index = 0;
			for (ExcepcioLogDto excepcio: excepcions) {
				excepcio.setIndex(Long.valueOf(index++));
			}
		}
		return excepcions;
	}

	public void addExcepcio(
			Throwable exception,
			String source) {
		synchronized(excepcions) {
			while (excepcions.size() >= DEFAULT_MAX_EXCEPCIONS) {
				excepcions.remove(excepcions.size() - 1);
			}
			excepcions.add(
					0,
					new ExcepcioLogDto(exception, source));
		}
	}

}
