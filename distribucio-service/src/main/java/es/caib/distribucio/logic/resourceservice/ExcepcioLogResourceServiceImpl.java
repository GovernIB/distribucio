package es.caib.distribucio.logic.resourceservice;

import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.ExcepcioLogResource;
import es.caib.distribucio.logic.intf.resourceservice.ExcepcioLogResourceService;
import es.caib.distribucio.persist.resourceentity.ExcepcioLogResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació del servei de recurs de consulta del log d'excepcions.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcepcioLogResourceServiceImpl extends BaseMutableResourceService<ExcepcioLogResource, Long, ExcepcioLogResourceEntity> implements ExcepcioLogResourceService {}