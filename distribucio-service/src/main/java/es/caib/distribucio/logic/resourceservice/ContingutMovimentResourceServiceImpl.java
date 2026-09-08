package es.caib.distribucio.logic.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ContingutMovimentResource;
import es.caib.distribucio.logic.intf.resourceservice.ContingutMovimentResourceService;
import es.caib.distribucio.persist.resourceentity.ContingutMovimentResourceEntity;
import es.caib.distribucio.persist.resourceentity.UsuariResourceEntity;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de recurs per a la consulta dels moviments d'un contingut.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContingutMovimentResourceServiceImpl
		extends BaseMutableResourceService<ContingutMovimentResource, Long, ContingutMovimentResourceEntity>
		implements ContingutMovimentResourceService {

	private final UsuariResourceRepository usuariResourceRepository;

	/**
	 * El {@code remitent} ja arriba resolt automàticament com a {@code ResourceReference} (mateix
	 * mecanisme que {@code entitat}/{@code pare} a {@code ContingutResource}); només cal completar
	 * el nom complet de qui ha creat el moviment, igual que a {@code ContingutResourceServiceImpl}.
	 */
	@Override
	protected void afterConversion(ContingutMovimentResourceEntity entity, ContingutMovimentResource resource) {
		if (entity.getCreatedBy() != null) {
			UsuariResourceEntity usuari = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
			if (usuari != null && usuari.getNom() != null) {
				resource.setCreatedByFullName(usuari.getNom());
			}
		}
	}

}
