package es.caib.distribucio.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.distribucio.logic.intf.dto.TipusTransicioEnumDto;
import es.caib.distribucio.core.audit.DistribucioAuditable;


@Entity
@Table(name = "dis_unitat_organitzativa")
@EntityListeners(AuditingEntityListener.class)
public class UnitatOrganitzativaEntity extends DistribucioAuditable<Long>{


	@JoinTable(name = "dis_uo_sinc_rel", joinColumns = {
	@JoinColumn(name = "antiga_uo", referencedColumnName = "id", nullable = false) }, inverseJoinColumns = {
	@JoinColumn(name = "nova_uo", referencedColumnName = "id", nullable = false) })
	@ManyToMany
	private List<UnitatOrganitzativaEntity> noves = new ArrayList<UnitatOrganitzativaEntity>();
	
	@ManyToMany(mappedBy = "noves")
	private List<UnitatOrganitzativaEntity> antigues = new ArrayList<UnitatOrganitzativaEntity>();
	
	@Column(name = "tipus_transicio", length = 12)
	@Enumerated(EnumType.STRING)
	private TipusTransicioEnumDto tipusTransicio;

	private static final long serialVersionUID = 1L;
	@Column(name = "codi", length = 9, nullable = false)
	private String codi;
	@Column(name = "denominacio", length = 300, nullable = false)
	private String denominacio;
	@Column(name = "nif_cif", length = 9)
	private String nifCif;
	@Column(name = "codi_unitat_superior", length = 9)
	private String codiUnitatSuperior;

	@Column(name = "codi_unitat_arrel", length = 9) 
	private String codiUnitatArrel;
	
	@Column(name = "codi_dir3_entitat")
	private String codiDir3Entitat;
	
	@Column(name = "data_creacio_oficial") 
	private Date dataCreacioOficial;
	@Column(name = "data_supressio_oficial") 
	private Date dataSupressioOficial;
	@Column(name = "data_extincio_funcional") 
	private Date dataExtincioFuncional;
	@Column(name = "data_anulacio") 
	private Date dataAnulacio;
	@Column(name = "estat", length = 1) 
	private String estat; // V: Vigente, E: Extinguido, A: Anulado, T: Transitorio

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

	
	


	

	public List<UnitatOrganitzativaEntity> getNoves() {
		return noves;
	}
	public TipusTransicioEnumDto getTipusTransicio() {
		return tipusTransicio;
	}

	public void updateTipusTransicio(TipusTransicioEnumDto tipusTransicio) {
		this.tipusTransicio = tipusTransicio;
	}

	public void addNova(UnitatOrganitzativaEntity nova) {
		noves.add(nova);
	}

	public void addAntiga(UnitatOrganitzativaEntity antiga) {
		antigues.add(antiga);
	}

	public List<UnitatOrganitzativaEntity> getAntigues() {
		return antigues;
	}

	public String getCodi() {
		return codi;
	}

	public String getDenominacio() {
		return denominacio;
	}

	public String getNifCif() {
		return nifCif;
	}

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public String getCodiUnitatArrel() {
		return codiUnitatArrel;
	}
	
	public String getCodiDir3Entitat() {
		return codiDir3Entitat;
	}
	
	public void setCodiDir3Entitat(String codiDir3Entitat) {
		this.codiDir3Entitat = codiDir3Entitat;
	}

	public Date getDataCreacioOficial() {
		return dataCreacioOficial;
	}

	public Date getDataSupressioOficial() {
		return dataSupressioOficial;
	}

	public Date getDataExtincioFuncional() {
		return dataExtincioFuncional;
	}

	public Date getDataAnulacio() {
		return dataAnulacio;
	}

	public String getEstat() {
		return estat;
	}

	public String getCodiPais() {
		return codiPais;
	}

	public String getCodiComunitat() {
		return codiComunitat;
	}

	public String getCodiProvincia() {
		return codiProvincia;
	}

	public String getCodiPostal() {
		return codiPostal;
	}

	public String getNomLocalitat() {
		return nomLocalitat;
	}

	public String getLocalitat() {
		return localitat;
	}

	public String getAdressa() {
		return adressa;
	}

	public Long getTipusVia() {
		return tipusVia;
	}

	public String getNomVia() {
		return nomVia;
	}

	public String getNumVia() {
		return numVia;
	}
	
	public String getCodiAndNom() {
		return this.codi + " - " + this.denominacio + "";
	}
	
