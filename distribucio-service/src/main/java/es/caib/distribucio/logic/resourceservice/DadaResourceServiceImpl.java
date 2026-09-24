package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model. DadaResource;
import es.caib.distribucio.logic.intf.resourceservice. DadaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity. DadaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadaResourceServiceImpl extends BaseMutableResourceService<DadaResource, Long,  DadaResourceEntity> implements  DadaResourceService {

    @PostConstruct
    public void init() {
    }

}