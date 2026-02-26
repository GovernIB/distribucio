/**
 * 
 */
package es.caib.distribucio.back.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.distribucio.back.validation.EntitatCodi;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.web.multipart.MultipartFile;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.back.validation.CodiEntitatNoRepetit;
import es.caib.distribucio.back.validation.DocumentIdentitat;
import es.caib.distribucio.logic.intf.dto.EntitatDto;

/**
 * Command per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiEntitatNoRepetit(campId = "id", campCodi = "codi")
@EntitatCodi
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

	private MultipartFile logoCap;
	private boolean eliminarLogoCap;
	private String colorFons;
	private String colorLletra;
	
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
	public MultipartFile getLogoCap() {
		return logoCap;
	}
	public void setLogoCap(MultipartFile logoCap) {
		this.logoCap = logoCap;
	}
	public boolean isEliminarLogoCap() {
		return eliminarLogoCap;
	}
	public void setEliminarLogoCap(boolean eliminarLogoCap) {
		this.eliminarLogoCap = eliminarLogoCap;
	}
	public String getColorFons() {
		return colorFons;
	}
	public void setColorFons(String colorFons) {
		this.colorFons = colorFons;
	}
	public String getColorLletra() {
		return colorLletra;
	}
	public void setColorLletra(String colorLletra) {
		this.colorLletra = colorLletra;
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
	public static EntitatDto asDto(EntitatCommand command) throws IOException {
		EntitatDto entitat = ConversioTipusHelper.convertir(
				command,
				EntitatDto.class);
		String fileExtension = FilenameUtils.getExtension(command.getLogoCap().getOriginalFilename());
		entitat.setLogoExtension(fileExtension);
		entitat.setLogoCapBytes(command.getLogoCap().getBytes());
		return entitat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
