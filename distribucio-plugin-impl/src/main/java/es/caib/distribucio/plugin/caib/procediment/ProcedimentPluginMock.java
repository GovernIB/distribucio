/**
 * 
 */
package es.caib.distribucio.plugin.caib.procediment;


import java.util.ArrayList;
import java.util.List;

import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;

/**
 * Implementació del plugin de consulta de procediments emprant MOCK.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginMock implements ProcedimentPlugin {

	@Override
	public List<Procediment> findAmbCodiDir3(
			String codiDir3) throws SistemaExternException {
		List<Procediment> response = new ArrayList<>();
		Procediment p = new Procediment();
		p.setCodigo("1324");
		p.setCodigoSIA("1315");
		response.add(p);
		return response;
	}

	@Override
	public String getUsuariIntegracio() {
		return "ProcedimentsMock";
	}

	@Override
	public ProcedimentDto findAmbCodiSia(String codiSia) throws SistemaExternException {
		ProcedimentDto response = new ProcedimentDto();
		response.setCodi(codiSia);
		response.setCodiSia(codiSia);
		response.setNom("Procediment mock " + codiSia);
		return response;
	}

}
