import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import Collapse from '@mui/material/Collapse';
import Typography from '@mui/material/Typography';
import { MetriquesBarra, MetriquesBarraSegment } from './MetriquesBarra';
import MetriquesTimerDetall from './MetriquesTimerDetall';
import { construeixTimers, MetriquesData } from './metriquesUtils';

/** Llegenda de les barres de la llista. */
const Llegenda: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Box sx={{ p: 2 }}>
            <Typography>{t('page.metriques.llegenda.title')}</Typography>
            <MetriquesBarra sx={{ mt: 0.5 }}>
                <MetriquesBarraSegment ample={100} color="success">
                    {t('page.metriques.llegenda.pes')}
                </MetriquesBarraSegment>
            </MetriquesBarra>
            <MetriquesBarra sx={{ mt: '2px' }}>
                <MetriquesBarraSegment ample={50} color="error" ratllat>
                    {t('page.metriques.llegenda.tempsMig')}
                </MetriquesBarraSegment>
                <MetriquesBarraSegment ample={50} color="error">
                    {t('page.metriques.llegenda.tempsMaxim')}
                </MetriquesBarraSegment>
            </MetriquesBarra>
        </Box>
    );
};

/** Llegenda i llista de timers ordenats per pes; clicant un timer es desplega el seu detall. */
export const MetriquesTimers: React.FC<{ dades?: MetriquesData }> = ({ dades }) => {
    const { t } = useTranslation();
    const timers = React.useMemo(() => construeixTimers(dades), [dades]);
    const [desplegats, setDesplegats] = React.useState<Set<string>>(new Set());

    // Amb unes dades noves (importació) es perd l'estat dels desplegats, com a l'antiga interfície.
    React.useEffect(() => {
        setDesplegats(new Set());
    }, [dades]);

    const alternaDesplegat = (nom: string) =>
        setDesplegats((actuals) => {
            const nous = new Set(actuals);
            if (!nous.delete(nom)) {
                nous.add(nom);
            }
            return nous;
        });

    return (
        <Box>
            <Llegenda />
            {timers.length > 0 && (
                <Box sx={{ px: 2, pb: 2 }}>
                    <Typography variant="h6" component="h4" sx={{ mb: 1 }}>
                        {t('page.metriques.generics')}
                    </Typography>
                    {timers.map((timer) => (
                        <Box key={timer.name} sx={{ mb: 1 }}>
                            <Card
                                variant="outlined"
                                onClick={() => alternaDesplegat(timer.name)}
                                sx={{ p: 1.5, cursor: 'pointer' }}
                            >
                                <Typography fontWeight="500" fontStyle="italic">
                                    {timer.name} ({t('page.metriques.execucions', { count: timer.count })})
                                </Typography>
                                <MetriquesBarra sx={{ mt: 0.5, mb: '2px' }}>
                                    <MetriquesBarraSegment ample={timer.pctPes} color="success">
                                        {timer.pes}
                                    </MetriquesBarraSegment>
                                </MetriquesBarra>
                                <MetriquesBarra>
                                    <MetriquesBarraSegment ample={timer.pctMitja} color="error" ratllat>
                                        {timer.mitja}
                                    </MetriquesBarraSegment>
                                    <MetriquesBarraSegment ample={timer.pctMaxim} color="error">
                                        {timer.maxim}
                                    </MetriquesBarraSegment>
                                </MetriquesBarra>
                            </Card>
                            {/* Fora de la targeta clicable: clicar dins el detall no el plega. */}
                            <Collapse in={desplegats.has(timer.name)} mountOnEnter>
                                <Card variant="outlined" sx={{ mt: 0.5 }}>
                                    <MetriquesTimerDetall timer={timer.data} />
                                </Card>
                            </Collapse>
                        </Box>
                    ))}
                </Box>
            )}
        </Box>
    );
};

export default MetriquesTimers;
