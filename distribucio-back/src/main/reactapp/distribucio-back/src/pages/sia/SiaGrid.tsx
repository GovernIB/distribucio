import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid, { ToolbarButton } from '../../components/StyledMuiGrid';
import SiaFilter from './SiaFilter.tsx';
import useSiaAccions from './SiaAccions.tsx';
import {ROLE_ADMIN, useDistribucioContext} from "../../components/DistribucioContext.ts";
import useServeiActualitzarTots from "../servei/ServeiActualitzarTots.tsx";
import useProcedimentActualitzarTots from "../procediment/ProcedimentActualitzarTots.tsx";

const columns: MuiDataGridColDef[] = [
    { field: 'codiSia', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'unitatOrganitzativa', flex: 3 },
    { field: 'comu', flex: 1, type: 'boolean' },
    { field: 'estat', flex: 1 },
    { field: 'tipus', flex: 1 },
];

const sortModel: any = [{ field: 'codiSia', sort: 'desc' }];

export const SiaGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole == ROLE_ADMIN
    const [springFilter, setSpringFilter] = React.useState<string>();

    const refresh = () => apiRef.current?.refresh?.();

    const accions = useSiaAccions(refresh);
    const { handleShow: handleActualitzarTotsP, content: contentActualitzarTotsP } = useProcedimentActualitzarTots(refresh);
    const { handleShow: handleActualitzarTotsS, content: contentActualitzarTotsS } = useServeiActualitzarTots(refresh);

    return (
        <GridPage>
            <ScreenFrame icon={icons.sia} title={t('page.sia.title')}>
                <SiaFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    resourceName="siaResource"
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
                                    icon={icons.sincronitza}
                                    onClick={handleActualitzarTotsP}
                                    color={'primary'}
                                    hidden={!isAdmin}
                                >
                                    {t('page.procediments.accio.actualitzarTots.title')}
                                </ToolbarButton>
                            ),
                        },
                        {
                            position: 3,
                            element: (
                                <ToolbarButton
                                    title={t('page.serveis.accio.actualitzarTots.title')}
                                    icon={icons.sincronitza}
                                    onClick={handleActualitzarTotsS}
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
                {contentActualitzarTotsP}
                {contentActualitzarTotsS}
            </ScreenFrame>
        </GridPage>
    );
};

export default SiaGrid;
