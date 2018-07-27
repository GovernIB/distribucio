
package es.caib.portafib.indra.callback.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "MCGDwsService", targetNamespace = "http://www.indra.es/portafirmasmcgdws/mcgdws", wsdlLocation = "http://portafibcaib.fundaciobit.org/portafib/cbindra/v0/PortafirmasCallBack?wsdl")
public class MCGDwsService
    extends Service
{

    private final static URL MCGDWSSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(es.caib.portafib.indra.callback.server.MCGDwsService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = es.caib.portafib.indra.callback.server.MCGDwsService.class.getResource(".");
            url = new URL(baseUrl, "http://portafibcaib.fundaciobit.org/portafib/cbindra/v0/PortafirmasCallBack?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://portafibcaib.fundaciobit.org/portafib/cbindra/v0/PortafirmasCallBack?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        MCGDWSSERVICE_WSDL_LOCATION = url;
    }

    public MCGDwsService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MCGDwsService() {
        super(MCGDWSSERVICE_WSDL_LOCATION, new QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "MCGDwsService"));
    }

    /**
     * 
     * @return
     *     returns MCGDws
     */
    @WebEndpoint(name = "MCGDWS")
    public MCGDws getMCGDWS() {
        return super.getPort(new QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "MCGDWS"), MCGDws.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns MCGDws
     */
    @WebEndpoint(name = "MCGDWS")
    public MCGDws getMCGDWS(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.indra.es/portafirmasmcgdws/mcgdws", "MCGDWS"), MCGDws.class, features);
    }

}
