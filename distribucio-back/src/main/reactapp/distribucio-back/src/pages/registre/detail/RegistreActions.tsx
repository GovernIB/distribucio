import {useTranslation} from "react-i18next";
import useContingutHistorialDialog from "../../contingut/actions/ContingutHistorialDialog.tsx";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";
import {useAlertes} from "./Alertes.tsx";
import useClassificar from "../actions/Classificar.tsx";
import { Divider } from "@mui/material";
import {useExecucioMassivaGrid} from "../../execucioMassiva/ExecucioMassivaGrid.tsx";
import useEnviarViaEmail from "../actions/EnviarViaEmail.tsx";
import useReenviar from "../actions/Reenviar.tsx";
import useMarcarProcessada from "../actions/MarcarProcessada.tsx";
import useMarcarPendent from "../actions/MarcarPendent.tsx";

export const useActions = () => {
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
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.email.ok`, {numero: result.numero}), 'success');
    })
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.reenviar.ok`, {numero: result.numero}), 'success');
    })
    const { handleShow: handleProcessada, content: contentProcessada } = useMarcarProcessada((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.marcarProcessada.ok`, {numero: result.numero}), 'success');
    })
    const { handleShow: handlePendent, content: contentPendent } = useMarcarPendent((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.marcarPendent.ok`, {numero: result.numero}), 'success');
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
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.registre.accio.email.label'),
            icon: 'mail',
            action: 'ENVIAR_EMAIL',
            showInMenu: true,
            onClick: (id:any) => handleEnviarEmail([id], false),
        },
        {
            label: t('page.registre.accio.reenviar.label'),
            icon: 'send',
            action: 'REENVIAR',
            showInMenu: true,
            onClick: (id:any) => handleReenviar([id], false),
        },
        {
            label: t('page.registre.accio.marcarProcessada.label'),
            icon: 'check_circle',
            action: 'MARCAR_PROCESSADA',
            showInMenu: true,
            onClick: (id:any) => handleProcessada([id], false),
            disabled: (row:any) => {
                return row.pendentExecucioMassiva || !(
                    row.procesEstat == 'BUSTIA_PENDENT'
                    || (row.procesEstat == 'BACK_ERROR' && row.reintentsEsgotat)
                    || (row.procesEstat == 'ARXIU_PENDENT' && row.reintentsEsgotat)
                    || row.procesEstat == 'BACK_REBUTJADA'
                )
            },
            hidden: (row:any) => !(row.procesEstatSimple == 'PENDENT'),
        },
        {
            label: t('page.registre.accio.marcarPendent.label'),
            icon: 'undo',
            action: 'MARCAR_PENDENT',
            showInMenu: true,
            onClick: (id:any) => handlePendent([id], false),
            disabled: (row:any) => row.pendentExecucioMassiva,
            hidden: (row:any) => !(row.procesEstat == 'BUSTIA_PROCESSADA'),
        },
    ]

    const components = <>
        {componentHistoric}
        {componentAlertes}
        {contentClassificar}
        {contentEnviarEmail}
        {contentReenviar}
        {contentProcessada}
        {contentPendent}
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
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.email.ok`), 'success');
    })
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.reenviar.ok`), 'success');
    })
    const { handleShow: handleProcessada, content: contentProcessada } = useMarcarProcessada(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.marcarProcessada.ok`), 'success');
    })
    const { handleShow: handlePendent, content: contentPendent } = useMarcarPendent(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.marcarPendent.ok`), 'success');
    })

    const actions:any[] = [
        {
            label: t('page.registre.accio.classifica.label'),
            icon: 'inbox',
            action: 'CLASSIFICAR',
            showInMenu: true,
            onClick: (ids:any) => handleClassificar(ids, true),
        },
        {
            label: t('page.registre.accio.email.label'),
            icon: 'mail',
            action: 'ENVIAR_EMAIL',
            showInMenu: true,
            onClick: (ids:any) => handleEnviarEmail(ids, true),
        },
        {
            label: t('page.registre.accio.reenviar.label'),
            icon: 'send',
            action: 'REENVIAR',
            showInMenu: true,
            onClick: (ids:any) => handleReenviar(ids, true),
        },
        {
            label: t('page.registre.accio.marcarProcessada.label'),
            icon: 'check_circle',
            action: 'MARCAR_PROCESSADA',
            showInMenu: true,
            onClick: (ids:any) => handleProcessada(ids, true),
        },
        {
            label: t('page.registre.accio.marcarPendent.label'),
            icon: 'undo',
            action: 'MARCAR_PENDENT',
            showInMenu: true,
            onClick: (ids:any) => handlePendent(ids, true),
        },
    ]

    const components = <>
        {contentClassificar}
        {componentEM}
        {contentEnviarEmail}
        {contentReenviar}
        {contentProcessada}
        {contentPendent}
    </>

    return {
        actions,
        components
    }
}