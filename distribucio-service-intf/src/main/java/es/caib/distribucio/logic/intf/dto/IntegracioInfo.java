package es.caib.distribucio.logic.intf.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IntegracioInfo {

	IntegracioCodi codi;
	String numeroRegistre;
	String codiEntitat;
	String descripcio;
	String usuariIntegracio;
	String aplicacio;
	Long notificacioId;
	IntegracioAccioTipusEnumDto tipus;
	Long tempsInici;
	List<AccioParam> params = new ArrayList<>();
	
	public IntegracioInfo(IntegracioCodi codi, String numeroRegistre, String descripcio, String usuariIntegracio, IntegracioAccioTipusEnumDto tipus, AccioParam... params) {

		super();
		this.tempsInici = System.currentTimeMillis();
		this.numeroRegistre = numeroRegistre;
		this.codi = codi;
		this.descripcio = descripcio;
		this.usuariIntegracio = usuariIntegracio;
		this.tipus = tipus;
		for (AccioParam param: params) {
			this.params.add(param);
		}
//		if ("CALLBACK".equals(this.codi)) {
//			this.aplicacio = params.length == 4 ? params[2].getValor() : params.length == 3 ? params[2].getValor() : null;
//		}
	}

	public void addParam(String key, String value) {
		this.params.add(new AccioParam(key, value));
	}

	public Long getTempsResposta() {
		return System.currentTimeMillis() - tempsInici;
	}

	public void setAplicacio(String aplicacio) {
		this.aplicacio = aplicacio;
	}
	
}
