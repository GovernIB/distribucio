package es.caib.distribucio.core.api.service.bantel.wsClient.v2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Interfície per al client del WS de Bantel per a notificar anotacions de registre a 
 * backoffices tipus sistra.
 * Quan es defineix una regla de tipus backoffice a Distribucio es pot escollir si el backoffice és
 * de tipus Distribucio o Sistra. En el cas que sigui tipus Sistra Distribucio notificarà mitjançant
 * aquesta interfície les noves entrades als backoffices de tipus Sistra.
 * 
 */
 
@WebService(targetNamespace = "urn:es:caib:bantel:ws:v2:services", name = "BantelFacade")
@XmlSeeAlso({es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ObjectFactory.class})
public interface BantelFacadeWsClient {

    @ResponseWrapper(localName = "avisoEntradasResponse", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade", className = "es.caib.bantel.wsClient.v2.model.AvisoEntradasResponse")
    @RequestWrapper(localName = "avisoEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade", className = "es.caib.bantel.wsClient.v2.model.AvisoEntradas")
    @WebMethod
    public void avisoEntradas(
        @WebParam(name = "numeroEntradas", targetNamespace = "urn:es:caib:bantel:ws:v2:model:BantelFacade")
        es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciasEntrada numeroEntradas
    ) throws BantelFacadeException;
}
