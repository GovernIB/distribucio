import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {GridPage, MuiDataGridColDef} from "reactlib";
import React from "react";
import {CardPage} from "../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import UnitatOrganitzativaFilter from "./UnitatOrganitzativaFilter.tsx";
import {useUnitatOrganitzativaOrganigrama} from "./UnitatOrganitzativaOrganigrama.tsx";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import {useSincronitzar} from "./actions/Sincronitzar.tsx";
import {Icon, Tooltip} from "@mui/material";
import {formatDate} from "../../util/dateUtils.ts";

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 1 },
    { field: 'denominacio', flex: 3 },
    { field: 'unitatSuperior', flex: 2 },
    { field: 'unitatArrel', flex: 2 },
    { field: 'estat', flex: 1 },
];

export const UnitatOrganitzativaGrid = () => {
    const { t } = useTranslation();
    const { currentEntitat } = useDistribucioContext();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);
    const {handleOpen: handleOrgOpen, dialog: organigrama} = useUnitatOrganitzativaOrganigrama()
    const {handleOpen: handleSinc, dialog: dialogSinc} = useSincronitzar()
    return (
        <GridPage>
            <CardPage title={t('page.unitatOrganitzativa.grid.title')}>
                <UnitatOrganitzativaFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />

                <StyledMuiGrid
                    resourceName="unitatOrganitzativaResource"
                    columns={columns}
                    filter={springFilter}
                    namedQueries={namedQueries}
                    toolbarElementsWithPositions={[
                        {
                            position: 2,
                            element: <Tooltip title={<>
                                <strong>{t('page.unitatOrganitzativa.grid.dataSinc')}:</strong> {formatDate(currentEntitat?.fechaSincronizacion) || " - "}<br/>
                                <strong>{t('page.unitatOrganitzativa.grid.dataDarrerSinc')}:</strong> {formatDate(currentEntitat?.fechaActualizacion) || " - "}
                            </>}>
                                <Icon color={'primary'}>info</Icon>
                            </Tooltip>
                        },
                        {
                            position: 2,
                            element: <ToolbarButton
                                icon={'account_tree'}
                                onClick={handleOrgOpen}
                            >
                                {t('page.unitatOrganitzativa.accio.organigrama.label')}
                            </ToolbarButton>
                        },
                        {
                            position: 2,
                            element: <ToolbarButton
                                icon={'cached'}
                                onClick={handleSinc}
                            >
                                {t('page.unitatOrganitzativa.accio.sincronitzar.label')}
                            </ToolbarButton>
                        },
                    ]}
                    toolbarShowFilterCount
                    paginationActive
                    toolbarHideRefresh
                    readOnly
                />
                {organigrama}
                {dialogSinc}
            </CardPage>
        </GridPage>
    )
}