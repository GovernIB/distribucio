import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Icon, Tooltip } from '@mui/material';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { formatDate } from '../../util/dateUtils';
import ContingutFilter from './ContingutFilter';
import useContingutAccions from './ContingutAccions';
import useContingutHistorialDialog from './actions/ContingutHistorialDialog';
import useContingutDetailDialog from './actions/ContingutDetailDialog';

const ContingutNomCell: React.FC<{ params: any }> = ({ params }) => {
    const { t } = useTranslation();
    const row = params?.row;

    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, width: '100%', minWidth: 0 }}>
            <Box
                sx={{ display: 'flex', alignItems: 'center', gap: 0.5, minWidth: 0, flexShrink: 1 }}
            >
                <Tooltip
                    title={
                        row?.tipus === 'REGISTRE'
                            ? t('page.contingut.grid.icona.registre')
                            : t('page.contingut.grid.icona.bustia')
                    }
                >
                    <Icon fontSize="small" sx={{ flexShrink: 0 }}>
                        {row?.tipus === 'REGISTRE' ? 'menu_book' : 'inbox'}
                    </Icon>
                </Tooltip>
                <Box
                    component="span"
                    title={row?.nom}
                    sx={{
                        minWidth: 0,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                    }}
                >
                    {row?.nom}
                </Box>
            </Box>
            {(!!row?.esborrat || row?.alerta) && (
                <Box
                    sx={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 0.5,
                        flexShrink: 0,
                        ml: 'auto',
                    }}
                >
                    {!!row?.esborrat && (
                        <Tooltip title={t('page.contingut.grid.icona.esborrat')}>
                            <Icon fontSize="small" sx={{ fontSize: '18px' }}>
                                delete
                            </Icon>
                        </Tooltip>
                    )}
                    {row?.alerta && (
                        <Tooltip title={t('page.contingut.grid.icona.alerta')}>
                            <Icon fontSize="small" color="warning" sx={{ fontSize: '18px' }}>
                                warning
                            </Icon>
                        </Tooltip>
                    )}
                </Box>
            )}
        </Box>
    );
};

const ContingutPathCell: React.FC<{ params: any }> = ({ params }) => {
    const { t } = useTranslation();
    const path: string[] = params?.row?.path ?? [];

    return (
        <Box sx={{ whiteSpace: 'normal', lineHeight: 1.4 }}>
            {path.map((nom, index) => (
                <React.Fragment key={index}>
                    {index > 0 && ' / '}
                    <Tooltip
                        title={
                            index === 0
                                ? t('page.contingut.grid.icona.unitat')
                                : t('page.contingut.grid.icona.bustia')
                        }
                    >
                        <Icon
                            fontSize="small"
                            sx={{ fontSize: '18px', verticalAlign: 'text-bottom', mr: 0.5 }}
                        >
                            {index === 0 ? 'account_tree' : 'inbox'}
                        </Icon>
                    </Tooltip>
                    {nom}
                </React.Fragment>
            ))}
        </Box>
    );
};

const columns: MuiDataGridColDef[] = [
    { field: 'nom', flex: 3, renderCell: (params: any) => <ContingutNomCell params={params} /> },
    { field: 'createdByFullName', flex: 1.5 },
    {
        field: 'createdDate',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
    },
    {
        field: 'path',
        flex: 3,
        sortable: false,
        filterable: false,
        renderCell: (params: any) => <ContingutPathCell params={params} />,
    },
];

export const ContingutGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const { show: mostrarHistorial, component: historialDialog } = useContingutHistorialDialog();
    const { show: mostrarDetall, component: detailDialog } = useContingutDetailDialog();
    const accions = useContingutAccions(mostrarDetall, mostrarHistorial);

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.contingut.grid.title')}>
                <ContingutFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    toolbarHideCreate
                    resourceName="contingutResource"
                    apiRef={apiRef}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    paginationActive
                    rowHideUpdateButton
                    rowHideDeleteButton
                    rowAdditionalActions={accions}
                    sortModel={[{ field: 'createdDate', sort: 'desc' }]}
                />
                {historialDialog}
                {detailDialog}
            </CardPage>
        </GridPage>
    );
};

export default ContingutGrid;
