/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * Mètodes comuns per a gestionar les properties carregades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class LoadedPropertiesHelper {

	private final static Map<String, Boolean> propertiesLoaded = new HashMap<>();

	public Map<String, Boolean> getLoadedProperties() {
		return propertiesLoaded;
	}

	public void reloadProperties(String codeProperties) {
		if (propertiesLoaded.containsKey(codeProperties)) {
			propertiesLoaded.put(codeProperties, false);
		}
	}

}
