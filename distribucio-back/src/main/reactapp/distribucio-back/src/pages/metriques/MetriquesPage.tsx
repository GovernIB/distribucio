import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, useBaseAppContext, useResourceApiService } from 'reactlib';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import { SCREEN_GRID_MIN_HEIGHT } from '../../util/layout';
import { ToolbarButton } from '../../components/StyledMuiGrid';
import MetriquesTimers from './MetriquesTimers';
import { esMetriquesValides, MetriquesData } from './metriquesUtils';
import { Divider } from '@mui/material';

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
            <ScreenFrame
                icon={icons.metriques}
                title={t('page.metriques.title')}
                actions={
                    <>
                        <input ref={fitxerRef} type="file" accept=".json,application/json" hidden onChange={importar} />
                        <ToolbarButton
                            title={t('page.metriques.accio.importar')}
                            icon={icons.carrega}
                            color="primary"
                            onClick={() => fitxerRef.current?.click()}
                        >
                            {t('page.metriques.accio.importar')}
                        </ToolbarButton>
                        <ToolbarButton
                            title={t('page.metriques.accio.exportar')}
                            icon={icons.descarrega}
                            color="primary"
                            disabled={!dades}
                            onClick={exportar}
                        >
                            {t('page.metriques.accio.exportar')}
                        </ToolbarButton>
                    </>
                }
            >
                <Typography variant="h6" component="h2" sx={{ flexShrink: 0 }}>
                    {t('page.metriques.timers')}
                </Typography>
                <Divider sx={{ flexShrink: 0 }} />
                <Box sx={{ flex: '1 1 0', minHeight: SCREEN_GRID_MIN_HEIGHT + 'px', overflow: 'auto' }}>
                    <MetriquesTimers dades={dades} />
                </Box>
            </ScreenFrame>
        </GridPage>
    );
};

export default MetriquesPage;
