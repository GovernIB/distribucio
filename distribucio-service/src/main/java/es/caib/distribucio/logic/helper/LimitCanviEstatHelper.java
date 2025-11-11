package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.persist.entity.LimitCanviEstatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LimitCanviEstatHelper {

    public final ConversioTipusHelper conversioTipusHelper;

    public LimitCanviEstatDto toDto(LimitCanviEstatEntity entity) {
        return conversioTipusHelper.convertir(entity, LimitCanviEstatDto.class);
    }

}
