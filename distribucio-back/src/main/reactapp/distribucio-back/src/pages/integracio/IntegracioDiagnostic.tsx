import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Collapse from '@mui/material/Collapse';
import Divider from '@mui/material/Divider';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { MuiDialog, useResourceApiService } from 'reactlib';
import { IntegracioStacktrace } from './IntegracioStacktrace';

const INTEGRACIO_RESOURCE = 'monitorIntegracioResource';
const ACTION_INTEGRACIONS_DIAGNOSTIC = 'INTEGRACIONS_DIAGNOSTIC';
const ACTION_DIAGNOSTIC = 'DIAGNOSTIC';

/** Resposta de l'acció INTEGRACIONS_DIAGNOSTIC (MonitorIntegracioResource.IntegracionsDiagnosticResult). */
interface IntegracionsDiagnosticResult {
    codis: string[];
    entitats: { id: number; description: string }[];
    entitatPerDefecteId?: number;
}

/** Resposta de l'acció DIAGNOSTIC (MonitorIntegracioResource.DiagnosticResult). */
interface DiagnosticResult {
    correcte: boolean;
    prova?: string;
    errMsg?: string;
    excepcioStacktrace?: string;
}

/** Estat de la prova d'una integració: sense resultat mentre s'executa. */
interface DiagnosticEstat {
    executant: boolean;
    resultat?: DiagnosticResult;
}

const DiagnosticFila: React.FC<{
    codi: string;
    estat?: DiagnosticEstat;
    onExecuta: (codi: string) => void;
}> = ({ codi, estat, onExecuta }) => {
    const { t } = useTranslation();
    const [tracaOberta, setTracaOberta] = React.useState(false);
    const resultat = estat?.resultat;
    const stacktrace = !estat?.executant ? resultat?.excepcioStacktrace : undefined;

    React.useEffect(() => {
        estat?.executant && setTracaOberta(false);
    }, [estat?.executant]);

    return (
        <Stack spacing={1}>
            <Stack direction="row" alignItems="center" spacing={2}>
                <Typography variant="subtitle2" sx={{ width: 180, flexShrink: 0 }}>
                    {t(`page.integracio.pipella.${codi}`)}
                </Typography>
                <Box sx={{ flex: 1, minWidth: 0 }}>
                    {estat == null || estat.executant ? (
                        <Stack direction="row" alignItems="center" spacing={1} sx={{ py: 1 }}>
                            <CircularProgress size={20} />
                            <Typography variant="body2" color="text.secondary">
                                {t('page.integracio.diagnostic.executant')}
                            </Typography>
                        </Stack>
                    ) : (
                        <Alert
                            severity={resultat?.correcte ? 'success' : 'error'}
                            action={
                                stacktrace && (
                                    <Tooltip
                                        title={t(
                                            tracaOberta
                                                ? 'page.integracio.diagnostic.amagaTraca'
                                                : 'page.integracio.diagnostic.mostraTraca'
                                        )}
                                    >
                                        <IconButton
                                            color="inherit"
                                            size="small"
                                            onClick={() => setTracaOberta((oberta) => !oberta)}
                                        >
                                            <Icon fontSize="small">
                                                {tracaOberta ? 'zoom_out' : 'zoom_in'}
                                            </Icon>
                                        </IconButton>
                                    </Tooltip>
                                )
                            }
                            sx={{ wordBreak: 'break-word' }}
                        >
                            {resultat?.correcte ? resultat?.prova : resultat?.errMsg}
                        </Alert>
                    )}
                </Box>
                <Tooltip title={t('page.integracio.diagnostic.executa')}>
                    <span>
                        <IconButton
                            onClick={() => onExecuta(codi)}
                            disabled={estat == null || estat.executant}
                        >
                            <Icon>refresh</Icon>
                        </IconButton>
                    </span>
                </Tooltip>
            </Stack>
            {stacktrace && (
                <Collapse in={tracaOberta} unmountOnExit>
                    <IntegracioStacktrace stacktrace={stacktrace} />
                </Collapse>
            )}
        </Stack>
    );
};

/**
 * Diagnòstic de les integracions (equivalent a la modal integracioDiagnostic.jsp): en obrir-se
 * executa la prova de totes les integracions a la vegada i cadascuna es pot tornar a executar
 * per separat. Si una prova falla es mostra el missatge i, a demanda, la traça de l'excepció.
 * <p>
 * Els plugins es configuren per entitat: la JSP fa servir l'entitat actual de la sessió i aquí
 * se selecciona (per defecte, la mateixa que tindria la JSP). Canviar-la torna a executar-ho tot.
 */
