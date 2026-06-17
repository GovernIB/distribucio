package es.caib.distribucio.logic.intf.dto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OpcionsPaginacio {
    DEU(10L, "10"),
    VINT(20L, "20"),
    CINQUANTA(50L, "50"),
    CENT(100L, "100"),
    DOS_CENTS_CINQUANTA(250L, "250");

    private final Long valor;
    private final String etiqueta;

    OpcionsPaginacio(Long valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public Long getValor() { return valor; }
    public String getEtiqueta() { return etiqueta; }

    public static List<IdNomDto> toDtoList() {
        return Arrays.stream(values())
                .map(opt -> new IdNomDto(opt.valor, opt.etiqueta))
                .collect(Collectors.toList());
    }

    public static String toJsonArray() {
        return Arrays.stream(values())
                .map(opt -> String.valueOf(opt.valor))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
