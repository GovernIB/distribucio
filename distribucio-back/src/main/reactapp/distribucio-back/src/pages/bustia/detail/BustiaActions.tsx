import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useMoureAnotacions} from "../actions/MoureAnotacions.tsx";
import {iniciaDescargaBlob} from "../../../util/downloadUtils.ts";

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

    return {
        apiIsReady,
        perDefecte,
        activar,
        desactivar,
        usersBustia,
    }
}

export const useBustiaActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const { perDefecte, activar, desactivar } = useActions(refresh)
    const {handleShow, content} = useMoureAnotacions(refresh)

    const actions:any[] = [
        {
            label: t('page.bustia.accio.perDefecte.label'),
            icon: 'check_box',
            onClick: perDefecte,
            hidden: (row:any) => row.perDefecte,
            showInMenu: true,
        },
        {
            label: t('page.bustia.accio.activar.label'),
            icon: 'check',
            hidden: (row:any) => row.activa,
            onClick: activar,
            showInMenu: true,
        },
        {
            label: t('page.bustia.accio.desactivar.label'),
            icon: 'close',
            onClick: desactivar,
            hidden: (row:any) => !row.activa,
            showInMenu: true,
        },
        {
            label: t('page.bustia.accio.moureAnotacions.label'),
            icon: 'turn_right',
            showInMenu: true,
            onClick: handleShow,
        },
    ]

    const components = <>
        {content}
    </>

    return {
        actions,
        components
    }
}