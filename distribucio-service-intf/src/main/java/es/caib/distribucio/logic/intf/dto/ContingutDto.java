/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
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

    private boolean potModificar;
	
	private boolean alerta;
	
	protected List<ContingutDto> pathInicial;
	protected Long destiLogic;

	public String getDarrerMovimentOrigenUoAndBustia() {
		if (this.darrerMovimentOrigenUo != null && !this.darrerMovimentOrigenUo.isEmpty()
				&& this.darrerMovimentOrigenBustia != null && !this.darrerMovimentOrigenBustia.isEmpty())
			return this.darrerMovimentOrigenUo + " / " + this.darrerMovimentOrigenBustia;
		else 
			return null;
		
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
