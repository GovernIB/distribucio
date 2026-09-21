import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Typography } from '@mui/material';
import { useMuiContentDialog } from 'reactlib';

/** Estats de processament d'una anotació, en l'ordre de la llegenda. */
const PROCES_ESTATS = [
    'ARXIU_PENDENT',
    'REGLA_PENDENT',
    'BUSTIA_PENDENT',
    'BUSTIA_PROCESSADA',
    'BACK_PENDENT',
    'BACK_COMUNICADA',
    'BACK_REBUDA',
    'BACK_PROCESSADA',
    'BACK_REBUTJADA',
    'BACK_ERROR',
];

// eslint-disable-next-line react-refresh/only-export-components
const ProcesEstatLegendContent: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Box component={'ul'} sx={{ m: 0, pl: 3 }}>
            {PROCES_ESTATS.map((estat) => (
                <li key={estat}>
                    <Typography variant={'body2'}>
                        <strong>{t(`component.ProcesEstatLegend.estat.${estat}.label`)}</strong> :
                    </Typography>
                    <Typography variant={'body2'} sx={{ mb: 1 }}>
                        {t(`component.ProcesEstatLegend.estat.${estat}.info`)}
                    </Typography>
                </li>
            ))}
        </Box>
    );
};

/** Diàleg amb la llegenda dels estats de processament d'una anotació de registre. */
export const useProcesEstatLegend = () => {
    const { t } = useTranslation();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = () => {
        dialogShow(
            t('component.ProcesEstatLegend.title'),
            <ProcesEstatLegendContent />,
            [{ value: false, text: t('component.ProcesEstatLegend.tanca') }],
            { maxWidth: 'md', fullWidth: true }
        ).catch(() => {
            // El diàleg es tanca rebutjant la promesa: no cal fer res
        });
    };

    return { handleOpen, component: dialogComponent };
};

export default useProcesEstatLegend;
