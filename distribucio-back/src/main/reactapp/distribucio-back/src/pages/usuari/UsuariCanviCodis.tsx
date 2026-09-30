import React from 'react';
import { useTranslation } from 'react-i18next';
import { Alert, Box, Button, Grid, Icon, LinearProgress, Typography } from '@mui/material';
import {
    GridPage,
    MuiForm,
    useBaseAppContext,
    useConfirmDialogButtons,
    useFormApiRef,
    useResourceApiService,
} from 'reactlib';
import { CardPage } from '../../components/CardData';
import GridFormField from '../../components/GridFormField';
import UsuariCanviCodisResultat, {
    type CanviCodisLinia,
    type CanviCodisResultatData,
} from './UsuariCanviCodisResultat';

const ACCIO_CANVI_CODIS = 'CANVI_CODIS';

/** Línia ben formada "codiActual=codiNou". */
const LINIA_CANVI_CODI = /^([^\s=]+)=([^\s=]+)$/;

type Entrada = { numLinia: number; text: string };
type Progres = { fetes: number; total: number; actual?: string };

/** Error que no es pot atribuir a una línia (sessió caducada, sense permís o sense connexió) */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const isErrorGlobal = (error: any) => error?.status == null || error.status === 401 || error.status === 403;

/**
 * Canvi de codis d'usuaris: un textarea amb una entrada "codiActual=codiNou" per línia. Les línies es processen
 * d'una en una, cada una amb una crida a l'acció CANVI_CODIS, i van apareixent a la taula a mesura que s'acaben.
 */
