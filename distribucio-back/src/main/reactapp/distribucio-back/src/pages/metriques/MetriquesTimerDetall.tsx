import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { MetriquesBarra, MetriquesBarraSegment } from './MetriquesBarra';
import { capitalitza, formatNumero, percentatge, TimerData } from './metriquesUtils';

type FilaBarra = { label: string; valor: number };
type Columna = { title: string; subtitle?: string; files: FilaBarra[]; max: number };

/** Columna del detall: títol, text opcional i files etiqueta + barra + valor. */
const Columna: React.FC<Columna> = ({ title, subtitle, files, max }) => (
    <Grid size={{ xs: 12, md: 4 }}>
        <Typography variant="subtitle1" fontWeight="bold">
            {title}
        </Typography>
        <Typography variant="body2" color="textSecondary" sx={{ minHeight: '1.5em' }}>
            {subtitle}
        </Typography>
        <Box sx={{ display: 'grid', gridTemplateColumns: 'auto 1fr auto', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
            {files.map((fila) => (
                <React.Fragment key={fila.label}>
                    <Typography variant="body2" sx={{ pr: 0.5 }}>
                        {fila.label}
                    </Typography>
                    <MetriquesBarra>
                        <MetriquesBarraSegment ample={percentatge(fila.valor, max)} color="info" />
                    </MetriquesBarra>
                    <Typography variant="body2" sx={{ minWidth: '3em', pl: 0.5 }}>
                        {formatNumero(fila.valor)}
                    </Typography>
                </React.Fragment>
            ))}
        </Box>
    </Grid>
);

/** Detall d'un timer: freqüència, durada i percentils. */
export const MetriquesTimerDetall: React.FC<{ timer: TimerData }> = ({ timer }) => {
    const { t } = useTranslation();
    const detall = (clau: string, options?: Record<string, unknown>) => t(`page.metriques.detail.${clau}`, options);
    const maxTaxa = Math.max(timer.mean_rate, timer.m1_rate, timer.m5_rate, timer.m15_rate);

    return (
        <Grid container spacing={2} sx={{ p: 1.5 }}>
            <Columna
                title={detall('frequencia')}
                subtitle={`${timer.rate_units ?? ''} (${detall('total', { count: timer.count })})`}
                max={maxTaxa}
                files={[
                    { label: detall('minut1'), valor: timer.m1_rate },
                    { label: detall('minut5'), valor: timer.m5_rate },
                    { label: detall('minut15'), valor: timer.m15_rate },
                    { label: detall('mitjana'), valor: timer.mean_rate },
                ]}
            />
            <Columna
                title={detall('durada')}
                subtitle={capitalitza(timer.duration_units)}
                max={timer.max}
                files={[
                    { label: detall('minim'), valor: timer.min },
                    { label: detall('mitjana'), valor: timer.mean },
                    { label: detall('maxim'), valor: timer.max },
                    { label: detall('desviacio'), valor: timer.stddev },
                ]}
            />
            <Columna
                title={detall('percentils')}
                max={timer.max}
                files={[
                    { label: '99.9%', valor: timer.p999 },
                    { label: '99%', valor: timer.p99 },
                    { label: '98%', valor: timer.p98 },
                    { label: '95%', valor: timer.p95 },
                    { label: '75%', valor: timer.p75 },
                    { label: '50%', valor: timer.p50 },
                ]}
            />
        </Grid>
    );
};

export default MetriquesTimerDetall;
