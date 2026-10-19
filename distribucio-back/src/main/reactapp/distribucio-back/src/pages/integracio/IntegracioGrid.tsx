import React from 'react';
import { useTranslation } from 'react-i18next';
import {GridPage, MuiDataGridColDef, useFilterApiRef, useMuiDataGridApiRef} from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid, { ToolbarButton } from '../../components/StyledMuiGrid';
import { formatDate } from '../../util/dateUtils';
import * as builder from '../../util/springFilterUtils';
import IntegracioFilter from './IntegracioFilter';
import { useIntegracioTabs } from './IntegracioTabs';
import useIntegracioDetail from './detail/IntegracioDetail.tsx';
import useIntegracioDiagnostic from './detail/IntegracioDiagnostic.tsx';
import { IntegracioEstat } from './IntegracioEstat';
import { GridSortModel } from '@mui/x-data-grid-pro';
import {useActions, useIntegracioActions} from "./detail/IntegracioActions.tsx";

const columns: MuiDataGridColDef[] = [
    {
        field: 'data',
        flex: 0.6,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
    },
    { field: 'numeroRegistre', flex: 0.7 },
    { field: 'descripcio', flex: 1.5 },
    { field: 'tipus', flex: 0.5 },
    { field: 'codiUsuari', flex: 0.5 },
    { field: 'entitat', flex: 0.7 },
    {
        field: 'tempsResposta',
        flex: 0.5,
        valueFormatter: (value: number) => (value != null ? `${value} ms` : ''),
    },
    {
        field: 'estat',
        flex: 0.4,
        renderCell: (params: any) => (
            <IntegracioEstat
                value={params?.row?.estat}
                excepcioMessage={params?.row?.excepcioMessage}
            />
        ),
    },
];

const sortModel: GridSortModel = [{ field: 'data', sort: 'desc' }];

export const IntegracioGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const filterApiRef = useFilterApiRef();
    const [formFilter, setFormFilter] = React.useState<string>();
    const [filterData, setFilterData] = React.useState<any>();
    // El recompte d'errors de les pestanyes es demana amb les dades del filtre APLICAT, no amb les del
    // formulari: el filtre s'aplica sol i les dades canvien a cada tecla.
    const alAplicarFiltre = React.useCallback(
        (springFilter?: string) => {
            setFormFilter(springFilter);
            setFilterData(filterApiRef.current?.getData?.());
        },
        [filterApiRef]
    );
    const { value: tab, tabElement, refreshCounts } = useIntegracioTabs(filterData);
    const { show: mostrarDetall, component: detailDialog } = useIntegracioDetail();
    const {
        apiIsReady: diagnosticIsReady,
        show: mostrarDiagnostic,
        component: diagnosticDialog,
    } = useIntegracioDiagnostic();

    const {remove} = useActions(() => apiRef.current?.refresh());
    const {actions, components} = useIntegracioActions()

    // El codi de la pestanya seleccionada s'afegeix com un filtre més (codi és un camp normal del recurs)
    const filter = React.useMemo(
        () => builder.and(builder.eq('codi', tab ? `'${tab}'` : undefined), formFilter),
        [tab, formFilter]
    );

    return (
        <GridPage>
            <ScreenFrame icon={icons.integracio} title={t('page.integracio.grid.title')}>
                <IntegracioFilter apiRef={filterApiRef} onSpringFilterChange={alAplicarFiltre} />
                {tabElement}
                <StyledMuiGrid
                    readOnly
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                    resourceName="monitorIntegracioResource"
                    apiRef={apiRef}
                    columns={columns}
                    filter={filter}
                    // toolbarShowFilterCount
                    paginationActive
                    sortModel={sortModel}
                    onRefresh={refreshCounts}
                    onRowClick={(params: any) => mostrarDetall(params?.row?.id, params?.row)}
                    rowAdditionalActions={actions}
                    toolbarElementsWithPositions={[
                        {
                            position: 2,
                            element: (
                                <ToolbarButton
                                    icon="delete"
                                    onClick={() => tab && remove(tab)}
                                    disabled={!diagnosticIsReady}
                                    color={"error"}
                                >
                                    {t('common.delete')}
                                </ToolbarButton>
                            ),
                        },
                        {
                            position: 2,
                            element: (
                                <ToolbarButton
                                    icon={icons.diagnostic}
                                    onClick={mostrarDiagnostic}
                                    disabled={!diagnosticIsReady}
                                >
                                    {t('page.integracio.diagnostic.boto')}
                                </ToolbarButton>
                            ),
                        },
                    ]}
                />
                {detailDialog}
                {diagnosticDialog}
                {components}
            </ScreenFrame>
        </GridPage>
    );
};

export default IntegracioGrid;
