package es.caib.distribucio.logic.intf.service;

import java.util.List;

import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaInfo;

public interface SalutService {

	List<IntegracioInfo> getIntegracions();

	List<SubsistemaInfo> getSubsistemes();

	SalutInfo checkSalut(String version, String string);

}
