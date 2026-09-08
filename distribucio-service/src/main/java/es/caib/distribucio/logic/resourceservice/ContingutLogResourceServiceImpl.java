package es.caib.distribucio.logic.resourceservice;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ContingutLogResource;
import es.caib.distribucio.logic.intf.resourceservice.ContingutLogResourceService;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;
import es.caib.distribucio.persist.resourceentity.ContingutLogResourceEntity;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.ContingutResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de recurs per a la consulta del log d'accions d'un contingut.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutLogResourceServiceImpl
		extends BaseMutableResourceService<ContingutLogResource, Long, ContingutLogResourceEntity>
		implements ContingutLogResourceService {

	private final UsuariResourceRepository usuariResourceRepository;
	private final ContingutResourceRepository contingutResourceRepository;

	/**
	 * Completa els camps que el mapeig genèric per reflexió no pot resoldre: el nom complet del
	 * remitent (mateix patró que {@code ContingutResourceServiceImpl}), la llista ordenada de
	 * paràmetres del log, i el nom real de l'objecte modificat (quan n'hi ha).
	 */
	@Override
	protected void afterConversion(ContingutLogResourceEntity entity, ContingutLogResource resource) {
		if (entity.getCreatedBy() != null) {
			UsuariResourceEntity usuari = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
			if (usuari != null && usuari.getNom() != null) {
				resource.setCreatedByFullName(usuari.getNom());
			}
		}

		resource.setParams(
				entity.getParams().stream()
						.map(ContingutLogParamEntity::getValor)
						.collect(Collectors.toList()));

		if (entity.getObjecteId() != null) {
			resource.setObjecteNom(resoldreObjecteNom(entity.getObjecteTipus(), entity.getObjecteId()));
		}
	}

	/**
	 * Mateix algorisme que {@code ContingutLogHelper.findLogDetalls} del manteniment legacy: per a
	 * CONTINGUT/CARPETA/DOCUMENT/EXPEDIENT/REGISTRE l'{@code objecteId} és directament l'id d'un
	 * {@code ContingutResourceEntity} (tots els subtipus comparteixen la mateixa jerarquia i el
	 * mateix camp {@code nom}); per RELACIO és "id1#id2" i es concatenen els dos noms; per la resta
	 * (o si l'id no té el format esperat) es manté el fallback "???TIPUS#ID???" del legacy.
	 */
	private String resoldreObjecteNom(LogObjecteTipusEnumDto objecteTipus, String objecteId) {
		switch (objecteTipus) {
			case CONTINGUT:
			case CARPETA:
			case DOCUMENT:
			case EXPEDIENT:
			case REGISTRE:
				ContingutResourceEntity<?> contingut = contingutResourceRepository.getReferenceById(Long.valueOf(objecteId));
				return contingut.getNom();
			case RELACIO:
				String[] ids = objecteId.split("#");
				if (ids.length >= 2) {
					ContingutResourceEntity<?> c1 = contingutResourceRepository.getReferenceById(Long.valueOf(ids[0]));
					ContingutResourceEntity<?> c2 = contingutResourceRepository.getReferenceById(Long.valueOf(ids[1]));
					return c1.getNom() + " <-> " + c2.getNom();
				}
				// Sense els 2 ids esperats: mateix fall-through intencionat que el legacy cap al fallback.
			case ALTRES:
			default:
				return "???" + objecteTipus.name() + "#" + objecteId + "???";
		}
	}

}
