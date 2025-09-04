package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.UNITATS;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class UnitatsOrganitzativesPluginHelper extends AbstractPluginHelper<UnitatsOrganitzativesPlugin> {

	public static final String GRUP = "UNITATS";
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	public UnitatsOrganitzativesPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository) {
		super(integracioHelper, configHelper, entitatRepository);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var entitats = entitatRepository.findAll();
		IntegracioDiagnostic diagnostic;
		var diagnosticOk = true;
		String codi;
		for (var entitat : entitats) {
			codi = entitat.getCodi();
			try {
				var plugin = pluginMap.get(codi);
				if (plugin == null)  {
					continue;
				}
				var unitats = plugin.cercaUnitats(entitat.getCodiDir3(), null, null, null, null, null, null, null);
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(unitats != null && !unitats.isEmpty());
				diagnostics.put(codi, diagnostic);
			} catch(Exception ex) {
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setErrMsg(ex.getMessage());
				diagnostics.put(codi, diagnostic);
				diagnosticOk = false;
			}
		}
		if (diagnostics.isEmpty() && !entitats.isEmpty()) {
			var entitat = entitatRepository.findByCodi(getCodiEntitatActual());
			var unitats = unitatsOrganitzativesFindByFiltre(entitat.getCodiDir3(), null, null, null, null, null, null);
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(unitats != null && !unitats.isEmpty());
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	public UnitatOrganitzativa findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		String accioDescripcio = "Consulta unitat donat un pare"; 
		
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			UnitatOrganitzativa unitat = getPlugin().findUnidad(
					pareCodi, fechaActualizacion, fechaSincronizacion);
			if (unitat != null) {
				// RegistreNumero no cal!!!
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				return unitat;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge,
						null);
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}
	
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		String accioDescripcio = "Consulta llista d'unitats donat un pare";
		
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativa> arbol = getPlugin().findAmbPare(
					pareCodi, 
					fechaActualizacion,
					fechaSincronizacion);
			// Remove from list unitats that are substituted by itself
			removeUnitatsSubstitutedByItself(arbol);

			if (arbol != null && !arbol.isEmpty()) {
				
				log.info("Consulta d'unitats a WS [tot camps](" +
						"codiDir3=" + pareCodi + ", " +
						"fechaActualizacion=" + fechaActualizacion + ", " +
						"fechaSincronizacion=" + fechaSincronizacion + ")");
				for (UnitatOrganitzativa un : arbol) {
					log.info(ToStringBuilder.reflectionToString(un));
				}
				// RegistreNumero no cal!!!
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				
			} else {
				log.info("No s'han trobat cap unitats per consulta a WS (" +
						"codiDir3=" + pareCodi + ", " +
						"fechaActualizacion=" + fechaActualizacion + ", " +
						"fechaSincronizacion=" + fechaSincronizacion + ")");
				
				accioDescripcio = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
			return arbol;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}

	public List<UnitatOrganitzativaDto> unitatsOrganitzativesFindByFiltre(
			String codiUnitat, 
			String denominacioUnitat,
			String codiNivellAdministracio, 
			String codiComunitat, 
			String codiProvincia, 
			String codiLocalitat, 
			Boolean esUnitatArrel) {
		String accioDescripcio = "Consulta d'unitats organitzatives donat un filtre";

		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiUnitat", codiUnitat);
		accioParams.put("denominacioUnitat", denominacioUnitat);
		accioParams.put("codiNivellAdministracio", codiNivellAdministracio);
		accioParams.put("codiComunitat", codiComunitat);
		accioParams.put("codiProvincia", codiProvincia);
		accioParams.put("codiLocalitat", codiLocalitat);
		accioParams.put("esUnitatArrel", esUnitatArrel == null ? "null" : esUnitatArrel.toString() );
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativaDto> unitatsOrganitzatives = conversioTipusHelper.convertirList(
					getPlugin().cercaUnitats(
							codiUnitat, 
							denominacioUnitat, 
							toLongValue(codiNivellAdministracio), 
							toLongValue(codiComunitat), 
							false, 
							esUnitatArrel, 
							toLongValue(codiProvincia), 
							codiLocalitat),
					UnitatOrganitzativaDto.class);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return unitatsOrganitzatives;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al realitzar la cerca de unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	protected UnitatsOrganitzativesPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin d'unitats organitzatives";
			log.error(msg);
			throw new SistemaExternException(UNITATS.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (UnitatsOrganitzativesPlugin)clazz.
						getDeclaredConstructor(Properties.class).
						newInstance(properties);
			} catch (Exception ex) {
				throw new SistemaExternException(
						UNITATS.name(),
						"Error al crear la instància del plugin d'unitats organitzatives amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					UNITATS.name(),
					"No està configurada la classe pel plugin d'unitats organitzatives");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.unitats.organitzatives.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.DIR;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}
	
	private Long toLongValue(String text) {
		if (text == null || text.isEmpty())
			return null;
		return Long.parseLong(text);
	}
	
	/**
	 * Remove from list unitats that are substituted by itself
	 * for example if webservice returns two elements:
	 * 
	 * UnitatOrganitzativa(codi=A00000010, estat=E, historicosUO=[A00000010])
	 * UnitatOrganitzativa(codi=A00000010, estat=V, historicosUO=null)
	 * 
	 * then remove the first one.
	 * That way this transition can be treated by application the same way as transition CANVI EN ATRIBUTS
	 */
	private void removeUnitatsSubstitutedByItself(List<UnitatOrganitzativa> unitatsOrganitzatives) {
		if (!unitatsOrganitzatives.isEmpty()) {
			Iterator<UnitatOrganitzativa> i = unitatsOrganitzatives.iterator();
			while (i.hasNext()) {
				UnitatOrganitzativa unitatOrganitzativa = i.next();
				if (unitatOrganitzativa.getHistoricosUO()!=null 
						&& !unitatOrganitzativa.getHistoricosUO().isEmpty() 
						&& unitatOrganitzativa.getHistoricosUO().size() == 1 
						&& unitatOrganitzativa.getHistoricosUO().get(0).equals(unitatOrganitzativa.getCodi())) {
					i.remove();
				}
			}
		}
	}
}
