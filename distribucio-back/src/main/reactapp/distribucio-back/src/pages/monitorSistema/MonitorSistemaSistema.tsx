import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService } from 'reactlib';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import LinearProgress from '@mui/material/LinearProgress';
import Typography from '@mui/material/Typography';
import { ContenidoData, DetailCard, DetailCardContent } from '../../components/CardData';
import { ToolbarButton } from '../../components/StyledMuiGrid';

const ACCIO_INFORMACIO_SISTEMA = 'INFORMACIO_SISTEMA';

/** Percentatge d'ocupació a partir del qual la barra passa a avís i a error. */
const LLINDAR_AVIS = 80;
const LLINDAR_ERROR = 90;

type MemoriaUs = { usada?: number; total?: number; usadaFormatada?: string; totalFormatada?: string };

/** Resultat de l'acció (MonitorFilResource.InformacioSistema al backend). */
type InformacioSistema = {
    sistemaOperatiu?: string;
    arquitectura?: string;
    processadors?: number;
    versioJboss?: string;
    servidorAplicacions?: string;
    jvm?: string;
    versioJdk?: string;
    dataArrencada?: string;
    tempsFuncionant?: string;
    memoriaJvm?: MemoriaUs;
    memoriaMaxima?: string;
    memoriaFisica?: MemoriaUs;
    filsActius?: number;
    filsPic?: number;
    filsDaemon?: number;
    filsDeadlock?: number;
    gcExecucions?: number;
    gcTemps?: number;
    nuclis?: number;
    carregaMitjana?: string;
    carregaCpuSistema?: string;
    carregaCpuProces?: string;
    discos?: {
        nom?: string;
        usat?: number;
        total?: number;
        usatFormatat?: string;
        totalFormatat?: string;
    }[];
};

const useInformacioSistema = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('monitorFilResource');
    const [informacio, setInformacio] = React.useState<InformacioSistema>();
    const [loading, setLoading] = React.useState(false);

    const refresh = () => {
        if (!apiIsReady) {
            return;
        }
        setLoading(true);
        apiArtifactAction(undefined, { code: ACCIO_INFORMACIO_SISTEMA })
            .then((response: InformacioSistema) => setInformacio(response))
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.monitorSistema.accio.error'),
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

    return { informacio, loading, refresh };
};

/** Fila d'ocupació (memòria o disc): etiqueta, barra i detall a la mateixa línia. */
const UsageBar: React.FC<{ label?: string; used?: number; total?: number; detail: string }> = ({
    label,
    used,
    total,
    detail,
}) => {
    const percentatge = total ? Math.min(100, Math.round(((used ?? 0) * 100) / total)) : 0;
    return (
        <>
            <Grid size={2} sx={{ fontWeight: 'bold' }}>
                {label}
            </Grid>
            <Grid size={6}>
                <LinearProgress
                    variant="determinate"
                    value={percentatge}
                    color={percentatge >= LLINDAR_ERROR ? 'error' : percentatge >= LLINDAR_AVIS ? 'warning' : 'success'}
                    sx={{ width: '100%', height: 15, borderRadius: '4px' }}
                />
            </Grid>
            <Grid size={1} />
            <Grid size={3}>
                <Typography variant="inherit" color="textSecondary">
                    {detail} ({percentatge} %)
                </Typography>
            </Grid>
        </>
    );
};

const detallMemoria = (memoria?: MemoriaUs) => `${memoria?.usadaFormatada ?? '-'} / ${memoria?.totalFormatada ?? '-'}`;

/** Camp de la fitxa: mateixa presentació (títol en negreta) per a tots. */
const Camp: React.FC<React.ComponentProps<typeof DetailCardContent>> = (props) => (
    <DetailCardContent componentTitleProps={{ fontWeight: 'bold' }} {...props} />
);

/** Fila etiqueta + valor a la mateixa línia */
const Fila: React.FC<{ title: string; children?: React.ReactNode }> = ({ title, children }) => (
    <ContenidoData
        size={6}
        titleSize={6}
        textSize={6}
        title={title}
        componentTitleProps={{
            fontWeight: 'bold',
            color: (theme: any) => (theme.palette.mode === 'dark' ? '#ffffff' : '#2b2b2b'),
        }}
    >
        {children}
    </ContenidoData>
);

