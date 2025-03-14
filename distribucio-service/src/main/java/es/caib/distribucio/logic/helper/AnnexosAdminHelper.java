/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;

/**
 * Utilitat per a gestionar annexos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AnnexosAdminHelper {
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	
	public RegistreAnnexDto toRegistreAnnexDto(
			RegistreAnnexEntity registreAnnexEntity) {		
		RegistreAnnexDto registreAnnexDto = conversioTipusHelper.convertir(registreAnnexEntity, RegistreAnnexDto.class);		
		return registreAnnexDto;
	}

	public List<String> getTitolsAnnexes(RegistreEntity registre) {
		return registreAnnexRepository.findTitolByRegistre(registre);
	}
	
}