/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.ContingutLogDto;
import es.caib.distribucio.core.api.dto.ContingutMovimentDto;
import es.caib.distribucio.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutLogEntity;
import es.caib.distribucio.core.entity.ContingutLogEntity.Builder;
import es.caib.distribucio.core.entity.ContingutLogParamEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.repository.ContingutLogParamRepository;
import es.caib.distribucio.core.repository.ContingutLogRepository;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.ContingutRepository;

/**
 * Utilitat per a gestionar el registre d'accions dels contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ContingutLogHelper {

	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private ContingutLogRepository contingutLogRepository;
	@Resource
	private ContingutLogParamRepository contingutLogParamRepository;
	@Resource
	private ContingutMovimentRepository contingutMovimentRepository;

	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;



	public ContingutLogEntity logCreacio(
			ContingutEntity contingut,
			boolean logContingutPare) {
		
		List<String> params = new ArrayList<>();
		params.add(contingut.getNom());
		params.add((contingut.getPare() != null) ? contingut.getPare().getId().toString() : null);
		
		return log(
				contingut,
				LogTipusEnumDto.CREACIO,
				null,
				contingut,
				getLogObjecteTipusPerContingut(contingut),
				null,
				params,
				logContingutPare);
	}

	public ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			List<String> params,
			boolean logContingutPare) {
		return log(
				contingut,
				tipus,
				null,
				null,
				null,
				null,
				params,
				logContingutPare);
	}
//	public ContingutLogEntity log(
//			ContingutEntity contingut,
//			LogTipusEnumDto tipus,
//			Persistable<? extends Serializable> objecte,
//			LogObjecteTipusEnumDto objecteTipus,
//			LogTipusEnumDto objecteLogTipus,
//			String param1,
//			String param2,
//			boolean logContingutPare) {
//		return log(
//				contingut,
//				tipus,
//				null,
//				objecte,
//				objecteTipus,
//				objecteLogTipus,
//				param1,
//				param2,
//				logContingutPare);
//	}
	public ContingutLogEntity logMoviment(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutMovimentEntity contingutMoviment,
			boolean logContingutPare) {
		return log(
				contingut,
				tipus,
				contingutMoviment,
				null,
				null,
				null,
				null,
				logContingutPare);
	}

	public List<ContingutLogDto> findLogsContingut(
			ContingutEntity contingut) {
		List<ContingutLogEntity> logs = contingutLogRepository.findByContingutOrderByCreatedDateAsc(
				contingut);
		List<ContingutLogDto> dtos = new ArrayList<ContingutLogDto>();
		for (ContingutLogEntity log: logs) {
			ContingutLogDto dto = new ContingutLogDto();
			emplenarLogDto(log, dto);
			dtos.add(dto);
		}
		return dtos;
	}
	
	
	
	
	
	
	
	

	public ContingutLogDetallsDto findLogDetalls(
			ContingutEntity contingut,
			Long contingutLogId) {
		ContingutLogEntity log = contingutLogRepository.findOne(contingutLogId);
		if (!log.getContingut().equals(contingut)) {
			throw new ValidationException(
					contingutLogId,
					ContingutLogEntity.class,
					"El contingut del log (id=" + log.getContingut().getId() + ") no coincideix amb el contingut (id=" + contingut.getId() + ") expecificat");
		}
		ContingutLogDetallsDto detalls = new ContingutLogDetallsDto();
		emplenarLogDto(log, detalls);
		if (log.getContingutMoviment() != null) {
			detalls.setContingutMoviment(
					toContingutMovimentDto(
							log.getContingutMoviment(),
							contenidorHelper.toContingutDto(
									log.getContingutMoviment().getContingut())));
		}
		if (log.getPare() != null) {
			ContingutLogDto pare = new ContingutLogDto();
			emplenarLogDto(log.getPare(), pare);
			detalls.setPare(pare);
		}
		if (log.getObjecteId() != null) {
			String objecteNom = null;
			switch (log.getObjecteTipus()) {
			case CONTINGUT:
			case CARPETA:
			case DOCUMENT:
			case EXPEDIENT:
			case REGISTRE:
				ContingutEntity c = contingutRepository.findOne(
						new Long(log.getObjecteId()));
				objecteNom = c.getNom();
				break;
			case RELACIO:
				String[] ids = log.getObjecteId().split("#");
				if (ids.length >= 2) {
					ContingutEntity exp1 = contingutRepository.findOne(
							new Long(ids[0]));
					ContingutEntity exp2 = contingutRepository.findOne(
							new Long(ids[1]));
					objecteNom = exp1.getNom() + " <-> " + exp2.getNom();
					break;
				}
			case ALTRES:
			default:
				objecteNom = "???" + log.getObjecteTipus().name() + "#" + log.getObjecteId() + "???";
				break;
			}
			detalls.setObjecteNom(objecteNom);
		} 
		if (detalls.getContenidorMoviment() != null) {
			detalls.getContenidorMoviment().setContingut(null); //to avoid circular dependency in json
		}
		return detalls;
	}

	public List<ContingutMovimentDto> findMovimentsContingut(
			ContingutEntity contingut) {
		List<ContingutMovimentEntity> moviments = contingutMovimentRepository.findByContingutOrderByCreatedDateAsc(
				contingut);
		ContingutDto contingutDto = contenidorHelper.toContingutDto(contingut);
		List<ContingutMovimentDto> dtos = new ArrayList<ContingutMovimentDto>();
		for (ContingutMovimentEntity moviment: moviments) {
			dtos.add(
					toContingutMovimentDto(
							moviment,
							contingutDto));
		}
		return dtos;
	}



	private ContingutLogEntity log(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutMovimentEntity contingutMoviment,
			Persistable<? extends Serializable> objecte,
			LogObjecteTipusEnumDto objecteTipus,
			LogTipusEnumDto objecteLogTipus,
			List<String> params,
			boolean logContingutPare) {
		ContingutLogEntity logPare = logSave(
				contingut,
				tipus,
				null,
				contingutMoviment,
				objecte,
				objecteTipus,
				objecteLogTipus,
				params);
		if (logContingutPare) {
			if (contingutMoviment == null) {
				if (contingut.getPare() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingut.getPare(),
							logPare);
				}
			} else {
				if (contingutMoviment.getOrigen() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingutMoviment.getOrigen(),
							logPare);
				}
				if (contingutMoviment.getDesti() != null) {
					logContingutSuperior(
							contingut,
							tipus,
							contingutMoviment.getDesti(),
							logPare);
				}
			}
		}
		return logPare;
	}

	private void logContingutSuperior(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutEntity contingutSuperior,
			ContingutLogEntity contingutLogPare) {
		logSave(
				contingutSuperior,
				LogTipusEnumDto.MODIFICACIO,
				contingutLogPare,
				null,
				contingut,
				getLogObjecteTipusPerContingut(contingut),
				tipus,
				null);
	}

	private ContingutLogEntity logSave(
			ContingutEntity contingut,
			LogTipusEnumDto tipus,
			ContingutLogEntity pare,
			ContingutMovimentEntity contingutMoviment,
			Persistable<? extends Serializable> objecte,
			LogObjecteTipusEnumDto objecteTipus,
			LogTipusEnumDto objecteLogTipus,
			List<String> params) {
		
		
		logger.debug("Guardant log per contenidor (" +
				"contingutId=" + contingut.getId() + ", " +
				"tipus=" + tipus + ", " +
				"logPareId=" + ((pare != null) ? pare.getId() : null) + ", " +
				"contingutMovimentId=" + ((contingutMoviment != null) ? contingutMoviment.getId() : null) + ", " +
				"objecte=" + ((objecte != null) ? objecte.getId() : "null") + ", " +
				"objecteLogTipus=" + ((objecteLogTipus != null) ? objecteLogTipus.name() : "null") + ", " +
				"params=" + params +  ")");
		
		
		Builder logBuilder = ContingutLogEntity.getBuilder(
				tipus,
				contingut).
				param1(params != null ? params.get(0) : null).
				param2(params != null ? params.get(1) : null).
				pare(pare).
				contingutMoviment(contingutMoviment);
		
		if (objecte != null) {
			logBuilder.
			objecte(objecte).
			objecteTipus(objecteTipus).
			objecteLogTipus(objecteLogTipus);
		}
		
		ContingutLogEntity contingutLogEntity = contingutLogRepository.save(
				logBuilder.build());
		
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				if (params.get(i) != null) {
					ContingutLogParamEntity contingutLogParamEntity = ContingutLogParamEntity.getBuilder(
							contingutLogEntity,
							i + 1,
							params.get(i)).build();
					contingutLogParamRepository.save(contingutLogParamEntity);
					contingutLogEntity.addLogParam(contingutLogParamEntity);
				}
			}
		}

		return contingutLogEntity;
	}
	
	


	private void emplenarLogDto(
			ContingutLogEntity log,
			ContingutLogDto dto) {
		dto.setId(log.getId());
		if (log.getCreatedDate() != null)
			dto.setCreatedDate(log.getCreatedDate().toDate());
		dto.setCreatedBy(
				conversioTipusHelper.convertir(
						log.getCreatedBy(),
						UsuariDto.class));
		if (log.getLastModifiedDate() != null)
			dto.setLastModifiedDate(log.getLastModifiedDate().toDate());
		dto.setLastModifiedBy(
				conversioTipusHelper.convertir(
						log.getLastModifiedBy(),
						UsuariDto.class));
		dto.setTipus(
				LogTipusEnumDto.valueOf(
						log.getTipus().name()));
		if (log.getObjecteId() != null) {
			dto.setObjecteId(log.getObjecteId());
			dto.setObjecteTipus(
					LogObjecteTipusEnumDto.valueOf(
							log.getObjecteTipus().name()));
			if (log.getObjecteLogTipus() != null) {
				dto.setObjecteLogTipus(
						LogTipusEnumDto.valueOf(
								log.getObjecteLogTipus().name()));
			}
		}
		
//		dto.setParam1(log.getParam1());
//		dto.setParam2(log.getParam2());
		
		List<String> params = new ArrayList<>();
		
		Collections.sort(log.getParams());
		for (ContingutLogParamEntity contingutLogParamEntity : log.getParams()) {
			params.add(contingutLogParamEntity.getValor());
		}
		dto.setParams(params);
		
		logger.debug("Reading log (" +
				"id=" + log.getId() + ", " +
				"tipus=" + log.getTipus() + ", " +
				"logPareId=" + ((log.getPare() != null) ? log.getPare().getId() : null) + ", " +
				"contingutMovimentId=" + ((log.getContingutMoviment() != null) ? log.getContingutMoviment().getId() : null) + ", " +
				"objecteId=" + log.getObjecteId()  + ", " +
				"objecteLogTipus=" + ((log.getObjecteTipus() != null) ? log.getObjecteTipus().name() : "null") + ", " +
				"params=" + params + ")");
		
	}
	
	public String getParam(
			List<ContingutLogParamEntity> logParams,
			long numero) {
		for (ContingutLogParamEntity contingutLogParam : logParams) {
			if (contingutLogParam.getNumero() == numero) {
				return contingutLogParam.getValor();
			}
		}
		return null;
	}
	
	

	private ContingutMovimentDto toContingutMovimentDto(
			ContingutMovimentEntity moviment,
			ContingutDto contingut) {
		ContingutMovimentDto dto = new ContingutMovimentDto();
		dto.setId(moviment.getId());
		if (moviment.getCreatedDate() != null)
			dto.setData(moviment.getCreatedDate().toDate());
		dto.setComentari(moviment.getComentari());
		dto.setContingut(contingut);
		dto.setRemitent(
				conversioTipusHelper.convertir(
						moviment.getRemitent(),
						UsuariDto.class));
		if (moviment.getOrigen() != null) {
			dto.setOrigen(
					contenidorHelper.toContingutDto(
							moviment.getOrigen()));
		}
		dto.setDesti(
				contenidorHelper.toContingutDto(
						moviment.getDesti()));
		return dto;
	}

	private LogObjecteTipusEnumDto getLogObjecteTipusPerContingut(
			ContingutEntity contingut) {
		LogObjecteTipusEnumDto objecteTipus;
		if (contingut instanceof BustiaEntity) {
			objecteTipus = LogObjecteTipusEnumDto.BUSTIA;
		} else {
			objecteTipus = LogObjecteTipusEnumDto.CONTINGUT;
		}
		return objecteTipus;
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutLogHelper.class);

}