	public void update(
			String codi,
			String denominacio,
			String nifCif,
			String estat, 
			String codiUnitatSuperior,
			String codiUnitatArrel,
			String codiPais,
			String codiComunitat,
			String codiProvincia,
			String codiPostal,
			String localitat,
			Long tipusVia,
			String nomVia,
			String numVia) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.estat = estat;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.codiPais = codiPais;
		this.codiComunitat = codiComunitat;
		this.codiProvincia = codiProvincia;
		this.codiPostal = codiPostal;
		this.localitat = localitat;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
	}
	
	public void update(
			String codi,
			String denominacio,
			String nifCif,
			String codiUnitatSuperior,
			String codiUnitatArrel,
			Date dataCreacioOficial,
			Date dataSupressioOficial,
			Date dataExtincioFuncional,
			Date dataAnulacio,
			String estat, 
			String codiPais,
			String codiComunitat,
			String codiProvincia,
			String codiPostal,
			String nomLocalitat,
			String localitat,
			String adressa,
			Long tipusVia,
			String nomVia,
			String numVia) {
		this.codi = codi;
		this.denominacio = denominacio;
		this.nifCif = nifCif;
		this.codiUnitatSuperior = codiUnitatSuperior;
		this.codiUnitatArrel = codiUnitatArrel;
		this.dataCreacioOficial = dataCreacioOficial;
		this.dataSupressioOficial = dataSupressioOficial;
		this.dataExtincioFuncional = dataExtincioFuncional;
		this.dataAnulacio = dataAnulacio;
		this.estat = estat;
		this.codiPais = codiPais;
		this.codiComunitat = codiComunitat;
		this.codiProvincia = codiProvincia;
		this.codiPostal = codiPostal;
		this.nomLocalitat = nomLocalitat;
		this.localitat = localitat;
		this.adressa = adressa;
		this.tipusVia = tipusVia;
		this.nomVia = nomVia;
		this.numVia = numVia;
	}
	
	public void updateDenominacio(
			String denominacio ) {
		this.denominacio = denominacio;
	}
	
	public static Builder getBuilder(
			String codi,
			String denominacio) {
		return new Builder(
				codi,
				denominacio);
	}
	
	public static class Builder {
		UnitatOrganitzativaEntity built;
		Builder(
				String codi,
				String denominacio) {
			built = new UnitatOrganitzativaEntity();
			built.codi = codi;
			built.denominacio = denominacio;
		}

		public Builder nifCif(String nifCif) {
			built.nifCif = nifCif;
			return this;
		}
		public Builder codiUnitatSuperior(String codiUnitatSuperior) {
			built.codiUnitatSuperior = codiUnitatSuperior;
			return this;
		}
		public Builder codiUnitatArrel(String codiUnitatArrel) {
			built.codiUnitatArrel = codiUnitatArrel;
			return this;
		}
		public Builder dataCreacioOficial(Date dataCreacioOficial) {
			built.dataCreacioOficial = dataCreacioOficial;
			return this;
		}
		public Builder dataSupressioOficial(Date dataSupressioOficial) {
			built.dataSupressioOficial = dataSupressioOficial;
			return this;
		}
		public Builder dataExtincioFuncional(Date dataExtincioFuncional) {
			built.dataExtincioFuncional = dataExtincioFuncional;
			return this;
		}
		public Builder dataAnulacio(Date dataAnulacio) {
			built.dataAnulacio = dataAnulacio;
			return this;
		}
		public Builder estat(String estat) {
			built.estat = estat;
			return this;
		}
		public Builder codiPais(String codiPais) {
			built.codiPais = codiPais;
			return this;
		}
		public Builder codiComunitat(String codiComunitat) {
			built.codiComunitat = codiComunitat;
			return this;
		}
		public Builder codiProvincia(String codiProvincia) {
			built.codiProvincia = codiProvincia;
			return this;
		}
		public Builder codiPostal(String codiPostal) {
			built.codiPostal = codiPostal;
			return this;
		}
		public Builder nomLocalitat(String nomLocalitat) {
			built.nomLocalitat = nomLocalitat;
			return this;
		}
		public Builder localitat(String localitat) {
			built.localitat = localitat;
			return this;
		}
		public Builder adressa(String adressa) {
			built.adressa = adressa;
			return this;
		}
		public Builder tipusVia(Long tipusVia) {
			built.tipusVia = tipusVia;
			return this;
		}
		public Builder nomVia(String nomVia) {
			built.nomVia = nomVia;
			return this;
		}
		public Builder numVia(String numVia) {
			built.numVia = numVia;
			return this;
		}
		
		
		public UnitatOrganitzativaEntity build() {
			return built;
		}
	}
}
