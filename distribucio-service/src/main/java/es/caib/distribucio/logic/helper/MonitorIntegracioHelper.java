package es.caib.distribucio.logic.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.intf.dto.MonitorIntegracioDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioParamDto;
import es.caib.distribucio.persist.entity.MonitorIntegracioEntity;
import es.caib.distribucio.persist.entity.MonitorIntegracioParamEntity;
import es.caib.distribucio.persist.repository.MonitorIntegracioParamRepository;
import es.caib.distribucio.persist.repository.MonitorIntegracioRepository;

@Component
public class MonitorIntegracioHelper {

	@Resource
	private MonitorIntegracioRepository monitorIntegracioRepository;
	@Resource
	private MonitorIntegracioParamRepository monitorIntegracioParamRepository;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
		MonitorIntegracioEntity entity = monitorIntegracioRepository.save(
				MonitorIntegracioEntity.getBuilder(
						monitorIntegracio.getCodi(),
						monitorIntegracio.getData(),
						monitorIntegracio.getDescripcio(),
						monitorIntegracio.getTipus(),
						monitorIntegracio.getTempsResposta(),
						monitorIntegracio.getEstat(),
						monitorIntegracio.getCodiUsuari(),
						monitorIntegracio.getCodiEntitat(),
						monitorIntegracio.getErrorDescripcio(),
						monitorIntegracio.getExcepcioMessage(),
						monitorIntegracio.getExcepcioStacktrace()).build());
		for (MonitorIntegracioParamDto paramDto : monitorIntegracio.getParametres()) {
			MonitorIntegracioParamEntity paramEntity = MonitorIntegracioParamEntity.getBuilder(
							entity, 
							paramDto.getNom(), 
							paramDto.getDescripcio()).build();
			paramEntity = monitorIntegracioParamRepository.save(paramEntity); 
			entity.getParametres().add(paramEntity);
		}
		
		return conversioTipusHelper.convertir(
				entity,
				MonitorIntegracioDto.class);
	}
 }
