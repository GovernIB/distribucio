/**
 * 
 */
package es.caib.distribucio.back.command;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.UsuariDto;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class UsuariCommand implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;	
	private String[] rols;
	private String idioma;
	private Boolean rebreEmailsBustia;
	private Boolean rebreEmailsAgrupats;
	private Long bustiaPerDefecte;
    private Long entitatPerDefecteId;

	public static UsuariCommand asCommand(UsuariDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				UsuariCommand.class);
	}
	public static UsuariDto asDto(UsuariCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				UsuariDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	private static final long serialVersionUID = -139254994389509932L;

}
