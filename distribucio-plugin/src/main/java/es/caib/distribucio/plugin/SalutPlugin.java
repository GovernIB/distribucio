package es.caib.distribucio.plugin;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import io.micrometer.core.instrument.MeterRegistry;

public interface SalutPlugin {

	void init(MeterRegistry registry, String codiPlugin);
	
    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();
    
    IntegracioPeticions getPeticionsPlugin();

}
