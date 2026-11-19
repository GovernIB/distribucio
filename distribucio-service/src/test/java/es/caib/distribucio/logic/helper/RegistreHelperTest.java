package es.caib.distribucio.logic.helper;

import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.persist.entity.ContingutLogEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.ContingutLogRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistreHelperTest {

    @Mock private ContingutLogRepository contingutLogRepository;

    @InjectMocks
    private RegistreHelper registreHelper;

    private static Stream<Arguments> isCanviRebutjadaRebudaValid_values() {
        return Stream.of(
                Arguments.of(new ArrayList<>(List.of(LogTipusEnumDto.AGAFAR, LogTipusEnumDto.BACK_REBUTJADA)), true),
                Arguments.of(new ArrayList<>(List.of(LogTipusEnumDto.BACK_REBUTJADA, LogTipusEnumDto.SOBREESCRIURE)), true),
                Arguments.of(new ArrayList<>(List.of(LogTipusEnumDto.SOBREESCRIURE, LogTipusEnumDto.BACK_REBUTJADA)), false)
        );
    }

    @ParameterizedTest
    @MethodSource("isCanviRebutjadaRebudaValid_values")
    void isCanviRebutjadaRebudaValid(List<LogTipusEnumDto> tipusList, boolean expected) {
        RegistreEntity registre = new RegistreEntity();

        List<ContingutLogEntity> logs = new ArrayList<>();

        for (LogTipusEnumDto tipus : tipusList) {
            ContingutLogEntity contingutLogEntity = ContingutLogEntity
                    .getBuilder(tipus, null)
                    .build();
            logs.add(contingutLogEntity);
        }

        when(contingutLogRepository.findByContingutOrderByCreatedDateDesc(eq(registre))).thenReturn(logs);

        boolean result = registreHelper.isCanviRebutjadaRebudaValid(registre);
        Assertions.assertEquals(expected, result);
    }
}