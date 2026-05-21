/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.caib.distribucio.logic.intf.registre.RegistreInteressat;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa una anotació de registre amb id.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreDto extends ContingutDto {

	private RegistreTipusEnum registreTipus;
	private String unitatAdministrativa;
	private String unitatAdministrativaDescripcio;
	private Date procesData;
	private RegistreProcesEstatEnum procesEstat;
	private UsuariDto processadaPer;
	private String estatDescripcio;
	private RegistreProcesEstatSistraEnum procesEstatSistra;
	private String procesError;
	private Integer procesIntents;
	private String backCodi;
	private Date backPendentData;
	private Date backRebudaData;
	private Date backProcesRebutjErrorData;
	private String backObservacions;
	private Date backRetryEnviarData;
	private String backRetryEnviarDataString;
	private boolean error;
	private boolean alerta;
	private boolean arxiuTancat;
	private boolean arxiuTancatError;
	private Date dataTancament;
	// Copiat de es.caib.distribucio.logic.intf.registre.RegistreAnotacio
	private String expedientArxiuUuid;
	private String numero;
	private Date data;
	private Date dataOrigen;
	private Date dataPosadaBustia; 
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
	private String serveiCodi;
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
	private List<RegistreAnnexDto> annexos;
	private RegistreAnnexDto justificant;
	private Integer numeroCopia;
	private String bustiaNom;
	
	private String justificantArxiuUuid;
	private boolean justificantDescarregat;
	
	private Boolean llegida;
	private Boolean presencial;
	
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private boolean procesAutomatic;
	private long numComentaris;
	private boolean isBustiaActiva;
	
	private boolean enviatPerEmail;
	private List<String> enviamentsPerEmail = new ArrayList<String>();

	private boolean reintentsEsgotat;
	private int maxReintents;
	
	private UsuariDto agafatPer;
	

	private boolean reactivat;

	private boolean sobreescriure;
	
	private int annexosEstatEsborrany;
	
	private ReglaDto regla;

	private List<DadaDto> dades;
	
	private String motiuRebuig;
	
	private String tramitCodi;
	private String tramitNom;
	
	public int getDadesCount() {
		if (dades == null)
			return 0;
		else
			return dades.size();
	}
	
	public boolean isAgafat() {
		return agafatPer != null;
	}
	
	public String getBustiaNom() {
		if  (getPath().size()>0) {
			return getPath().get(0).getNom();
		} else {
			return "";
		}		
	}

	protected RegistreDto copiarContenidor(ContingutDto original) {
		RegistreDto copia = new RegistreDto();
		copia.setId(original.getId());
		copia.setNom(original.getNom());
		return copia;
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
					interessatsResum += "- " + getNomComplet(interessat);
				}
				
				if (interessat.getRepresentant() != null) {
					interessatsResum += " (R: " + getNomComplet(interessat.getRepresentant()) + ")";
				}
				
				if (interessat.getRepresentat() == null) {
					interessatsResum += "<br>";
				}
			}
		return interessatsResum;
	}
	
	private String getNomComplet(RegistreInteressat persona) {
		String nomComplet = "";
		if (persona.getTipus().equals("PERSONA_FIS")) {
			nomComplet += persona.getNom() == null ? "" : persona.getNom() + " ";
			nomComplet += persona.getLlinatge1() == null ? "" : persona.getLlinatge1() + " ";
			nomComplet += persona.getLlinatge2() == null ? "" : persona.getLlinatge2();
		} else {
			nomComplet += persona.getRaoSocial();
		}
		
		return nomComplet;
	}

	public boolean isEstatPendent() {
		return RegistreProcesEstatEnum.isPendent(procesEstat);
	}
}
