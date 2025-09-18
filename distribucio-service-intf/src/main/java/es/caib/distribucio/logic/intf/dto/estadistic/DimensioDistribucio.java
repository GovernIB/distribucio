package es.caib.distribucio.logic.intf.dto.estadistic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.comanda.ms.estadistica.model.Dimensio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DimensioDistribucio implements Dimensio {

    @JsonIgnore
    private DimEnum tipus;
    private String valor;

    @Override
    public String getCodi() {
        return tipus.name();
    }

    public DimensioDistribucio(DimEnum tipus, Double valor) {
        this.tipus = tipus;
        this.valor = valor != null ? String.valueOf(valor) : null;
    }

    public DimensioDistribucio(DimEnum tipus, Long valor) {
        this.tipus = tipus;
        this.valor = valor != null ? String.valueOf(valor) : null;
    }

    public <E extends Enum<E>> DimensioDistribucio(DimEnum tipus, E valor) {
        this.tipus = tipus;
        this.valor = valor != null ? valor.name() : null;
    }

}
