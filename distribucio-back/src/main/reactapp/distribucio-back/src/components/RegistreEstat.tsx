import React from 'react';
import { useTranslation } from 'react-i18next';
import { Icon, Typography } from '@mui/material';
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
    return (
        <>
            {children}

            {entity.procesEstat == 'REGLA_PENDENT' &&
                (reglaNom != null ? (
                    reglaNom
                ) : (
                    <Icon title={t('page.registre.estat.regla')} color={'error'}>
                        warning
                    </Icon>
                ))}

            {entity.procesEstat == 'BACK_REBUTJADA' && entity.backObservacions != '' && (
                <Icon title={entity.backObservacions} color={'warning'}>
                    error
                </Icon>
            )}

            <Typography
                variant={'inherit'}
                hidden={
                    entity.procesEstat == 'ARXIU_PENDENT' ||
                    entity.procesEstat == 'REGLA_PENDENT' ||
                    entity.procesEstat == 'BUSTIA_PENDENT' ||
                    entity.procesEstat == 'BUSTIA_PROCESSADA'
                }>
                &nbsp;<strong>{entity.backCodi}</strong>
            </Typography>
            {showReintents && (
                <>
                    <Typography
                        variant={'inherit'}
                        title={t('page.registre.estat.maxReintents', {
                            num: entity.procesIntents,
                            max: entity.maxReintents,
                        })}
                        color={entity.reintentsEsgotat ? 'error' : 'warning'}
                        hidden={
                            entity.procesEstat != 'ARXIU_PENDENT' &&
                            entity.procesEstat != 'REGLA_PENDENT' &&
                            entity.procesEstat != 'BACK_PENDENT' &&
                            entity.procesEstat != 'BACK_ERROR'
                        }>
                        &nbsp;({entity.procesIntents}/{entity.maxReintents})
                    </Typography>

                    {entity.backRetryEnviarData && !entity.reintentsEsgotat && (
                        <>&nbsp; Proper reintent: {formatDate(entity.backRetryEnviarData)}</>
                    )}
                </>
            )}
        </>
    );
};

export default RegistreEstat;
