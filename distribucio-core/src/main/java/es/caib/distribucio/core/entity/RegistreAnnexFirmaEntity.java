package es.caib.distribucio.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.audit.DistribucioAuditable;

@Entity
@Table(	name = "dis_registre_annex_firma")
@EntityListeners(AuditingEntityListener.class)
public class RegistreAnnexFirmaEntity extends DistribucioAuditable<Long> {
	
	@Column(name = "tipus", length = 30)
	private String tipus;
	@Column(name = "perfil", length = 30)
	private String perfil;
	@Column(name = "fitxer_nom", length = 256)
	private String fitxerNom;
	@Column(name = "tipus_mime", length = 30)
	private String tipusMime;
	@Column(name = "csv_regulacio", length = 640)
	private String csvRegulacio;
	@Column(name = "autofirma")
	private Boolean autofirma = false;
	@Column(name = "gesdoc_fir_id")
	private String gesdocFirmaId;
	
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "annex_id")
	@ForeignKey(name = "dis_firma_annex_fk")
	private RegistreAnnexEntity annex;
	
	public String getTipus() {
		return tipus;
	}

	public String getPerfil() {
		return perfil;
	}

	public String getFitxerNom() {
		return fitxerNom;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTipusMime() {
		return tipusMime;
	}

	public String getCsvRegulacio() {
		return csvRegulacio;
	}

	public boolean isAutofirma() {
		return autofirma != null ? autofirma : false ;
	}

	public String getGesdocFirmaId() {
		return gesdocFirmaId;
	}

	public RegistreAnnexEntity getAnnex() {
		return annex;
	}
	
	public void updateGesdocFirmaId(String gesdocFirmaId) {
		this.gesdocFirmaId = gesdocFirmaId;
	}
	
	
	public static Builder getBuilder(
			String tipus,
			String perfil,
			String fitxerNom,
			String tipusMime,
			String csvRegulacio,
			boolean autofirma,
			RegistreAnnexEntity annex) {
		return new Builder(
				tipus,
				perfil,
				fitxerNom,
				tipusMime,
				csvRegulacio,
				autofirma,
				annex);
	}
	public static class Builder {
		RegistreAnnexFirmaEntity built;
		Builder(
				String tipus,
				String perfil,
				String fitxerNom,
				String tipusMime,
				String csvRegulacio,
				boolean autofirma,
				RegistreAnnexEntity annex) {
			built = new RegistreAnnexFirmaEntity();
			
			built.tipus = tipus;
			built.perfil = perfil;
			built.fitxerNom = fitxerNom;
			built.tipusMime = tipusMime;
			built.csvRegulacio =csvRegulacio;
			built.autofirma = autofirma;
			built.annex = annex;
		}
		
		public Builder gesdocFirmaId(String gesdocFirmaId) {
			built.gesdocFirmaId = gesdocFirmaId;
			return this;
		}
		public RegistreAnnexFirmaEntity build() {
			return built;
		}
	}

	private static final long serialVersionUID = -1201133969119981591L;

	public void updatePerNovaFirma(
			String tipus, 
			String perfil, 
			String fitxerNom, 
			String tipusMime,
			String csvRegulacio, 
			Boolean autofirma, 
			String gesdocFirmaId, 
			RegistreAnnexEntity annex) {
		this.tipus = tipus;
		this.perfil = perfil;
		this.fitxerNom = fitxerNom;
		this.tipusMime = tipusMime;
		this.csvRegulacio = csvRegulacio;
		this.autofirma = autofirma;
		this.gesdocFirmaId = gesdocFirmaId;
		this.annex = annex;
	}

}
