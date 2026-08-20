import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, MuiDataGridColDef } from 'reactlib';
import EntitatFilter from './EntitatFilter';
import FilterCountChip from '../../components/FilterCountChip';

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
    // El MuiFilter ja empeny el filtre al DataGridContext pare, però es manté l'estat explícit
    // (com fa RIPEA) perquè la graella el rebi per la prop `filter` i el xip pugui comptar-ne
    // els criteris aplicats.
    const [springFilter, setSpringFilter] = React.useState<string>();
    const columnsWithLabels = React.useMemo(
        () =>
            columns.map((column) => ({
                ...column,
                headerName: t(`page.entitats.grid.column.${column.field}`),
            })),
        [t]
    );
    // Posició 1: just després del títol de la barra d'eines (la posició 0 el precediria).
    const toolbarElements = React.useMemo(
        () => [
            {
                position: 1,
                element: <FilterCountChip filter={springFilter} sx={{ ml: 1.5 }} />,
            },
        ],
        [springFilter]
    );
    return (
        <GridPage>
            <EntitatFilter onSpringFilterChange={setSpringFilter} />
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                columns={columnsWithLabels}
                filter={springFilter}
                toolbarElementsWithPositions={toolbarElements}
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
