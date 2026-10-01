import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, useBaseAppContext, useResourceApiService } from 'reactlib';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { CardPage } from '../../components/CardData';
import MetriquesTimers from './MetriquesTimers';
import { esMetriquesValides, MetriquesData } from './metriquesUtils';
import { Divider, Icon } from '@mui/material';

const ACCIO_OBTENIR_METRIQUES = 'OBTENIR_METRIQUES';
const FITXER_METRIQUES = 'metrics.json';

/** Obté les mètriques de l'aplicació en obrir la pantalla i permet substituir-les per les d'un fitxer importat. */
const useMetriques = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('metriquesResource');
    const [dades, setDades] = React.useState<MetriquesData>();

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(undefined, { code: ACCIO_OBTENIR_METRIQUES })
            .then((response: MetriquesData) => setDades(response))
            .catch((error: { description?: string; message?: string }) =>
                temporalMessageShow(
                    t('page.metriques.accio.errorCarrega'),
                    error?.description ?? error?.message ?? '',
                    'error'
                )
            );
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady]);

    return { dades, setDades };
};

/** Pantalla "Mètriques": timers de l'aplicació amb importació i exportació en JSON. */
export const MetriquesPage: React.FC = () => {
    const { t } = useTranslation();
    const { temporalMessageShow, saveAs } = useBaseAppContext();
    const { dades, setDades } = useMetriques();
    const fitxerRef = React.useRef<HTMLInputElement>(null);

    const exportar = () => {
        saveAs?.(new Blob([JSON.stringify(dades)], { type: 'text/plain;charset=utf-8' }), FITXER_METRIQUES);
    };

    const importar = (event: React.ChangeEvent<HTMLInputElement>) => {
        const fitxer = event.target.files?.[0];
        // Es buida el valor perquè es pugui tornar a seleccionar el mateix fitxer.
        event.target.value = '';
        if (!fitxer) {
            return;
        }
        fitxer
            .text()
            .then((contingut) => {
                const importades = JSON.parse(contingut);
                if (!esMetriquesValides(importades)) {
                    throw new Error();
                }
                setDades(importades);
            })
            .catch(() =>
                temporalMessageShow(t('page.metriques.accio.error'), t('page.metriques.accio.errorImportar'), 'error')
            );
    };

    return (
        <GridPage>
            <CardPage title={t('page.metriques.title')}>
                <Box display="flex" justifyContent="space-between" alignItems="center" gap={1} mb={1}>
                    <Typography variant="h6" component="h2">
                        {t('page.metriques.timers')}
                    </Typography>
                    <Box display="flex" gap={1}>
                        <input ref={fitxerRef} type="file" accept=".json,application/json" hidden onChange={importar} />
                        <Button
                            variant="outlined"
                            onClick={() => fitxerRef.current?.click()}
                            startIcon={<Icon sx={{ mr: 1 }}>file_upload</Icon>}
                        >
                            {t('page.metriques.accio.importar')}
                        </Button>
                        <Button
                            variant="outlined"
                            disabled={!dades}
                            onClick={exportar}
                            startIcon={<Icon sx={{ mr: 1 }}>file_download</Icon>}
                        >
                            {t('page.metriques.accio.exportar')}
                        </Button>
                    </Box>
                </Box>
                <Divider />
                {/* Alçada fixa: la llista de timers fa scroll dins el contenidor. */}
                <Box sx={{ height: 'calc(100vh - 270px)', minHeight: '420px', overflow: 'auto' }}>
                    <MetriquesTimers dades={dades} />
                </Box>
            </CardPage>
        </GridPage>
    );
};

export default MetriquesPage;
