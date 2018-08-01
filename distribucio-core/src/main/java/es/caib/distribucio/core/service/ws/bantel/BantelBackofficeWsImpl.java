/**
 * 
 */
package es.caib.distribucio.core.service.ws.bantel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.bantel.ws.v2.BantelBackofficeWs;
import es.caib.distribucio.core.api.service.bantel.ws.v2.BantelBackofficeWsException;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.DatosDocumentoTelematico;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.DocumentoBTE;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.DocumentosBTE;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.ObjectFactory;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.ReferenciaEntrada;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.ReferenciasEntrada;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.TramiteBTE;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.TramiteSubsanacion;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.XmlHelper;

/**
 * Implementació dels mètodes per al servei de consulta i modificació de tràmits amb interfície
 * Sistra per als backoffices notificats amb regles de tipus backoffice Sistra.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService (
		name = "BackofficeFacade",
		serviceName = "BackofficeFacadeService",
		portName = "BackofficeFacade",
		endpointInterface = "es.caib.distribucio.core.api.service.bantel.ws.v2.BantelBackofficeWs",
		targetNamespace = "urn:es:caib:bantel:ws:v2:services")
public class BantelBackofficeWsImpl implements BantelBackofficeWs {
		
	@Resource
	private RegistreService registreService;
	
	/// Constants del processament del backoffice	
	/** Entrada procesada correctament pel BackOffice */		
	public final static String ENTRADA_PROCESSADA = "S";
	/** Entrada no ha estat procesada pel BackOffice */
	public final static String ENTRADA_NO_PROCESSADA = "N";
	/** Entrada procesada con error por el BackOffice */
	public final static String ENTRADA_PROCESSADA_ERROR = "X";
	
	@Override
	public TramiteBTE obtenerEntrada(ReferenciaEntrada referencia) throws BantelBackofficeWsException {		
		logger.debug(
			"processant petició l'obtenció de dades al servei de backoffice de tipus Sistra amb interfície Bantel (" +
			"numeroEntrada:" + referencia.getNumeroEntrada() + ")");
		
		// TODO: convindria controlar l'accès a les entrades segons l'autenticació al WS, no està clar com

		// Comprova que s'hagi informat el número d'entrada
    	if (referencia == null || referencia.getNumeroEntrada() == null)
    		throw new BantelBackofficeWsException("No s'ha informat correctament el número d'entrada");
    	
		// Recuperar anotació registre i establir resultat Sistra i resultat processament (descripció error)
    	RegistreAnotacioDto registre = registreService.findAmbIdentificador(referencia.getNumeroEntrada());
		if (registre == null)
    		throw new BantelBackofficeWsException("No s'ha trobat cap entrada amb aquest número d'entrada: " + referencia.getNumeroEntrada());          	
		
		// Comprova la clau d'accés a l'entrada
		if ( referencia.getClaveAcceso() == null || !ReglaHelper.encrypt(referencia.getNumeroEntrada()).equals(referencia.getClaveAcceso().getValue()))
			throw new BantelBackofficeWsException("La clau d'accés no concorda amb la clau esperada per a l'entrada amb número \"" + referencia.getNumeroEntrada() + "\"");

		// Construeix la resposta
		TramiteBTE tramitBte;
		try {
			tramitBte = getDadesSistra(registre);
		} catch( Exception e) {
			throw new BantelBackofficeWsException("Error obtenint les dades del registre", e);
		}
		
		return tramitBte;
	}


	/** Obté les dades de l'anotació de registre per construir la resposta Sistra. 
	 * @throws Exception */
	private TramiteBTE getDadesSistra(RegistreAnotacioDto registre) throws Exception {
		TramiteBTE tramitBte = new TramiteBTE();
    	
		// Dades de Distribucio
		tramitBte.setNumeroEntrada(registre.getIdentificador());
		tramitBte.setCodigoEntrada(Long.parseLong(registre.getNumero()));
		tramitBte.setProcesada(getProcesada(registre.getProcesEstatSistra()));
		
		// Constants
		tramitBte.setTipo("E");
		tramitBte.setFirmadaDigitalmente(false);
		
		// Informació continguda en els annexos
		for (RegistreAnnex annex : registre.getAnnexos()) {
			if ("Asiento.xml".equals(annex.getFitxerNom())
					|| "DatosPropios.xml".equals(annex.getFitxerNom()))
				this.extreureDadesAnnex(registre, tramitBte, annex);
		}
				
		return tramitBte;
	}


	/** Mètode per explorar l'XML de l'annex i extreure la informació de Sistra. 
	 * @param registre 
	 * 			Objecte registre per accedir a tots els seus annexos.
	 * @param tramitBte
	 * 			Objecte on omplir l'informació obtinguda dels annexos
	 * @param annex
	 * 			Objexte annex del registre amb la informació per accedir al fitxer físic
	 * @throws Exception */
	private void extreureDadesAnnex(RegistreAnotacioDto registre, TramiteBTE tramitBte, RegistreAnnex annex) throws Exception {

		try {
			ObjectFactory bantelObjectFactory = new ObjectFactory();
			String pathName = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.bustia.contingut.documents.dir");
			File file = new File(pathName + "/" + annex.getId() + "_d." + annex.getFitxerTipusMime());
			byte[] contingut = Files.readAllBytes(file.toPath());
			Document doc = XmlHelper.getDocumentFromContent(contingut);

			if (annex.getFitxerNom().equals("DatosPropios.xml")) {
				// DatosPropios.xml
				Node instrucciones = XmlHelper.getNode(doc.getDocumentElement(), "INSTRUCCIONES");
				if (instrucciones != null) {
					// habilitarAvisos					:	DATOS_PROPIOS.INSTRUCCIONES.HABILITAR_AVISOS
					JAXBElement<String> habilitarAvisos = bantelObjectFactory.createTramiteBTEHabilitarAvisos(XmlHelper.getNodeValue(instrucciones, "HABILITAR_AVISOS"));
					tramitBte.setHabilitarAvisos(habilitarAvisos);

					// avisoEmail						:	DATOS_PROPIOS.INSTRUCCIONES.AVISO_EMAIL
					JAXBElement<String> avisoEmail = bantelObjectFactory.createTramiteBTEAvisoEmail(XmlHelper.getNodeValue(instrucciones, "AVISO_EMAIL"));
					tramitBte.setAvisoEmail(avisoEmail);
					
					// avisoSMS						:	DATOS_PROPIOS.INSTRUCCIONES.AVISO_SMS
					JAXBElement<String> avisoSMS = bantelObjectFactory.createTramiteBTEAvisoSMS(XmlHelper.getNodeValue(instrucciones, "AVISO_SMS"));
					tramitBte.setAvisoSMS(avisoSMS);

					// habilitarNotificacionTelematica	:	DATOS_PROPIOS.INSTRUCCIONES.HABILITAR_NOTIFICACION_TELEMATICA
					JAXBElement<String> habilitarNotificacionTelematica = bantelObjectFactory.createTramiteBTEHabilitarNotificacionTelematica(XmlHelper.getNodeValue(instrucciones, "HABILITAR_NOTIFICACION_TELEMATICA"));
					tramitBte.setHabilitarNotificacionTelematica(habilitarNotificacionTelematica);

					// tramiteSubsanacion	:	DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION
					Node tramiteSubsanacionNode = XmlHelper.getNode(instrucciones, "TRAMITE_SUBSANACION");
					if (tramiteSubsanacionNode != null) {
						TramiteSubsanacion tramiteSubsanacion = bantelObjectFactory.createTramiteSubsanacion();
						
						// expedienteCodigo : DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION.EXPEDIENTE_CODIGO
						tramiteSubsanacion.setExpedienteCodigo(XmlHelper.getNodeValue(tramiteSubsanacionNode, "EXPEDIENTE_CODIGO"));
						
						// expedienteUnidadAdministrativa  : DATOS_PROPIOS.INSTRUCCIONES.TRAMITE_SUBSANACION.EXPEDIENTE_UNIDAD_ADMINISTRATIVA
						String expedienteUnidadAdministrativa = XmlHelper.getNodeValue(tramiteSubsanacionNode, "EXPEDIENTE_UNIDAD_ADMINISTRATIVA");
						if (expedienteUnidadAdministrativa != null)
							tramiteSubsanacion.setExpedienteUnidadAdministrativa(Long.parseLong(expedienteUnidadAdministrativa));
						tramitBte.setTramiteSubsanacion(bantelObjectFactory.createTramiteBTETramiteSubsanacion(tramiteSubsanacion));					
					}
				}
			} else if (annex.getFitxerNom().equals("Asiento.xml")) {
				
				// Asiento.xml
				
				Node datosAsunto = XmlHelper.getNode(doc.getDocumentElement(), "DATOS_ASUNTO");
				if (datosAsunto != null) {				
					// unidadAdministrativa	: ASIENTO_REGISTRAL.DATOS_ASUNTO.CODIGO_UNIDAD_ADMINISTRATIVA
					tramitBte.setUnidadAdministrativa(Long.parseLong(XmlHelper.getNodeValue(datosAsunto, "CODIGO_UNIDAD_ADMINISTRATIVA")));

					// fecha				: ASIENTO_REGISTRAL.DATOS_ASUNTO.FECHA_ASUNTO
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					GregorianCalendar c = new GregorianCalendar();
					c.setTime(sdf.parse(XmlHelper.getNodeValue(datosAsunto, "FECHA_ASUNTO")));
					XMLGregorianCalendar fechaAsunto = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
					tramitBte.setFecha(fechaAsunto);
									
					// identificadorTramite	: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDENTIFICADOR_TRAMITE
					tramitBte.setIdentificadorTramite(XmlHelper.getNodeValue(datosAsunto, "IDENTIFICADOR_TRAMITE"));

					// idioma				: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDIOMA_ASUNTO
					tramitBte.setIdioma(XmlHelper.getNodeValue(datosAsunto, "IDIOMA_ASUNTO"));
					
					// descripcionTramite				: ASIENTO_REGISTRAL.DATOS_ASUNTO.EXTRACTO_ASUNTO
					tramitBte.setDescripcionTramite(XmlHelper.getNodeValue(datosAsunto, "EXTRACTO_ASUNTO"));
				}
				
				Node datosInteresado = XmlHelper.getNode(doc.getDocumentElement(), "DATOS_INTERESADO");
				if (datosInteresado != null) {				
					// nivelAutenticacion	: ASIENTO_REGISTRAL.DATOS_INTERESADO.NIVEL_AUTENTICACION
					tramitBte.setNivelAutenticacion(XmlHelper.getNodeValue(datosInteresado, "NIVEL_AUTENTICACION"));

					// usuarioSeycon		: ASIENTO_REGISTRAL.DATOS_INTERESADO.USUARIO_SEYCON
					JAXBElement<String> usuariSeycon = bantelObjectFactory.createTramiteBTEUsuarioSeycon(XmlHelper.getNodeValue(datosInteresado, "USUARIO_SEYCON"));
					tramitBte.setUsuarioSeycon(usuariSeycon);

					// usuarioNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
					JAXBElement<String> usuarioNif = bantelObjectFactory.createTramiteBTEUsuarioNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
					tramitBte.setUsuarioNif(usuarioNif);

					// usuarioNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
					JAXBElement<String> usuarioNombre = bantelObjectFactory.createTramiteBTEUsuarioNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
					tramitBte.setUsuarioNombre(usuarioNombre);

					// representadoNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
					JAXBElement<String> representadoNif = bantelObjectFactory.createTramiteBTERepresentadoNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
					tramitBte.setRepresentadoNif(representadoNif);

					// representadoNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
					JAXBElement<String> representadoNombre = bantelObjectFactory.createTramiteBTERepresentadoNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
					tramitBte.setRepresentadoNombre(representadoNombre);

					// delegadoNif	:	ASIENTO_REGISTRAL.DATOS_INTERESADO.NUMERO_IDENTIFICACION
					JAXBElement<String> delegadoNif = bantelObjectFactory.createTramiteBTEDelegadoNif(XmlHelper.getNodeValue(datosInteresado, "NUMERO_IDENTIFICACION"));
					tramitBte.setDelegadoNif(delegadoNif);

					// delegadoNombre:	ASIENTO_REGISTRAL.DATOS_INTERESADO.IDENTIFICACION_INTERESADO
					JAXBElement<String> delegadoNombre = bantelObjectFactory.createTramiteBTEDelegadoNombre(XmlHelper.getNodeValue(datosInteresado, "IDENTIFICACION_INTERESADO"));
					tramitBte.setDelegadoNombre(delegadoNombre);
				}
				
				// Accedeix a la informació dels nodes de documents annexos i els adjunta al tramitBte
				extreureDadesDocumentsAnnexos(registre, tramitBte, doc);
			}		
		} catch (Exception e) {
			logger.error(
					"Error extraient les dades de l'annex " + annex.getFitxerNom() + " amb id " + annex.getId() + " per l'obtenció de l'entrada número " + tramitBte.getNumeroEntrada(),
					e);
			throw e;
		}


	}
	

	/** Mètode per adjuntar els documments annexos al tramitBTE. Cerca el fitxer "Asiento.xml" per trobar
	 * la informació dels annexos i per cada node trobat l'afegeix al tramitBte.
	 * Es crida dins de extreureDadesAnnex per aprofitar el document ja obert de "Asiento.xml".
	 * @param registre
	 * 				Dades de Distribucio amb els fitxers adjunts a l'anotació de registre.
	 * @param tramitBte
	 * 				Objecte on afegir la informació dels documents annexos.
	 * @param doc
	 * 				Objecgte Document amb el contingut de "Asiento.xml"
	 * @throws IOException 
	 *				Error si no es pot obrir el fitxer annex.
	 */
	private void extreureDadesDocumentsAnnexos(RegistreAnotacioDto registre, TramiteBTE tramitBte, Document doc) throws IOException {

		ObjectFactory bantelObjectFactory = new ObjectFactory();
		String pathName = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.bustia.contingut.documents.dir");
		File file;
		byte[] contingut;

		// Recupera els nodes amb la informació dels documents
		NodeList documents = XmlHelper.getNodes(doc.getDocumentElement(), "DATOS_ANEXO_DOCUMENTACION");
		// Crea un Map<String nom fitxer, Node document> per informar els annexos
		Map<String, Node> documentsMap = new HashMap<String, Node>();
		Node document;
		String nomFitxer;
		for (int i = 0; i < documents.getLength(); i++) {
			document = documents.item(i);
			nomFitxer = XmlHelper.getNodeValue(document, "NOMBRE_DOCUMENTO");
			documentsMap.put(nomFitxer, document);
		}
		// Recorre tots els annexos i afegeix al tràmit BTE aquels que estiguin llistat a l' "Asiento.xml"
		DocumentosBTE documentsBte = new DocumentosBTE(); 
		DocumentoBTE documentBte;
		for (RegistreAnnex annex : registre.getAnnexos()) {
			nomFitxer = annex.getFitxerNom();
			if (documentsMap.containsKey(nomFitxer)) {
				documentBte = new DocumentoBTE();
				document = documentsMap.get(nomFitxer);
				
				// Omple la informació del document annex BTE		
				
				// Nom descriptiu del document :	ASIENTO_REGISTRAL.DATOS_ANEXO_DOCUMENTACION.EXTRACTO_DOCUMENTO
				String nomDescriptiu = XmlHelper.getNodeValue(document, "EXTRACTO_DOCUMENTO");
				documentBte.setNombre(nomDescriptiu != null? nomDescriptiu : nomFitxer);
				
				// identificadorDocumento	:	ASIENTO_REGISTRAL.DATOS_ANEXO_DOCUMENTACION.IDENTIFICADOR_DOCUMENTO
				documentBte.setIdentificador(XmlHelper.getNodeValue(document, "IDENTIFICADOR_DOCUMENTO"));
				
				// Dades document telemàtic
				DatosDocumentoTelematico dadesTelematiques = new DatosDocumentoTelematico();
				// Nombre
				dadesTelematiques.setNombre(nomFitxer);
				// Contingut en el sistema de fitxers
				file = new File(pathName + "/" + annex.getId() + "_d." + annex.getFitxerTipusMime());
				contingut = Files.readAllBytes(file.toPath());
				dadesTelematiques.setContent(contingut);				
				documentBte.setPresentacionTelematica(bantelObjectFactory.createDocumentoBTEPresentacionTelematica(dadesTelematiques));
				// Estructurat: 	:	ASIENTO_REGISTRAL.DATOS_ANEXO_DOCUMENTACION.ESTRUCTURADO
				dadesTelematiques.setEstructurado("S".equals(XmlHelper.getNodeValue(document, "ESTRUCTURADO")));
				// Clave RDS
				dadesTelematiques.setClaveReferenciaRds(XmlHelper.getNodeValue(document, "CODIGO_RDS"));
				// Extensio
				String extensio = null;
				if (annex.getFitxerNom().contains("."))
					extensio = annex.getFitxerNom().substring(annex.getFitxerNom().lastIndexOf(".") + 1, annex.getFitxerNom().length()).toUpperCase();
				dadesTelematiques.setExtension(extensio);
				
				// afegeix el document al tràmit
				documentsBte.getDocumento().add(documentBte);
			}
		}
		tramitBte.setDocumentos(documentsBte);
	}


	@Override
	public void establecerResultadoProceso(
			ReferenciaEntrada referencia, 
			String resultado,
			String resultadoProcesamiento) throws BantelBackofficeWsException {
		logger.debug(
			"processant petició d'establir resultat del procés al servei de backoffice de tipus Sistra amb interfície Bantel (" +
			"numeroEntrada:" + referencia + ", " +
			"resultado:" + resultado + ", " +
			"resultadoProcesamiento:" + resultadoProcesamiento + ")");
		
		// TODO: convindria controlar l'accès a les entrades segons l'autenticació al WS, no està clar com
		
    	// Comprova que s'hagi informat el número d'entrada
    	if (referencia == null || referencia.getNumeroEntrada() == null)
    		throw new BantelBackofficeWsException("No s'ha informat correctament el número d'entrada");

		// Comprova la clau d'accés a l'entrada
		if ( referencia.getClaveAcceso() == null || !ReglaHelper.encrypt(referencia.getNumeroEntrada()).equals(referencia.getClaveAcceso().getValue()))
			throw new BantelBackofficeWsException("La clau d'accés no concorda amb la clau esperada per a l'entrada amb número \"" + referencia.getNumeroEntrada() + "\"");

		// Determina l'estat de l'anotació segons el resultat
		RegistreProcesEstatSistraEnum procesEstatSistra = this.getEstatProcesSistra(resultado);
		if (procesEstatSistra == null)
			throw new BantelBackofficeWsException("S'ha d'informar el valor pel resultat del procés ('S' processat, 'N' no processada, 'X' error)");

		// Determina l'estat de l'anotació per Distribucio
		RegistreProcesEstatEnum procesEstat;
		switch (procesEstatSistra) {
		case PROCESSADA:
			procesEstat = RegistreProcesEstatEnum.PROCESSAT;
			break;
		case ERROR:
		case PENDENT:
		default:
    		procesEstat = RegistreProcesEstatEnum.PENDENT;
			break;		
		}
    	
		// Recuperar anotació registre i establir resultat Sistra i resultat processament (descripció error)
    	RegistreAnotacioDto registre = registreService.findAmbIdentificador(referencia.getNumeroEntrada());
		if (registre == null)
    		throw new BantelBackofficeWsException("No s'ha trobat cap entrada amb aquest número d'entrada: " + referencia.getNumeroEntrada());          	

		// Si s'ha processat sense errors posar el resultat de processament a null
		if (procesEstatSistra.equals(ENTRADA_NO_PROCESSADA))
			resultadoProcesamiento = null;

		registreService.updateProces(
				registre.getId(),
				procesEstat,
				procesEstatSistra,
				resultadoProcesamiento);
	}

	@Override
	public ReferenciasEntrada obtenerNumerosEntradas(
			String identificadorProcedimiento, 
			String identificadorTramite,
			String procesada, 
			XMLGregorianCalendar desde, 
			XMLGregorianCalendar hasta) throws BantelBackofficeWsException {
		logger.debug(
			"processant petició d'obtenició de números d'entrada al servei de backoffice de tipus Sistra amb interfície Bantel (" +
			"identificadorProcedimiento:" + identificadorProcedimiento + ", " +
			"identificadorTramite:" + identificadorTramite + ", " +
			"procesada:" + procesada + ", " +
			"desde:" + desde + ", " +
			"hasta:" + hasta + ")");
		
    	// Determina l'estat de processament SISTRA
		RegistreProcesEstatSistraEnum procesEstatSistra = this.getEstatProcesSistra(procesada);
	    	
    	// Dates
    	Date desdeDate = null;
		Date finsDate = null;
		if(desde != null){
			desdeDate = desde.toGregorianCalendar().getTime();
		}if(hasta != null){
			finsDate = hasta.toGregorianCalendar().getTime();
		}
		
		// Realitza la consulta de la llista d'identificadors d'anotacions de registre
		List<String> numerosEntrada = registreService.findPerBackofficeSistra(
				identificadorProcedimiento,
				identificadorTramite,
				procesEstatSistra,
				desdeDate,
				finsDate);
		
		// Converteix els identificadors en referències d'entrades
		ObjectFactory bantelObjectFactory = new ObjectFactory();
		ReferenciasEntrada referencies = new ReferenciasEntrada();
		List<ReferenciaEntrada> referenciesEntrades = referencies.getReferenciaEntrada();
		ReferenciaEntrada referencia;
		String clauAcces;
		for (String numeroEntrada : numerosEntrada) {
			referencia = new ReferenciaEntrada();
			referencia.setNumeroEntrada(numeroEntrada);
			// Xifra el número d'entrada com a clau d'accés de l'entrada
			clauAcces = ReglaHelper.encrypt(numeroEntrada);
			referencia.setClaveAcceso(bantelObjectFactory.createReferenciaEntradaClaveAcceso(clauAcces));
			referenciesEntrades.add(referencia);
		}
		return referencies;
	}


	/** Mètode privat per obtenir l'estat de sistra a partir del codi String passat com a paràmetre. 
	 * @throws BantelBackofficeWsException */
	private RegistreProcesEstatSistraEnum getEstatProcesSistra(String procesada) throws BantelBackofficeWsException {
		
		RegistreProcesEstatSistraEnum procesEstatSistra;
		if (procesada != null) {
	    	if (ENTRADA_PROCESSADA.equals(procesada)) {
	    		procesEstatSistra = RegistreProcesEstatSistraEnum.PROCESSADA;
	    	} else if (ENTRADA_NO_PROCESSADA.equals(procesada)) {
	    		procesEstatSistra = RegistreProcesEstatSistraEnum.PENDENT;
	    	} else if (ENTRADA_PROCESSADA_ERROR.equals(procesada)) {
	    		procesEstatSistra = RegistreProcesEstatSistraEnum.ERROR;
	    	} else
	    		throw new BantelBackofficeWsException("Valor no vàlid per l'atribut processada: " + procesada); 
		} else
			procesEstatSistra = null;		
		return procesEstatSistra;
	}

	/** Mètode per obtenir la cadena de l'estat sistra a partir de l'estat sistra de l'anotació de registre. */
	private String getProcesada(RegistreProcesEstatSistraEnum procesEstatSistra) {
		String processada;
		if (procesEstatSistra != null) {
			switch(procesEstatSistra) {
			case ERROR:
				processada = ENTRADA_PROCESSADA_ERROR;
				break;
			case PENDENT:
				processada = ENTRADA_NO_PROCESSADA;
				break;
			case PROCESSADA:
				processada = ENTRADA_PROCESSADA;
				break;
			default:
				processada = null;
				break;
			
			}
		} else
			processada = null;
		return processada;
	}
	


	private static final Logger logger = LoggerFactory.getLogger(BantelBackofficeWsImpl.class);
}
