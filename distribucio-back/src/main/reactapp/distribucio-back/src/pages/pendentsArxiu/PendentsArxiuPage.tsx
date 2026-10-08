import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, useBaseAppContext, useResourceApiService } from 'reactlib';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import type { Theme } from '@mui/material/styles';
import { CardPage, ContenidoData, DetailCard } from '../../components/CardData';
import { ToolbarButton } from '../../components/StyledMuiGrid';
import PendentsArxiuGrafic from './PendentsArxiuGrafic';
import { formatDataHora, Histograma } from './pendentsArxiuUtils';

const ACCIO_OBTENIR_HISTOGRAMA = 'OBTENIR_HISTOGRAMA';

/** Obté l'històric de la tasca en obrir la pantalla i cada cop que es crida `refresh`. */
const useHistograma = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('pendentsArxiuResource');
    const [histograma, setHistograma] = React.useState<Histograma>();
    const [loading, setLoading] = React.useState(false);

    const refresh = () => {
        if (!apiIsReady) {
            return;
        }
        setLoading(true);
        apiArtifactAction(undefined, { code: ACCIO_OBTENIR_HISTOGRAMA })
            .then((response: Histograma) => setHistograma(response))
            .catch((error: { description?: string; message?: string }) =>
                temporalMessageShow(
                    t('page.pendentsArxiu.errorCarrega'),
                    error?.description ?? error?.message ?? '',
                    'error'
                )
            )
            .finally(() => setLoading(false));
    };

    React.useEffect(() => {
        refresh();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady]);

    return { histograma, loading, refresh };
};

/** Fila etiqueta + valor a la mateixa línia (mateixa presentació que el monitor de sistema). */
const Fila: React.FC<{ title: string; children?: React.ReactNode }> = ({ title, children }) => (
    <ContenidoData
        size={12}
        titleSize={6}
        textSize={6}
        title={title}
        componentTitleProps={{
            fontWeight: 'bold',
            color: (theme: Theme) => (theme.palette.mode === 'dark' ? '#ffffff' : '#2b2b2b'),
        }}
    >
        {children}
    </ContenidoData>
);

/** Pantalla "Anotacions pendents d'Arxiu": dades de la tasca i històric de les darreres execucions. */
export const PendentsArxiuPage: React.FC = () => {
    const { t, i18n } = useTranslation();
    const { histograma, loading, refresh } = useHistograma();
    const entrades = histograma?.entrades;
    const darrera = entrades?.length ? entrades[entrades.length - 1] : undefined;
    const text = (clau: string) => t(`page.pendentsArxiu.${clau}`);

    return (
        <GridPage>
            <CardPage title={text('title')}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={1}>
                    <Typography variant="h6" component="h2">
                        {text('tasca')}
                    </Typography>
                    <ToolbarButton
                        title={text('refrescar')}
                        icon="refresh"
                        color="primary"
                        disabled={loading}
                        onClick={refresh}
                    />
                </Box>
                <Grid container spacing={1}>
                    <DetailCard>
                        <Grid size={4} container alignContent="flex-start" p={1}>
                            <Fila title={text('dataConsulta')}>{formatDataHora(darrera?.data, i18n.language)}</Fila>
                        </Grid>
                        <Grid size={4} container alignContent="flex-start" p={1}>
                            <Fila title={text('threads')}>{histograma?.numeroThreads}</Fila>
                            <Fila title={text('pendents')}>{darrera?.pendentArxiu}</Fila>
                        </Grid>
                        <Grid size={4} container alignContent="flex-start" p={1}>
                            <Fila title={text('cronInactivitat')}>{histograma?.expressioInactivitat || '-'}</Fila>
                        </Grid>
                    </DetailCard>
                </Grid>
                <Typography variant="h6" component="h2" mt={2} mb={1}>
                    {text('histograma')}
                </Typography>
                <PendentsArxiuGrafic entrades={entrades} />
            </CardPage>
        </GridPage>
    );
};

export default PendentsArxiuPage;
