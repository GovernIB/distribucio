import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {Icon, Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";

export const fieldType = (tipus:string) :string => {
    switch (tipus) {
        case 'BOOLEA':
            return "checkbox";
        case 'DATA':
            return "date";
        case 'SENCER':
        case 'FLOTANT':
            return "number";
        case 'DOMINI':
            return "reference";
        default:
            return "text";
    }
}

const useActions = (refresh?: () => void) => {
    const {t} = useTranslation();
    const {
        patch: apiPatch,
        artifactAction: apiAction,
    } = useResourceApiService('metaDadaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const active = (id:any) => {
        apiPatch(id, {data: { activa: true }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metadada.accio.activar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const desactive = (id:any) => {
        apiPatch(id, {data: { activa: false }})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.metadada.accio.desactivar.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const reordering = (id:any, ordre:number) => {
        apiAction(id, { code: 'REORDENAR', data: ordre })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {active, desactive, reordering}
}

const MetaDadaForm = () => {
    const {data, apiRef} = useFormContext()
    const isDomini = data.tipus == "DOMINI"
    const decimalScale = data.tipus === 'SENCER' ? 0 : undefined;

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="codi" disabled={data.id} />
        <GridFormField size={12} name="nom" />
        <GridFormField size={12} name="tipus" />
        <GridFormField size={12} name="multiplicitat" disabled={isDomini} />
        <GridFormField size={12}
                       name="value"
                       type={fieldType(data.tipus)}
                       decimalScale={decimalScale}
                       hidden={isDomini} />
        <GridFormField size={12}
                       name="domini"
                       optionDataFields={['codi']}
                       onChange={(resource:any) => {
                           apiRef.current?.setFieldValue("value", resource?.data?.codi)
                       }}
                       hidden={!isDomini} />
        <GridFormField size={12} name="noAplica" hidden={!isDomini} />
        <GridFormField size={12} name="descripcio" />
    </Grid>
}

const columns = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 1 },
    { field: 'tipus', flex: 1 },
    { field: 'activa', flex: 1,
        renderCell: (params:any) => params.row.activa && <Icon>check</Icon>
    },
]
const sortModel:any = [{ field: 'ordre', sort: 'asc' }]
export const MetaDadaGrid = () => {
    const { t } = useBaseAppContext();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {active, desactive, reordering} = useActions(refresh)

    const actions = [
        {
            label: t('page.metadada.accio.activar.label'),
            icon: "check",
            showInMenu: true,
            onClick: active,
            hidden: (row:any) => row?.activa,
        },
        {
            label: t('page.metadada.accio.desactivar.label'),
            icon: "close",
            showInMenu: true,
            onClick: desactive,
            hidden: (row:any) => !row?.activa,
        },
    ]

    const handleDragEnd = (params: any) => {
        if (params.targetIndex != params.oldIndex) {
            reordering(params.row.id, params.targetIndex)
        }
    }

    return (
        <GridPage>
            <CardPage title={t('page.metadada.title')}>
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName={'metaDadaResource'}
                    columns={columns}
                    fixedSortModel={sortModel}
                    disableColumnSorting

                    toolbarShowQuickFilter

                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.metadada.title')}
                    toolbarCreateTitle={t('page.metadada.accio.new.label')}
                    popupEditFormContent={<MetaDadaForm/>}

                    rowAdditionalActions={actions}
                    rowReordering
                    onRowOrderChange={handleDragEnd}

                    paginationActive
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.metadada.accio.new.ok',
                        updateSuccess: 'page.metadada.accio.update.ok',
                        deleteSuccess: 'page.metadada.accio.delete.ok',
                    }}
                />
            </CardPage>
        </GridPage>
    )
}