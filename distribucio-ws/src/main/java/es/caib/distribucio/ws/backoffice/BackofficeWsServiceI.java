package es.caib.distribucio.ws.backoffice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;

@WebService(
		name = BackofficeWsServiceI.SERVICE_NAME,
		targetNamespace = BackofficeWsServiceI.NAMESPACE_URI)
public interface BackofficeWsServiceI {

	public static final String SERVICE_NAME = "Backoffice";
	public static final String NAMESPACE_URI = "http://www.caib.es/distribucio/ws/backoffice";

	@WebMethod
	public void comunicarAnotacionsPendents(
			@WebParam(name="ids") @XmlElement(required=true) List<AnotacioRegistreId> ids);

}
