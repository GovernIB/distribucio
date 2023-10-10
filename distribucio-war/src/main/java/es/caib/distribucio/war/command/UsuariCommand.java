/**
 * 
 */
package es.caib.distribucio.war.command;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;	
	private String[] rols;
	private String idioma;
	private Boolean rebreEmailsBustia;
	private Boolean rebreEmailsAgrupats;
	private Long bustiaPerDefecte;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmailAlternatiu() {
		return emailAlternatiu;
	}
	public void setEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = emailAlternatiu;
	}
	public String[] getRols() {
		return rols;
	}
	public void setRols(String[] rols) {
		this.rols = rols;
	}
	public String getIdioma() {
		return idioma;
	}
	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	public Boolean getRebreEmailsBustia() {
		return rebreEmailsBustia;
	}
	public void setRebreEmailsBustia(Boolean rebreEmailsBustia) {
		this.rebreEmailsBustia = rebreEmailsBustia;
	}
	public Boolean getRebreEmailsAgrupats() {
		return rebreEmailsAgrupats;
	}
	public void setRebreEmailsAgrupats(Boolean rebreEmailsAgrupats) {
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
	}
	public Long getBustiaPerDefecte() {
		return bustiaPerDefecte;
	}
	public void setBustiaPerDefecte(Long bustiaPerDefecte) {
		this.bustiaPerDefecte = bustiaPerDefecte;
	}
	
	public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				UsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	private static final long serialVersionUID = -139254994389509932L;

}
