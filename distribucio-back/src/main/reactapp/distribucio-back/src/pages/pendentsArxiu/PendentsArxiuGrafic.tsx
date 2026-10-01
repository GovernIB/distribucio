import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { LineChart } from '@mui/x-charts/LineChart';
import { EntradaHistograma, formatHora, msASegons } from './pendentsArxiuUtils';

/** Colors de les sèries */
const COLOR_PENDENTS = '#4db8ff';
const COLOR_PROCESSATS = '#79d279';
const COLOR_ERRORS = '#ff4d4d';
const COLOR_TEMPS_MITJA = '#ecc6d9';

type PendentsArxiuGraficProps = {
    entrades?: EntradaHistograma[];
};

/** Gràfic de línies de l'històric: pendents, processats, errors i temps mig (en segons) per execució. */
export const PendentsArxiuGrafic: React.FC<PendentsArxiuGraficProps> = ({ entrades }) => {
    const { t, i18n } = useTranslation();
    const serie = (clau: string) => t(`page.pendentsArxiu.serie.${clau}`);

    if (!entrades?.length) {
        return (
            <Box display="flex" alignItems="center" justifyContent="center" flex={1} minHeight={320}>
                <Typography color="textSecondary">{t('page.pendentsArxiu.senseDades')}</Typography>
            </Box>
        );
    }

    const valorMaxim = Math.max(
        ...entrades.flatMap((entrada) => [
            entrada.pendentArxiu,
            entrada.processats,
            entrada.errors,
            msASegons(entrada.tempsMitjaMs),
        ])
    );

    return (
        <Box flex={1} minHeight={320}>
            <LineChart
                xAxis={[
                    {
                        scaleType: 'point',
                        data: entrades.map((entrada) => formatHora(entrada.data, i18n.language)),
                    },
                ]}
                // Si tot és 0 l'eix quedaria amb min = max i la línia al mig; es fixa a 0-1.
                yAxis={[{ min: 0, max: valorMaxim > 0 ? undefined : 1 }]}
                slotProps={{ legend: { toggleVisibilityOnClick: true } }} // Clicar un valor de la llegenda amaga/mostra la sèrie
                series={[
                    {
                        id: 'pendents',
                        label: serie('pendents'),
                        data: entrades.map((entrada) => entrada.pendentArxiu),
                        color: COLOR_PENDENTS,
                        showMark: false,
                    },
                    {
                        id: 'processats',
                        label: serie('processats'),
                        data: entrades.map((entrada) => entrada.processats),
                        color: COLOR_PROCESSATS,
                        showMark: false,
                    },
                    {
                        id: 'errors',
                        label: serie('errors'),
                        data: entrades.map((entrada) => entrada.errors),
                        color: COLOR_ERRORS,
                        showMark: false,
                    },
                    {
                        id: 'tempsMitja',
                        label: serie('tempsMitja'),
                        data: entrades.map((entrada) => msASegons(entrada.tempsMitjaMs)),
                        color: COLOR_TEMPS_MITJA,
                        showMark: false,
                    },
                ]}
            />
        </Box>
    );
};

export default PendentsArxiuGrafic;
