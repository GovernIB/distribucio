import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid, { ToolbarButton } from '../../components/StyledMuiGrid';
import ProcedimentFilter from './ProcedimentFilter';
import useProcedimentAccions from './ProcedimentAccions';
import useProcedimentActualitzarTots from './ProcedimentActualitzarTots';

const columns: MuiDataGridColDef[] = [
    { field: 'codiSia', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'unitatOrganitzativa', flex: 3 },
    { field: 'comu', flex: 1, type: 'boolean' },
    { field: 'estat', flex: 1 },
];

const sortModel: any = [{ field: 'codiSia', sort: 'desc' }];

export const ProcedimentGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const refresh = () => apiRef.current?.refresh?.();
    const accions = useProcedimentAccions(refresh);
    const { handleShow: handleActualitzarTots, content: contentActualitzarTots } =
        useProcedimentActualitzarTots(refresh);

    return (
        <GridPage>
            <CardPage title={t('page.procediments.grid.title')}>
                <ProcedimentFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    toolbarHideCreate
                    resourceName="procedimentResource"
                    apiRef={apiRef}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    toolbarElementsWithPositions={[
                        {
                            position: 3,
                            element: (
                                <ToolbarButton
                                    title={t('page.procediments.accio.actualitzarTots.title')}
                                    icon={'sync'}
                                    onClick={handleActualitzarTots}
                                    color={'primary'}
                                >
                                    {t('page.procediments.accio.actualitzarTots.title')}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                    paginationActive
                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.procediments.accio.actualitzarTots.title')}
                    rowHideUpdateButton
                    rowHideDeleteButton
                    rowAdditionalActions={accions}
                    defaultSortModel={sortModel}
                />
                {contentActualitzarTots}
            </CardPage>
        </GridPage>
    );
};

export default ProcedimentGrid;
