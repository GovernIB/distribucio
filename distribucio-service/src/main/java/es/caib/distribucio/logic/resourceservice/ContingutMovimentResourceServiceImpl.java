package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
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
    private final BustiaResourceRepository bustiaResourceRepository;

    /**
	 * El {@code remitent} ja arriba resolt automàticament com a {@code ResourceReference} (mateix
	 * mecanisme que {@code entitat}/{@code pare} a {@code ContingutResource}); només cal completar
	 * el nom complet de qui ha creat el moviment, igual que a {@code ContingutResourceServiceImpl}.
	 */
	@Override
	protected void afterConversion(ContingutMovimentResourceEntity entity, ContingutMovimentResource resource) {
		if (entity.getOrigenId() != null) {
            bustiaResourceRepository.findById(entity.getOrigenId())
                    .ifPresent(bustia -> {
                        resource.setBustiaOrigen(ResourceReference.toResourceReference(
                                bustia.getId(), bustia.getNom() ));

                        if (bustia.getUnitatOrganitzativa() != null) {
                            resource.setUnitatOrganitzativaOrigen(ResourceReference.toResourceReference(
                                    bustia.getUnitatOrganitzativa().getId(), bustia.getUnitatOrganitzativa().getCodiNom() ));
                        }
                    });
        }
		if (entity.getDestiId() != null) {
            bustiaResourceRepository.findById(entity.getDestiId())
                    .ifPresent(bustia -> {
                        resource.setBustiaDesti(ResourceReference.toResourceReference(
                                bustia.getId(), bustia.getNom() ));

                        if (bustia.getUnitatOrganitzativa() != null) {
                            resource.setUnitatOrganitzativaDesti(ResourceReference.toResourceReference(
                                    bustia.getUnitatOrganitzativa().getId(), bustia.getUnitatOrganitzativa().getCodiNom() ));
                        }
                    });
        }

        if (entity.getCreatedBy() != null) {
			UsuariResourceEntity usuari = usuariResourceRepository.findById(entity.getCreatedBy()).orElse(null);
			if (usuari != null && usuari.getNom() != null) {
				resource.setCreatedByFullName(usuari.getNom());
			}
		}
	}

}
