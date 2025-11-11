package es.caib.distribucio.rest.client.bustia.domini;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class PeticioAlta {

	private String entitatCodi;
	private String unitatAdministrativaCodi;
	private RegistreAnotacio registreAnotacio;
	
}
