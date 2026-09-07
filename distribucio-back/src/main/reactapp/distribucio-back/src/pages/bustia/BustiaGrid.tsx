import {GridPage, MuiDataGridColDef, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import BustiaFilter from "./BustiaFilter.tsx";
import React, {useMemo} from "react";
import {Icon} from "@mui/material";
import IconButton from "@mui/material/IconButton";
import Badge from "@mui/material/Badge";
import {BustiaForm} from "./BustiaForm.tsx";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import {useAclCustomPermissionManager} from "../../components/AclPermissionManager.tsx";
import {Link} from "../../components/BaseApp.tsx";
import {BustiaPermisosForm, useBustiaPermisosColumns} from "./BustiaPermisosForm.tsx";
import {useActions, useBustiaActions} from "./detail/BustiaActions.tsx";

const columns: MuiDataGridColDef[] = [
    { field: 'nom', flex: 4,
        renderCell: (params) => (<>
            {params.formattedValue}
            {params.row.pendent &&
                <Icon color={'error'} sx={{ marginLeft: 'auto' }}>warning</Icon>}
        </>)
    },
    { field: 'unitatOrganitzativa', flex: 4 },
    { field: 'perDefecte', flex: 1, type: 'boolean' },
    { field: 'activa', flex: 1, type: 'boolean' },
];
const perspectives = ['PERMISOS_COUNT']

export const BustiaGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const { currentEntitatId } = useDistribucioContext();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const additionalColumns = useMemo(() => [
        ...columns,
        { field: 'permisosCount', flex: 1,
            sortable: false,
            filterable: false,
            width: 100,
            align: 'center' as const,
            headerAlign: 'center' as const,
            renderCell: (params: any) => (
                <IconButton
                    title={t('component.AclPermissionManager.title')}
                    onClick={(event) => {
                        event.stopPropagation();
                        permissionShow(params.row?.id, params.row?.nom);
                    }}
                >
                    <Badge
                        badgeContent={params.row?.permisosCount ?? 0}
                        color="primary"
                        showZero
                    >
                        <Icon>key</Icon>
                    </Badge>
                </IconButton>
            ),
        },
    ], [t, columns])

    const {actions, components} = useBustiaActions(refresh);
    const { usersBustia } = useActions()

    const {
        show: permissionShow,
        component: permissionComponent
    } = useAclCustomPermissionManager({
        resourceType: 'BUSTIA',
        columns: useBustiaPermisosColumns(),
        formContent: <BustiaPermisosForm/>,
        additionalData: { readAllowed: true },
        onEntryChanged: refresh,
    });

    return (
        <GridPage>
            <CardPage title={t('page.bustia.grid.title')}>
                <BustiaFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />

                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="bustiaResource"
                    columns={additionalColumns}
                    perspectives={perspectives}
                    namedQueries={namedQueries}
                    filter={springFilter}
                    toolbarShowFilterCount

                    toolbarCreateTitle={t('page.bustia.accio.new.label')}
                    popupEditFormDialogResourceTitle={t('page.bustia.title')}
                    popupEditActive
                    popupEditFormContent={<BustiaForm/>}
                    formAdditionalData={{
                        entitat: { id: currentEntitatId }
                    }}
                    rowAdditionalActions={actions}
                    toolbarElementsWithPositions={[
                        {
                            position: 1,
                            element: <ToolbarButton
                                icon={'table'}
                                variant={'contained'}
                                component={Link}
                                to={'/bustiaAdminOrganigrama'}
                            >{t('page.bustia.vista')}</ToolbarButton>
                        },
                        {
                            position: 2,
                            element: <ToolbarButton
                                icon={'description'}
                                variant={'contained'}
                                color={'success'}
                                onClick={() => usersBustia(springFilter, namedQueries)}
                            >{t('page.bustia.accio.usuarisBustia.label')}</ToolbarButton>
                        }
                    ]}

                    paginationActive
                />
                {permissionComponent}
                {components}
            </CardPage>
        </GridPage>
    );
}