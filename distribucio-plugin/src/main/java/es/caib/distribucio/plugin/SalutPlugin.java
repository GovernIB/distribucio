package es.caib.distribucio.plugin;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;

public interface SalutPlugin {

    boolean teConfiguracioEspecifica();

    EstatSalut getEstatPlugin();
    IntegracioPeticions getPeticionsPlugin();

}
