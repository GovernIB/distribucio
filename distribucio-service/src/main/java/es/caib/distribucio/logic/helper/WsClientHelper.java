/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Utilitat per a instanciar clients de serveis SOAP.
 * 
 * @author Limit Tecnologies
 */
public class WsClientHelper<T> {

	public T generarClientWs(
			URL wsdlResourceUrl,
			String endpoint,
			QName qname,
			String username,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, RemoteException, NamingException {
		URL url = wsdlResourceUrl;
		if (url == null) {
			if (!endpoint.endsWith("?wsdl"))
				url = new URL(endpoint + "?wsdl");
			else
				url = new URL(endpoint);
		}
		Service service = Service.create(null, qname);
		T bustiaWs = service.getPort(clazz);
		BindingProvider bindingProvider = (BindingProvider)bustiaWs;
		// Configura l'adreça del servei
		String endpointAddress;
		if (!endpoint.endsWith("?wsdl"))
			endpointAddress = endpoint;
		else
			endpointAddress = endpoint.substring(0, endpoint.length() - "?wsdl".length());
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				endpointAddress);
		// Configura l'autenticació si és necessària
		if (username != null && !username.isEmpty()) {
			bindingProvider.getRequestContext().put(
					BindingProvider.USERNAME_PROPERTY,
					username);
			bindingProvider.getRequestContext().put(
					BindingProvider.PASSWORD_PROPERTY,
					password);
		}
		// Configura el log de les peticions
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new SOAPLoggingHandler());
		// Configura handlers addicionals
		for (int i = 0; i < handlers.length; i++) {
			if (handlers[i] != null)
				handlerChain.add(handlers[i]);
		}
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		if (soapAction != null) {
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_USE_PROPERTY,
					true);
			bindingProvider.getRequestContext().put(
					BindingProvider.SOAPACTION_URI_PROPERTY,
					soapAction);
		}
		return bustiaWs;
	}

	public T generarClientWs(
			String endpoint,
			QName qname,
			String userName,
			String password,
			String soapAction,
			Class<T> clazz,
			Handler<?>... handlers) throws MalformedURLException, RemoteException, NamingException {
		return this.generarClientWs(
				null,
				endpoint,
				qname,
				userName,
				password,
				soapAction,
				clazz,
				handlers);
	}

	@Slf4j
	public static class SOAPLoggingHandler implements SOAPHandler<SOAPMessageContext> {
		public Set<QName> getHeaders() {
			return null;
		}
		public boolean handleMessage(SOAPMessageContext smc) {
			logToSystemOut(smc);
			return true;
		}
		public boolean handleFault(SOAPMessageContext smc) {
			logToSystemOut(smc);
			return true;
		}
		public void close(MessageContext messageContext) {
		}
		private void logToSystemOut(SOAPMessageContext smc) {
			boolean processLog = log.isDebugEnabled();
			if (processLog) {
				StringBuilder sb = new StringBuilder();
				Boolean outboundProperty = (Boolean)smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				if (outboundProperty.booleanValue())
					sb.append("Missatge sortint: ");
				else
					sb.append("Missatge entrant: ");
				SOAPMessage message = smc.getMessage();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					message.writeTo(baos);
					sb.append(baos.toString());
				} catch (Exception ex) {
					sb.append("Error al imprimir el missatge XML: " + ex.getMessage());
				}
				log.debug(sb.toString());
			}
		}
	}

}