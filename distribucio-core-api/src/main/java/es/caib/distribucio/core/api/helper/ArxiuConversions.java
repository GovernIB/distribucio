package es.caib.distribucio.core.api.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;

/**
 * Mètodes comuns per convertir tipus i enumeracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuConversions {

	/** Map amb el mapeig dels perfils de firma cap als perfils admesos per l'Arxiu. */
	private static Map<String, String> mapPerfilsFirma = new HashMap<String, String>();
	static {
		mapPerfilsFirma.put("AdES-BES", "BES");
		mapPerfilsFirma.put("AdES-EPES", "EPES");
		mapPerfilsFirma.put("AdES-T", "T");
		mapPerfilsFirma.put("AdES-C", "C");
		mapPerfilsFirma.put("AdES-X", "X");
		mapPerfilsFirma.put("AdES-X1", "X");
		mapPerfilsFirma.put("AdES-X2", "X");
		mapPerfilsFirma.put("AdES-XL", "XL");
		mapPerfilsFirma.put("AdES-XL1", "XL");
		mapPerfilsFirma.put("AdES-XL2", "XL");
		mapPerfilsFirma.put("AdES-A", "A");
		mapPerfilsFirma.put("PAdES-LTV", "LTV");
		mapPerfilsFirma.put("PAdES-Basic", "BES");
	}
	
	/** Map amb els perfils de firma reconeguts a l'Arxi per tipus de firma segons el model de l'Arxiu.
	 * @see <a href=”https://github.com/GovernIB/gdib/blob/9167685d96211e792ea34059ad5284d17dd2e769/gdib-amp/src/main/amp/config/alfresco/module/gdib-amp/models/caibModel.xml#L531”>modelCaib.xml</a> */
	private static Map<ArxiuFirmaTipusEnumDto, List<String>> mapPerfilsPermesos = new HashMap<ArxiuFirmaTipusEnumDto, List<String>>();
	static {
		// XADES, CADES
		mapPerfilsPermesos.put(ArxiuFirmaTipusEnumDto.XADES_DET, Arrays.asList( "BES", "EPES", "T", "C", "X", "XL", "A", "BASELINE B-Level", "BASELINE LT- Level", "BASELINE LTA-Level"));
		mapPerfilsPermesos.put(ArxiuFirmaTipusEnumDto.XADES_ENV, mapPerfilsPermesos.get(ArxiuFirmaTipusEnumDto.XADES_DET));
		mapPerfilsPermesos.put(ArxiuFirmaTipusEnumDto.CADES_ATT, mapPerfilsPermesos.get(ArxiuFirmaTipusEnumDto.XADES_DET));
		mapPerfilsPermesos.put(ArxiuFirmaTipusEnumDto.CADES_DET, mapPerfilsPermesos.get(ArxiuFirmaTipusEnumDto.XADES_DET));
		// PADES
		mapPerfilsPermesos.put(ArxiuFirmaTipusEnumDto.PADES, Arrays.asList(  "BES", "EPES", "BASELINE B-Level", "BASELINE LT- Level", "BASELINE LTA-Level", "LTV" ));
	}
	
	/** Mètode estàtic per comprobar si el perfil està acceptat per l'Arxiu per un tipus de firma. */
	public static boolean checkTipusPrefil(ArxiuFirmaTipusEnumDto firmaTipus, String firmaPerfil) {
		boolean valid = false;
		if (firmaTipus != null && firmaPerfil != null) {
			List<String> perfils = mapPerfilsPermesos.get(firmaTipus);
			valid = perfils != null && perfils.contains(firmaPerfil);
		}
		return valid;
	}
	/** Consulta els perfils admesos pel tipus de firma. */
	public static List<String> getPerfilsAdmesosPerTipus(ArxiuFirmaTipusEnumDto firmaTipus) {
		List<String> perfils = new ArrayList<>();
		if (firmaTipus != null && mapPerfilsPermesos.containsKey(firmaTipus)) {
			for (String perfil : mapPerfilsPermesos.get(firmaTipus)) {
				perfils.add(perfil);
			}
		}
		return perfils;
	}
	
	/** Mapeja els diferents perfils de firma que pot retornar el plugin de firma simple o de validació cap
	 * als perfils admesos per l'Arxiu.
	 * 
	 * @param perfil
	 * @return
	 */
	public static String toPerfilFirmaArxiu(String perfil) {
		if (mapPerfilsFirma.containsKey(perfil))
			perfil = mapPerfilsFirma.get(perfil);
		return perfil;
	}
	
	/** Mètode per obrtenir el tipus de firma a partir del tipus i format retornat pel plugin de validació de firmes. */
	public static String toFirmaTipus(String tipus, String format) {
		
		String tipusFirma = null;
		if (tipus.equals("XAdES") && format.equals("explicit/detached")) {
			tipusFirma = "TF02"; // ArxiuFirmaTipusEnumDto.XADES_DET
		} else if (tipus.equals("XAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = "TF03"; // ArxiuFirmaTipusEnumDto.XADES_ENV;
		} else if (tipus.equals("CAdES") && format.equals("explicit/detached")) {
			tipusFirma = "TF04"; // ArxiuFirmaTipusEnumDto.CADES_DET;
		} else if (tipus.equals("CAdES") && format.equals("implicit_enveloping/attached")) {
			tipusFirma = "TF05"; // ArxiuFirmaTipusEnumDto.CADES_ATT;
		} else if (tipus.equals("PAdES") || format.equals("implicit_enveloped/attached")) {
				tipusFirma = "TF06"; // ArxiuFirmaTipusEnumDto.PADES
		} else {
			tipusFirma = tipus;
		}
		return tipusFirma;
	}

	public static ArxiuFirmaTipusEnumDto toArxiuFirmaTipus(String tipusFirmaEni) {
		
		switch (tipusFirmaEni) {
			case "TF01":
				return ArxiuFirmaTipusEnumDto.CSV;
			case "TF02":
				return ArxiuFirmaTipusEnumDto.XADES_DET;
			case "TF03":
				return ArxiuFirmaTipusEnumDto.XADES_ENV;
			case "TF04":
				return ArxiuFirmaTipusEnumDto.CADES_DET;
			case "TF05":
				return ArxiuFirmaTipusEnumDto.CADES_ATT;
			case "TF06":
				return ArxiuFirmaTipusEnumDto.PADES;
			case "TF07":
				return ArxiuFirmaTipusEnumDto.SMIME;
			case "TF08":
				return ArxiuFirmaTipusEnumDto.ODT;
			case "TF09":
				return ArxiuFirmaTipusEnumDto.OOXML;
			default:
				return null;
		}
	}
	
}
