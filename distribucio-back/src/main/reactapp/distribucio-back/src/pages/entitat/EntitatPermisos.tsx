import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import { DataGridPro, GridActionsCellItem, type GridColDef } from '@mui/x-data-grid-pro';
import {
    MuiDialog,
    useBaseAppContext,
    useConfirmDialogButtons,
    useFormContext,
    useFormDialogButtons,
    useResourceApiService,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import FormActionDialog, { type FormActionDialogApi } from '../../components/FormActionDialog';

/** Artefactes d'EntitatResource que donen servei a aquesta pantalla. */
const PERSPECTIVA_PERMISOS = 'PERMISOS';
const ACCIO_PERMIS_GUARDAR = 'PERMIS_GUARDAR';
const ACCIO_PERMIS_ESBORRAR = 'PERMIS_ESBORRAR';
/** Camp enumerat (PrincipalTipusEnumDto) del qual la graella ha de mostrar la descripció. */
const CAMP_PRINCIPAL_TIPUS = 'principalTipus';

/**
 * Camps del formulari d'un permís, els mateixos que la interfície JSP (entitatPermisForm.jsp).
 * Les etiquetes i les opcions del desplegable de tipus les aporta el backend (formClass
 * EntitatResource.FormPermis).
 *
 * El principal (tipus i nom) identifica el permís i no es pot canviar un cop creat: modificar-lo
 * no seria una modificació sinó un permís nou, i el vell hi quedaria. Per canviar-lo cal esborrar
 * el permís i tornar-lo a crear, igual que a la JSP.
 */
const EntitatPermisFormContent: React.FC = () => {
    const { data } = useFormContext();
    const esModificacio = data?.id != null;
    return (
        <Grid container spacing={2}>
            <GridFormField size={{ xs: 12, sm: 4 }} name="principalTipus" disabled={esModificacio} />
            <GridFormField size={{ xs: 12, sm: 8 }} name="principalNom" disabled={esModificacio} />
            <GridFormField size={{ xs: 12, sm: 4 }} name="administracio" />
            <GridFormField size={{ xs: 12, sm: 4.5 }} name="adminLectura" />
            <GridFormField size={{ xs: 12, sm: 3.5 }} name="usuari" />
        </Grid>
    );
};

type EntitatPermisosContentProps = {
    entitatId: any;
    /** Avisa amb l'entitat carregada perquè el diàleg en pugui compondre el títol. */
    onEntitatChange?: (entitat: any) => void;
};

/**
 * Llistat dels permisos d'una entitat amb les seves accions, l'equivalent d'entitatPermis.jsp.
 *
 * Els permisos no són cap recurs propi: arriben dins la mateixa entitat, amb la perspectiva
 * PERMISOS, i per això la graella és un DataGrid amb les files ja carregades i no pas el
 * MuiDataGrid de base-react (que sempre consulta un recurs). Com a la JSP, el llistat no es
 * pagina ni es filtra: són uns pocs permisos.
 */
const EntitatPermisosContent: React.FC<EntitatPermisosContentProps> = (props) => {
    const { entitatId, onEntitatChange } = props;
    const { t } = useTranslation();
    const { temporalMessageShow, messageDialogShow } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const formDialogButtons = useFormDialogButtons();
    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        artifacts: apiArtifacts,
        artifactAction: apiArtifactAction,
    } = useResourceApiService('entitatResource');
    const [entitat, setEntitat] = React.useState<any>();
    const [carregant, setCarregant] = React.useState<boolean>(false);
    const formDialogApiRef = React.useRef<FormActionDialogApi | undefined>(undefined);

    const refresh = React.useCallback(() => {
        if (!apiIsReady || entitatId == null) {
            return;
        }
        setCarregant(true);
        apiGetOne(entitatId, { perspectives: [PERSPECTIVA_PERMISOS] })
            .then((data: any) => {
                setEntitat(data);
                onEntitatChange?.(data);
            })
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.entitats.permis.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            )
            .finally(() => setCarregant(false));
        // onEntitatChange no és a les dependències a propòsit: si qui l'aporta hi posa una
        // funció nova a cada render, tornar-la a consultar seria un bucle infinit.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, entitatId]);
    React.useEffect(() => refresh(), [refresh]);

    // Etiquetes de les columnes i valors del desplegable de tipus. Els permisos no són cap recurs
    // propi (arriben dins l'entitat amb la perspectiva PERMISOS), de manera que aquesta graella no
    // és el MuiDataGrid de base-react i ningú li omple les capçaleres. Es prenen dels camps del
    // formulari de l'acció PERMIS_GUARDAR (EntitatResource.FormPermis), que són els mateixos
    // atributs que mostra el llistat: així el llistat i el formulari no dupliquen res i tot surt
    // del backend -- les etiquetes del `_prompt` i les descripcions dels valors de
    // PrincipalTipusEnumDto de les opcions del HAL-FORMS (distribucio-service-messages).
    const [etiquetes, setEtiquetes] = React.useState<Record<string, string>>({});
    const [tipusOpcions, setTipusOpcions] = React.useState<Record<string, string>>({});
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        apiArtifacts({}).then((artifacts) => {
            const camps =
                artifacts.find((a) => a.type === 'ACTION' && a.code === ACCIO_PERMIS_GUARDAR)
                    ?.fields ?? [];
            setEtiquetes(Object.fromEntries(camps.map((f) => [f.name, f.label ?? f.name])));
            setTipusOpcions(camps.find((f) => f.name === CAMP_PRINCIPAL_TIPUS)?.options ?? {});
        });
        // apiArtifacts no és a les dependències a propòsit: el servei el torna a crear a cada
        // render i tornar-lo a consultar seria un bucle infinit.
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady]);

    const esborrar = (permisId: any) => {
        messageDialogShow(
            t('page.entitats.permis.esborrar.title'),
            t('page.entitats.permis.esborrar.confirm'),
            confirmDialogButtons,
            { fullWidth: true, maxWidth: 'sm' }
        )
            .then(() =>
                apiArtifactAction(entitatId, {
                    code: ACCIO_PERMIS_ESBORRAR,
                    data: { permisId },
                })
                    .then(() => {
                        refresh();
                        temporalMessageShow(
                            null,
                            t('page.entitats.permis.accio.esborrarOk'),
                            'success'
                        );
                    })
                    .catch((error: any) =>
                        temporalMessageShow(
                            t('page.entitats.permis.accio.error'),
                            error?.description ?? error?.message,
                            'error'
                        )
                    )
            )
            // El diàleg de confirmació rebutja la promesa quan es tanca sense acceptar; sense
            // aquest catch la cancel·lació deixaria un error a la consola.
            .catch(() => undefined);
    };

    // Les columnes es reconstrueixen a cada renderització a posta, sense useMemo: `getActions`
    // tanca damunt les funcions d'aquesta renderització i, si es memoritzessin, es quedarien
    // amb les de la primera -- quan el servei del recurs encara s'estava carregant i
    // artifactAction rebutjava qualsevol crida amb "API is still loading". La graella té quatre
    // files i no en guarda cap estat de columnes, de manera que reconstruir-les no costa res.
    const columns: GridColDef[] = [
        {
            field: CAMP_PRINCIPAL_TIPUS,
            headerName: etiquetes.principalTipus ?? '',
            flex: 1,
            valueFormatter: (value: any) => (value != null ? (tipusOpcions[value] ?? value) : ''),
        },
        {
            field: 'principalNom',
            headerName: etiquetes.principalNom ?? '',
            flex: 2,
        },
        {
            field: 'administracio',
            headerName: etiquetes.administracio ?? '',
            type: 'boolean',
            flex: 1,
            sortable: false,
        },
        {
            field: 'adminLectura',
            headerName: etiquetes.adminLectura ?? '',
            type: 'boolean',
            flex: 1,
            sortable: false,
        },
        {
            field: 'usuari',
            headerName: etiquetes.usuari ?? '',
            type: 'boolean',
            flex: 1,
            sortable: false,
        },
        {
            field: 'accions',
            type: 'actions',
            width: 60,
            getActions: (params: any) => [
                <GridActionsCellItem
                    key="modificar"
                    label={t('page.entitats.permis.accio.modificar')}
                    icon={<Icon>edit</Icon>}
                    onClick={() => formDialogApiRef.current?.show(entitatId, params.row)}
                    showInMenu
                />,
                <GridActionsCellItem
                    key="esborrar"
                    label={t('page.entitats.permis.accio.esborrar')}
                    icon={<Icon>delete</Icon>}
                    onClick={() => esborrar(params.id)}
                    showInMenu
                />,
            ],
        },
    ];

    return (
        <>
            <Box sx={{ display: 'flex', justifyContent: 'flex-end', mb: 1 }}>
                <Button
                    variant="contained"
                    startIcon={<Icon>add</Icon>}
                    onClick={() => formDialogApiRef.current?.show(entitatId)}
                >
                    {t('page.entitats.permis.accio.nou')}
                </Button>
            </Box>
            <DataGridPro
                rows={entitat?.permisos ?? []}
                columns={columns}
                loading={carregant}
                autoHeight
                disableColumnMenu
                disableRowSelectionOnClick
                hideFooter
                localeText={{ noRowsLabel: t('page.entitats.permis.grid.buit') }}
            />
            <FormActionDialog
                resourceName="entitatResource"
                action={ACCIO_PERMIS_GUARDAR}
                title={(dades?: any) =>
                    t(
                        dades?.id != null
                            ? 'page.entitats.permis.form.titleUpdate'
                            : 'page.entitats.permis.form.titleCreate'
                    )
                }
                buttons={formDialogButtons}
                dialogComponentProps={{ fullWidth: true, maxWidth: 'sm' }}
                apiRef={formDialogApiRef}
                onSuccess={() => {
                    refresh();
                    temporalMessageShow(null, t('page.entitats.permis.accio.guardarOk'), 'success');
                }}
            >
                <EntitatPermisFormContent />
            </FormActionDialog>
        </>
    );
};

