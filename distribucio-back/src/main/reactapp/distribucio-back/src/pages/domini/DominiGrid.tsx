import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useFormContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import { CardPage } from "../../components/CardData";
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";

const consultaPlaceholder = "SELECT field_id AS ID, field_valor AS VALOR FROM tables"
const cadenaPlaceholder = `<local-tx-datasource>
    <connection-url>jdbc:oracle:thin:@localhost:1521/orcl</connection-url>
    <driver-class>oracle.jdbc.driver.OracleDriver</driver-class>
    <user-name>usuari</user-name>
</local-tx-datasource>`

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactAction: apiAtion,
    } = useResourceApiService('dominiResource');
    const {temporalMessageShow} = useBaseAppContext();

    const cleanCache = () => {
        if (apiIsReady) {
            apiAtion(undefined, {code: "CLEAN_CACHE"})
                .then(() => {
                    refresh?.()
                    temporalMessageShow(null, t('page.domini.accio.cache.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    return {
        cleanCache
    }
}

const DominiForm = () => {
    const {data} = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="codi" disabled={data.id} />
        <GridFormField size={12} name="nom" />
        <GridFormField size={12} name="descripcio" type={'textarea'} />
        <GridFormField size={12} name="consulta" type={'textarea'} componentProps={{
            InputLabelProps: { shrink: true },
            placeholder: consultaPlaceholder
        }} />
        <GridFormField size={12} name="cadena" type={'textarea'} componentProps={{
            InputLabelProps: { shrink: true },
            placeholder: cadenaPlaceholder
        }} />
        <GridFormField size={12} name="contrasenya" />
    </Grid>
}

const columns = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 1 },
    { field: 'descripcio', flex: 1 },
]
const sortModel:any = [{ field: 'codi', sort: 'asc' }]
export const DominiGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const { cleanCache } = useActions(refresh)

    return (
        <GridPage>
            <CardPage title={t('page.domini.title')}>
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="dominiResource"
                    columns={columns}
                    sortModel={sortModel}
                    toolbarShowQuickFilter

                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.domini.title')}
                    toolbarCreateTitle={t('page.domini.accio.new.label')}
                    popupEditFormContent={<DominiForm/>}

                    toolbarElementsWithPositions={[
                        {
                            position: 3,
                            element: <ToolbarButton
                                title={t('page.domini.accio.cache.title')}
                                onClick={() => cleanCache()}
                                icon={'cached'}
                                color={'warning'}
                                variant={'contained'}
                            >
                                {t('page.domini.accio.cache.label')}
                            </ToolbarButton>
                        }
                    ]}

                    paginationActive
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.domini.accio.new.ok',
                        updateSuccess: 'page.domini.accio.update.ok',
                        deleteSuccess: 'page.domini.accio.delete.ok',
                    }}
                />
            </CardPage>
        </GridPage>
    )
}