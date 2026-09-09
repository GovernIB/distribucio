import {useBaseAppContext, useConfirmDialogButtons, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useSnackbar} from "notistack";

const useActions = (refresh?: () => void) => {
    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
        bulkDelete: apiBulckDelete,
        // bulkAction: apiBulckAction,
    } = useResourceApiService('backofficeResource');
    const {temporalMessageShow, messageDialogShow, t: tLib} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};
    const { enqueueSnackbar } = useSnackbar();

    const massiveProva = (ids:any[]) => {
        if (apiIsReady) {
            apiAction(undefined, {code: "PROVAR", data: { ids }})
                .then((response) => {
                    response?.forEach((r:any) => {
                        enqueueSnackbar(r.message, { variant: r.severity })
                    } )
                })
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }

    const massiveDelete = (ids:any[]) => {
        messageDialogShow(
            tLib('datacommon.delete.multiple.label'),
            tLib('datacommon.delete.multiple.confirm', { count: ids.length }),
            confirmDialogButtons,
            confirmDialogComponentProps
        )
            .then((value: any) => {
                if (value) {
                    apiBulckDelete(ids)
                        .then((response) => {
                            if (response.errorCount === 0) {
                                refresh?.()
                                temporalMessageShow(null, tLib('datacommon.delete.multiple.success', { count: response.successCount }), 'success');
                            } else {
                                temporalMessageShow(null, tLib('datacommon.delete.multiple.error', { count: response.errorCount }), 'warning');
                            }
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            })
    }

    return {
        massiveProva,
        massiveDelete
    }
}

export const useBackofficeActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const { massiveProva } = useActions(refresh)

    const actions:any[] = [
        {
            label: t('page.backoffice.accio.prova.label'),
            icon: 'settings',
            showInMenu: true,
            onClick: (id:any) => massiveProva([id])
        },
    ]

    const components = <>
    </>

    return {
        actions,
        components
    }
}
export const useBackofficeMassiveActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const { massiveProva, massiveDelete } = useActions(refresh)

    const actions:any[] = [
        {
            label: t('page.backoffice.accio.prova.label'),
            icon: 'settings',
            showInMenu: true,
            onClick: massiveProva
        },
        {
            label: t('common.delete'),
            icon: 'delete',
            showInMenu: true,
            onClick: massiveDelete,
        },
    ]

    const components = <>
    </>

    return {
        actions,
        components
    }
}