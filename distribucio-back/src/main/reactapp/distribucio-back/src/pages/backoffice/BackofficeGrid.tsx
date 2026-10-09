import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import BackofficeFilter from "./BackofficeFilter.tsx";
import React from "react";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import Grid from "@mui/material/Grid";
import GridFormField from "../../components/GridFormField.tsx";
import {useBackofficeActions, useBackofficeMassiveActions} from "./detail/BackofficeActions.tsx";

const BackofficeForm = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="codi" disabled={data?.id} componentProps={{ helperText: t('page.backoffice.form.codi') }} />
        <GridFormField size={12} name="nom" />
        <GridFormField size={12} name="url" componentProps={{ helperText: t('page.backoffice.form.url') }} />
        <GridFormField size={12} name="tipus" />
        <GridFormField size={12} name="usuari" componentProps={{ helperText: t('page.backoffice.form.usuari') }} />
        <GridFormField size={12} name="contrasenya" componentProps={{ helperText: t('page.backoffice.form.contrasenya') }} />
        <GridFormField size={12} name="enviamentEmail" />
        <GridFormField size={12} name="emailResponsable" />
    </Grid>
}

const columns = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 1 },
    { field: 'url', flex: 2 },
    { field: 'tipus', flex: 0.5 },
]
const sortModel:any = [{ field: 'codi', sort: 'asc' }]
export const BackofficeGrid = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = useDistribucioContext();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const {actions, components} = useBackofficeActions(refresh);
    const {actions: massiveActions, components: massiveComponents} = useBackofficeMassiveActions(refresh);

    return (
        <GridPage>
            <CardPage title={t('page.backoffice.title')}>
                <BackofficeFilter onSpringFilterChange={setSpringFilter} />

                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="backofficeResource"
                    columns={columns}
                    filter={springFilter}
                    sortModel={sortModel}
                    toolbarShowFilterCount

                    toolbarCreateTitle={t('page.backoffice.accio.new.label')}
                    popupEditFormDialogResourceTitle={t('page.backoffice.form.resourceTitle')}
                    popupEditActive
                    popupEditFormContent={<BackofficeForm/>}
                    formAdditionalData={{
                        entitat: { id: currentEntitatId }
                    }}

                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    selectionActive
                    paginationActive
                />
                {components}
                {massiveComponents}
            </CardPage>
        </GridPage>
    )
}