package es.caib.distribucio.rest.client.bustia.domini;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa la firma d'un annex de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Firma {

	private String tipus;//4
	private String perfil;//4
	private String fitxerNom;//comes from document custody plugin
	private byte[] contingut;//comes from document custody plugin
	private String tipusMime;//comes from document custody plugin
	private String csv;//255
	private String csvRegulacio;//13
	
}
