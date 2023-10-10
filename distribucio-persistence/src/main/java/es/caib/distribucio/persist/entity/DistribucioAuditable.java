/**
 * 
 */
package es.caib.distribucio.persist.entity;

import java.io.Serializable;

import org.springframework.data.jpa.domain.AbstractAuditable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioAuditable<PK extends Serializable> extends AbstractAuditable<UsuariEntity, PK> {

}
