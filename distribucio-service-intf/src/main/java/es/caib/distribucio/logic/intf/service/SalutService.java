package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SalutService {

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
	List<IntegracioInfo> getIntegracions();

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
	List<SubsistemaInfo> getSubsistemes();

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
	public List<ContextInfo> getContexts(String baseUrl);

	SalutInfo checkSalut(String version, String string);

}
