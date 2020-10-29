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

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
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


	
	@Column(name = "tipus", length = 256, nullable = false)
	@Enumerated(EnumType.STRING)
	private BackofficeTipusEnumDto tipus;
	@Column(name = "codi", length = 20, nullable = false)
	private String codi;
	@Column(name = "nom", length = 64, nullable = false)
	private String nom;
	@Column(name = "url", length = 256, nullable = false)
	private String url;
	@Column(name = "usuari", length = 64)
	private String usuari;
	@Column(name = "contrasenya", length = 64)
	private String contrasenya;
	@Column(name = "intents")
	private Integer intents;
	@Column(name = "temps_entre_intents")
	private Integer tempsEntreIntents;
	
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "ipa_entitat_ipa_backoffice_fk")
	private EntitatEntity entitat;


	
	
	public BackofficeTipusEnumDto getTipus() {
	    return tipus;
	}
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
	public static Builder getBuilder(
			BackofficeTipusEnumDto tipus,
			String codi,
			String nom,
			String url,
			EntitatEntity entitat) {
		return new Builder(
				tipus,
				codi,
				nom,
				url,
				entitat);
	}
	public static class Builder {

	    BackofficeEntity built;

		Builder(
				BackofficeTipusEnumDto tipus,
				String codi,
				String nom,
				String url,
				EntitatEntity entitat) {
			built = new BackofficeEntity();
			built.tipus = tipus;
	        built.codi = codi;
	        built.nom = nom;
	        built.url = url;
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
			BackofficeTipusEnumDto tipus,
			String codi,
			String nom,
			String url,
			String usuari,
			String contrasenya,
			Integer intents,
			Integer tempsEntreIntents) {
		this.tipus = tipus;
		this.codi = codi;
		this.nom = nom;
		this.url = url;
		this.usuari = usuari;
		this.contrasenya = contrasenya;
		this.intents = intents;
		this.tempsEntreIntents = tempsEntreIntents;
	}
	
	
	
	
	
	private static final long serialVersionUID = -8765569320503898715L;

}