export const useIntegracioDiagnostic = () => {
    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction } = useResourceApiService(INTEGRACIO_RESOURCE);
    const [open, setOpen] = React.useState(false);
    const [codis, setCodis] = React.useState<string[]>([]);
    const [entitats, setEntitats] = React.useState<IntegracionsDiagnosticResult['entitats']>([]);
    const [entitatId, setEntitatId] = React.useState<number>();
    const [estats, setEstats] = React.useState<Record<string, DiagnosticEstat>>({});
    const [errorCarrega, setErrorCarrega] = React.useState<string>();
    // Número de l'última execució llançada per a cada integració. Una resposta que arriba tard
    // (s'ha tornat a executar o s'ha tancat la modal) no ha de sobreescriure l'estat actual.
    const execucionsRef = React.useRef<Record<string, number>>({});

    const setEstat = (codi: string, estat: DiagnosticEstat) =>
        setEstats((prev) => ({ ...prev, [codi]: estat }));

    const executa = (codi: string, entitat: number | undefined = entitatId) => {
        const execucio = (execucionsRef.current[codi] ?? 0) + 1;
        execucionsRef.current[codi] = execucio;
        const esVigent = () => execucionsRef.current[codi] === execucio;
        setEstat(codi, { executant: true });
        artifactAction(undefined, {
            code: ACTION_DIAGNOSTIC,
            data: { codiIntegracio: codi, entitatId: entitat },
        })
            .then((resultat: DiagnosticResult) => {
                esVigent() && setEstat(codi, { executant: false, resultat });
            })
            .catch((error: any) => {
                esVigent() &&
                    setEstat(codi, {
                        executant: false,
                        resultat: { correcte: false, errMsg: error?.message },
                    });
            });
    };

    const executaTotes = (codisIntegracio: string[], entitat: number | undefined = entitatId) =>
        codisIntegracio.forEach((codi) => executa(codi, entitat));

    const canviaEntitat = (entitat: number) => {
        setEntitatId(entitat);
        executaTotes(codis, entitat);
    };

    const invalidaExecucions = () => {
        Object.keys(execucionsRef.current).forEach((codi) => execucionsRef.current[codi]++);
    };

    const handleOpen = () => {
        if (!apiIsReady) {
            return;
        }
        setEstats({});
        setErrorCarrega(undefined);
        setOpen(true);
        artifactAction(undefined, { code: ACTION_INTEGRACIONS_DIAGNOSTIC })
            .then((result: IntegracionsDiagnosticResult) => {
                const codisIntegracio = result?.codis ?? [];
                setCodis(codisIntegracio);
                setEntitats(result?.entitats ?? []);
                setEntitatId(result?.entitatPerDefecteId);
                executaTotes(codisIntegracio, result?.entitatPerDefecteId);
            })
            .catch((error: any) => {
                setCodis([]);
                setEntitats([]);
                setEntitatId(undefined);
                setErrorCarrega(error?.message);
            });
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            invalidaExecucions();
            setOpen(false);
        }
    };

    const algunaExecutant = codis.some((codi) => estats[codi] == null || estats[codi].executant);

    const buttons = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
            componentProps: { variant: 'outlined' },
        },
    ];

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.integracio.diagnostic.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={() => handleClose()}
        >
            {errorCarrega ? (
                <Alert severity="error">{errorCarrega}</Alert>
            ) : (
                <Stack spacing={1.5} divider={<Divider />} sx={{ pt: 1 }}>
                    <Stack direction="row" spacing={2} alignItems="flex-start">
                        <TextField
                            select
                            size="small"
                            label={t('page.integracio.diagnostic.entitat')}
                            value={entitatId ?? ''}
                            onChange={(event) => canviaEntitat(Number(event.target.value))}
                            disabled={entitats.length === 0 || algunaExecutant}
                            helperText={t('page.integracio.diagnostic.entitatAjuda')}
                            sx={{ flex: 1, maxWidth: 600 }}
                        >
                            {entitats.map((entitat) => (
                                <MenuItem key={entitat.id} value={entitat.id}>
                                    {entitat.description}
                                </MenuItem>
                            ))}
                        </TextField>
                        <Button
                            variant="contained"
                            color="success"
                            startIcon={<Icon>refresh</Icon>}
                            onClick={() => executaTotes(codis)}
                            disabled={codis.length === 0 || algunaExecutant}
                            sx={{ flexShrink: 0, height: 40 }}
                        >
                            {t('page.integracio.diagnostic.executaTotes')}
                        </Button>
                    </Stack>
                    {codis.map((codi) => (
                        <DiagnosticFila
                            key={codi}
                            codi={codi}
                            estat={estats[codi]}
                            onExecuta={executa}
                        />
                    ))}
                </Stack>
            )}
        </MuiDialog>
    );

    return { apiIsReady, show: handleOpen, component: dialog };
};

export default useIntegracioDiagnostic;
