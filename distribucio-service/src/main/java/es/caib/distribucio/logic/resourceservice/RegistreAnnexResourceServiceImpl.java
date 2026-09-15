package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.DownloadableFile;
import es.caib.distribucio.logic.intf.base.model.ReportFileType;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.model.RegistreAnnexResource;
import es.caib.distribucio.logic.intf.resourceservice.RegistreAnnexResourceService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.persist.resourceentity.RegistreAnnexResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
	private final ConfigService configService;
	private final RegistreService registreService;
	private final EntitatRepository entitatRepository;
	private final AuthenticationHelper authenticationHelper;

	@PostConstruct
	public void init() {
		register(RegistreAnnexResource.REPORT_DESCARREGAR_ORIGINAL_CODE, new DescarregarReportGenerator(false));
		register(RegistreAnnexResource.REPORT_DESCARREGAR_IMPRIMIBLE_CODE, new DescarregarReportGenerator(true));
	}

	@Override
	protected Specification<RegistreAnnexResourceEntity> additionalSpecification(String[] namedQueries) {
		Long entitatId = SessioActualUtil.getEntitatId();
		if (entitatId != null) {
			return (root, query, cb) -> cb.equal(root.get("registre").get("entitat").get("id"), entitatId);
		}
		return null;
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
			FitxerDto fitxer = registreService.getAnnexFitxer(annex.getId(), ambVersioImprimible);
			return new DownloadableFile(fitxer.getNom(), fitxer.getContentType(), fitxer.getContingut());
		}

		@Override
		public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
		}

	}

}
