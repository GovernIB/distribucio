import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid, { ToolbarButton } from '../../components/StyledMuiGrid';
import ServeiFilter from './ServeiFilter';
import useServeiAccions from './ServeiAccions';
import useServeiActualitzarTots from './ServeiActualitzarTots';
import {ROLE_ADMIN, useDistribucioContext} from "../../components/DistribucioContext.ts";

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

    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole == ROLE_ADMIN
    const [springFilter, setSpringFilter] = React.useState<string>();

    const refresh = () => apiRef.current?.refresh?.();

    const accions = useServeiAccions(refresh);
    const { handleShow: handleActualitzarTots, content: contentActualitzarTots } =
        useServeiActualitzarTots(refresh);

    return (
        <GridPage>
            <ScreenFrame icon={icons.servei} title={t('page.serveis.grid.title')}>
                <ServeiFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
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
                                    icon={icons.sincronitza}
                                    onClick={handleActualitzarTots}
                                    color={'primary'}
                                    hidden={!isAdmin}
                                >
                                    {t('page.serveis.accio.actualitzarTots.title')}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                    paginationActive
                    rowAdditionalActions={accions}
                    defaultSortModel={sortModel}
                    readOnly
                    // ocultar acciones para admin lectura
                    initialState={{
                        columns: {
                            columnVisibilityModel: {
                                [' ']: isAdmin,
                            }
                        }
                    }}
                />
                {contentActualitzarTots}
            </ScreenFrame>
        </GridPage>
    );
};

export default ServeiGrid;
