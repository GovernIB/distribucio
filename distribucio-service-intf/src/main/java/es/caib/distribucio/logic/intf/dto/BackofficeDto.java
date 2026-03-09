package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BackofficeDto {

    private Long id;
    private String codi;
    private String nom;
    private String url;
    private String usuari;
    private String contrasenya;
    private Integer intents;
    private Integer tempsEntreIntents;
    private Long entitatId;    
    private BackofficeTipusEnumDto tipus;

    private Boolean enviamentEmail;
    private String emailResponsable;
//    private Date darrerEmailResponsable;

}
