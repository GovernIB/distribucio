package es.caib.distribucio.logic.intf.dto;

import java.util.stream.Stream;

public enum IntegracioCodi {

	USUARIS,
	UNITATS,
	ARXIU,
	DADESEXT,
	SIGNATURA,
	VALIDASIG,
	GESDOC,
	BUSTIAWS,
	PROCEDIMENT,
	SERVEI,
	DISTRIBUCIO,
	BACKOFFICE;

    public static Stream<IntegracioCodi> stream() {
        return Stream.of(IntegracioCodi.values());
    }
	
}
