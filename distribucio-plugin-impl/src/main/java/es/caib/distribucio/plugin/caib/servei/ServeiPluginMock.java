/**
 * 
 */
package es.caib.distribucio.plugin.caib.servei;


import java.util.ArrayList;
import java.util.List;

import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.servei.Servei;
import es.caib.distribucio.plugin.servei.ServeiPlugin;

/**
 * Implementació del plugin de consulta de serveis emprant MOCK.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiPluginMock implements ServeiPlugin {

	@Override
	public List<Servei> findAmbCodiDir3(
			String codiDir3) throws SistemaExternException {
		List<Servei> response = new ArrayList<>();
		Servei p = new Servei();
		p.setCodigo("1324");
		p.setCodigoSIA("1315");
		response.add(p);
		return response;
	}

	@Override
	public String getUsuariIntegracio() {
		return "ServeisMock";
	}

	@Override
	public ServeiDto findAmbCodiSia(String codiSia) throws SistemaExternException {
		ServeiDto response = new ServeiDto();
		response.setCodi(codiSia);
		response.setCodiSia(codiSia);
		response.setNom("Servei mock " + codiSia);
		return response;
	}

}
