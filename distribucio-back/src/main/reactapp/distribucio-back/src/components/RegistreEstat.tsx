import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Icon, Typography } from '@mui/material';
import { formatDate } from '../util/dateUtils.ts';

type RegistreEstatProps = {
    /** Fila amb les dades de l'anotació (RegistreResource o VistaMovimentResource). */
    entity: any;
    /** Text de l'estat de processament. */
    children?: React.ReactNode;
    /** Mostra el comptador d'intents i el proper reintent. Per defecte `true`. */
    showReintents?: boolean;
};

/**
 * Detall de l'estat de processament d'una anotació: nom de la regla a REGLA_PENDENT, codi del backoffice
 * als estats de backoffice i, opcionalment, el comptador de reintents.
 */
export const RegistreEstat: React.FC<RegistreEstatProps> = ({ entity, children, showReintents = true }) => {
    const { t } = useTranslation();
    // RegistreResource informa `regla` (no arriba mai) i VistaMovimentResource informa `reglaNom`
    const reglaNom = entity.reglaNom ?? entity.regla?.nom;
    const estat = entity.procesEstat;
    const senseCodi = ['ARXIU_PENDENT', 'REGLA_PENDENT', 'BUSTIA_PENDENT', 'BUSTIA_PROCESSADA'].includes(estat);
    const ambReintents = ['ARXIU_PENDENT', 'REGLA_PENDENT', 'BACK_PENDENT', 'BACK_ERROR'].includes(estat);
    const liniaNova = { flexBasis: '100%' };

    return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', columnGap: 0.5, minWidth: 0 }}>
            <span>{children}</span>

            {estat == 'BACK_REBUTJADA' && !!entity.backObservacions && (
                <Icon title={entity.backObservacions} color={'warning'} fontSize={'small'}>
                    error
                </Icon>
            )}

            {showReintents && ambReintents && (
                <Typography
                    variant={'inherit'}
                    title={t('page.registre.estat.maxReintents', {
                        num: entity.procesIntents,
                        max: entity.maxReintents,
                    })}
                    color={entity.reintentsEsgotat ? 'error' : 'warning'}
                >
                    ({entity.procesIntents}/{entity.maxReintents})
                </Typography>
            )}

            {estat == 'REGLA_PENDENT' && (
                <Box sx={liniaNova}>
                    {reglaNom != null ? (
                        reglaNom
                    ) : (
                        <Icon title={t('page.registre.estat.regla')} color={'error'} fontSize={'small'}>
                            warning
                        </Icon>
                    )}
                </Box>
            )}

            {!senseCodi && !!entity.backCodi && (
                <Box sx={liniaNova}>
                    <Typography variant="overline">{entity.backCodi}</Typography>
                </Box>
            )}

            {showReintents && estat == 'BACK_PENDENT' && entity.backRetryEnviarData && !entity.reintentsEsgotat && (
                <Box sx={liniaNova}>Proper reintent: {formatDate(entity.backRetryEnviarData)}</Box>
            )}
        </Box>
    );
};

export default RegistreEstat;
