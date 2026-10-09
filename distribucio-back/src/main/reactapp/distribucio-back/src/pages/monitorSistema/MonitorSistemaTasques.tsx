import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    MuiDataGridColDef,
    MuiDataGridProps,
    useBaseAppContext,
    useMuiDataGridApiRef,
    useResourceApiService,
} from 'reactlib';
import { useGridApiRef } from '@mui/x-data-grid-pro';
import Box from '@mui/material/Box';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';
import { EMPTY_SELECTION_MODEL } from '../../util/selectionModelUtils';

const ACCIO_REINICIAR = 'REINICIAR_TASCA';

// Les columnes no són ordenables: el servei retorna les tasques en memòria, ordenades per codi, i les dates són cadenes ja formatades.
const columns: MuiDataGridColDef[] = [
    { field: 'nom', flex: 3, sortable: false },
    { field: 'estat', flex: 1, sortable: false },
    { field: 'dataInici', flex: 1, sortable: false },
    { field: 'tempsExecucio', flex: 1, sortable: false },
    { field: 'properaExecucio', flex: 1, sortable: false },
];

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/** Pestanya "Tasques en segon pla" */
export const MonitorSistemaTasques: React.FC = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const apiRef = useMuiDataGridApiRef();
    const datagridApiRef = useGridApiRef();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('monitorTascaResource');

    const reiniciar = (ids: string[], clauMissatgeOk: string) => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(undefined, { code: ACCIO_REINICIAR, data: { ids } })
            .then(() => {
                apiRef.current?.refresh?.();
                datagridApiRef.current?.setRowSelectionModel?.(EMPTY_SELECTION_MODEL);
                temporalMessageShow(null, t(clauMissatgeOk), 'success');
            })
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.monitorSistema.accio.error'),
                    error?.description ?? error?.message ?? '',
                    'error'
                )
            );
    };

    const accions: AccionsFila = [
        {
            label: t('page.monitorSistema.accio.reiniciar.label'),
            icon: 'cached',
            showInMenu: false,
            action: ACCIO_REINICIAR,
            onClick: (id: string) => reiniciar([id], 'page.monitorSistema.accio.reiniciar.ok'),
        },
    ];

    const accionsMassives: MassiveActionProps[] = [
        {
            title: t('page.monitorSistema.accio.reiniciarTotes.label'),
            icon: 'cached',
            showInMenu: false,
            action: ACCIO_REINICIAR,
            onClick: (ids: string[]) => reiniciar(ids, 'page.monitorSistema.accio.reiniciarTotes.ok'),
        },
    ];

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <StyledMuiGrid
                resourceName="monitorTascaResource"
                apiRef={apiRef}
                datagridApiRef={datagridApiRef}
                columns={columns}
                toolbarShowQuickFilter
                toolbarHideCreate
                selectionActive
                rowHideUpdateButton
                rowHideDeleteButton
                rowHideDetailsButton
                rowAdditionalActions={accions}
                toolbarMassiveActions={accionsMassives}
            />
        </Box>
    );
};

export default MonitorSistemaTasques;
