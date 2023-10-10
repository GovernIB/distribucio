/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.ArbreNodeDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;

/**
 * Mètodes comuns per a gestionar els arbres de UOs.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ArbreNodeHelper {

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

	/**
	 * 
	 * @param unitatOrganitzativa - in first call it is unitat arrel, later the children nodes
	 * @param unitatsOrganitzatives
	 * @param pare - in first call it is null, later pare
	 * @return
	 */
	public ArbreNodeDto<UnitatOrganitzativaDto> getNodeArbreUnitatsOrganitzatives(
			UnitatOrganitzativa unitatOrganitzativa,
			List<UnitatOrganitzativa> unitatsOrganitzatives,
			ArbreNodeDto<UnitatOrganitzativaDto> pare) {
		// creating current arbre node and filling it with pare arbre node and dades as current unitat
		ArbreNodeDto<UnitatOrganitzativaDto> currentArbreNode = new ArbreNodeDto<UnitatOrganitzativaDto>(
				pare,
				conversioTipusHelper.convertir(
						unitatOrganitzativa,
						UnitatOrganitzativaDto.class));
		String codiUnitat = (unitatOrganitzativa != null) ? unitatOrganitzativa.getCodi() : null;
		// for every child of current unitat call recursively getNodeArbreUnitatsOrganitzatives()
		for (UnitatOrganitzativa uo: unitatsOrganitzatives) {
			//searches for children of current unitat
			if (	(codiUnitat == null && uo.getCodiUnitatSuperior() == null) ||
					(uo.getCodiUnitatSuperior() != null && uo.getCodiUnitatSuperior().equals(codiUnitat))) {
				currentArbreNode.addFill(
						getNodeArbreUnitatsOrganitzatives(
								uo,
								unitatsOrganitzatives,
								currentArbreNode));
			}
		}
		return currentArbreNode;
	}

}
