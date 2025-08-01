package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.ToString;

@Getter @ToString(includeFieldNames = true)
public class AccioParam {

	String codi;
	String valor;

	public AccioParam(String codi, String valor) {
		this.codi = codi;
		setValor(valor);
	}

	public void setValor(String valor) {
		valor = valor != null && valor.getBytes().length > 1000 ? valor.substring(0, 997) + "..." : valor;
		this.valor = valor;
	}
}
