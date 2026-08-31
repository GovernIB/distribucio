/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.domain.Auditable;
import org.springframework.lang.Nullable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * Els camps d'usuari guarden el codi de l'usuari ({@link UsuariEntity#getCodi()})
 * i no una referència a l'entitat, per a poder compartir el mateix
 * {@link org.springframework.data.domain.AuditorAware} amb les entitats de
 * recurs de la capa REACT
 * ({@link es.caib.distribucio.persist.base.entity.BaseAuditableEntity}).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@MappedSuperclass
public class DistribucioAuditable<PK extends Serializable> extends DistribucioPersistable<PK> implements Auditable<String, PK, LocalDateTime> {

	@Column(name = "createdby_codi", length = 64)
	private @Nullable String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	private @Nullable Date createdDate;

	@Column(name = "lastmodifiedby_codi", length = 64)
	private @Nullable String lastModifiedBy;

	@Temporal(TemporalType.TIMESTAMP)
	private @Nullable Date lastModifiedDate;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getCreatedBy()
	 */
	@Override
	public Optional<String> getCreatedBy() {
		return Optional.ofNullable(createdBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setCreatedBy(java.lang.Object)
	 */
	@Override
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getCreatedDate()
	 */
	@Override
	public Optional<LocalDateTime> getCreatedDate() {
		return null == createdDate ? Optional.empty()
				: Optional.of(LocalDateTime.ofInstant(createdDate.toInstant(), ZoneId.systemDefault()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setCreatedDate(java.time.temporal.TemporalAccessor)
	 */
	@Override
	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = Date.from(createdDate.atZone(ZoneId.systemDefault()).toInstant());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getLastModifiedBy()
	 */
	@Override
	public Optional<String> getLastModifiedBy() {
		return Optional.ofNullable(lastModifiedBy);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setLastModifiedBy(java.lang.Object)
	 */
	@Override
	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#getLastModifiedDate()
	 */
	@Override
	public Optional<LocalDateTime> getLastModifiedDate() {
		return null == lastModifiedDate ? Optional.empty()
				: Optional.of(LocalDateTime.ofInstant(lastModifiedDate.toInstant(), ZoneId.systemDefault()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Auditable#setLastModifiedDate(java.time.temporal.TemporalAccessor)
	 */
	@Override
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = Date.from(lastModifiedDate.atZone(ZoneId.systemDefault()).toInstant());
	}

}
