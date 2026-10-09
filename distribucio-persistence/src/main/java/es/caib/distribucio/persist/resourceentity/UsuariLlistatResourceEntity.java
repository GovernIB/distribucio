package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.UsuariLlistatResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entitat de només lectura del recurs {@link UsuariLlistatResource}.
 * <p>
 * Mapeja la mateixa taula que {@link UsuariResourceEntity} i que l'entitat de negoci
 * {@link es.caib.distribucio.persist.entity.UsuariEntity}. Cal una entitat pròpia perquè
 * {@link BaseResourceEntity} lliga cada entitat a un únic tipus de recurs.
 *
 * @author Límit Tecnologies
 */
@Entity
@Immutable
@Table(name = "dis_usuari")
@Getter
@Setter
@NoArgsConstructor
public class UsuariLlistatResourceEntity extends BaseResourceEntity<UsuariLlistatResource, String> {

	@Id
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String id;

	@Column(name = "nom", length = 200)
	private String nom;

	/** No es publica al recurs; només serveix per al filtre ràpid. */
	@Column(name = "nif", length = 9)
	private String nif;

}
