package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.comanda.model.v1.salut.ContextInfo;
import es.caib.comanda.model.v1.salut.IntegracioInfo;
import es.caib.comanda.model.v1.salut.SalutInfo;
import es.caib.comanda.model.v1.salut.SubsistemaInfo;

public interface SalutService {

	List<IntegracioInfo> getIntegracions();

	List<SubsistemaInfo> getSubsistemes();
	
	public List<ContextInfo> getContexts(String baseUrl);

	SalutInfo checkSalut(String version, String string);

}
