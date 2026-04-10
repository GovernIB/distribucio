package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class MassiveCommand {
    @NotNull
    @NotEmpty
    private List<Long> ids;
}
