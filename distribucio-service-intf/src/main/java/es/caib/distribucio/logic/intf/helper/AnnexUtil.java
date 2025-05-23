package es.caib.distribucio.logic.intf.helper;

import java.util.Map;

/**
 * 
 * @author Limit Tecnologies <limit@limit.es>
 * 
 */
public class AnnexUtil {

	public static final int MAX_FITXER_TITOL = 200;
	public static final int MAX_FITXER_NOM = 200;
	public static final int MAX_FITXER_OBSERVACIO = 50;
	
    public static String truncar(String text, int maxLen) {
        if (text == null) return null;
        return text.length() > maxLen ? text.substring(0, Math.max(0, maxLen - 3)) + "..." : text;
    }
    
    public static String truncarNomFitxer(String original, int maxLen) {
        if (original == null) return null;

        String extensio = "";
        int idxPunt = original.lastIndexOf('.');
        if (idxPunt > 0 && idxPunt < original.length() - 1) {
            extensio = original.substring(idxPunt);
            original = original.substring(0, idxPunt);
        }

        int espaiDisponible = maxLen - extensio.length();
        String baseTruncat = truncar(original, espaiDisponible);

        return baseTruncat + extensio;
    }
    
    public static String prepararTitol(String original, int maxLen, Map<String, Integer> titolsComprovats) {
        if (original == null) return null;
        if (original.equals("") || original.startsWith(".")) return String.valueOf(System.currentTimeMillis());
        
        int intent = titolsComprovats.getOrDefault(original, 0);
        String resultat;

        while (true) {
            String sufix = intent > 0 ? " (" + (intent - 1) + ")" : "";
            int espaiDisponible = maxLen - sufix.length();

            boolean truncat = original.length() > espaiDisponible;
            String base = truncat
                    ? original.substring(0, Math.max(0, espaiDisponible - 3)) + "..."
                    : original;

            resultat = base + sufix;

            if (!titolsComprovats.containsKey(resultat)) break;
            intent++;
        }

        titolsComprovats.put(original, intent + 1);
        titolsComprovats.put(resultat, 1);
        return resultat;
    }
    
}
