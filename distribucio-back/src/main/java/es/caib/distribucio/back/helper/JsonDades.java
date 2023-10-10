package es.caib.distribucio.back.helper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class JsonDades {

    private Map<String, List<JsonDadesUo>> jsonDadesUoMap;
    private Map<String, List<JsonDadesEstat>> jsonDadesEstatMap;
    private Map<String, List<JsonDadesBustia>> jsonDadesBustiaMap;
	
    public JsonDades(Map<String, List<JsonDadesUo>> jsonDadesUoMap, Map<String, List<JsonDadesEstat>> jsonDadesEstatMap,
			Map<String, List<JsonDadesBustia>> jsonDadesBustiaMap) {
		this.jsonDadesUoMap = jsonDadesUoMap;
		this.jsonDadesEstatMap = jsonDadesEstatMap;
		this.jsonDadesBustiaMap = jsonDadesBustiaMap;
	}

	public Map<String, List<JsonDadesUo>> getJsonDadesUoMap() {
		return jsonDadesUoMap;
	}

	public void setJsonDadesUoMap(Map<String, List<JsonDadesUo>> jsonDadesUoMap) {
		this.jsonDadesUoMap = jsonDadesUoMap;
	}

	public Map<String, List<JsonDadesEstat>> getJsonDadesEstatMap() {
		return jsonDadesEstatMap;
	}

	public void setJsonDadesEstatMap(Map<String, List<JsonDadesEstat>> jsonDadesEstatMap) {
		this.jsonDadesEstatMap = jsonDadesEstatMap;
	}

	public Map<String, List<JsonDadesBustia>> getJsonDadesBustiaMap() {
		return jsonDadesBustiaMap;
	}

	public void setJsonDadesBustiaMap(Map<String, List<JsonDadesBustia>> jsonDadesBustiaMap) {
		this.jsonDadesBustiaMap = jsonDadesBustiaMap;
	}

}
