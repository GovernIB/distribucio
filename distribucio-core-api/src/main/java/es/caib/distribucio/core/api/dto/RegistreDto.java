/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.Date;
import java.util.List;


import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;

/**
 * Classe que representa una anotació de registre amb id.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreDto extends ContingutDto {

	private RegistreTipusEnum registreTipus;
	private String unitatAdministrativa;
	private String unitatAdministrativaDescripcio;
	private Date procesData;
	private RegistreProcesEstatEnum procesEstat;
	private RegistreProcesEstatSistraEnum procesEstatSistra;
	private String procesError;
	private Integer procesIntents;
	private String backCodi;
	private Date backPendentData;
	private Date backRebudaData;
	private Date backProcesRebutjErrorData;
	private String backObservacions;
	private Date backRetryEnviarData;
	private boolean error;
	private boolean alerta;
	// Copiat de es.caib.distribucio.core.api.registre.RegistreAnotacio
	private String expedientArxiuUuid;
	private String numero;
	private Date data;
	private Date dataOrigen;
	private String identificador;
	private String entitatCodi;
	private String entitatDescripcio;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private String oficinaOrigenCodi;
	private String oficinaOrigenDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String extracte;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private String assumpteCodi;
	private String assumpteDescripcio;
	private String procedimentCodi;
	private String referencia;
	private String expedientNumero;
	private String numeroOrigen;
	private String idiomaCodi;
	private String idiomaDescripcio;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String transportNumero;
	private String usuariCodi;
	private String usuariNom;
	private String usuariContacte;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String documentacioFisicaCodi;
	private String documentacioFisicaDescripcio;
	private String observacions;
	private String exposa;
	private String solicita;
	private List<RegistreInteressat> interessats;
	private String interessatsNoms;
	private List<RegistreAnnex> annexos;
	private RegistreAnnexDto justificant;
	
	private String justificantArxiuUuid;
	private boolean justificantDescarregat;
	
	private Boolean llegida;
	private Boolean presencial;
	
	private long pareId;
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private boolean procesAutomatic;
	private long numComentaris;
	private boolean isBustiaActiva;
	// == BustiaContingutDto
	
	
	public Boolean getPresencial() {
		return presencial;
	}
	public void setPresencial(Boolean presencial) {
		this.presencial = presencial;
	}
	public long getPareId() {
		return pareId;
	}
	public RegistreProcesEstatSimpleEnumDto getProcesEstatSimple() {
		return procesEstatSimple;
	}
	public void setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto procesEstatSimple) {
		this.procesEstatSimple = procesEstatSimple;
	}
	public void setPareId(long pareId) {
		this.pareId = pareId;
	}
	public List<ContingutDto> getPath() {
		return path;
	}
	public void setPath(List<ContingutDto> path) {
		this.path = path;
	}
	public boolean isProcesAutomatic() {
		return procesAutomatic;
	}
	public void setProcesAutomatic(boolean procesAutomatic) {
		this.procesAutomatic = procesAutomatic;
	}
	public long getNumComentaris() {
		return numComentaris;
	}
	public void setNumComentaris(long numComentaris) {
		this.numComentaris = numComentaris;
	}
	public boolean isBustiaActiva() {
		return isBustiaActiva;
	}
	public void setBustiaActiva(boolean isBustiaActiva) {
		this.isBustiaActiva = isBustiaActiva;
	}
	public String getBackCodi() {
		return backCodi;
	}
	public void setBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}	
	public Date getBackRetryEnviarData() {
		return backRetryEnviarData;
	}
	public void setBackRetryEnviarData(Date backRetryEnviarData) {
		this.backRetryEnviarData = backRetryEnviarData;
	}
	public Date getBackPendentData() {
		return backPendentData;
	}
	public void setBackPendentData(Date backPendentData) {
		this.backPendentData = backPendentData;
	}
	public Date getBackRebudaData() {
		return backRebudaData;
	}
	public void setBackRebudaData(Date backRebudaData) {
		this.backRebudaData = backRebudaData;
	}
	public Date getBackProcesRebutjErrorData() {
		return backProcesRebutjErrorData;
	}
	public void setBackProcesRebutjErrorData(Date backProcesRebutjErrorData) {
		this.backProcesRebutjErrorData = backProcesRebutjErrorData;
	}
	public String getBackObservacions() {
		return backObservacions;
	}
	public void setBackObservacions(String backObservacions) {
		this.backObservacions = backObservacions;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public boolean isAlerta() {
		return alerta;
	}
	public void setAlerta(boolean alerta) {
		this.alerta = alerta;
	}
	public RegistreTipusEnum getRegistreTipus() {
		return registreTipus;
	}
	public void setRegistreTipus(RegistreTipusEnum registreTipus) {
		this.registreTipus = registreTipus;
	}
	public String getUnitatAdministrativa() {
		return unitatAdministrativa;
	}
	public void setUnitatAdministrativa(String unitatAdministrativa) {
		this.unitatAdministrativa = unitatAdministrativa;
	}
	public String getUnitatAdministrativaDescripcio() {
		return unitatAdministrativaDescripcio;
	}
	public void setUnitatAdministrativaDescripcio(String unitatAdministrativaDescripcio) {
		this.unitatAdministrativaDescripcio = unitatAdministrativaDescripcio;
	}
	public Date getProcesData() {
		return procesData;
	}
	public void setProcesData(Date procesData) {
		this.procesData = procesData;
	}
	public RegistreProcesEstatEnum getProcesEstat() {
		return procesEstat;
	}
	public void setProcesEstat(RegistreProcesEstatEnum procesEstat) {
		this.procesEstat = procesEstat;
	}
	public RegistreProcesEstatSistraEnum getProcesEstatSistra() {
		return procesEstatSistra;
	}
	public void setProcesEstatSistra(RegistreProcesEstatSistraEnum procesEstatSistra) {
		this.procesEstatSistra = procesEstatSistra;
	}
	public String getProcesError() {
		return procesError;
	}
	public void setProcesError(String procesError) {
		this.procesError = procesError;
	}
	public Integer getProcesIntents() {
		return procesIntents;
	}
	public void setProcesIntents(Integer procesIntents) {
		this.procesIntents = procesIntents;
	}

	protected RegistreDto copiarContenidor(ContingutDto original) {
		RegistreDto copia = new RegistreDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
	}

	public String getJustificantArxiuUuid() {
		return justificantArxiuUuid;
	}
	public void setJustificantArxiuUuid(String justificantArxiuUuid) {
		this.justificantArxiuUuid = justificantArxiuUuid;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getDataOrigen() {
		return dataOrigen;
	}
	public void setDataOrigen(Date dataOrigen) {
		this.dataOrigen = dataOrigen;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}
	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}
	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}
	public String getOficinaOrigenCodi() {
		return oficinaOrigenCodi;
	}
	public void setOficinaOrigenCodi(String oficinaOrigenCodi) {
		this.oficinaOrigenCodi = oficinaOrigenCodi;
	}
	public String getOficinaOrigenDescripcio() {
		return oficinaOrigenDescripcio;
	}
	public void setOficinaOrigenDescripcio(String oficinaOrigenDescripcio) {
		this.oficinaOrigenDescripcio = oficinaOrigenDescripcio;
	}
	public String getLlibreCodi() {
		return llibreCodi;
	}
	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}
	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}
	public void setLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}
	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}
	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}
	public void setAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public String getAssumpteDescripcio() {
		return assumpteDescripcio;
	}
	public void setAssumpteDescripcio(String assumpteDescripcio) {
		this.assumpteDescripcio = assumpteDescripcio;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getIdiomaDescripcio() {
		return idiomaDescripcio;
	}
	public void setIdiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}
	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}
	public void setTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}
	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}
	public void setTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}
	public String getTransportNumero() {
		return transportNumero;
	}
	public void setTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public String getUsuariContacte() {
		return usuariContacte;
	}
	public void setUsuariContacte(String usuariContacte) {
		this.usuariContacte = usuariContacte;
	}
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}
	public String getAplicacioVersio() {
		return aplicacioVersio;
	}
	public void setAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
	}
	public String getDocumentacioFisicaCodi() {
		return documentacioFisicaCodi;
	}
	public void setDocumentacioFisicaCodi(String documentacioFisicaCodi) {
		this.documentacioFisicaCodi = documentacioFisicaCodi;
	}
	public String getDocumentacioFisicaDescripcio() {
		return documentacioFisicaDescripcio;
	}
	public void setDocumentacioFisicaDescripcio(String documentacioFisicaDescripcio) {
		this.documentacioFisicaDescripcio = documentacioFisicaDescripcio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getExposa() {
		return exposa;
	}
	public void setExposa(String exposa) {
		this.exposa = exposa;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}
	public List<RegistreInteressat> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<RegistreInteressat> interessats) {
		this.interessats = interessats;
	}
	public List<RegistreAnnex> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<RegistreAnnex> annexos) {
		this.annexos = annexos;
	}

	public RegistreAnnexDto getJustificant() {
		return justificant;
	}
	public void setJustificant(RegistreAnnexDto justificant) {
		this.justificant = justificant;
	}
	
	public String getInteressatsAndRepresentantsResum() {
		String interessatsResum = "";
		if (this.interessats != null)
			for (RegistreInteressat interessat: this.interessats) {
				interessatsResum+= interessat.getNom()==null ? "" :interessat.getNom()+" ";
				interessatsResum+=  interessat.getLlinatge1()==null ? "": interessat.getLlinatge1()+" ";
				interessatsResum+=  interessat.getLlinatge2()==null ? "" : interessat.getLlinatge2()  + "<br>"; 
			}
		
		return interessatsResum;
	}
	
	public String getInteressatsResum() {
		String interessatsResum = "";
		if (this.interessats != null)
			for (RegistreInteressat interessat : this.interessats) {
				if (interessat.getRepresentat() == null) {
					if (interessat.getTipus().equals("PERSONA_FIS")) {
					interessatsResum += interessat.getNom() == null ? "" : interessat.getNom() + " ";
					interessatsResum += interessat.getLlinatge1() == null ? "" : interessat.getLlinatge1() + " ";
					interessatsResum += interessat.getLlinatge2() == null ? "" : interessat.getLlinatge2() + "<br>";
					} else {
						interessatsResum += interessat.getRaoSocial() + "<br>";
					}
				}
			}
		return interessatsResum;
	}
	

	public Boolean getLlegida() {
		return llegida;
	}
	public void setLlegida(Boolean llegida) {
		this.llegida = llegida;
	}
	public String getExpedientArxiuUuid() {
		return expedientArxiuUuid;
	}
	public void setExpedientArxiuUuid(String expedientArxiuUuid) {
		this.expedientArxiuUuid = expedientArxiuUuid;
	}
	public String getInteressatsNoms() {
		return interessatsNoms;
	}
	public void setInteressatsNoms(String interessatsNoms) {
		this.interessatsNoms = interessatsNoms;
	}
	public boolean isJustificantDescarregat() {
		return justificantDescarregat;
	}
	public void setJustificantDescarregat(boolean justificantDescarregat) {
		this.justificantDescarregat = justificantDescarregat;
	}
	
}
