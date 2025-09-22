package es.caib.distribucio.plugin.caib.arxiu;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.arxiu.ArxiuPlugin;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.caib.ArxiuPluginCaib;
import io.micrometer.core.instrument.MeterRegistry;

public class ArxiuPluginSalutCaib extends ArxiuPluginCaib implements ArxiuPlugin {
	
	@Override
	public ContingutArxiu expedientCrear(
			final Expedient expedient) throws ArxiuException {
		try {
			long start = System.currentTimeMillis();
			ContingutArxiu resposta = super.expedientCrear(expedient);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			
			return resposta;
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw e;
		}
	}
	@Override
	public void expedientEsborrar(
			final String identificador) throws ArxiuException {
		try {
			long start = System.currentTimeMillis();
			super.expedientEsborrar(identificador);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public String expedientReobrir(
			final String identificador) throws ArxiuException {
		try {
			long start = System.currentTimeMillis();
			String resposta = super.expedientReobrir(identificador);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			
			return resposta;
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public String expedientTancar(
			final String identificador) throws ArxiuException {
		try {
			long start = System.currentTimeMillis();
			String resposta = super.expedientTancar(identificador);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			
			return resposta;
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw e;
		}
	}
	
	@Override
	public Expedient expedientDetalls(
			final String identificador,
			final String versio) throws ArxiuException {
		try {
			long start = System.currentTimeMillis();
			Expedient resposta = super.expedientDetalls(identificador, versio);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			
			return resposta;
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw e;
		}
	}
	
	
	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }
    
    @Override
	public boolean teConfiguracioEspecifica() {
		return salutPluginComponent.teConfiguracioEspecifica();
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return salutPluginComponent.getEstatPlugin();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return salutPluginComponent.getPeticionsPlugin();
	}
	
}
