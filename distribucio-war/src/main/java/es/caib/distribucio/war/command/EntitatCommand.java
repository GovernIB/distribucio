/**
 * 
 */
package es.caib.distribucio.war.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;
import es.caib.distribucio.war.validation.CodiEntitatNoRepetit;
import es.caib.distribucio.war.validation.DocumentIdentitat;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
public class EntitatCommand {

	private Long id;

	@NotEmpty @Size(max=64)
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotEmpty @Size(max=9) @DocumentIdentitat
	private String cif;
	@NotEmpty @Size(max=9)
	private String codiDir3;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public String getCodiDir3() {
		return codiDir3;
	}
	public void setCodiDir3(String codiDir3) {
		this.codiDir3 = codiDir3;
	}

	public static List<EntitatCommand> toEntitatCommands(
			List<EntitatDto> dtos) {
		List<EntitatCommand> commands = new ArrayList<EntitatCommand>();
		for (EntitatDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							EntitatCommand.class));
		}
		return commands;
	}

	public static EntitatCommand asCommand(EntitatDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				EntitatCommand.class);
	}
	public static EntitatDto asDto(EntitatCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
