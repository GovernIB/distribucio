package es.caib.distribucio.logic.resourceservice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.AnnexosAdminHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexFirmaDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.model.RegistreAnnexResource;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.resourceservice.RegistreAnnexResourceService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.entity.RegistreFirmaDetallEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.persist.resourceentity.RegistreAnnexResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de recurs per al localitzador d'annexos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistreAnnexResourceServiceImpl
		extends BaseMutableResourceService<RegistreAnnexResource, Long, RegistreAnnexResourceEntity>
		implements RegistreAnnexResourceService {

	private final RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
	private final RegistreAnnexRepository registreAnnexRepository;
	private final RegistreHelper registreHelper;
	private final ConfigService configService;
	private final EntitatRepository entitatRepository;
	private final AuthenticationHelper authenticationHelper;
	private final AnnexosAdminHelper annexosAdminHelper;

	@PostConstruct
	public void init() {
		register(RegistreAnnexResource.REPORT_DESCARREGAR_ORIGINAL_CODE, new DescarregarReportGenerator(false));
		register(RegistreAnnexResource.REPORT_DESCARREGAR_IMPRIMIBLE_CODE, new DescarregarReportGenerator(true));
		register(RegistreAnnexResource.REPORT_DESCARREGAR_FIRMA_CODE, new DescarregarFirmaReportGenerator());
		register(RegistreAnnexResource.ACTION_GUARDAR_DEFINITIU_CODE, new GuardarDefinitiuActionExecutor());
	}

	@Override
	protected Specification<RegistreAnnexResourceEntity> additionalSpecification(String[] namedQueries) {
		Long entitatId = SessioActualUtil.getEntitatId();
		if (entitatId != null) {
			return (root, query, cb) -> cb.equal(root.get("registre").get("entitat").get("id"), entitatId);
		}
		return null;
	}

	/**
	 * Acció "Detalls de l'annex": aquest mètode només s'executa en obrir el detall d'un annex concret.
	 */
	@Override
	@Transactional(readOnly = true)
	public RegistreAnnexResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		RegistreAnnexResource resource = super.getOne(id, perspectives);
		RegistreAnnexResourceEntity entity = getEntity(id);
		resource.setMetaDadesMap(parseMetaDadesMap(id, entity.getMetaDades()));
		resource.setNtiTipusDocument(enumNameOrRaw(
				RegistreAnnexNtiTipusDocumentEnum.valorAsEnum(entity.getNtiTipusDocument()),
				entity.getNtiTipusDocument()));
		resource.setNtiElaboracioEstat(enumNameOrRaw(
				RegistreAnnexElaboracioEstatEnum.valorAsEnum(entity.getNtiElaboracioEstat()),
				entity.getNtiElaboracioEstat()));
		resource.setSicresTipusDocument(enumNameOrRaw(
				RegistreAnnexSicresTipusDocumentEnum.valorAsEnum(entity.getSicresTipusDocument()),
				entity.getSicresTipusDocument()));
		resource.setOrigenCiutadaAdmin(enumNameOrRaw(
				RegistreAnnexOrigenEnum.valorAsEnum(entity.getOrigenCiutadaAdmin()),
				entity.getOrigenCiutadaAdmin()));
		List<RegistreAnnexFirmaEntity> firmes = registreAnnexFirmaRepository.getRegistreAnnexFirmesSenseDetall(id);
		resource.setFirmes(toFirmesDto(firmes));
		if (AnnexEstat.ESBORRANY.equals(entity.getArxiuEstat())) {
			resource.setGesdocFirmes(toGesdocFirmesDto(firmes));
		}
		return resource;
	}

	private String enumNameOrRaw(Enum<?> enumValue, String raw) {
		return enumValue != null ? enumValue.name() : raw;
	}

	/**
	 * Bloc "Gestió documental"
	 */
	private List<RegistreAnnexFirmaDto> toGesdocFirmesDto(List<RegistreAnnexFirmaEntity> firmes) {
		return firmes.stream().map(firma -> {
			RegistreAnnexFirmaDto dto = new RegistreAnnexFirmaDto();
			dto.setTipus(firma.getTipus());
			dto.setPerfil(firma.getPerfil());
			dto.setFitxerNom(firma.getFitxerNom());
			dto.setTipusMime(firma.getTipusMime());
			dto.setCsvRegulacio(firma.getCsvRegulacio());
			dto.setAutofirma(firma.isAutofirma());
			dto.setGesdocFirmaId(firma.getGesdocFirmaId());
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * Bloc "Firmes". No s'omple {@code contingut} (bytes de la firma)
	 * es reserva per a una futura acció de descàrrega de la firma individual.
	 */
	private List<ArxiuFirmaDto> toFirmesDto(List<RegistreAnnexFirmaEntity> firmes) {
		return firmes.stream().map(firma -> {
			ArxiuFirmaDto dto = new ArxiuFirmaDto();
			dto.setTipus(firma.getTipus() != null ? ArxiuConversions.toArxiuFirmaTipus(firma.getTipus()) : null);
			dto.setPerfil(parseArxiuFirmaPerfil(firma.getPerfil()));
			dto.setFitxerNom(firma.getFitxerNom());
			dto.setTipusMime(firma.getTipusMime());
			dto.setCsvRegulacio(firma.getCsvRegulacio());
			dto.setAutofirma(firma.isAutofirma());
			dto.setDetalls(firma.getDetalls().stream().map(this::toFirmaDetallDto).collect(Collectors.toList()));
			return dto;
		}).collect(Collectors.toList());
	}

	private ArxiuFirmaPerfilEnumDto parseArxiuFirmaPerfil(String perfil) {
		try {
			return perfil != null ? ArxiuFirmaPerfilEnumDto.valueOf(perfil) : null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private ArxiuFirmaDetallDto toFirmaDetallDto(RegistreFirmaDetallEntity detall) {
		ArxiuFirmaDetallDto dto = new ArxiuFirmaDetallDto();
		dto.setData(detall.getData());
		dto.setResponsableNif(detall.getResponsableNif());
		dto.setResponsableNom(detall.getResponsableNom());
		dto.setEmissorCertificat(detall.getEmissorCertificat());
		return dto;
	}

	private Map<String, String> parseMetaDadesMap(Long id, String metaDadesJson) {
		if (metaDadesJson == null || metaDadesJson.isEmpty()) {
			return null;
		}
		try {
			return new ObjectMapper().readValue(metaDadesJson, new TypeReference<Map<String, String>>() {});
		} catch (Exception e) {
			log.warn("No s'han pogut llegir les metadades de l'annex (id={})", id, e);
			return null;
		}
	}

	@Override
	protected void afterConversion(RegistreAnnexResourceEntity entity, RegistreAnnexResource resource) {
		List<RegistreAnnexFirmaEntity> firmes = registreAnnexFirmaRepository.getRegistreAnnexFirmesSenseDetall(entity.getId());
		boolean ambFirma = firmes != null && !firmes.isEmpty();
		resource.setAmbFirma(ambFirma);
		if (ambFirma) {
			RegistreAnnexFirmaEntity firma = firmes.get(0);
			String tipus = firma.getTipus();
			ArxiuFirmaTipusEnumDto arxiuFirmaTipus = tipus != null ? ArxiuConversions.toArxiuFirmaTipus(tipus) : null;
			resource.setSignaturaInfo(
					(tipus != null ? tipus : "") +
					(arxiuFirmaTipus != null ? " " + arxiuFirmaTipus.name() : "") +
					(firma.getPerfil() != null ? " " + firma.getPerfil() : ""));
		} else {
			resource.setSignaturaInfo("");
		}
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.getReferenceById(entity.getId());
		resource.setPotGenerarVersioImprimible(registreHelper.potGenerarVersioImprimible(registreAnnexEntity));

		// Enllaç "Veure a CONCSV": només per ROLE_ADMIN i només si l'annex té firma CSV i la URL base de
		// CONCSV està configurada. No és un artifact (és un camp normal del recurs), per això el
		// rol es comprova aquí directament enlloc d'un accessConstraints d'artifact.
		String concsvBaseUrl = configService.getConcsvBaseUrl();
		if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)
				&& entity.getFirmaCsv() != null && !entity.getFirmaCsv().isEmpty()
				&& concsvBaseUrl != null && !concsvBaseUrl.isEmpty()) {
			resource.setConcsvUrl(concsvBaseUrl + "/view.xhtml?hash=" + entity.getFirmaCsv());
		}
	}

	/**
	 * Accions "Descarregar original" / "Descarregar imprimible" (annexosAdminList.jsp:
	 * Reutilitza el mateix mètode que fa servir a la pantalla antiga; el booleà distingeix quina
	 * versió es demana, igual que al controller antic.
	 */
	@RequiredArgsConstructor
	private class DescarregarReportGenerator
			implements ReportGenerator<RegistreAnnexResourceEntity, Serializable, RegistreAnnexResource> {

		private final boolean ambVersioImprimible;

		@Override
		public List<RegistreAnnexResource> generateData(String code, RegistreAnnexResourceEntity entity, Serializable params) {
			return List.of(objectMappingHelper.newInstanceMap(entity, RegistreAnnexResource.class));
		}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			Long entitatActualId = SessioActualUtil.getEntitatId();
			entitatRepository.findById(entitatActualId)
					.ifPresent(entitat -> ConfigHelper.setEntitatActualCodi(entitat.getCodi()));

			RegistreAnnexResource annex = ((List<RegistreAnnexResource>) data).get(0);
			FitxerDto fitxer = registreHelper.getAnnexFitxer(annex.getId(), ambVersioImprimible);
			return new DownloadableFile(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

	}

	/** Dades mínimes que calen a {@link DescarregarFirmaReportGenerator#generateFile} per recuperar el fitxer. */
	@RequiredArgsConstructor
	private static class FirmaRef implements Serializable {
		private final Long annexId;
		private final int firmaIndex;
	}

	/**
	 * Acció "Descarregar firma":
	 * Descàrrega del fitxer d'una de les firmes de l'annex, per índex.
	 * Reutilitza {@link RegistreHelper#getAnnexFirmaFitxer(Long, int)}.
	 */
	private class DescarregarFirmaReportGenerator
			implements ReportGenerator<RegistreAnnexResourceEntity, RegistreAnnexResource.DescarregarFirmaForm, FirmaRef> {

		@Override
		public List<FirmaRef> generateData(String code, RegistreAnnexResourceEntity entity, RegistreAnnexResource.DescarregarFirmaForm params) {
			return List.of(new FirmaRef(entity.getId(), params.getFirmaIndex()));
		}

		@Override
		public DownloadableFile generateFile(String code, List<?> data, ReportFileType fileType, OutputStream out) {
			Long entitatActualId = SessioActualUtil.getEntitatId();
			entitatRepository.findById(entitatActualId)
					.ifPresent(entitat -> ConfigHelper.setEntitatActualCodi(entitat.getCodi()));

			FirmaRef firmaRef = ((List<FirmaRef>) data).get(0);
			FitxerDto fitxer = registreHelper.getAnnexFirmaFitxer(firmaRef.annexId, firmaRef.firmaIndex);
			return new DownloadableFile(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());
		}

		@Override
		public void onChange(Serializable id, RegistreAnnexResource.DescarregarFirmaForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, RegistreAnnexResource.DescarregarFirmaForm target) {
		}

	}

	/**
	 * Acció "Custòdia": botó "Guardar Definitiu",
	 * {@code AnnexosAdminController.guardarDefinitiu} -&gt; {@code annexosService.guardarComADefinitiu}).
	 * Reutilitza {@link AnnexosAdminHelper#guardarComADefinitiu(Long)}.
	 * <p>
	 * El front executa l'acció directament sobre la fila. El resultat és un mapa lliure amb {@code ok},
	 * {@code keyMessage}, {@code annexTitol}, {@code anotacioNumero} i {@code error} (indica
	 * si hi ha hagut una excepció, per triar el color del missatge al front).
	 */
	private class GuardarDefinitiuActionExecutor implements ActionExecutor<RegistreAnnexResourceEntity, Serializable, HashMap<String, Object>> {

		@Override
		public void onChange(
				Serializable id,
				Serializable previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				Serializable target) {
		}

		@Override
		public HashMap<String, Object> exec(
				String code,
				RegistreAnnexResourceEntity entity,
				Serializable params) throws ActionExecutionException {
			ResultatAnnexDefinitiuDto resultat = annexosAdminHelper.guardarComADefinitiu(entity.getId());
			String keyMessage = resultat.getKeyMessage();
			String keyMessageCurt = keyMessage != null && keyMessage.contains(".")
					? keyMessage.substring(keyMessage.lastIndexOf('.') + 1)
					: keyMessage;

			HashMap<String, Object> resposta = new HashMap<>();
			resposta.put("ok", resultat.isOk());
			resposta.put("keyMessage", keyMessageCurt);
			resposta.put("annexTitol", resultat.getAnnexTitol());
			resposta.put("anotacioNumero", resultat.getAnotacioNumero());
			resposta.put("error", resultat.getThrowable() != null);
			return resposta;
		}

	}

}
