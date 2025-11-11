package es.caib.distribucio.api.interna.model.anotacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe amb la informació necessària per donar d'alta una anotació a una bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class DadesAltaAnotacio {

	private String entitatCodi;
	private String unitatAdministrativaCodi;
	private RegistreAnotacio registreAnotacio;
	
}
