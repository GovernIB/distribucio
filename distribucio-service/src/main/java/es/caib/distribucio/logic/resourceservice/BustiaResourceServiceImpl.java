package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.helper.AuthenticationHelper;
import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganizzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.logic.intf.resourceservice.BustiaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.resourceentity.BustiaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UnitatOrganitzativaResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BustiaResourceServiceImpl extends BaseMutableResourceService<BustiaResource, Long, BustiaResourceEntity> implements BustiaResourceService {

    private final AuthenticationHelper authenticationHelper;
    private final PermisosHelper permisosHelper;
    private final UnitatOrganitzativaResourceRepository unitatOrganitzativaResourceRepository;
    private final BustiaResourceRepository bustiaResourceRepository;

    @PostConstruct
    public void init() {
        register(BustiaResource.PERSPECTIVE_PERMISOS_COUNT_CODE, new PermisosCountPerspectiveApplicator());
    }

    private void beforeSave(BustiaResourceEntity entity, BustiaResource resource) {
        BustiaResourceEntity pare = bustiaResourceRepository.findByEntitatAndUnitatOrganitzativaAndPareNull(
                entity.getEntitat(), entity.getUnitatOrganitzativa());

        if (pare == null) {
            pare = new BustiaResourceEntity();
            pare.setEntitat(entity.getEntitat());
            pare.setNom(entity.getUnitatOrganitzativa().getDenominacio());
            pare.setUnitatOrganitzativa(entity.getUnitatOrganitzativa());
            bustiaResourceRepository.save(pare);
        }
        entity.setPare(pare);
    }

    @Override
    protected void beforeCreateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
    }

    @Override
    protected void beforeUpdateSave(BustiaResourceEntity entity, BustiaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
    }

    @Override
	protected Specification<BustiaResourceEntity> additionalSpecification(String[] namedQueries) {
		// S'exclou la bústia arrel de cada unitat organitzativa (pare_id null), que és un
		// element merament tècnic i mai s'ha de gestionar directament (veure BustiaRepository,
		// que aplica sempre el mateix filtre a l'equivalent JSP).
        Long entitatActualId = SessioActualUtil.getEntitatId();

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNotNull(root.get("pare").get("id")));

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            if (mapaNamedQueries.containsKey("PERMIS_PER_USUARI")) {
                // TODO: implementar filtro por numero de permisos
            }

            if (mapaNamedQueries.containsKey("UNITAT_SUPERIOR")) {
                String unitatId = mapaNamedQueries.get("UNITAT_SUPERIOR");
                List<Long> idsJerarquia = unitatOrganitzativaResourceRepository.findUnitatAndAllDescendentsIds(Long.valueOf(unitatId));
                if (idsJerarquia.isEmpty()) {
                    return cb.disjunction(); // No existe tal unidad, no devolver nada
                }

                int chunkSize = 900;
                List<Predicate> orPredicates = new ArrayList<>();

                for (int i = 0; i < idsJerarquia.size(); i += chunkSize) {
                    List<Long> chunk = idsJerarquia.subList(i, Math.min(i + chunkSize, idsJerarquia.size()));
                    orPredicates.add(root.get("unitatOrganitzativa").get("id").in(chunk));
                }

                predicates.add(cb.notEqual(root.get("id"), unitatId));
                predicates.add( cb.or(orPredicates.toArray(new Predicate[0])) );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
	}

    @Override
    protected void afterConversion(BustiaResourceEntity entity, BustiaResource resource) {
        resource.setPendent(!UnitatOrganizzativaEstatEnumDto.V.equals(entity.getUnitatOrganitzativa().getEstat()));
    }

    private class PermisosCountPerspectiveApplicator implements PerspectiveApplicator<BustiaResourceEntity, BustiaResource> {

        @Override
        public boolean applyMultiple(
                String code,
                List<BustiaResourceEntity> entities,
                List<BustiaResource> resources) {
            if (!authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
                return true;
            }
            List<Long> ids = entities.stream().
                    map(BustiaResourceEntity::getId).
                    collect(Collectors.toList());
            Map<Long, List<PermisDto>> permisosPerEntitat = permisosHelper.findPermisos(
                    ids,
                    BustiaEntity.class);
            IntStream.range(0, entities.size()).forEach(i -> {
                List<PermisDto> permisos = permisosPerEntitat.get(entities.get(i).getId());
                resources.get(i).setPermisosCount(permisos != null ? permisos.size() : 0);
            });
            return true;
        }

        @Override
        public void applySingle(
                String code,
                BustiaResourceEntity entity,
                BustiaResource resource) {
            applyMultiple(
                    code,
                    Collections.singletonList(entity),
                    Collections.singletonList(resource));
        }

    }

}