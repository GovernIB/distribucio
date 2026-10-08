package es.caib.distribucio.logic.intf.base.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MassiveForm implements Serializable {
    @NotEmpty
    private List<Long> ids;
    private boolean massive;
}
