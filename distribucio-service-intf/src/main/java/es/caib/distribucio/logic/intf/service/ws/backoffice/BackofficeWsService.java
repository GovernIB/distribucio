package es.caib.distribucio.logic.intf.service.ws.backoffice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

@WebService(
		name = BackofficeWsService.SERVICE_NAME,
		targetNamespace = BackofficeWsService.NAMESPACE_URI)
public interface BackofficeWsService {

	public static final String SERVICE_NAME = "Backoffice";
	public static final String NAMESPACE_URI = "http://www.caib.es/distribucio/ws/backoffice";

	@WebMethod
	void comunicarAnotacionsPendents(
			@WebParam(name="ids") @XmlElement(required=true) List<AnotacioRegistreId> ids);

}
