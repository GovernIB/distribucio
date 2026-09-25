import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    Alert,
    Box,
    Button,
    CircularProgress,
    Grid,
    Icon,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';
import { MuiForm, useFormApiRef, useFormContext, useMuiContentDialog, useResourceApiService } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import GridUnitatField from '../../components/GridUnitatField';
import * as builder from '../../util/springFilterUtils';

const ACCIO_SIMULAR = 'SIMULAR';

/** Com al simulador antic, només s'ofereixen les bústies actives. */
const filtreBustiesActives = builder.eq('activa', true);

type SimulacioAccio = {
    accio: string;
    param?: string;
    reglaNom?: string;
};

// eslint-disable-next-line react-refresh/only-export-components
const SimulaFila: React.FC<{ onSimula: () => void; loading: boolean }> = ({ onSimula, loading }) => {
    const { t } = useTranslation();
    const { data } = useFormContext();

    return (
        <Grid size={12} sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2">
                <b>{t('page.regla.simulador.avaluarTotes')}: </b>
                {data?.avaluarTotes ? t('common.boolean.true') : t('common.boolean.false')}
            </Typography>
            <Button
                variant="contained"
                startIcon={<Icon sx={{ mr: 0.5 }}>settings</Icon>}
                onClick={onSimula}
                disabled={loading}
            >
                {t('page.regla.accio.simular')}
            </Button>
        </Grid>
    );
};

/** Taula de resultat: una fila per acció que faria la simulació. */
// eslint-disable-next-line react-refresh/only-export-components
const SimulacioResultat: React.FC<{ accions: SimulacioAccio[] }> = ({ accions }) => {
    const { t } = useTranslation();

    // Sense accions (bústia triada i cap regla aplicable): s'avisa perquè l'usuari distingeixi
    // "no hi ha regles" de "no s'ha simulat".
    if (accions.length === 0) {
        return (
            <Alert severity="info" sx={{ mt: 2 }}>
                {t('page.regla.simulador.senseResultat')}
            </Alert>
        );
    }

    return (
        <Table size="small" sx={{ mt: 2, '& tbody tr:nth-of-type(odd)': { backgroundColor: 'action.hover' } }}>
            <TableHead>
                <TableRow>
                    <TableCell>{t('page.regla.simulador.columna.ordre')}</TableCell>
                    <TableCell>{t('page.regla.simulador.columna.descripcio')}</TableCell>
                    <TableCell>{t('page.regla.simulador.columna.regla')}</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {accions.map((accio, index) => (
                    <TableRow key={index}>
                        <TableCell>{index + 1}</TableCell>
                        <TableCell>
                            {t(`page.regla.simulador.accio.${accio.accio}`)}
                            {accio.param != null && ` "${accio.param}"`}
                        </TableCell>
                        <TableCell>{accio.reglaNom}</TableCell>
                    </TableRow>
                ))}
            </TableBody>
        </Table>
    );
};

// eslint-disable-next-line react-refresh/only-export-components
const ReglaSimuladorContingut: React.FC = () => {
    const { t } = useTranslation();
    const formApiRef = useFormApiRef();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('reglaResource');
    const [loading, setLoading] = React.useState(false);
    const [resultat, setResultat] = React.useState<SimulacioAccio[]>();

    const simula = () => {
        const formApi = formApiRef.current;
        if (!apiIsReady || formApi == null) {
            return;
        }
        setLoading(true);
        setResultat(undefined);
        apiArtifactAction(undefined, { code: ACCIO_SIMULAR, data: formApi.getData() })
            .then((result: any) => setResultat(Array.isArray(result) ? result : []))
            .catch((error: any) => formApi.handleSubmissionErrors(error, t('page.regla.accio.error')))
            .finally(() => setLoading(false));
    };

    return (
        <>
            <MuiForm
                resourceName="reglaResource"
                resourceType="ACTION"
                resourceTypeCode={ACCIO_SIMULAR}
                initOnChangeRequest
                hiddenToolbar
                apiRef={formApiRef}
            >
                <Grid container spacing={2} sx={{ pt: 1 }}>
                    <GridUnitatField size={5} name="unitat" required />
                    <GridFormField size={5} name="bustia" filter={filtreBustiesActives} />
                    <GridFormField size={2} name="presencial" />
                    <GridFormField size={3} name="procedimentCodi" />
                    <GridFormField size={3} name="serveiCodi" />
                    <GridFormField size={3} name="tramitCodi" />
                    <GridFormField size={3} name="assumpteCodi" />
                    <SimulaFila onSimula={simula} loading={loading} />
                </Grid>
            </MuiForm>
            {loading && (
                <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
                    <CircularProgress size={40} />
                </Box>
            )}
            {resultat != null && <SimulacioResultat accions={resultat} />}
        </>
    );
};

/**
 * Simulador de regles: diàleg amb el formulari d'una anotació hipotètica i, a sota, les accions que faria la simulació.
 * El formulari es manté per poder tornar a simular sense tancar.
 */
export const useReglaSimulador = () => {
    const { t } = useTranslation();
    const { isReady: apiIsReady, artifacts: apiArtifacts } = useResourceApiService('reglaResource');
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const [disponible, setDisponible] = React.useState(false);

    React.useEffect(() => {
        if (apiIsReady) {
            apiArtifacts({ includeLinks: true }).then((artifacts: any[]) =>
                setDisponible(artifacts.some((a) => a.type === 'ACTION' && a.code === ACCIO_SIMULAR))
            );
        }
    }, [apiIsReady]);

    const handleShow = () => {
        dialogShow(
            t('page.regla.simulador.title'),
            <ReglaSimuladorContingut />,
            [{ value: false, text: t('common.close'), componentProps: { variant: 'outlined' } }],
            { maxWidth: 'lg', fullWidth: true }
        ).catch(() => {});
    };

    return { handleShow, disponible, component: dialogComponent };
};

export default useReglaSimulador;
