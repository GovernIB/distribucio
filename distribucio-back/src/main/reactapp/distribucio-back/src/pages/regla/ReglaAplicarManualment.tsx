import React from 'react';
import { useTranslation } from 'react-i18next';
import { Alert, Box } from '@mui/material';
import { useBaseAppContext, useConfirmDialogButtons, useMuiContentDialog, useResourceApiService } from 'reactlib';
import StyledMuiGrid from '../../components/StyledMuiGrid';

const ACCIO_APLICAR_MANUALMENT = 'APLICAR_MANUALMENT';

const sortModel: any = [{ field: 'identificador', sort: 'asc' }];

/**
 * Previsualització de les anotacions que quedarien afectades: graella paginada de registres filtrada pel namedQuery APLICABLES_REGLA.
 * `INACTIVES` perquè no es descartin les anotacions de bústies inactives, que la consulta d'aplicar tampoc no filtra.
 */
// eslint-disable-next-line react-refresh/only-export-components
const ReglaAplicarPreview: React.FC<{ reglaId: any }> = ({ reglaId }) => {
    const { t } = useTranslation();
    const namedQueries = React.useMemo(() => [`APLICABLES_REGLA#${reglaId}`, 'INACTIVES'], [reglaId]);

    const columns = [
        { field: 'numero', headerName: t('page.regla.aplicar.columna.numero'), flex: 1 },
        { field: 'nom', headerName: t('page.regla.aplicar.columna.titol'), flex: 2 },
        { field: 'data', headerName: t('page.regla.aplicar.columna.data'), flex: 1, maxWidth: 160, minWidth: 160 },
        {
            field: 'unitatAdministrativaDescripcio',
            headerName: t('page.regla.aplicar.columna.unitatOrganitzativa'),
            flex: 1,
            sortable: false,
        },
    ];

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
            <Alert severity="info">{t('page.regla.aplicar.confirm')}</Alert>
            <StyledMuiGrid
                resourceName="registreResource"
                columns={columns}
                namedQueries={namedQueries}
                sortModel={sortModel}
                paginationActive
                readOnly
                toolbarHideCreate
                toolbarHideRefresh
                toolbarHide
                rowHideUpdateButton
                rowHideDeleteButton
                autoHeight
            />
        </Box>
    );
};

/**
 * "Aplicar manualment" d'una regla: mostra en un diàleg les anotacions afectades i, en confirmar, crida
 * l'acció APLICAR_MANUALMENT (assigna la regla a les anotacions perquè es processin en segon pla).
 */
export const useAplicarManualment = (refresh: () => void) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('reglaResource');
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const confirmDialogButtons = useConfirmDialogButtons();

    const aplicar = (id: any) => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(id, { code: ACCIO_APLICAR_MANUALMENT })
            .then((result: any) => {
                refresh();
                temporalMessageShow(
                    null,
                    t('page.regla.accio.aplicarManualmentOk', { count: result?.count ?? 0 }),
                    'success'
                );
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.regla.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    const handleShow = (id: any) => {
        dialogShow(
            t('page.regla.aplicar.title'),
            <ReglaAplicarPreview reglaId={id} />,
            [
                {
                    value: true,
                    text: t('page.regla.accio.aplicarManualment'),
                    icon: 'settings',
                    componentProps: { variant: 'contained' },
                },
                confirmDialogButtons[0],
            ],
            { maxWidth: 'xl', fullWidth: true }
        ).then((confirmat: any) => {
            if (confirmat) {
                aplicar(id);
            }
        });
    };

    return { handleShow, component: dialogComponent };
};

export default useAplicarManualment;
