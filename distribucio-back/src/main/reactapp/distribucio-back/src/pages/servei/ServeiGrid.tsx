import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid, { ToolbarButton } from '../../components/StyledMuiGrid';
import ServeiFilter from './ServeiFilter';
import useServeiAccions from './ServeiAccions';
import useServeiActualitzarTots from './ServeiActualitzarTots';

const columns: MuiDataGridColDef[] = [
    { field: 'codiSia', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'unitatOrganitzativa', flex: 3 },
    { field: 'comu', flex: 1, type: 'boolean' },
    { field: 'estat', flex: 1 },
];

const sortModel: any = [{ field: 'codiSia', sort: 'desc' }];

export const ServeiGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const refresh = () => apiRef.current?.refresh?.();
    const accions = useServeiAccions(refresh);
    const { handleShow: handleActualitzarTots, content: contentActualitzarTots } =
        useServeiActualitzarTots(refresh);

    return (
        <GridPage>
            <CardPage title={t('page.serveis.grid.title')}>
                <ServeiFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    toolbarHideCreate
                    resourceName="serveiResource"
                    apiRef={apiRef}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    toolbarElementsWithPositions={[
                        {
                            position: 3,
                            element: (
                                <ToolbarButton
                                    title={t('page.serveis.accio.actualitzarTots.title')}
                                    icon={'sync'}
                                    onClick={handleActualitzarTots}
                                    color={'primary'}
                                >
                                    {t('page.serveis.accio.actualitzarTots.title')}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                    paginationActive
                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.serveis.accio.actualitzarTots.title')}
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

export default ServeiGrid;
