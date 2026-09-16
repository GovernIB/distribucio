import {useTranslation} from "react-i18next";
import useContingutHistorialDialog from "../../contingut/actions/ContingutHistorialDialog.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";
import {useAlertes} from "./Alertes.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactReport: apiReport,
    } = useResourceApiService('registreResource');
    const {temporalMessageShow} = useBaseAppContext();

    const informeLogs = (id:any) => {
        if (apiIsReady) {
            apiReport(id, {code: "INFORME_LOGS", fileType: 'PDF'})
                .then((result) => {
                    iniciaDescargaBlob(result)
                    temporalMessageShow(null, t('page.contingut.historial.informe.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    return {
        apiIsReady,
        informeLogs
    }
}

export const useRegistreActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {show: handleHistoric, component: componentHistoric} = useContingutHistorialDialog()
    const {handleOpen: handleAlertes, component: componentAlertes} = useAlertes();

    const actions:any[] = [
        {
            label: t('page.contingut.accio.historial.label'),
            icon: 'list',
            showInMenu: true,
            onClick: handleHistoric,
        },
        {
            label: t('page.alerta.label'),
            icon: 'note_stack',
            showInMenu: true,
            onClick: handleAlertes,
            hidden: (row:any) => !row.alerta,
        },
    ]

    const components = <>
        {componentHistoric}
        {componentAlertes}
    </>

    return {
        actions,
        components
    }
}