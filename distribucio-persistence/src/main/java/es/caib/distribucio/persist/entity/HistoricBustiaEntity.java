package es.caib.distribucio.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe de model de dades que conté la informació de les dades estadístiques
 * històriques per bústies i per unitat organitzativa. Guarda informació del
 * número d'usuaris per permís directe o per rol.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@Entity
@Table(name = BaseConfig.DB_PREFIX + "his_bustia")
public class HistoricBustiaEntity extends DistribucioPersistable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "his_bustia_entitat_fk"))
	private EntitatEntity entitat;

	/** Distinció per unitat organitzativa. Si és null llavors és un registre dels
	 * agregats de l'entitat.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "unitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "his_bustia_unitat_fk"))
	private UnitatOrganitzativaEntity unitat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 16, nullable = false)
	private HistoricTipusEnumDto tipus;

	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@Column(name = "bustia_id", nullable = false)
	private Long bustiaId;

	@Column(name = "nom", length = 1024, nullable = false)
	private String nom;
	
	/** Número d'usuaris total*/
	@Column(name = "usuaris")
	private Long usuaris = 0L;

	/** Número d'usuaris per permís directe.*/
	@Column(name = "usuaris_permis")
	private Long usuarisPermis = 0L;

	/** Número d'usuaris per permís per rol.*/
	@Column(name = "usuaris_rol")
	private Long usuarisRol= 0L;

    @Column(name = "noves")
    private Long noves = 0L;

    @Column(name = "errors")
    private Long errors = 0L;

    @Column(name = "pendents")
    private Long pendents = 0L;

}
