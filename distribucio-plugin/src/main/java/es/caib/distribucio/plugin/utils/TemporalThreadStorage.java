package es.caib.distribucio.plugin.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestiona dades temporals per thread (ThreadLocal).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TemporalThreadStorage {

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        threadLocal.get().put(key, value);
    }

    public static Object get(String key) {
        return threadLocal.get().get(key);
    }

    public static void remove(String key) {
        threadLocal.get().remove(key);
    }

    public static void clear() {
        threadLocal.remove();
    }
    
}
