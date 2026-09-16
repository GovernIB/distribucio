import {useTranslation} from "react-i18next";
import useContingutHistorialDialog from "../../contingut/actions/ContingutHistorialDialog.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";
import {useAlertes} from "./Alertes.tsx";
import useClassificar from "../actions/Classificar.tsx";
import { Divider } from "@mui/material";
import {useExecucioMassivaGrid} from "../../execucioMassiva/ExecucioMassivaGrid.tsx";

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
    const {temporalMessageShow} = useBaseAppContext();

    const {show: handleHistoric, component: componentHistoric} = useContingutHistorialDialog()
    const {handleOpen: handleAlertes, component: componentAlertes} = useAlertes();
    const { handleShow: handleClassificar, content: contentClassificar } = useClassificar((result:any) => {
        // console.log("result", result)
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.classifica.ok.${result.tipus}`, { numero: result.numero, sia: result.sia }), 'success');
    })

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
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.registre.accio.classifica.label'),
            icon: 'inbox',
            action: 'CLASSIFICAR',
            showInMenu: true,
            onClick: (id:any) => handleClassificar([id], false),
        },
    ]

    const components = <>
        {componentHistoric}
        {componentAlertes}
        {contentClassificar}
    </>

    return {
        actions,
        components
    }
}

export const useRegistreMassiveActions = () => {
    const { t } = useTranslation();

    const { handleOpen: handleEM, component: componentEM } = useExecucioMassivaGrid();

    const { handleShow: handleClassificar, content: contentClassificar } = useClassificar(() => {
        handleEM()
        // temporalMessageShow(null, t('page.registre.accio.classifica.ok'), 'success');
    })

    const actions:any[] = [
        {
            label: t('page.registre.accio.classifica.label'),
            icon: 'inbox',
            action: 'CLASSIFICAR',
            showInMenu: true,
            onClick: (ids:any) => handleClassificar(ids, true),
        },
    ]

    const components = <>
        {contentClassificar}
        {componentEM}
    </>

    return {
        actions,
        components
    }
}