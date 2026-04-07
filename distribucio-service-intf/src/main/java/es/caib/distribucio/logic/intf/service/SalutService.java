package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;

public interface SalutService {

	List<IntegracioInfo> getIntegracions();

	List<SubsistemaInfo> getSubsistemes();
	
	public List<ContextInfo> getContexts(String baseUrl);

	SalutInfo checkSalut(String version, String string);

}
