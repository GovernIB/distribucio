package es.caib.distribucio.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.core.audit.DistribucioAuditable;

@Entity
@Table(	name = "dis_registre_firma_detall")
@EntityListeners(AuditingEntityListener.class)
public class RegistreFirmaDetallEntity extends DistribucioAuditable<Long> {
	
	
	@Column(name = "data")
	private Date data;
	@Column(name = "responsable_nif", length = 30)
	private String responsableNif;
	@Column(name = "responsable_nom", length = 256)
	private String responsableNom;
	@Column(name = "emissor_certificat", length = 2000)
	private String emissorCertificat;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "firma_id")
	private RegistreAnnexFirmaEntity firma;
	
	
	public static Builder getBuilder(
			ArxiuFirmaDetallDto arxiuFirmaDetallDto,
			RegistreAnnexFirmaEntity firma) {
		return new Builder(
				arxiuFirmaDetallDto.getData(), 
				arxiuFirmaDetallDto.getResponsableNif(), 
				arxiuFirmaDetallDto.getResponsableNom(), 
				arxiuFirmaDetallDto.getEmissorCertificat(), 
				firma);
	}
	
	public static Builder getBuilder(
			RegistreFirmaDetallEntity registreFirmaDetallEntity,
			RegistreAnnexFirmaEntity firma) {
		return new Builder(
				registreFirmaDetallEntity.getData(), 
				registreFirmaDetallEntity.getResponsableNif(), 
				registreFirmaDetallEntity.getResponsableNom(), 
				registreFirmaDetallEntity.getEmissorCertificat(), 
				firma);
	}
	
	public static Builder getBuilder(
			Date data,
			String responsableNif,
			String responsableNom,
			String emissorCertificat,
			RegistreAnnexFirmaEntity firma) {
		return new Builder(
				data, 
				responsableNif, 
				responsableNom, 
				emissorCertificat, 
				firma);
	}
	public static class Builder {
		RegistreFirmaDetallEntity built;
		Builder(
				Date data,
				String responsableNif,
				String responsableNom,
				String emissorCertificat,
				RegistreAnnexFirmaEntity firma) {
			built = new RegistreFirmaDetallEntity();
			built.data = data;
			built.responsableNif = responsableNif;
			built.responsableNom = responsableNom;
			built.emissorCertificat = emissorCertificat;
			built.firma = firma;

		}
		
		public RegistreFirmaDetallEntity build() {
			return built;
		}
	}

	
	
	public Date getData() {
		return data;
	}

	public String getResponsableNif() {
		return responsableNif;
	}

	public String getResponsableNom() {
		return responsableNom;
	}

	public RegistreAnnexFirmaEntity getFirma() {
		return firma;
	}

	public String getEmissorCertificat() {
		return emissorCertificat;
	}



	private static final long serialVersionUID = -1201133969119981591L;



}
