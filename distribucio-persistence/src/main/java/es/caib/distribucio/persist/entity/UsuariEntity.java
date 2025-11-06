/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Classe de model de dades que conté la informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Getter
@Setter
@Table(name = BaseConfig.DB_PREFIX + "usuari")
public class UsuariEntity implements Serializable {

	@Id
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;
	@Column(name = "nom", length = 200)
	private String nom;
	@Column(name = "nif", length = 9, nullable = false)
	private String nif;
	@Column(name = "email", length = 200)
	private String email;
	@Column(name = "email_alternatiu", length = 200)
	private String emailAlternatiu;	
	@Column(name="idioma", length = 2)
	private String idioma;
	@Column(name = "inicialitzat")
	private boolean inicialitzat = false;
	@Column(name = "rebre_emails")
	private boolean rebreEmailsBustia = true;
	@Column(name = "emails_agrupats")
	private boolean rebreEmailsAgrupats = true;
	@Column(name="rol_actual", length = 64)
	private String rolActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entitat_defecte_id")
    @org.hibernate.annotations.ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_usuari_fk")
    private EntitatEntity entitatPerDefecte;
    @Column(name="num_elements_pagina")
    private Long numElementsPagina;
	
	@Version
	private long version = 0;
	
	public void update(
			String nom,
			String nif,
			String email) {
		this.nom = nom;
		this.nif = nif;
		this.email = email;		
		this.inicialitzat = true;
	}
	
	public void updateEmailAlternatiu(String emailAlternatiu) {
		this.emailAlternatiu = emailAlternatiu;
	}
	
	public void update(
			boolean rebreEmailsBustia,
			boolean rebreEmailsAgrupats,
			String idioma,
            EntitatEntity entitatPerDefecte,
            Long numElementsPagina) {
		this.rebreEmailsBustia = rebreEmailsBustia;
		this.rebreEmailsAgrupats = rebreEmailsAgrupats;
		this.idioma = idioma;
        this.entitatPerDefecte = entitatPerDefecte;
        this.numElementsPagina = numElementsPagina;
	}
	
	public void updateRolActual(String rolActual) {
		this.rolActual = rolActual;
	}	

	/**
	 * Obté el Builder per a crear objectes de tipus Usuari.
	 * 
	 * @param codi
	 *            El codi de l'usuari.
	 * @param nom
	 *            El nom de l'usuari.
	 * @param nif
	 *            El nif de l'usuari.
	 * @param email
	 *            L'areça de correu electrònic de l'usuari.
	 * @return Una nova instància del Builder.
	 */
	public static Builder getBuilder(
			String codi,
			String nom,
			String nif,
			String email,
			String emailAlternatiu,
			String idioma) {
		return new Builder(
				codi,
				nom,
				nif,
				email,
				emailAlternatiu,
				idioma);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta entitat.
	 * 
	 * @author Limit Tecnologies <limit@limit.es>
	 */
	public static class Builder {
		UsuariEntity built;
		Builder(String codi,
				String nom,
				String nif,
				String email,
				String emailAlternatiu,
				String idioma) {
			built = new UsuariEntity();
			built.codi = codi;
			built.nom = nom;
			built.nif = nif;
			built.email = email;
			built.emailAlternatiu = emailAlternatiu;
			built.idioma = idioma;
			built.inicialitzat = true;
		}
		public UsuariEntity build() {
			return built;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codi == null) ? 0 : codi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuariEntity other = (UsuariEntity) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (!codi.equals(other.codi))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -6657066865382086237L;

}
