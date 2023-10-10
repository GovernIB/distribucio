/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del moviment d'un contenidor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutMovimentDto implements Serializable {

	private Long id;
	private ContingutDto contingut;
	private Long origenId;
	private Long destiId;
	private String origenNom;
	private String destiNom;
	private Date data;
	private UsuariDto remitent;
	private String comentari;



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContingutDto getContingut() {
		return contingut;
	}
	public void setContingut(ContingutDto contingut) {
		this.contingut = contingut;
	}
	public Long getOrigenId() {
		return origenId;
	}
	public void setOrigenId(Long origenId) {
		this.origenId = origenId;
	}
	public Long getDestiId() {
		return destiId;
	}
	public void setDestiId(Long destiId) {
		this.destiId = destiId;
	}
	public String getOrigenNom() {
		return origenNom;
	}
	public void setOrigenNom(String origenNom) {
		this.origenNom = origenNom;
	}
	public String getDestiNom() {
		return destiNom;
	}
	public void setDestiNom(String destiNom) {
		this.destiNom = destiNom;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public UsuariDto getRemitent() {
		return remitent;
	}
	public void setRemitent(UsuariDto remitent) {
		this.remitent = remitent;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
