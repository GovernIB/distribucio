package es.caib.distribucio.logic.intf.dto.estadistic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.comanda.model.server.monitoring.Fet;
import lombok.Getter;

@Getter
public class FetDistribucio extends Fet {

    @JsonIgnore
    private FetEnum tipus;

    @Override
    public String getCodi() {
        return tipus.name();
    }

    public FetDistribucio(FetEnum tipus, Double valor) {
    	super();
        this.tipus = tipus;
        super.setCodi(tipus.name());
        super.setValor(valor);
    }

    public FetDistribucio(FetEnum tipus, Long valor) {
    	super();
        this.tipus = tipus;
        super.setCodi(tipus.name());
        super.setValor(valor != null ? valor.doubleValue() : null);
    }
}
