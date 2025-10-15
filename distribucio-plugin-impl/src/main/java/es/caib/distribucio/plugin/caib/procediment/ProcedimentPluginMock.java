/**
 * 
 */
package es.caib.distribucio.plugin.caib.procediment;


import java.util.ArrayList;
import java.util.List;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import io.micrometer.core.instrument.MeterRegistry;

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
	
	@Override
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {
		UnitatAdministrativa ua = new UnitatAdministrativa();
		ua.setCodi(codi);
		ua.setCodiDir3("123456789");
		ua.setNom("Unitat Administrativa Mock");
		ua.setPareCodi(null);
		return ua;
	}

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }
    
	@Override
	public boolean teConfiguracioEspecifica() {
		return false;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return EstatSalut.builder().estat(EstatSalutEnum.UP).latencia(1).build();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return IntegracioPeticions.builder().build();
	}
	
}
