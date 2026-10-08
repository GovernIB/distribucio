package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.List;

/**
 * Fil d'execució de la JVM (pestanya "Fils d'execució" del monitor de sistema) i acció per obtenir la informació
 * general del sistema (pestanya "Sistema").
 * <p>
 * Recurs sense base de dades: les dades es llegeixen en temps real de la JVM. Només accessible per al rol
 * DIS_SUPER; l'acció porta les seves pròpies restriccions perquè, si no, comprovaria el permís WRITE sobre el recurs.
 *
 * @author Límit Tecnologies
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { MonitorFilResource.Fields.nom },
		descriptionField = MonitorFilResource.Fields.nom,
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = MonitorFilResource.ACTION_INFORMACIO_SISTEMA_CODE,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_SUPER },
										grantedPermissions = { PermissionEnum.READ }
								)
						})
		})
public class MonitorFilResource extends BaseResource<Long> {

	public static final String ACTION_INFORMACIO_SISTEMA_CODE = "INFORMACIO_SISTEMA";

	private String nom;
	private String estat;
	private String tempsCpu;
	private String tempsEspera;
	private String tempsBloqueig;

	/**
	 * Resultat de l'acció {@link #ACTION_INFORMACIO_SISTEMA_CODE}. Uneix les dades que ja mostrava la pantalla
	 * /monitor de la interfície JSP (processadors, memòria, SO, fils, discs) amb les que dona la llibreria de
	 * COMANDA (JVM, servidor d'aplicacions, càrrega). No s'exposen els tipus de la llibreria perquè canvien entre
	 * versions.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class InformacioSistema implements Serializable {
		// Generals
		private String sistemaOperatiu;
		private String arquitectura;
		private Integer processadors;
		private String versioJboss;
		private String servidorAplicacions;
		private String jvm;
		private String versioJdk;
		private String dataArrencada;
		private String tempsFuncionant;
		// Memòria de la JVM
		private MemoriaUs memoriaJvm;
		/** Memòria màxima de la JVM formatada, o null si no té límit. */
		private String memoriaMaxima;
		private MemoriaUs memoriaFisica;
		// Fils
		private Integer filsActius;
		private Integer filsPic;
		private Integer filsDaemon;
		private Integer filsDeadlock;
		private Long gcExecucions;
		private Long gcTemps;
		// CPU
		private Integer nuclis;
		private String carregaMitjana;
		/** Càrrega de CPU del sistema, o null si no es pot mesurar. */
		private String carregaCpuSistema;
		/** Càrrega de CPU del procés, o null si no es pot mesurar. */
		private String carregaCpuProces;
		// Discs
		private List<EspaiDisc> discos;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class MemoriaUs implements Serializable {
		private Long usada;
		private Long total;
		private String usadaFormatada;
		private String totalFormatada;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class EspaiDisc implements Serializable {
		private String nom;
		private Long usat;
		private Long total;
		private String usatFormatat;
		private String totalFormatat;
	}

}
