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
import {useSnackbar} from "notistack";
import useTornarProcessar from "../actions/TornarProcessar.tsx";
import useMarcarSobreescriure from "../actions/MarcarSobreescriure.tsx";
import useDescargaMassiva from "../actions/DescargaMassiva.tsx";
import useReenviarBackoffice from "../actions/ReenviarBackoffice.tsx";
import useEnviarMarcar from "../actions/EnviarMarcar.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactReport: apiReport,
        artifactAction: apiAction,
    } = useResourceApiService('registreResource');
    const {temporalMessageShow} = useBaseAppContext();

    const marcarSobreescriure = (id:any) => {
        if (apiIsReady) {
            apiAction(undefined, {code: "MARCAR_SOBREESCRIURE", data: { ids: [id], massive: false } })
                .then((response) => {
                    refresh?.()
                    temporalMessageShow(null, t('page.registre.accio.sobreescriure.ok', {numero: response.numero}), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const reenviarBackoffice = (id:any) => {
        if (apiIsReady) {
            apiAction(undefined, {code: "REENVIAR_BACKOFFICE", data: { ids: [id], massive: false } })
                .then((response) => {
                    refresh?.()
                    temporalMessageShow(null, t('page.registre.accio.reenviarBackoffice.ok', {numero: response.numero}), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

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

    const justificant = (id:any) => {
        if (apiIsReady) {
            apiReport(id, {code: "JUSTIFICANT", fileType: 'PDF'})
                .then((result) => {
                    iniciaDescargaBlob(result)
                    temporalMessageShow(null, t('page.registre.accio.justificant.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const descarregarDoc = (id:any, imprimible:boolean) => {
        if (apiIsReady) {
            apiReport(id, {code: "DESCARREGAR_DOC", data: {imprimible}, fileType: 'PDF'})
                .then((result) => {
                    iniciaDescargaBlob(result)
                    imprimible
                        ?temporalMessageShow(null, t('page.registre.accio.descargaAutentica.ok'), 'success')
                        :temporalMessageShow(null, t('page.registre.accio.descargaOriginal.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const exportRegistre = (ids:any[], fileType?:any) => {
        if (apiIsReady) {
            apiReport(undefined, {code: "EXPORT", data: { ids }, fileType})
                .then((result) => {
                    iniciaDescargaBlob(result)
                    temporalMessageShow(null, t(`page.registre.accio.export.ok`, {format: fileType}), 'success')
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    return {
        apiIsReady,
        marcarSobreescriure,
        descarregarDoc,
        informeLogs,
        justificant,
        exportRegistre,
        reenviarBackoffice,
    }
}

export const useRegistreActions = (refresh?: (code?:string) => void) => {
    const { t } = useTranslation();
    const {temporalMessageShow} = useBaseAppContext();
    const { enqueueSnackbar } = useSnackbar();

    const {marcarSobreescriure, descarregarDoc} = useActions(refresh)
    const {show: handleHistoric, component: componentHistoric} = useContingutHistorialDialog()
    const {handleOpen: handleAlertes, component: componentAlertes} = useAlertes();
    const { handleShow: handleClassificar, content: contentClassificar } = useClassificar((result:any) => {
        refresh?.()
        if (result.message)
            enqueueSnackbar(result.message?.text, { variant: result.message?.severity })
        enqueueSnackbar(t(`page.registre.accio.classifica.ok.${result.tipus}`, { numero: result.numero, sia: result.sia }), { variant: 'success' })
        // temporalMessageShow(null, t(`page.registre.accio.classifica.ok.${result.tipus}`, { numero: result.numero, sia: result.sia }), 'success');
    })
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.email.ok`, {numero: result.numero}), 'success');
    })
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar((result:any) => {
        refresh?.('REENVIAR')
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
            disabled: (row:any) => row.procesEstat == 'ARXIU_PENDENT',
            hidden: (row:any) => !row.potModificar,
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            action: 'CLASSIFICAR',
            showInMenu: true,
            disabled: true,
            hidden: (row:any) => !row.potModificar,
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
            hidden: (row:any) => !row.potModificar,
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
            hidden: (row:any) => !(row.procesEstatSimple == 'PENDENT') || !row.potModificar,
        },
        {
            label: t('page.registre.accio.marcarPendent.label'),
            icon: 'undo',
            action: 'MARCAR_PENDENT',
            showInMenu: true,
            onClick: (id:any) => handlePendent([id], false),
            disabled: (row:any) => row.pendentExecucioMassiva,
            hidden: (row:any) => !(row.procesEstat == 'BUSTIA_PROCESSADA') || !row.potModificar,
        },
        {
            label: t('page.registre.accio.sobreescriure.label'),
            icon: 'history',
            action: 'MARCAR_SOBREESCRIURE',
            showInMenu: true,
            onClick: marcarSobreescriure,
            hidden: (row:any) => row.sobreescriure || !row.potModificar,
        },
        {
            label: t('page.registre.accio.descargaOriginal.label'),
            icon: 'download',
            report: 'DESCARREGAR_DOC',
            onClick: (id:any) => descarregarDoc(id, false),
            showInMenu: true,
        },
        {
            label: t('page.registre.accio.descargaAutentica.label'),
            icon: 'download',
            report: 'DESCARREGAR_DOC',
            onClick: (id:any) => descarregarDoc(id, true),
            showInMenu: true,
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

    const {exportRegistre} = useActions()
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
    const { handleShow: handleTornarProcessar, content: contentTornarProcessar } = useTornarProcessar(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.tornarProcessar.ok`), 'success');
    })
    const { handleShow: handleSobreescriure, content: contentSobreescriure } = useMarcarSobreescriure(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.sobreescriure.ok`), 'success');
    })
    const { handleShow: handleDescargaMassiva, content: contentDescargaMassiva } = useDescargaMassiva(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.descargaMassiva.ok`), 'success');
    })
    const { handleShow: handleReenviarBackoffice, content: contentReenviarBackoffice } = useReenviarBackoffice(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.reenviarBackoffice.ok`), 'success');
    })
    const { handleShow: handleEnviaMarca, content: contentEnviaMarca } = useEnviarMarcar(() => {
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.enviaMarca.ok`), 'success');
    })

    const actions:any[] = [
        {
            label: t('page.registre.accio.reenviarBackoffice.label'),
            icon: 'settings',
            action: 'REENVIAR_BACKOFFICE',
            showInMenu: true,
            onClick: (ids:any) => handleReenviarBackoffice(ids, true),
        },
        {
            label: t('page.registre.accio.tornarProcessar.label'),
            icon: 'settings',
            action: 'TORNAR_PROCESSAR',
            showInMenu: true,
            onClick: (ids:any) => handleTornarProcessar(ids, true),
        },
        {
            label: t('page.registre.accio.sobreescriure.label'),
            icon: 'history',
            action: 'MARCAR_SOBREESCRIURE',
            showInMenu: true,
            onClick: (ids:any) => handleSobreescriure(ids, true),
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            action: 'TORNAR_PROCESSAR',
            showInMenu: true,
            disabled: true,
        },
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
        {
            label: t('page.registre.accio.enviaMarca.label'),
            icon: 'mail',
            action: 'ENVIAR_MARCAR',
            showInMenu: true,
            onClick: (ids:any) => handleEnviaMarca(ids, true),
        },
        {
            label: t('page.registre.accio.export.ODS'),
            icon: 'download',
            report: 'EXPORT',
            showInMenu: true,
            onClick: (ids:any) => exportRegistre(ids, 'ODS'),
        },
        {
            label: t('page.registre.accio.export.CSV'),
            icon: 'download',
            report: 'EXPORT',
            showInMenu: true,
            onClick: (ids:any) => exportRegistre(ids, 'CSV'),
        },
        {
            label: t('page.registre.accio.descargaMassiva.label'),
            icon: 'download',
            action: 'DESCARREGAR_MASSIU',
            showInMenu: true,
            onClick: (ids:any) => handleDescargaMassiva(ids, true),
        },
    ]

    const components = <>
        {contentClassificar}
        {componentEM}
        {contentEnviarEmail}
        {contentReenviar}
        {contentProcessada}
        {contentPendent}
        {contentTornarProcessar}
        {contentSobreescriure}
        {contentDescargaMassiva}
        {contentReenviarBackoffice}
        {contentEnviaMarca}
    </>

    return {
        actions,
        components
    }
}