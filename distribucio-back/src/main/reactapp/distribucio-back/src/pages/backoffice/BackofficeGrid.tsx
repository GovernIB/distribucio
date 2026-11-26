import {GridPage, useFormContext, useMuiDataGridApiRef} from "reactlib";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import BackofficeFilter from "./BackofficeFilter.tsx";
import React, {useState} from "react";
import {ROLE_ADMIN, useDistribucioContext} from "../../components/DistribucioContext.ts";
import GridFormField, {GridButton} from "../../components/GridFormField.tsx";
import {useActions, useBackofficeActions, useBackofficeMassiveActions} from "./detail/BackofficeActions.tsx";
import {Grid, Icon, Typography} from "@mui/material";
import {SectionDivider} from "../entitat/EntitatFormContent.tsx";
import CircularProgress from "@mui/material/CircularProgress";

const BackofficeForm = ({ isAdmin, prova }:any) => {
    const { t } = useTranslation();
    const { data, modified } = useFormContext();
    const [testing, setTesting] = useState<boolean>()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="codi" disabled={data?.id} componentProps={{ helperText: t('page.backoffice.form.codi') }} />
        <GridFormField size={12} name="nom" />
        <GridFormField size={12} name="url" componentProps={{ helperText: t('page.backoffice.form.url') }} />
        <GridFormField size={12} name="tipus" />
        <GridFormField size={12} name="usuari" componentProps={{ helperText: t('page.backoffice.form.usuari') }} />
        <GridFormField size={12} name="contrasenya" componentProps={{ helperText: t('page.backoffice.form.contrasenya') }} />
        <GridFormField size={12} name="enviamentEmail" />
        <GridFormField size={12} name="emailResponsable" />

        {isAdmin && data.id && <>
            <SectionDivider>{t('page.backoffice.accio.prova.title')}</SectionDivider>
            <GridButton
                size={2}
                variant={'contained'}
                onClick={() => {
                    setTesting(true)
                    prova?.([data.id])
                        ?.finally(() => setTesting(false))
                }}
                disabled={testing || modified}
            >
                {testing
                    ?<CircularProgress size={'20px'} sx={{ mr: 1 }}/>
                    :<Icon sx={{ mr: 1 }}>settings</Icon>
                }
                {t('page.backoffice.accio.prova.label')}
            </GridButton>
            <Grid size={12}>
                <Typography variant={'caption'} color="text.secondary">
                    {t('page.backoffice.accio.prova.helperText')}
                </Typography>
            </Grid>
        </>}
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

    const {currentRole} = useDistribucioContext();
    const isAdmin = currentRole == ROLE_ADMIN

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const { massiveProva } = useActions()
    const {actions, components} = useBackofficeActions(refresh);
    const {actions: massiveActions, components: massiveComponents} = useBackofficeMassiveActions(refresh);

    return (
        <GridPage>
            <ScreenFrame icon={icons.backoffice} title={t('page.backoffice.title')}>
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
                    popupEditFormContent={<BackofficeForm isAdmin={isAdmin} prova={massiveProva}/>}
                    formAdditionalData={{
                        entitat: { id: currentEntitatId }
                    }}

                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    selectionActive={isAdmin || undefined}
                    paginationActive
                />
                {components}
                {massiveComponents}
            </ScreenFrame>
        </GridPage>
    )
}