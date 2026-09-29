import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridSortModel } from '@mui/x-data-grid-pro';
import {
    GridPage,
    MuiDataGridColDef,
    MuiDetail,
    MuiDialog,
    useCloseDialogButtons,
    useDetailContext,
} from 'reactlib';
import { CardPage, DetailCard, DetailField } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { formatDate } from '../../util/dateUtils';
import IntegracioStacktrace from '../integracio/IntegracioStacktrace';

const RESOURCE_NAME = 'excepcioLogResource';

const formatDataHora = (value?: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : '');

// Les mateixes columnes que la pantalla JSP (excepcio.jsp)
const columns: MuiDataGridColDef[] = [
    {
        field: 'data',
        flex: 0.5,
        valueFormatter: (value: string) => formatDataHora(value),
    },
    { field: 'tipus', flex: 1 },
    { field: 'objectId', flex: 0.4 },
    { field: 'objectClass', flex: 0.8 },
    { field: 'message', flex: 1.5 },
];

const sortModel: GridSortModel = [{ field: 'data', sort: 'desc' }];

const ExcepcioStacktrace: React.FC = () => {
    const { data } = useDetailContext();
    return data?.stacktrace ? <IntegracioStacktrace stacktrace={data.stacktrace} height={400} /> : null;
};

/**
 * Detall d'una excepció. Es consulta per id (no es reaprofita la fila de la graella) perquè
 * mostri sempre el registre desat, amb la traça sencera. Les etiquetes dels camps vénen del
 * backend (_prompt de ExcepcioLogResource).
 */
const useExcepcioDetail = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const [open, setOpen] = React.useState(false);
    const [id, setId] = React.useState<any>();

    const handleOpen = (rowId: any) => {
        setId(rowId);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.excepcio.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            {open && id != null && (
                <MuiDetail
                    id={id}
                    resourceName={RESOURCE_NAME}
                    hiddenToolbar
                    componentProps={{ sx: { mt: 0 } }}
                >
                    <DetailCard sx={{ mb: 2 }}>
                        <DetailField
                            name="data"
                            size={6}
                            inline
                            formatterValue={(value: string) => formatDataHora(value)}
                        />
                        <DetailField name="entitatCodi" size={6} inline hiddenIfEmpty />
                        <DetailField name="tipus" inline />
                        <DetailField name="message" inline hiddenIfEmpty />
                        <DetailField name="origen" inline hiddenIfEmpty />
                        <DetailField name="uri" inline hiddenIfEmpty />
                        <DetailField name="objectId" size={6} inline hiddenIfEmpty />
                        <DetailField name="objectClass" size={6} inline hiddenIfEmpty />
                        <DetailField name="param1" size={6} inline hiddenIfEmpty />
                        <DetailField name="param2" size={6} inline hiddenIfEmpty />
                    </DetailCard>
                    <ExcepcioStacktrace />
                </MuiDetail>
            )}
        </MuiDialog>
    );

    return { show: handleOpen, component: dialog };
};

export const ExcepcioGrid: React.FC = () => {
    const { t } = useTranslation();
    const { show: mostrarDetall, component: detailDialog } = useExcepcioDetail();

    return (
        <GridPage>
            <CardPage title={t('page.excepcio.grid.title')}>
                <StyledMuiGrid
                    readOnly
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                    toolbarShowQuickFilter
                    resourceName={RESOURCE_NAME}
                    columns={columns}
                    paginationActive
                    sortModel={sortModel}
                    onRowClick={(params: any) => mostrarDetall(params?.row?.id)}
                    rowAdditionalActions={[
                        {
                            label: t('page.excepcio.detail.boto'),
                            icon: 'info',
                            showInMenu: false,
                            onClick: (id: any) => mostrarDetall(id),
                        },
                    ]}
                />
                {detailDialog}
            </CardPage>
        </GridPage>
    );
};

export default ExcepcioGrid;
