package es.caib.distribucio.logic.service;

import es.caib.distribucio.logic.helper.LimitCanviEstatHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.LimitCanviEstatService;
import es.caib.distribucio.persist.entity.LimitCanviEstatEntity;
import es.caib.distribucio.persist.repository.LimitCanviEstatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LimitCanviEstatServiceImpl implements LimitCanviEstatService {

    private final LimitCanviEstatRepository limitCanviEstatRepository;
    private final PaginacioHelper paginacioHelper;
    private final LimitCanviEstatHelper limitCanviEstatHelper;

    @Override
    public LimitCanviEstatDto findById(Long id) {
        LimitCanviEstatEntity limitCanviEstatEntity = limitCanviEstatRepository.findById(id).orElse(null);
        if (limitCanviEstatEntity == null) { return null; }
        return limitCanviEstatHelper.toDto(limitCanviEstatEntity);
    }

    @Override
    public LimitCanviEstatDto findByUsuariCodi(String usuariCodi) {
        LimitCanviEstatEntity limitCanviEstatEntity = limitCanviEstatRepository.findByUsuariCodi(usuariCodi).orElse(null);
        if (limitCanviEstatEntity == null) { return null; }
        return limitCanviEstatHelper.toDto(limitCanviEstatEntity);
    }

    @Override
    public LimitCanviEstatDto create(LimitCanviEstatDto limitCanviEstatDto) {
        LimitCanviEstatEntity limitCanviEstatEntity = LimitCanviEstatEntity.getBuilder(
                        limitCanviEstatDto.getUsuariCodi(),
                        limitCanviEstatDto.getDescripcio(),
                        limitCanviEstatDto.getLimitMinutLaboral(),
                        limitCanviEstatDto.getLimitMinutNoLaboral(),
                        limitCanviEstatDto.getLimitDiaLaboral(),
                        limitCanviEstatDto.getLimitDiaNoLaboral()
                )
                .build();
        limitCanviEstatRepository.save(limitCanviEstatEntity);
        return limitCanviEstatDto;
    }

    @Override
    public LimitCanviEstatDto update(LimitCanviEstatDto limitCanviEstatDto) {
        LimitCanviEstatEntity limitCanviEstatEntity = limitCanviEstatRepository.findById(limitCanviEstatDto.getId()).orElse(null);
        if (limitCanviEstatEntity == null) {
            throw new NotFoundException(limitCanviEstatDto.getId(), LimitCanviEstatEntity.class);
        } else {
            limitCanviEstatEntity.update(limitCanviEstatDto);
            limitCanviEstatRepository.save(limitCanviEstatEntity);
        }
        return limitCanviEstatDto;
    }

    @Override
    public void delete(Long limitCanviEstatId) {
        LimitCanviEstatEntity entity = limitCanviEstatRepository.findById(limitCanviEstatId).orElse(null);
        if (entity == null ) throw new NotFoundException(limitCanviEstatId, LimitCanviEstatEntity.class);
        limitCanviEstatRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaDto<LimitCanviEstatDto> findAllPaged(PaginacioParamsDto paginacioParams) {
        PaginaDto<LimitCanviEstatDto> resultPagina =  paginacioHelper.toPaginaDto(
                limitCanviEstatRepository.findAll(
                        paginacioHelper.toSpringDataPageable(paginacioParams)),
                LimitCanviEstatDto.class);
        return resultPagina;
    }
}
