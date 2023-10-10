/**
 *
 */
package es.caib.distribucio.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.audit.DistribucioAuditable;

/**
 * Classe del model de dades que representa un backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_backoffice")
@EntityListeners(AuditingEntityListener.class)
public class BackofficeEntity extends DistribucioAuditable<Long> {


	
	@Column(name = "codi", length = 20, nullable = false)
	private String codi;
	@Column(name = "nom", length = 64, nullable = false)
	private String nom;
	@Column(name = "url", length = 256, nullable = false)
	private String url;
	@Column(name = "usuari", length = 255)
	private String usuari;
	@Column(name = "contrasenya", length = 255)
	private String contrasenya;
	@Column(name = "intents")
	private Integer intents;
	@Column(name = "temps_entre_intents")
	private Integer tempsEntreIntents;
	
	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private BackofficeTipusEnumDto tipus;
	
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_ipa_backoffice_fk")
	private EntitatEntity entitat;


	
	
	public String getCodi() {
	    return codi;
	}
	public String getNom() {
	    return nom;
	}
	public String getUrl() {
	    return url;
	}
	public String getUsuari() {
	    return usuari;
	}
	public String getContrasenya() {
	    return contrasenya;
	}
	public Integer getIntents() {
	    return intents;
	}
	public Integer getTempsEntreIntents() {
	    return tempsEntreIntents;
	}
	public EntitatEntity getEntitat() {
	    return entitat;
	}
	public BackofficeTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(BackofficeTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public static Builder getBuilder(
			String codi,
			String nom,
			String url,
			BackofficeTipusEnumDto tipus,
			EntitatEntity entitat) {
		return new Builder(
				codi,
				nom,
				url,
				tipus,
				entitat);
	}
	public static class Builder {

	    BackofficeEntity built;

		Builder(
				String codi,
				String nom,
				String url,
				BackofficeTipusEnumDto tipus, 
				EntitatEntity entitat) {
			built = new BackofficeEntity();
	        built.codi = codi;
	        built.nom = nom;
	        built.url = url;
	        built.tipus = tipus;
	        built.entitat = entitat;
	    }

	    public BackofficeEntity build() {
	        return built;
	    }

	    public Builder usuari(String usuari) {
	        built.usuari = usuari;
	        return this;
	    }

	    public Builder contrasenya(String contrasenya) {
	        built.contrasenya = contrasenya;
	        return this;
	    }

	    public Builder intents(Integer intents) {
	        built.intents = intents;
	        return this;
	    }

	    public Builder tempsEntreIntents(Integer tempsEntreIntents) {
	        built.tempsEntreIntents = tempsEntreIntents;
	        return this;
	    }
	}

	public void update(
			String codi, 
			String nom,
			String url,
			String usuari,
			String contrasenya,
			Integer intents,
			Integer tempsEntreIntents, 
			BackofficeTipusEnumDto tipus) {
		this.codi = codi;
		this.nom = nom;
		this.url = url;
		this.usuari = usuari;
		this.contrasenya = contrasenya;
		this.intents = intents;
		this.tempsEntreIntents = tempsEntreIntents;
		this.tipus = tipus;
	}
	
	
	
	
	
	private static final long serialVersionUID = -8765569320503898715L;

}