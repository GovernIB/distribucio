/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.GestioDocumentalHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnosticDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDto;
import es.caib.distribucio.logic.intf.dto.IntegracioFiltreDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioDto;
import es.caib.distribucio.logic.intf.dto.MonitorIntegracioParamDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import es.caib.distribucio.persist.entity.MonitorIntegracioEntity;
import es.caib.distribucio.persist.entity.MonitorIntegracioParamEntity;
import es.caib.distribucio.persist.repository.BustiaRepository;
import es.caib.distribucio.persist.repository.MonitorIntegracioParamRepository;
import es.caib.distribucio.persist.repository.MonitorIntegracioRepository;

/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private MonitorIntegracioRepository monitorIntegracioRepository;
	@Resource
	private MonitorIntegracioParamRepository monitorIntegracioParamRepository;
	@Resource
	private BustiaRepository bustiaRepository;

	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Lazy
	@Resource
	private PluginHelper pluginHelper;

	@Override
	public List<IntegracioDto> integracioFindAll() {
		logger.trace("Consultant les integracions");
		return integracioHelper.findAll();
	}

	@Override
	public List<IntegracioDto> findPerDiagnostic() {
		logger.trace("Consultant les integracions per comprovar les conexions amb els plugins");
		return integracioHelper.findPerDiagnostic();
	}

	/** Mètode asíncron de creació del registre a la taula de monitor. */
	@Async
	@Transactional
	@Override
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
//		logger.trace("Creant una nova monitorIntegracio (" +
//				"monitorIntegracio=" + monitorIntegracio + ")");
		MonitorIntegracioEntity entity = monitorIntegracioRepository.save(
				MonitorIntegracioEntity.getBuilder(
						monitorIntegracio.getCodi(),
						monitorIntegracio.getData(),
						monitorIntegracio.getDescripcio(),
						monitorIntegracio.getTipus(),
						monitorIntegracio.getTempsResposta(),
						monitorIntegracio.getEstat(),
						monitorIntegracio.getCodiUsuari(),
						monitorIntegracio.getCodiEntitat(),
						monitorIntegracio.getErrorDescripcio(),
						monitorIntegracio.getExcepcioMessage(),
						monitorIntegracio.getExcepcioStacktrace()).build());
		for (MonitorIntegracioParamDto paramDto : monitorIntegracio.getParametres()) {
			MonitorIntegracioParamEntity paramEntity = MonitorIntegracioParamEntity.getBuilder(
							entity, 
							paramDto.getNom(), 
							paramDto.getDescripcio()).build();
			paramEntity = monitorIntegracioParamRepository.save(paramEntity); 
			entity.getParametres().add(paramEntity);
		}
		return conversioTipusHelper.convertir(
				entity,
				MonitorIntegracioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MonitorIntegracioDto findById(Long id) {
		logger.debug("Consulta de l'monitorIntegracio (" +
				"id=" + id + ")");

		MonitorIntegracioDto dto = conversioTipusHelper.convertir(
				monitorIntegracioRepository.findById(id).orElse(null),
				MonitorIntegracioDto.class);
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, IntegracioFiltreDto integracioFiltreDto/*String codiMonitor*/) {
		logger.debug("Consulta de totes les monitorIntegracios paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		boolean isDataNula = integracioFiltreDto.getData() == null;
		Calendar c = new GregorianCalendar();
		Date data = integracioFiltreDto.getData();
		Date dataFi = new Date();
		if (data != null) {
			c.setTime(data);
			data = c.getTime();
			c.add(Calendar.DAY_OF_YEAR, 1);
			dataFi = c.getTime();
		}
		String codiMonitor = integracioFiltreDto.getCodi();
		String descripcio = integracioFiltreDto.getDescripcio();
		String usuari = integracioFiltreDto.getUsuari();
		IntegracioAccioEstatEnumDto estat = integracioFiltreDto.getEstat();
		PaginaDto<MonitorIntegracioDto> resposta;
		resposta = paginacioHelper.toPaginaDto(
				monitorIntegracioRepository.findByFiltrePaginat(
						codiMonitor,
						isDataNula,
						data, 
						dataFi, 
						descripcio == null || descripcio.isEmpty(),
						descripcio != null && !descripcio.isEmpty() ? descripcio : "", 
						usuari == null || usuari.isEmpty(), 
						usuari != null && !usuari.isEmpty() ? usuari : "",
						estat == null, 
						estat != null ? estat : IntegracioAccioEstatEnumDto.OK, 
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				MonitorIntegracioDto.class);	
		return resposta;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorIntegracioServiceImpl.class);

	@Transactional(readOnly = true)
	@Override
	public Map<String, Integer> countErrors(int numeroHores) {
		DateTime date = new DateTime();
		long dataInici = date.getMillis() - (numeroHores * 60*60*1000);
		DateTime dataIniciDate = new DateTime(dataInici);
		
		Map<String, Integer> errors = new HashMap<String, Integer>();
		List<Object[]> resultats = monitorIntegracioRepository.countErrorsGroupByCodi(dataIniciDate.toDate());
		for (Object[] resultat : resultats) {
			errors.put(
					(String) resultat[0], 
					((Long) resultat[1]).intValue());
		}
		return errors;
	}

    public static Date toDate(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public Map<String, Integer> countCanvisEstatFromUser(String user) {
        Map<String, Integer> result = new HashMap<String, Integer>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inicioMinuto = now.withSecond(0).withNano(0);
        LocalDateTime finMinuto = inicioMinuto.plusMinutes(1).minusNanos(1);

        Integer conteoMinuto = monitorIntegracioRepository.countBackofficeCanvisEstatFromUser(user, toDate(inicioMinuto), toDate(finMinuto));
        result.put("minut", conteoMinuto);

        LocalDateTime inicioDia = now.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1).minusNanos(1);

        Integer conteoDia = monitorIntegracioRepository.countBackofficeCanvisEstatFromUser(user, toDate(inicioDia), toDate(finDia));
        result.put("dia", conteoDia);
        return result;
    }

    @Transactional
	@Override
	public int esborrarDadesAntigues(Date data) {
		logger.trace("Esborrant dades del monitor d'integració anteriors a : " + data);
		int total = 0;
		if (data != null) {
			total = monitorIntegracioRepository.countMonitorByDataBefore(data);
			monitorIntegracioParamRepository.deleteDataBefore(data);
			monitorIntegracioParamRepository.flush();
			monitorIntegracioRepository.deleteDataBefore(data);
		}
		return total;
	}

	@Transactional
	@Override
	public int delete(String codi) {
		logger.debug("Esborrant dades del monitor d'integració per la integració amb codi : " + codi);
		int n = 0;
		if (codi != null) {
			// Total
			n = monitorIntegracioRepository.countByCodi(codi);
			// Paràmetres
			monitorIntegracioParamRepository.deleteByMonitorIntegracioCodi(codi);
			// Entrades
			monitorIntegracioRepository.deleteByCodiMonitor(codi);
		}
		return n;
	}

	@Override
	public IntegracioDiagnosticDto diagnostic(String codiIntegracio,  UsuariDto usuari) {
		IntegracioDiagnosticDto diagnostic = new IntegracioDiagnosticDto();
		String accioDescripcio;
		try {
			switch (codiIntegracio) {
			case "USUARIS": 
				pluginHelper.dadesUsuariFindAmbCodi(usuari.getCodi());
				accioDescripcio = "Consulta d'usuari amb codi";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 				
				break;
				
			case "UNITATS":
				 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				pluginHelper.findAmbPare("A04003003", timestamp, timestamp);
				accioDescripcio = "Consulta unitats donat un pare";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "ARXIU":
				crearEsborrarArxiu();
				accioDescripcio = "Creació i eliminació d'un expedient";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "DADESEXT":
				pluginHelper.dadesExternesProvinciesFindAmbComunitat("10");
				accioDescripcio = "Consulta de tipus de via";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "SIGNATURA":				
				pluginHelper.signarDocument(
						String.valueOf( new Date().getTime()), 
						"test_firmat.pdf", 
						"Prova firma en servidor diagnòstic Distribucio", 
						imputAByte(this.getClass().getResourceAsStream("/diagnostic/test_firma.pdf")), 
						"application/pdf", 
						"TD99");
				accioDescripcio = "Comunicació amb el plugin de firma en servidor correcta";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
			case "VALIDASIG":
				byte[] axiuSignat = imputAByte(this.getClass().getResourceAsStream("/diagnostic/test_validacio_firma.pdf"));	
				// PENDENT DE REVISAR COM OBTENIR EL NUMERO DE REGISTRE
				pluginHelper.validaSignaturaObtenirDetalls("/diagnostic/test_validacio_firma.pdf", "application/pdf", axiuSignat, null, "");
				accioDescripcio = "Obtenir informació de document firmat";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "GESDOC":
				provaGestioDocumental();		
				accioDescripcio = "Consultant document a dins la gestió documental";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "PROCEDIMENT":
				pluginHelper.procedimentFindByCodiDir3("A04019281");
				accioDescripcio = "Consulta dels procediments pel codi DIR3";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
			case "SERVEI":
				pluginHelper.serveiFindByCodiDir3("A04019281");
				accioDescripcio = "Consulta dels serveis pel codi DIR3";
				diagnostic.setProva(accioDescripcio);
				diagnostic.setCorrecte(true); 
				break;
				
}
		} catch (Exception e) {
			StringBuilder err = new StringBuilder();
			err.append("Error amb la integració ").append(codiIntegracio).append(". Excepció: ").append(e.getMessage());
			diagnostic.setCorrecte(false); 
			diagnostic.setErrMsg(err.toString());
		}
		return diagnostic;
	}

	private void crearEsborrarArxiu() {
		byte[] array = new byte[7]; 
		byte[] array2 = new byte[5]; 
	    new Random().nextBytes(array);
	    new Random().nextBytes(array2);
	    String aleatori_1 = new String(array, Charset.forName("UTF-8"));	
	    String aleatori_2 = new String(array2, Charset.forName("UTF-8"));	
	    
	    String codContingu = pluginHelper.saveRegistreAsExpedientInArxiu(aleatori_1, aleatori_2, "LIM");
	 // PENDENT DE REVISAR COM OBTENIR EL NUMERO DE REGISTRE
		pluginHelper.arxiuExpedientEliminar(codContingu,"");
	}

	private void provaGestioDocumental() {
		// PENDENT DE REVISAR COM OBTENIR EL NUMERO DE REGISTRE
		String indenti = pluginHelper.gestioDocumentalCreate(GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, new byte[0], "");
		pluginHelper.gestioDocumentalDelete(indenti, GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP);
	}
	
	public static byte[] imputAByte(InputStream input) throws IOException
	{
	    byte[] buffer = new byte[8192];
	    int bytesRead;
	    ByteArrayOutputStream output = new ByteArrayOutputStream();
	    while ((bytesRead = input.read(buffer)) != -1)
	    {
	        output.write(buffer, 0, bytesRead);
	    }
	    return output.toByteArray();
	}
}
