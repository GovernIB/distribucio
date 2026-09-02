package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.distribucio.logic.intf.model.MenuEstilEnum;
import es.caib.distribucio.logic.intf.model.TemaAplicacioEnum;
import es.caib.distribucio.logic.intf.model.UsuariResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link UsuariResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.UsuariEntity}
 * (clau primària natural {@code codi}, sense generador), dedicada exclusivament al mapeig
 * genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_usuari")
@Getter
@Setter
@NoArgsConstructor
public class UsuariResourceEntity extends BaseResourceEntity<UsuariResource, String> {

	@Id
	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String id;

	@Column(name = "nom", length = 200)
	private String nom;

	@Column(name = "nif", length = 9, nullable = false)
	private String nif;

	@Column(name = "email", length = 200)
	private String email;

	@Column(name = "email_alternatiu", length = 200)
	private String emailAlternatiu;

	@Column(name = "idioma", length = 2)
	private String idioma;

	@Column(name = "rebre_emails")
	private Boolean rebreEmailsBustia;

	@Column(name = "emails_agrupats")
	private Boolean rebreEmailsAgrupats;

	@Column(name = "email_error_anotacio")
	private Boolean emailErrorAnotacio;

	@Column(name = "entitat_defecte_id")
	private Long entitatPerDefecteId;

	@Column(name = "num_elements_pagina")
	private Long numElementsPagina;

	@Column(name = "rol_actual", length = 64)
	private String rolActual;

	@Enumerated(EnumType.STRING)
	@Column(name = "tema_aplicacio", length = 16)
	private TemaAplicacioEnum temaAplicacio;

	@Enumerated(EnumType.STRING)
	@Column(name = "estil_menu", length = 16, nullable = false)
	private MenuEstilEnum estilMenu;

	@Enumerated(EnumType.STRING)
	@Column(name = "interficie_usuari", length = 5)
	private InterficieUsuariEnumDto interficieUsuari;

	@Version
	private long version = 0;

}
