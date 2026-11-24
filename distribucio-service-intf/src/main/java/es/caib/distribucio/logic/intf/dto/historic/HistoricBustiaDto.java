package es.caib.distribucio.logic.intf.dto.historic;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació de dades d'anotacions per bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@XmlRootElement(name = "dadaBustia")
@XmlAccessorType (XmlAccessType.PROPERTY)
public class HistoricBustiaDto {

	@JsonIgnore
	@XmlTransient
	private EntitatDto entitat;
	@JsonIgnore
	@XmlTransient
	private UnitatOrganitzativaDto unitat;

	private HistoricTipusEnumDto tipus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
	private Date data;

	private Long bustiaId;
	private String nom;
	private Long usuaris;
	private Long usuarisPermis;
	private Long usuarisRol;

	private Long noves;
	private Long errors;
	private Long pendents;

	@XmlElement(name = "unitatCodi")
	public String getUnitatCodi() {
		return this.unitat != null ? this.unitat.getCodi() : null;
	}
	@XmlElement(name = "unitatNom")
	public String getUnitatNom() {
		return this.unitat != null ? this.unitat.getDenominacio() : null;
	}
}
