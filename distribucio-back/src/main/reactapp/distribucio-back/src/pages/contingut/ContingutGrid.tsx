import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { formatDate } from '../../util/dateUtils';
import ContingutFilter from './ContingutFilter';
import useContingutAccions from './ContingutAccions';
import useContingutHistorialDialog from './actions/ContingutHistorialDialog';

// TODO: accions de fila "Detalls"/"Recuperar"/"Esborrar" pendents -- veure
// ContingutAdminController legacy. 
const columns: MuiDataGridColDef[] = [
    { field: 'nom', flex: 3 },
    { field: 'createdByFullName', flex: 1.5 },
    {
        field: 'createdDate',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm') : ''),
    },
    {
        field: 'path',
        flex: 3,
        sortable: false,
        filterable: false,
        renderCell: (params: any) => (params.row.path ?? []).join(' / '),
    },
];

export const ContingutGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const { show: mostrarHistorial, component: historialDialog } = useContingutHistorialDialog();
    const accions = useContingutAccions(mostrarHistorial);

    return (
        <GridPage>
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
            </CardPage>
        </GridPage>
    );
};

export default ContingutGrid;