/** Pestanya "Sistema": informació general, memòria, fils, CPU i discs. */
export const MonitorSistemaSistema: React.FC = () => {
    const { t } = useTranslation();
    const { informacio, loading, refresh } = useInformacioSistema();
    const detall = (clau: string) => t(`page.monitorSistema.detail.${clau}`);

    return (
        <Box>
            <Box display="flex" justifyContent="end" mb={1}>
                <ToolbarButton
                    title={t('page.monitorSistema.accio.refrescar')}
                    icon="refresh"
                    color="primary"
                    disabled={loading}
                    onClick={refresh}
                />
            </Box>
            <Grid container spacing={1}>
                <DetailCard>
                    <Camp size={4} sx={{ mt: 0 }} title={detall('sistemaOperatiu')}>
                        {informacio?.sistemaOperatiu}
                    </Camp>
                    <Camp size={4} sx={{ mt: 0 }} title={detall('arquitectura')}>
                        {informacio?.arquitectura}
                    </Camp>
                    <Camp size={4} sx={{ mt: 0 }} title={detall('processadors')}>
                        {informacio?.processadors}
                    </Camp>
                    <Camp size={4} title={detall('versioJboss')}>
                        {informacio?.versioJboss}
                    </Camp>
                    <Camp size={4} title={detall('servidorAplicacions')}>
                        {informacio?.servidorAplicacions}
                    </Camp>
                    <Camp size={4} title={detall('tempsFuncionant')}>
                        {informacio?.tempsFuncionant}
                    </Camp>
                    <Camp size={4} title={detall('jvm')}>
                        {informacio?.jvm}
                    </Camp>
                    <Camp size={4} title={detall('versioJdk')}>
                        {informacio?.versioJdk}
                    </Camp>
                    <Camp size={4} title={detall('dataArrencada')}>
                        {informacio?.dataArrencada}
                    </Camp>

                    <Camp title={detall('memoriaJvm')} isObject>
                        <Grid container alignItems="center">
                            <Fila title={detall('filsActius')}>{informacio?.filsActius}</Fila>
                            <Fila title={detall('gcExecucions')}>{informacio?.gcExecucions}</Fila>
                            <Fila title={detall('filsPic')}>{informacio?.filsPic}</Fila>
                            <Fila title={detall('gcTemps')}>{informacio?.gcTemps}</Fila>
                            <Fila title={detall('filsDaemon')}>{informacio?.filsDaemon}</Fila>
                            <Fila title={detall('filsDeadlock')}>{informacio?.filsDeadlock}</Fila>
                            <Fila title={detall('memoriaMaxima')}>
                                {informacio?.memoriaMaxima ?? (informacio ? detall('senseLimit') : undefined)}
                            </Fila>
                            <Fila title={detall('memoriaFisica')}>
                                {informacio?.memoriaFisica ? detallMemoria(informacio.memoriaFisica) : undefined}
                            </Fila>
                            <UsageBar
                                used={informacio?.memoriaJvm?.usada}
                                total={informacio?.memoriaJvm?.total}
                                detail={detallMemoria(informacio?.memoriaJvm)}
                            />
                        </Grid>
                    </Camp>

                    <Camp title={detall('discos')} isObject>
                        <Grid container alignItems="center">
                            <Fila title={detall('nuclis')}>{informacio?.nuclis}</Fila>
                            <Fila title={detall('carregaMitjana')}>{informacio?.carregaMitjana}</Fila>
                            <Fila title={detall('carregaCpuSistema')}>{informacio?.carregaCpuSistema}</Fila>
                            <Fila title={detall('carregaCpuProces')}>{informacio?.carregaCpuProces}</Fila>
                            {(informacio?.discos ?? []).map((disc, index) => (
                                <UsageBar
                                    key={disc.nom ?? index}
                                    label={disc.nom}
                                    used={disc.usat}
                                    total={disc.total}
                                    detail={`${disc.usatFormatat ?? '-'} / ${disc.totalFormatat ?? '-'}`}
                                />
                            ))}
                        </Grid>
                    </Camp>
                </DetailCard>
            </Grid>
        </Box>
    );
};

export default MonitorSistemaSistema;
