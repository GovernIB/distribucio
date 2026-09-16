package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexFirmaDto;
import es.caib.distribucio.logic.intf.dto.RegistreNumeroCopiaEnumDto;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Document (annex) adjunt a una anotació de registre. Anomenat "RegistreAnnexResource" perquè coincideixi
 * amb el nom de la pantalla i l'entitat de negoci legacy i evitar confusions.
 * <p>
 * Recurs de només lectura: només es concedeix el permís READ, tot i que el controller extén la classe mutable genèrica.
 * Els annexos es creen com a efecte secundari de l'entrada d'una anotació de registre, no des d'aquesta pantalla.
 * <p>
 * Camp NO inclòs deliberadament: {@code fitxerContingut} (bytes del document) -- mai s'ha d'incloure
 * en un recurs de llistat; l'acció de "Descàrrega" ja s'exposa per un artifact dedicat (veure
 * {@link #REPORT_DESCARREGAR_ORIGINAL_CODE}/{@link #REPORT_DESCARREGAR_IMPRIMIBLE_CODE}).
 * <p>
 * {@link #metaDadesMap} i {@link #firmes} només es calculen a {@code getOne} (acció "Detalls de
 * l'annex"); a la llista només hi ha el resum {@link #signaturaInfo}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = RegistreAnnexResource.Fields.titol,
		quickFilterFields = {RegistreAnnexResource.Fields.titol, RegistreAnnexResource.Fields.fitxerNom},
		defaultSortFields = {@ResourceConfig.ResourceSort(field = RegistreAnnexResource.Fields.dataAnotacio, direction = Sort.Direction.DESC)},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = {BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA},
						grantedPermissions = {PermissionEnum.READ}
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = RegistreAnnexResource.FILTER_CODE,
						formClass = RegistreAnnexResource.FormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = RegistreAnnexResource.REPORT_DESCARREGAR_ORIGINAL_CODE,
						requiresId = true,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = {BaseConfig.ROLE_ADMIN},
										grantedPermissions = {PermissionEnum.READ}
								)
						}),
				@ResourceArtifact(
						type = ResourceArtifactType.REPORT,
						code = RegistreAnnexResource.REPORT_DESCARREGAR_IMPRIMIBLE_CODE,
						requiresId = true,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = {BaseConfig.ROLE_ADMIN},
										grantedPermissions = {PermissionEnum.READ}
								)
						})
		}
)
public class RegistreAnnexResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String REPORT_DESCARREGAR_ORIGINAL_CODE = "DESCARREGAR_ORIGINAL";
	public static final String REPORT_DESCARREGAR_IMPRIMIBLE_CODE = "DESCARREGAR_IMPRIMIBLE";

	private String titol;
	private String fitxerNom;
	private Integer fitxerTamany;
	private String fitxerTipusMime;
	private String fitxerArxiuUuid;
	private Date dataCaptura;
	private String localitzacio;
	private String origenCiutadaAdmin;
	private String ntiTipusDocument;
	private String sicresTipusDocument;
	private String ntiElaboracioEstat;
	private String observacions;
	private Integer firmaMode;
	private String firmaCsv;
	/** Adreça per veure la firma CSV de l'annex a CONCSV. Buit si l'annex no té firma CSV o CONCSV no està configurat. */
	private String concsvUrl;
	/** Pendent: futura acció de detall de firma. */
	private String timestamp;
	/** Pendent: futura acció de detall/validació de firma. */
	private String validacioOCSP;
	/** Identificador del document al gestor documental; es mostra només si {@link #arxiuEstat} és ESBORRANY. */
	private String gesdocDocumentId;
	/**
	 * Firmes de gestió documental de l'annex (només es mostra si {@link #arxiuEstat} és ESBORRANY).
	 * Només s'omple a "Detalls de l'annex".
	 */
	private List<RegistreAnnexFirmaDto> gesdocFirmes;
	/** Indicador intern; pendent futura acció de detall de firmes. */
	private Boolean signaturaDetallsDescarregat;
	/** Metadades en brut (JSON); veure {@link #metaDadesMap} per a la versió ja processada. */
	private String metaDades;
	/** Metadades ENI/NTI de l'annex (clau -&gt; valor), extretes de {@link #metaDades}. Només s'omple a "Detalls de l'annex". */
	private Map<String, String> metaDadesMap;
	private ValidacioFirmaEnum validacioFirmaEstat;
	private String validacioFirmaError;
	private AnnexEstat arxiuEstat;

	/** Anotació de registre a la qual pertany l'annex. Pendent: futura acció "Detalls de l'anotació". */
	private ResourceReference<ContingutResource, Long> registre;

	/** Número de l'anotació de registre pare (p. ex. "GOIBE1784627775656/2026"). */
	private String registreNumero;
	/** 0 si l'anotació de registre és l'original, o el número de còpia si n'és una còpia. */
	private Integer registreNumeroCopia;
	/** Data de l'anotació de registre (no la data de captura del propi annex): és la que mostra i ordena el llistat antic. */
	private Date dataAnotacio;

	/**
	 * Codi curt (TFxx) del tipus de firma d'una de les firmes de l'annex, usat només per a poder
	 * filtrar (veure {@link FormFilter#tipusFirma}). Com que un annex pot tenir més d'una firma i el
	 * motor genèric de filtres només permet comparar un valor escalar, es simplifica a "una" firma
	 * (la primera per ordre alfabètic del codi) enlloc de comprovar totes com feia la pantalla antiga
	 * (que usava un EXISTS); la informació completa de firmes és feina de la futura acció de detalls.
	 */
	private String tipusFirma;
	/** Resum llegible de la primera firma de l'annex (tipus + perfil). Buit si l'annex no en té cap. */
	private String signaturaInfo;
	private Boolean ambFirma;
	/**
	 * Llistat complet de firmes amb tots els detalls (bloc "Firmes" de registreAnnex.jsp/
	 * registreAnnexFirmes.jsp). Només s'omple a "Detalls de l'annex"; a la llista només hi ha el
	 * resum {@link #signaturaInfo}. No inclou {@code contingut} (bytes de la firma): es reservarà
	 * per a una futura acció de descàrrega de la firma individual.
	 */
	private List<ArxiuFirmaDto> firmes;

	@Getter
	@Setter
	public static class FormFilter implements Serializable {
		// Mateixos valors per defecte que la UI antiga (AnnexosAdminController.getFiltreCommand):
		private String titol;
		private String fitxerNom;
		private String fitxerTipusMime;
		private AnnexEstat arxiuEstat = AnnexEstat.ESBORRANY;
		private String numero;
		private RegistreNumeroCopiaEnumDto numeroCopia;
		private ArxiuFirmaTipusEnumDto tipusFirma;
		private Date dataAnotacioInici = Date.from(LocalDate.now().minusMonths(3).atStartOfDay(ZoneId.systemDefault()).toInstant());
		private Date dataAnotacioFi;
	}

}
