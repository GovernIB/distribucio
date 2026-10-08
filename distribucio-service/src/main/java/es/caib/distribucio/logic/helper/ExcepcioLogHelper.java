/**
 *
 */
package es.caib.distribucio.logic.helper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.PermissionDeniedException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.persist.resourceentity.ExcepcioLogResourceEntity;
import es.caib.distribucio.persist.resourcerepository.ExcepcioLogResourceRepository;
import lombok.RequiredArgsConstructor;

/**
 * Mètodes per a la gestió del log d'excepcions (taula dis_excepcio_log).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class ExcepcioLogHelper {

	private final ExcepcioLogResourceRepository excepcioLogResourceRepository;
	private final PaginacioHelper paginacioHelper;
	private final ConversioTipusHelper conversioTipusHelper;

	/**
	 * Excepcions ja desades i l'id de la seva fila. Throwable no redefineix equals/hashCode,
	 * així que la comparació és per identitat; les claus febles eviten retenir les excepcions
	 * un cop acabada la petició que les ha produïdes.
	 */
	private final Map<Throwable, Long> excepcionsDesades = Collections.synchronizedMap(new WeakHashMap<>());

	/**
	 * Guarda l'excepció en una transacció nova perquè no es perdi amb el rollback de la
	 * transacció on s'ha produït.
	 * <p>
	 * La mateixa excepció arriba diverses vegades mentre puja per la pila: des de l'aspecte
	 * de cada servei que travessa i des del del controller. Només es desa la primera (la que
	 * té l'origen més precís); les següents, reconegudes per identitat a ella o a qualsevol de
	 * les seves causes (en mode EAR el controller la rep dins una EJBException), només hi
	 * afegeixen la URI si encara no en tenia.
	 * <p>
	 * Els textos es retallen a la mida de la columna: un missatge massa llarg faria fallar
	 * l'insert. Així i tot, qui el crida ha de capturar qualsevol error (veure
	 * {@code AplicacioServiceImpl.excepcioSave}), perquè la fallada de l'insert o del commit
	 * no pot substituir l'excepció original.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addExcepcio(
			String uri,
			Throwable exception,
			String origen) {
		if (exception == null) {
			return;
		}
		ExcepcioLogResourceEntity jaDesada = cercarJaDesada(exception);
		if (jaDesada != null) {
			if (jaDesada.getUri() == null && uri != null) {
				jaDesada.setUri(retallar(uri, ExcepcioLogResourceEntity.URI_LENGTH));
			}
			excepcionsDesades.put(exception, jaDesada.getId());
			return;
		}
		ExcepcioLogResourceEntity desada = excepcioLogResourceRepository.save(toEntity(uri, exception, origen));
		excepcionsDesades.put(exception, desada.getId());
	}

	@Transactional(readOnly = true)
	public PaginaDto<ExcepcioLogDto> findPage(PaginacioParamsDto paginacioParams) {
		Sort sort = paginacioHelper.toSpringDataSort(paginacioParams);
		if (sort == null) {
			sort = Sort.by(Sort.Direction.DESC, "data");
		}
		// Desempat per id perquè l'ordre de les excepcions del mateix instant sigui estable entre pàgines
		sort = sort.and(Sort.by(Sort.Direction.DESC, "id"));
		return paginacioHelper.toPaginaDto(
				excepcioLogResourceRepository.findAll(
						PageRequest.of(
								paginacioParams.getPaginaNum(),
								paginacioParams.getPaginaTamany(),
								sort)),
				ExcepcioLogDto.class);
	}

	@Transactional(readOnly = true)
	public ExcepcioLogDto findById(Long id) {
		ExcepcioLogResourceEntity excepcio = excepcioLogResourceRepository.findById(id).orElseThrow(
				() -> new NotFoundException(id, ExcepcioLogDto.class));
		return conversioTipusHelper.convertir(excepcio, ExcepcioLogDto.class);
	}

	/** Esborra les excepcions anteriors a la data indicada i retorna quantes se n'han esborrat. */
	@Transactional
	public int esborrarAnteriorsA(Date data) {
		return excepcioLogResourceRepository.deleteByDataBefore(data);
	}

	/**
	 * Cerca la fila d'una excepció ja desada que sigui l'excepció rebuda o una de les seves
	 * causes. Si la fila ja no existeix (el commit que la desava va fallar, o s'ha esborrat)
	 * retorna null perquè es torni a desar.
	 */
	private ExcepcioLogResourceEntity cercarJaDesada(Throwable exception) {
		@SuppressWarnings("unchecked")
		List<Throwable> cadena = ExceptionUtils.getThrowableList(exception);
		for (Throwable throwable: cadena) {
			Long id = excepcionsDesades.get(throwable);
			if (id != null) {
				return excepcioLogResourceRepository.findById(id).orElse(null);
			}
		}
		return null;
	}

	private ExcepcioLogResourceEntity toEntity(
			String uri,
			Throwable exception,
			String origen) {
		ExcepcioLogResourceEntity entity = new ExcepcioLogResourceEntity();
		entity.setData(new Date());
		entity.setTipus(retallar(exception.getClass().getName(), ExcepcioLogResourceEntity.TIPUS_LENGTH));
		entity.setMessage(retallar(exception.getMessage(), ExcepcioLogResourceEntity.MESSAGE_MAX_CARACTERS));
		entity.setStacktrace(ExceptionUtils.getStackTrace(exception));
		entity.setUri(retallar(uri, ExcepcioLogResourceEntity.URI_LENGTH));
		entity.setOrigen(retallar(origen, ExcepcioLogResourceEntity.ORIGEN_LENGTH));
		entity.setEntitatCodi(retallar(ConfigHelper.getEntitatActualCodi(), ExcepcioLogResourceEntity.ENTITAT_CODI_LENGTH));
		Object objectId = null;
		Class<?> objectClass = null;
		if (exception instanceof NotFoundException) {
			objectId = ((NotFoundException)exception).getObjectId();
			objectClass = ((NotFoundException)exception).getObjectClass();
		} else if (exception instanceof PermissionDeniedException) {
			objectId = ((PermissionDeniedException)exception).getObjectId();
			objectClass = ((PermissionDeniedException)exception).getObjectClass();
			entity.setParam1(retallar(((PermissionDeniedException)exception).getUserName(), ExcepcioLogResourceEntity.PARAM_LENGTH));
			entity.setParam2(retallar(((PermissionDeniedException)exception).getPermissionName(), ExcepcioLogResourceEntity.PARAM_LENGTH));
		} else if (exception instanceof ValidationException) {
			objectId = ((ValidationException)exception).getObjectId();
			objectClass = ((ValidationException)exception).getObjectClass();
			entity.setParam1(retallar(((ValidationException)exception).getError(), ExcepcioLogResourceEntity.PARAM_LENGTH));
		}
		if (objectId != null) {
			entity.setObjectId(retallar(objectId.toString(), ExcepcioLogResourceEntity.OBJECT_LENGTH));
		}
		if (objectClass != null) {
			entity.setObjectClass(retallar(objectClass.getName(), ExcepcioLogResourceEntity.OBJECT_LENGTH));
		}
		return entity;
	}

	private static String retallar(String text, int longitud) {
		return StringUtils.abbreviate(text, longitud);
	}

}
