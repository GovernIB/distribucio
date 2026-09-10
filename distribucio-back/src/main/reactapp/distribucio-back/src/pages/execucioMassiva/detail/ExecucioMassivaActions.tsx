import {useTranslation} from "react-i18next";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('execucioMassivaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const download = (id:any) => {
        apiReport(id, {code: "DOWNLOAD", fileType: "PDF"})
            .then((result) => {
                iniciaDescargaBlob(result)
                temporalMessageShow(null, t('page.massiva.accio.download.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const canviEstat = (id:any, estat:any, mssg:string) => {
        if (apiIsReady) {
            apiAction(id, {code: "CANVI_ESTAT", data: estat})
                .then(() => {
                    refresh?.()
                    temporalMessageShow(null, mssg, 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const play = (id:any) => canviEstat(id, "REPRENDRE", t('page.massiva.accio.play.ok'));
    const pause = (id:any) => canviEstat(id, "PAUSAR", t('page.massiva.accio.pause.ok'));
    const cancel = (id:any) => canviEstat(id, "CANCELAR", t('page.massiva.accio.cancel.ok'));

    return {
        download,
        play,
        pause,
        cancel,
    }
}

export const useExecucioMassivaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {download, play, pause, cancel} = useActions(refresh)

    const actions = [
        {
            label: t('page.massiva.accio.play.label'),
            icon: 'play_circle',
            showInMenu: true,
            onClick: play,
            hidden: (row:any) => row.estat != "PAUSADA" || row.estat == "FINALITZADA"
        },
        {
            label: t('page.massiva.accio.pause.label'),
            icon: 'pause',
            showInMenu: true,
            onClick: pause,
            hidden: (row:any) => (row.estat == "PAUSADA" || row.estat == "CANCELADA") || row.estat == "FINALITZADA"
        },
        {
            label: t('page.massiva.accio.cancel.label'),
            icon: 'close',
            showInMenu: true,
            onClick: cancel,
            hidden: (row:any) => row.estat == "CANCELADA" || row.estat == "FINALITZADA"
        },
        {
            label: t('page.massiva.accio.download.notFound'),
            icon: 'warning',
            showInMenu: false,
            hidden: (row:any) => row.tipus != "DESCARREGAR" || row.estat != "FINALITZADA" || row.nomDocument
        },
        {
            label: t('page.massiva.accio.download.label'),
            icon: 'download',
            showInMenu: false,
            onClick: download,
            hidden: (row:any) => row.tipus != "DESCARREGAR" || row.estat != "FINALITZADA" || !row.nomDocument
        }
    ]

    const components = <>
    </>

    return {
        actions,
        components
    }
}