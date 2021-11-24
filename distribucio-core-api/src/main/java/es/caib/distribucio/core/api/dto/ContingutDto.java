/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public abstract class ContingutDto extends AuditoriaDto {

	protected Long id;
	protected String movimentId;
	protected String nom;
	protected List<ContingutDto> fills;
	protected List<ContingutDto> path;
	protected EntitatDto entitat;
	protected int esborrat;
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;
	protected Date darrerMovimentData;
	protected UsuariDto darrerMovimentUsuari;
	protected String darrerMovimentComentari;
	protected String darrerMovimentOrigenUo;
	protected String darrerMovimentOrigenBustia;

	protected boolean perConvertirJson;
	protected boolean perConeixement;
	
	private boolean alerta;
	
	protected List<ContingutDto> pathInicial;
	protected Long destiLogic;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getMovimentId() {
		return movimentId;
	}
	public void setMovimentId(String movimentId) {
		this.movimentId = movimentId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public List<ContingutDto> getFills() {
		return fills;
	}
	public void setFills(List<ContingutDto> fills) {
		this.fills = fills;
	}
	public List<ContingutDto> getPath() {
		return path;
	}
	public void setPath(List<ContingutDto> path) {
		this.path = path;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public boolean isEsborrat() {
		return esborrat > 0;
	}
	public void setEsborrat(int esborrat) {
		this.esborrat = esborrat;
	}
	public String getArxiuUuid() {
		return arxiuUuid;
	}
	public void setArxiuUuid(String arxiuUuid) {
		this.arxiuUuid = arxiuUuid;
	}
	public Date getArxiuDataActualitzacio() {
		return arxiuDataActualitzacio;
	}
	public void setArxiuDataActualitzacio(Date arxiuDataActualitzacio) {
		this.arxiuDataActualitzacio = arxiuDataActualitzacio;
	}
	public Date getDarrerMovimentData() {
		return darrerMovimentData;
	}
	public void setDarrerMovimentData(Date darrerMovimentData) {
		this.darrerMovimentData = darrerMovimentData;
	}
	public UsuariDto getDarrerMovimentUsuari() {
		return darrerMovimentUsuari;
	}
	public void setDarrerMovimentUsuari(UsuariDto darrerMovimentUsuari) {
		this.darrerMovimentUsuari = darrerMovimentUsuari;
	}
	public String getDarrerMovimentComentari() {
		return darrerMovimentComentari;
	}
	public void setDarrerMovimentComentari(String darrerMovimentComentari) {
		this.darrerMovimentComentari = darrerMovimentComentari;
	}
	public String getDarrerMovimentOrigenUo() {
		return darrerMovimentOrigenUo;
	}
	public void setDarrerMovimentOrigenUo(String darrerMovimentOrigenUo) {
		this.darrerMovimentOrigenUo = darrerMovimentOrigenUo;
	}
	public String getDarrerMovimentOrigenBustia() {
		return darrerMovimentOrigenBustia;
	}
	public void setDarrerMovimentOrigenBustia(String darrerMovimentOrigenBustia) {
		this.darrerMovimentOrigenBustia = darrerMovimentOrigenBustia;
	}
	public String getDarrerMovimentOrigenUoAndBustia() {
		if (this.darrerMovimentOrigenUo != null && !this.darrerMovimentOrigenUo.isEmpty()
				&& this.darrerMovimentOrigenBustia != null && !this.darrerMovimentOrigenBustia.isEmpty())
			return this.darrerMovimentOrigenUo + " / " + this.darrerMovimentOrigenBustia;
		else 
			return null;
		
	}
	public boolean isPerConvertirJson() {
		return perConvertirJson;
	}
	public List<ContingutDto> getPathInicial() {
		return pathInicial;
	}
	public void setPathInicial(List<ContingutDto> pathInicial) {
		this.pathInicial = pathInicial;
	}
	public boolean isPerConeixement() {
		return perConeixement;
	}
	public void setPerConeixement(boolean perConeixement) {
		this.perConeixement = perConeixement;
	}
	public Long getDestiLogic() {
		return destiLogic;
	}
	public void setDestiLogic(Long destiLogic) {
		this.destiLogic = destiLogic;
	}
	public void setPerConvertirJson(boolean perConvertirJson) {
		this.perConvertirJson = perConvertirJson;
		if (fills != null) {
			for (ContingutDto fill: fills) {
				fill.setPerConvertirJson(perConvertirJson);
			}
		}
	}
	
	

	public ContingutDto getPare() {
		if (getPath() != null && !getPath().isEmpty())
			return getPath().get(getPath().size() - 1);
		else
			return null;
	}
	
	public Long getPareId() {
		ContingutDto pare = this.getPare();
		return pare != null ? pare.getId() : null;
	}


	public String getPathAsStringWebdav() {
		if (getPath() == null)
			return null;
		StringBuilder pathString = new StringBuilder();
		for (ContingutDto pathElement: getPath()) {
			pathString.append("/");
			pathString.append(pathElement.getNom());
		}
		return pathString.toString();
	}
	public String getPathAsStringWebdavAmbNom() {
		return getPathAsStringWebdav() + "/" + nom;
	}
	public String getPathAsStringExplorador() {
		if (getPath() == null)
			return null;
		StringBuilder pathString = new StringBuilder();
		for (ContingutDto pathElement: getPath()) {
			if (pathString.length() > 0)
				pathString.append(" / ");
			pathString.append(pathElement.getNom());
		}
		return pathString.toString();
	}
	public String getPathAsStringExploradorAmbNom() {
		return getPathAsStringExplorador() + " / " + nom;
	}


	public List<RegistreDto> getFillsRegistres() {
		List<RegistreDto> registres = new ArrayList<RegistreDto>();
		if (fills != null) {
			for (ContingutDto contenidor: fills) {
				if (contenidor instanceof RegistreDto)
					registres.add((RegistreDto)contenidor);
			}
		}
		return registres;
	}
	public List<ContingutDto> getFillsNoRegistres() {
		List<ContingutDto> noRegistres = new ArrayList<ContingutDto>();
		if (fills != null) {
			for (ContingutDto contenidor: fills) {
				if (!(contenidor instanceof RegistreDto))
					noRegistres.add(contenidor);
			}
		}
		return noRegistres;
	}

	public int getFillsCount() {
		return (fills == null) ? 0 : fills.size();
	}
	public int getFillsRegistresCount() {
		if  (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor: fills) {
				if (contenidor instanceof RegistreDto)
					count++;
			}
			return count;
		}
	}
	public int getFillsNoRegistresCount() {
		if  (fills == null) {
			return 0;
		} else {
			int count = 0;
			for (ContingutDto contenidor: fills) {
				if (!(contenidor instanceof RegistreDto))
					count++;
			}
			return count;
		}
	}

	public void setContenidorArrelIdPerPath(Long contenidorArrelId) {
		if (path != null) {
			if (!id.equals(contenidorArrelId)) {
				Iterator<ContingutDto> it = path.iterator();
				boolean trobat = false;
				while (it.hasNext()) {
					ContingutDto pathElement = it.next();
					if (pathElement.getId().equals(contenidorArrelId))
						trobat = true;
					if (!trobat) {
						it.remove();
					}
				}
			} else {
				path = null;
			}
		}
	}

	public boolean isReplicatDinsArxiu() {
		return arxiuUuid != null;
	}

	public boolean isBustia() {
		return this instanceof BustiaDto;
	}
	public boolean isRegistre() {
		return this instanceof RegistreDto;
	}

	public ContingutTipusEnumDto getTipus() {
		if (isBustia()) {
			return ContingutTipusEnumDto.BUSTIA;
		} else if (isRegistre()) {
			return ContingutTipusEnumDto.REGISTRE;
		} else {
			return null;
		}
	}
	
	public boolean isAlerta() {
		return alerta;
	}
	public void setAlerta(boolean alerta) {
		this.alerta = alerta;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
