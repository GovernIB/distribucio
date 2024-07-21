/**
 * 
 */
package es.caib.distribucio.logic.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;

/**
 * Utilitat per a gestionar annexos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AnnexosAdminHelper {
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	public RegistreAnnexDto toRegistreAnnexDto(
			RegistreAnnexEntity registreAnnexEntity) {		
		RegistreAnnexDto registreAnnexDto = conversioTipusHelper.convertir(registreAnnexEntity, RegistreAnnexDto.class);		
		return registreAnnexDto;
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosAdminHelper.class);

}