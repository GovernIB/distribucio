import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'descripcio', flex: 3 },
    { field: 'cif', flex: 1 },
    { field: 'codiDir3', flex: 1 },
    { field: 'activa', flex: 0.6, type: 'boolean' },
];

export const EntitatGrid: React.FC = () => {
    const { t } = useTranslation();
    const columnsWithLabels = React.useMemo(
        () =>
            columns.map((column) => ({
                ...column,
                headerName: t(`page.entitats.grid.column.${column.field}`),
            })),
        [t]
    );
    return (
        <GridPage>
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columnsWithLabels}
                paginationActive
                toolbarType="upper"
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default EntitatGrid;
