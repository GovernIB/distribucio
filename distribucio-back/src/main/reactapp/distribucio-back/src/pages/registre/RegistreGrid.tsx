import {useTranslation} from "react-i18next";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import RegistreFilter from "./RegistreFilter.tsx";
import React from "react";
import { Icon } from "@mui/material";
import {useCommentsColumn} from "../../components/CommentsColumn.tsx";
import {RegistreEstat} from "../../components/RegistreEstat.tsx";
import {useRegistreActions, useRegistreMassiveActions} from "./detail/RegistreActions.tsx";

const RegistreAvisos = ({entity}:any) => {
    const { t } = useTranslation();
    return <>
        {entity.sobreescriure && <Icon title={t('page.registre.avisos.sobreescriure')}>history</Icon>}
        {entity.enviatPerEmail && <Icon title={t('page.registre.avisos.enviatPerEmail')}>mail</Icon>} {/*enviamentsPerEmail*/}
        {entity.documentacioFisicaCodi == '1' && <Icon title={t('page.registre.avisos.documentacio._1')} sx={{color: '#D9534F'}}>inventory_2</Icon>}
        {entity.documentacioFisicaCodi == '2' && <>
            <Icon title={t('page.registre.avisos.documentacio._2')} sx={{color: '#F0AD4E'}}>code_xml</Icon>
            <Icon title={t('page.registre.avisos.documentacio._2')} sx={{color: '#F0AD4E'}}>inventory_2</Icon>
        </>}
        {entity.documentacioFisicaCodi == '3' && <Icon title={t('page.registre.avisos.documentacio._3')} sx={{color: '#5CB85C'}}>code_xml</Icon>}
        {entity.annexosEstatEsborrany > 0 && <Icon title={t('page.registre.avisos.estatEsborrany')} color={'warning'}>error</Icon>}
        {entity.alerta && <Icon title={t('page.registre.avisos.alerta')} color={'warning'}>note_stack</Icon>}
        {entity.procesError != null && (
            entity.procesError == 'ARXIU_PENDENT' ? <Icon title={t('page.registre.avisos.procesError.ARXIU_PENDENT')} color={'error'}>warning</Icon>
            :entity.procesError == 'REGLA_PENDENT' ? <Icon title={t('page.registre.avisos.procesError.REGLA_PENDENT')} color={'error'}>warning</Icon>
            :entity.procesError == 'BACK_PENDENT' ? <Icon title={t('page.registre.avisos.procesError.BACK_PENDENT')} color={'error'}>warning</Icon>
            :entity.procesError == 'BACK_ERROR' ? <Icon title={t('page.registre.avisos.procesError.BACK_ERROR')} color={'error'}>warning</Icon>
            : <Icon title={t('page.registre.avisos.procesError.default')} color={'error'}>warning</Icon>
        )}
        {entity.pendentExecucioMassiva && <Icon title={t('page.registre.avisos.pendentExecucioMassiva')} color={'warning'}>schedule</Icon>}
    </>
}

const RegistreRemitent = ({entity}:any) => {
    const { t } = useTranslation();
    return entity.darrerMoviment != null
        ?((entity.darrerMovimentResource.bustiaOrigen != null
            && entity.darrerMovimentResource.unitatOrganitzativaOrigen != null)
                ?<>/<Icon title={entity.darrerMovimentResource.unitatOrganitzativaOrigen.description} >account_tree</Icon>
                    /<Icon title={entity.darrerMovimentResource.bustiaOrigen.description} >inbox</Icon>
                    {entity.darrerMovimentResource.remitent.description}</>
                :<><Icon>home</Icon>{entity.oficinaDescripcio} ({entity.darrerMovimentResource.remitent.description})</>
        )
        :<Icon title={t('page.registre.remitentEmpty')} >block</Icon>
}

const columns = (t:any) => [
    { field: 'numero', flex: 2 },
    { field: 'extracte', flex: 2 },
    { field: 'numeroOrigen', flex: 1 },
    { field: 'oficinaDescripcio', headerName: t('page.registre.grid.remitent'), flex: 1.5,
        renderCell: (params:any) => <RegistreRemitent entity={params.row} />
    },
    // { field: 'createdDate', flex: 1 },
    // L'amplada mínima és la que necessita la part "dd/mm/aaaa" (76px) més el padding de la cel·la
    // (2 x 10px): per sota, la data quedava tallada. L'hora passa a la línia de sota.
    { field: 'data', flex: 1, minWidth: 100 },
    { field: 'procesEstat', flex: 2,
        renderCell: (params:any) => <RegistreEstat entity={params.row}>{params.formattedValue}</RegistreEstat>
    },
    { field: 'avisos', headerName: t('page.registre.grid.avisos'), flex: 1, sortable: false,
        renderCell: (params:any) => <RegistreAvisos entity={params.row}/>
    },
    { field: 'pare', flex: 3,
        renderCell: (params:any) => <>
            / <Icon>account_tree</Icon> {params.row.unitatAdministrativaDescripcio} / <Icon>inbox</Icon> {params.formattedValue}
        </>
    },
    { field: 'interessatsString', flex: 4, sortable: false },
]
const perspectives = ['DARRER_MOVIMENT', 'COMMENT_NUM']
const sortModel:any = [{ field: 'data', sort: 'desc' }]
export const RegistreGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const { column: commentsColumn, component: dialogComponent } = useCommentsColumn();

    const additionalColumns = [
        ...columns(t),
        commentsColumn,
    ]

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const {actions, components} = useRegistreActions(refresh);
    const {actions: massiveActions, components: massiveComponents} = useRegistreMassiveActions();

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.registre.title')}>
                <RegistreFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />

                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="registreResource"
                    columns={additionalColumns}
                    filter={springFilter}
                    perspectives={perspectives}
                    namedQueries={namedQueries}
                    sortModel={sortModel}

                    // filterCount={(num) => num + (namedQueries.length || 0)}
                    // toolbarShowFilterCount

                    rowAdditionalActions={actions}
                    // reactlib crea la columna del menú d'accions amb 100px fixos i només hi va el
                    // botó "⋮": s'estreny com a RIPEA (ExpedientGrid) per deixar més espai al text.
                    rowActionsColumnProps={{ width: 55, minWidth: 55 }}
                    toolbarMassiveActions={massiveActions}
                    selectionActive
                    isRowSelectable={(params:any) => !params.row.pendentExecucioMassiva}
                    paginationActive
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                />
                {dialogComponent}
                {components}
                {massiveComponents}
            </CardPage>
        </GridPage>
    )
}