package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.logic.intf.model.RegistreAnnexResource;
import es.caib.distribucio.logic.intf.resourceservice.RegistreAnnexResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.persist.resourceentity.RegistreAnnexResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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
	}

}
