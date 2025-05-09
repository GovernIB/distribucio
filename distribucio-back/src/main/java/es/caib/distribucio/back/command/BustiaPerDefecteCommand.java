package es.caib.distribucio.back.command;

import javax.validation.constraints.NotNull;

/**
 * Command selecció bústia destí per defecte en desactivar bústia
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaPerDefecteCommand {
	
	@NotNull
	private Long bustiaId;

	public Long getBustiaId() {
		return bustiaId;
	}

	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}

}

