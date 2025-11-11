/**
 *
 */
package es.caib.distribucio.back.command;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.distribucio.logic.intf.dto.TipusPermisEnumDto;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;

/**
 * Command per al manteniment de permisos.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class PermisCommand {

	private Long id;
	@NotEmpty @Size(max=64)
	private String principalNom;
	@NotNull
	private PrincipalTipusEnumDto principalTipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	private boolean adminLectura;

    public void setTipusPermis(TipusPermisEnumDto tipusPermis) {
        this.write = TipusPermisEnumDto.COMPLET.equals(tipusPermis);
        this.read = TipusPermisEnumDto.COMPLET.equals(tipusPermis) || TipusPermisEnumDto.NOMES_LECTURA.equals(tipusPermis) ;
    }
    public TipusPermisEnumDto getTipusPermis() {
        if (this.write) {
            return TipusPermisEnumDto.COMPLET;
        } else if (this.read) {
            return TipusPermisEnumDto.NOMES_LECTURA;
        }
        return null;
    }

	public static List<PermisCommand> toPermisCommands(
			List<PermisDto> dtos) {
		List<PermisCommand> commands = new ArrayList<PermisCommand>();
		for (PermisDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							PermisCommand.class));
		}
		return commands;
	}

	public static PermisCommand asCommand(PermisDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				PermisCommand.class);
	}
	public static PermisDto asDto(PermisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				PermisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
