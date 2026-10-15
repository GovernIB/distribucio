import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useMoureAnotacions} from "../actions/MoureAnotacions.tsx";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";
import {useDesactivar} from "../actions/Desactivar.tsx";

export const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
        artifactReport: apiReport,
    } = useResourceApiService('bustiaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const action = (id:any, code:string, msg:string) => {
        if (apiIsReady) {
            apiAction(id, {code: code})
                .then(() => {
                    refresh?.()
                    temporalMessageShow(null, msg, 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const perDefecte = (id:any) => action(id, 'PRINCIPAL', t('page.bustia.accio.perDefecte.ok'))
    const activar = (id:any) => action(id, 'ACTIVAR', t('page.bustia.accio.activar.ok'))
    const desactivar = (id:any) => action(id, 'DESACTIVAR', t('page.bustia.accio.desactivar.ok'))
    const favorite = (id:any, favorite:boolean) => {
        if (apiIsReady) {
            apiAction(id, {code: "TOOGLE_FAVORITA", data: favorite})
                .then(() => {
                    refresh?.()
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const usersBustia = (filter:any, namedQueries:string[]) => {
        if (apiIsReady) {
            apiReport(undefined, {code: "USUARIS_BUSTIA", data: {filter, namedQueries}, fileType: 'XLSX'})
                .then((result) => {
                    iniciaDescargaBlob(result)
                    temporalMessageShow(null, t('page.bustia.accio.usuarisBustia.ok'), 'success');
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const transicioInfo = (id:any) => {
        // El backend genera el report en JSON, però l'ExportFileType de base-react (lib/) no inclou
        // 'JSON'. Sense fileType, apiReport no retornaria el blob que llegeix BustiaObsoleta.
        return apiReport(id, {code: "TRANSICIO_INFO", fileType: 'JSON' as any})
    }

    return {
        apiIsReady,
        perDefecte,
        activar,
        desactivar,
        usersBustia,
        favorite,
        transicioInfo
    }
}

export const useBustiaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const { perDefecte, activar, desactivar } = useActions(refresh)
    const {handleShow, content} = useMoureAnotacions(refresh)
    const {handleShow: handleDesactivarDialog, content: contentDesactivarDialog} = useDesactivar(refresh)

    const actions:any[] = [
        {
            label: t('page.bustia.accio.perDefecte.label'),
            icon: 'check_box',
            action: 'PRINCIPAL',
            showInMenu: true,
            onClick: perDefecte,
            hidden: (row:any) => row.perDefecte,
        },
        {
            label: t('page.bustia.accio.activar.label'),
            icon: 'check',
            action: 'ACTIVAR',
            showInMenu: true,
            onClick: activar,
            hidden: (row:any) => row.activa,
        },
        {
            label: t('page.bustia.accio.desactivar.label'),
            icon: 'close',
            action: 'DESACTIVAR',
            showInMenu: true,
            onClick: (id:any, row:any) => row.perDefecte
                ?handleDesactivarDialog(id, row)
                :desactivar(id),
            hidden: (row:any) => !row.activa,
        },
        {
            label: t('page.bustia.accio.moureAnotacions.label'),
            icon: 'turn_right',
            action: 'MOURE_ANOTACIO',
            showInMenu: true,
            onClick: handleShow,
        },
    ]

    const components = <>
        {content}
        {contentDesactivarDialog}
    </>

    return {
        actions,
        components
    }
}