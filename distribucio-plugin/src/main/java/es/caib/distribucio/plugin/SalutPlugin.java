package es.caib.distribucio.plugin;

import es.caib.comanda.model.v1.salut.EstatSalut;
import es.caib.comanda.model.v1.salut.IntegracioPeticions;
import io.micrometer.core.instrument.MeterRegistry;

public interface SalutPlugin {

	void init(MeterRegistry registry, String codiPlugin);
	
    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();
    
    IntegracioPeticions getPeticionsPlugin();

}
