import {useBaseAppContext, useResourceApiService} from "reactlib";
import useIntegracioDetail from "./IntegracioDetail.tsx";
import {useTranslation} from "react-i18next";
import { icons } from "../../../util/icons.ts";

export const useActions = (refresh?: () => void) => {
    const {t} = useTranslation()
    const {
        isReady: apiIsReady,
        artifactAction: apiAction
    } = useResourceApiService('monitorIntegracioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const remove = (codi:string) => {
        apiAction(undefined, {code: 'ESBORRAR', data: { codi }})
            .then((response) => {
                refresh?.()
                temporalMessageShow(null, t('Se han borrado correctamente {{num}} entradas para la integración con código {{codi}}', {num: response.num, codi}), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const countErrors = (filterData: any) => {
        return apiAction(undefined, { code: "COUNT_ERRORS", data: filterData })
    }

    const diagnostic = (codiIntegracio:string, entitatId:number | undefined) => {
        return apiAction(undefined, { code: "DIAGNOSTIC", data: { codiIntegracio, entitatId } })
    }

    const integracioDiagnostic = () => {
        return apiAction(undefined, { code: "INTEGRACIONS_DIAGNOSTIC" })
    }

    return {
        apiIsReady,
        remove,
        countErrors,
        diagnostic,
        integracioDiagnostic,
    }
}
export const useIntegracioActions = () => {
    const {t} = useTranslation()
    const { show: mostrarDetall, component: detailDialog } = useIntegracioDetail();

    const actions:any[] = [
        {
            label: t('page.integracio.detail.title'),
            icon: icons.detall,
            showInMenu: false,
            onClick: (id: any, row: any) => mostrarDetall(id, row),
        },
    ]

    const components = <>
        {detailDialog}
    </>

    return {
        actions,
        components
    }
}