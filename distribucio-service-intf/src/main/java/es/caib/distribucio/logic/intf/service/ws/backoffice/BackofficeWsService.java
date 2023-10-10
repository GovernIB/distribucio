package es.caib.distribucio.logic.intf.service.ws.backoffice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;


@WebService(
		name = "Backoffice",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
public interface BackofficeWsService {

	@WebMethod
	void comunicarAnotacionsPendents(
			@WebParam(name="ids") @XmlElement(required=true) List<AnotacioRegistreId> ids);

}