/**
 * Finestra modal amb els permisos d'una entitat, per gestionar-los sense sortir del llistat
 * (a diferència de la JSP, que hi navega i obliga a tornar enrere perdent pàgina i filtre).
 *
 * @param onTancar es crida en tancar el diàleg, per refrescar el comptador de permisos del
 *        llistat d'entitats.
 */
export const useEntitatPermisosDialog = (onTancar?: () => void) => {
    const { t } = useTranslation();
    const [entitatId, setEntitatId] = React.useState<any>();
    const [entitat, setEntitat] = React.useState<any>();
    /**
     * @param nom nom de l'entitat, que la fila del llistat ja té: així el títol del diàleg surt
     *        complet des del primer moment i no espera que se'n consultin els permisos.
     */
    const handleShow = (id: any, nom?: string) => {
        setEntitat(nom != null ? { nom } : undefined);
        setEntitatId(id);
    };
    const handleClose = () => {
        setEntitatId(undefined);
        setEntitat(undefined);
        onTancar?.();
    };
    const dialog = (
        <MuiDialog
            open={entitatId != null}
            closeCallback={handleClose}
            title={t('page.entitats.permis.title', { nom: entitat?.nom ?? '' })}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
        >
            {entitatId != null ? (
                <EntitatPermisosContent entitatId={entitatId} onEntitatChange={setEntitat} />
            ) : (
                <></>
            )}
        </MuiDialog>
    );
    return { handleShow, handleClose, dialog };
};

export default useEntitatPermisosDialog;
