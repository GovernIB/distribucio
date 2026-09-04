package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.TipusTransicioEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganizzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.model.UnitatOrganitzativaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = BaseConfig.DB_PREFIX + "unitat_organitzativa")
@Getter
@Setter
@NoArgsConstructor
public class UnitatOrganitzativaResourceEntity extends BaseAuditableEntity<UnitatOrganitzativaResource, Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

//	@JoinTable(
//			name = BaseConfig.DB_PREFIX + "uo_sinc_rel",
//			joinColumns = {
//					@JoinColumn(name = "antiga_uo", referencedColumnName = "id", nullable = false)
//			},
//			inverseJoinColumns = {
//					@JoinColumn(name = "nova_uo", referencedColumnName = "id", nullable = false)
//			})
//	@ManyToMany
//	private List<UnitatOrganitzativaResourceEntity> noves = new ArrayList<UnitatOrganitzativaResourceEntity>();
//	@ManyToMany(mappedBy = "noves")
//	private List<UnitatOrganitzativaResourceEntity> antigues = new ArrayList<UnitatOrganitzativaResourceEntity>();
	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;
	@Column(name = "codi", length = 9, nullable = false, unique = true)
	private String codi;
	@Column(name = "denominacio", length = 300, nullable = false)
	private String denominacio;
	@Column(name = "nif_cif", length = 9)
	private String nifCif;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "codi_unitat_superior", referencedColumnName = "codi")
	private UnitatOrganitzativaResourceEntity unitatSuperior;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "codi_unitat_arrel", referencedColumnName = "codi")
	private UnitatOrganitzativaResourceEntity unitatArrel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codi_dir3_entitat", referencedColumnName = "codi_dir3")
    private EntitatResourceEntity entitat;

	@Column(name = "data_creacio_oficial")
	private Date dataCreacioOficial;
	@Column(name = "data_supressio_oficial") 
	private Date dataSupressioOficial;
	@Column(name = "data_extincio_funcional") 
	private Date dataExtincioFuncional;
	@Column(name = "data_anulacio") 
	private Date dataAnulacio;

    @Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 1)
	private UnitatOrganizzativaEstatEnumDto estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

	@Column(name = "codi_pais", length = 3) 
	private String codiPais;
	@Column(name = "codi_comunitat", length = 1) 
	private String codiComunitat;
	@Column(name = "codi_provincia", length = 1) 
	private String codiProvincia;
	@Column(name = "codi_postal", length = 5) 
	private String codiPostal;
	@Column(name = "nom_localitat", length = 50) 
	private String nomLocalitat;
	@Column(name = "localitat", length = 40) 
	private String localitat;
	@Column(name = "adressa", length = 70) 
	private String adressa;
	@Column(name = "tipus_via") 
	private Long tipusVia;
	@Column(name = "nom_via", length = 200) 
	private String nomVia;
	@Column(name = "num_via", length = 100) 
	private String numVia;

    public String getCodiAndNom() {
		return this.codi + " - " + this.denominacio;
	}
}
