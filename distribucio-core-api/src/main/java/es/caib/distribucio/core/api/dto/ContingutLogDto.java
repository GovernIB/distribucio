/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació de log d'una accio realitzada damunt un node.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutLogDto extends AuditoriaDto {

	private Long id;
	private LogTipusEnumDto tipus;
	private String objecteId;
	private LogObjecteTipusEnumDto objecteTipus;
	private LogTipusEnumDto objecteLogTipus;
//	private String param1;
//	private String param2;

	private List<String> params = new ArrayList<>();

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LogTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(LogTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getObjecteId() {
		return objecteId;
	}
	public void setObjecteId(String objecteId) {
		this.objecteId = objecteId;
	}
	public LogObjecteTipusEnumDto getObjecteTipus() {
		return objecteTipus;
	}
	public void setObjecteTipus(LogObjecteTipusEnumDto objecteTipus) {
		this.objecteTipus = objecteTipus;
	}
	public LogTipusEnumDto getObjecteLogTipus() {
		return objecteLogTipus;
	}
	public void setObjecteLogTipus(LogTipusEnumDto objecteLogTipus) {
		this.objecteLogTipus = objecteLogTipus;
	}
	
	
	
//	public String getParam1() {
//		return param1;
//	}
//	public void setParam1(String param1) {
//		this.param1 = param1;
//	}
//	public String getParam2() {
//		return param2;
//	}
//	public void setParam2(String param2) {
//		this.param2 = param2;
//	}

	public List<String> getParams() {
		return params;
	}
	public void setParams(List<String> params) {
		this.params = params;
	}
	public boolean isSecundari() {
		return tipus.equals(LogTipusEnumDto.MODIFICACIO) && objecteId != null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