export const UsuariCanviCodis: React.FC = () => {
    const { t } = useTranslation();
    const formApiRef = useFormApiRef();
    const { messageDialogShow, useBlocker } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('usuariResource');
    const [loading, setLoading] = React.useState(false);
    const [linies, setLinies] = React.useState<CanviCodisLinia[]>([]);
    const [progres, setProgres] = React.useState<Progres>({ fetes: 0, total: 0 });
    const [unifica, setUnifica] = React.useState(false);
    const [duradaTotal, setDuradaTotal] = React.useState<number>();
    // Si l'usuari surt de la pantalla el bucle no ha de començar cap línia més (ni tocar l'estat).
    const desmuntat = React.useRef(false);
    React.useEffect(() => {
        desmuntat.current = false;
        return () => {
            desmuntat.current = true;
        };
    }, []);

    // Sortir a mitges deixaria les línies pendents sense executar: es demana confirmació.
    useBlocker?.(() => loading && !confirm(t('page.usuariCanviCodis.info.sortir')));
    React.useEffect(() => {
        if (!loading) {
            return;
        }
        const avisa = (event: BeforeUnloadEvent) => event.preventDefault();
        window.addEventListener('beforeunload', avisa);
        return () => window.removeEventListener('beforeunload', avisa);
    }, [loading]);

    /** Processa una línia amb una crida a l'acció; retorna el resultat d'aquella línia. */
    const processaLinia = async (entrada: Entrada, unificaUsuaris: boolean, codisAntics: Set<string>) => {
        const match = LINIA_CANVI_CODI.exec(entrada.text);
        const codiAntic = match?.[1];
        const codiNou = match?.[2];
        if (codiAntic != null && codiAntic !== codiNou) {
            if (codisAntics.has(codiAntic)) {
                return { numLinia: entrada.numLinia, codiAntic, codiNou, estat: 'DUPLICAT' } as CanviCodisLinia;
            }
            codisAntics.add(codiAntic);
        }
        let result: CanviCodisResultatData | undefined;
        try {
            result = await apiArtifactAction(undefined, {
                code: ACCIO_CANVI_CODIS,
                data: { mapeig: entrada.text, unificaUsuarisExistents: unificaUsuaris },
            });
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (error: any) {
            if (isErrorGlobal(error)) {
                throw error;
            }
            // L'error d'una línia no atura les altres: queda a la taula com a error d'aquella línia.
            return {
                numLinia: entrada.numLinia,
                codiAntic,
                codiNou,
                estat: 'ERROR',
                missatge: error?.description ?? error?.message,
            } as CanviCodisLinia;
        }
        // El tractament de la resposta va fora del try perquè un error d'aquí
        // no es confongui amb un error global de l'API i no aturi les línies següents.
        const linia = result?.linies?.[0];
        if (linia == null) {
            return {
                numLinia: entrada.numLinia,
                codiAntic,
                codiNou,
                estat: 'ERROR',
                missatge: t('page.usuariCanviCodis.accio.respostaInesperada'),
            } as CanviCodisLinia;
        }
        return { ...linia, numLinia: entrada.numLinia };
    };

    const executa = async () => {
        const formApi = formApiRef.current;
        if (!apiIsReady || formApi == null) {
            return;
        }
        const data = formApi.getData();
        const unificaUsuaris = data?.unificaUsuarisExistents === true;
        const entrades: Entrada[] = String(data?.mapeig ?? '')
            .split(/\r\n|\r|\n/)
            .map((text, index) => ({ numLinia: index + 1, text: text.trim() }))
            .filter((entrada) => entrada.text !== '');
        setLoading(true);
        setLinies([]);
        setDuradaTotal(undefined);
        setUnifica(unificaUsuaris);
        setProgres({ fetes: 0, total: entrades.length });
        const t0 = Date.now();
        let processades = 0;
        try {
            if (entrades.length === 0) {
                // Sense línies no hi ha res a processar: l'acció torna l'error de validació del camp.
                await apiArtifactAction(undefined, { code: ACCIO_CANVI_CODIS, data });
                return;
            }
            const codisAntics = new Set<string>();
            for (const entrada of entrades) {
                if (desmuntat.current) {
                    return;
                }
                setProgres((previous) => ({ ...previous, actual: entrada.text }));
                const linia = await processaLinia(entrada, unificaUsuaris, codisAntics);
                if (desmuntat.current) {
                    return;
                }
                setLinies((previous) => [...previous, linia]);
                setProgres((previous) => ({ ...previous, fetes: previous.fetes + 1 }));
                processades++;
            }
            setDuradaTotal(Date.now() - t0);
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
        } catch (error: any) {
            if (!desmuntat.current) {
                // Aturada per un error global: les línies ja fetes queden a la taula amb el seu resum.
                if (processades > 0) {
                    setDuradaTotal(Date.now() - t0);
                }
                formApi.handleSubmissionErrors(error, t('page.usuariCanviCodis.accio.error'));
            }
        } finally {
            if (!desmuntat.current) {
                setLoading(false);
            }
        }
    };

    // L'acció és massiva i irreversible: es demana confirmació abans d'executar-la.
    const handleModifica = () => {
        const formApi = formApiRef.current;
        if (formApi == null) {
            return;
        }
        // Sense text no hi ha res a confirmar: l'acció torna l'error de validació del camp.
        if (!formApi.getData()?.mapeig?.trim?.()) {
            executa();
            return;
        }
        messageDialogShow(
            t('page.usuariCanviCodis.accio.confirmar.titol'),
            t('page.usuariCanviCodis.accio.confirmar.text'),
            confirmDialogButtons,
            { maxWidth: 'sm', fullWidth: true }
        ).then((value: unknown) => {
            if (value) {
                executa();
            }
        });
    };

    const percent = progres.total > 0 ? Math.round((progres.fetes / progres.total) * 100) : 0;

    return (
        <GridPage>
            <CardPage
                title={t('page.usuariCanviCodis.title')}
                cardProps={{ minHeight: 0 }}
                sx={{
                    height: '100%',
                    display: 'flex',
                    flexDirection: 'column',
                    gap: 2,
                    minHeight: 0,
                    overflow: 'auto',
                }}
            >
                <Box sx={{ flexShrink: 0 }}>
                    <MuiForm
                        resourceName="usuariResource"
                        resourceType="ACTION"
                        resourceTypeCode={ACCIO_CANVI_CODIS}
                        hiddenToolbar
                        apiRef={formApiRef}
                    >
                        <Grid container spacing={2} sx={{ pt: 1 }}>
                            <GridFormField
                                size={6}
                                name="mapeig"
                                type="textarea"
                                required
                                disabled={loading}
                                componentProps={{
                                    rows: 6,
                                    helperText: t('page.usuariCanviCodis.form.mapeig.ajuda'),
                                }}
                            />
                            <Grid
                                size={6}
                                sx={{
                                    display: 'flex',
                                    flexDirection: 'column',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    gap: 1,
                                }}
                            >
                                <GridFormField
                                    size={12}
                                    name="unificaUsuarisExistents"
                                    type="checkbox"
                                    disabled={loading}
                                    componentProps={{ helperText: t('page.usuariCanviCodis.form.unifica.ajuda') }}
                                />
                                <Grid
                                    size={12}
                                    sx={{
                                        display: 'flex',
                                        justifyContent: 'flex-end',
                                        alignItems: 'center',
                                        gap: 1,
                                        mb: 2,
                                    }}
                                >
                                    <Button
                                        variant="contained"
                                        startIcon={<Icon sx={{ mr: 0.5 }}>save</Icon>}
                                        onClick={handleModifica}
                                        disabled={loading || !apiIsReady}
                                    >
                                        {t('page.usuariCanviCodis.accio.modificar')}
                                    </Button>
                                </Grid>
                            </Grid>
                        </Grid>
                    </MuiForm>
                </Box>
                {loading && (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, flexShrink: 0 }}>
                        <Alert severity="info">{t('page.usuariCanviCodis.info.enExecucio')}</Alert>
                        <Box>
                            <Typography variant="body2">
                                {t('page.usuariCanviCodis.info.progres', {
                                    fetes: progres.fetes,
                                    total: progres.total,
                                    percent: percent,
                                })}
                                {progres.actual != null && progres.fetes < progres.total && (
                                    <Typography
                                        component="span"
                                        variant="caption"
                                        color="text.secondary"
                                        sx={{ fontStyle: 'italic' }}
                                    >
                                        &nbsp;&nbsp;{t('page.usuariCanviCodis.info.enCurs', { linia: progres.actual })}
                                    </Typography>
                                )}
                            </Typography>
                            <LinearProgress variant="determinate" value={percent} sx={{ my: 0.5, py: 0.5 }} />
                        </Box>
                    </Box>
                )}
                <UsuariCanviCodisResultat
                    linies={linies}
                    unifica={unifica}
                    finalitzat={!loading && duradaTotal != null}
                    duradaTotal={duradaTotal}
                />
            </CardPage>
        </GridPage>
    );
};

export default UsuariCanviCodis;
