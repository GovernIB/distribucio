package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.comanda.salut.model.AppInfo;
import es.caib.comanda.salut.model.IntegracioInfo;
import es.caib.comanda.salut.model.SalutInfo;

public interface SalutService {

	List<IntegracioInfo> getIntegracions();

	List<AppInfo> getSubsistemes();

	SalutInfo checkSalut(String version, String string);

}
