package es.caib.distribucio.persist.entity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = BaseConfig.DB_PREFIX + "limit_canvi_estat")
@EntityListeners(AuditingEntityListener.class)
public class LimitCanviEstatEntity extends DistribucioPersistable<Long> {

    @Column(name = "usuari_codi")
    private String usuariCodi;

    @Column(name = "descripcio")
    private String descripcio;

    @Column(name = "lim_min_lab")
    private Integer limitMinutLaboral;

    @Column(name = "lim_min_nolab")
    private Integer limitMinutNoLaboral;

    @Column(name = "lim_dia_lab")
    private Integer limitDiaLaboral;

    @Column(name = "lim_dia_nolab")
    private Integer limitDiaNoLaboral;

    public static Builder getBuilder(String usuariCodi,
                                     String descripcio,
                                     Integer limitMinutLaboral,
                                     Integer limitMinutNoLaboral,
                                     Integer limitDiaLaboral,
                                     Integer limitDiaNoLaboral) {
        return new Builder(usuariCodi,
                descripcio,
                limitMinutLaboral,
                limitMinutNoLaboral,
                limitDiaLaboral,
                limitDiaNoLaboral);
    }
    public static class Builder {
        LimitCanviEstatEntity built;
        Builder(String usuariCodi,
                String descripcio,
                Integer limitMinutLaboral,
                Integer limitMinutNoLaboral,
                Integer limitDiaLaboral,
                Integer limitDiaNoLaboral) {
            built = new LimitCanviEstatEntity();
            built.usuariCodi = usuariCodi;
            built.descripcio = descripcio;
            built.limitMinutLaboral = limitMinutLaboral;
            built.limitMinutNoLaboral = limitMinutNoLaboral;
            built.limitDiaLaboral = limitDiaLaboral;
            built.limitDiaNoLaboral = limitDiaNoLaboral;
        }
        public LimitCanviEstatEntity build() {
            return built;
        }
    }

    public void update(LimitCanviEstatDto limitCanviEstatDto) {
        this.update(
                limitCanviEstatDto.getUsuariCodi(),
                limitCanviEstatDto.getDescripcio(),
                limitCanviEstatDto.getLimitMinutLaboral(),
                limitCanviEstatDto.getLimitMinutNoLaboral(),
                limitCanviEstatDto.getLimitDiaLaboral(),
                limitCanviEstatDto.getLimitDiaNoLaboral()
        );
    }
    public void update(String usuariCodi,
                       String descripcio,
                       Integer limitMinutLaboral,
                       Integer limitMinutNoLaboral,
                       Integer limitDiaLaboral,
                       Integer limitDiaNoLaboral) {
        this.usuariCodi = usuariCodi;
        this.descripcio = descripcio;
        this.limitMinutLaboral = limitMinutLaboral;
        this.limitMinutNoLaboral = limitMinutNoLaboral;
        this.limitDiaLaboral = limitDiaLaboral;
        this.limitDiaNoLaboral = limitDiaNoLaboral;
    }

}
