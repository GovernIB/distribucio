package es.caib.distribucio.logic.intf.dto.estadistic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.comanda.ms.estadistica.model.Fet;
import lombok.Getter;

@Getter
public class FetDistribucio implements Fet {

    @JsonIgnore
    private FetEnum tipus;
    private Double valor;

    @Override
    public String getCodi() {
        return tipus.name();
    }

    public FetDistribucio(FetEnum tipus, Double valor) {
        this.tipus = tipus;
        this.valor = valor;
    }

    public FetDistribucio(FetEnum tipus, Long valor) {
        this.tipus = tipus;
        this.valor = valor != null ? valor.doubleValue() : null;
    }

}
