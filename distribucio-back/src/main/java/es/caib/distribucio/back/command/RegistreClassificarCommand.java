package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistreClassificarCommand {
	
	String titol;
	Long contingutId;
//	String codiProcediment;
//	String codiServei;
	String codiSia;
	// RegistreClassificarTipusEnum PROCEDIMENT / SERVEI
//	String tipus;

	public interface Classificar {}
}

