import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import { icons } from '../../util/icons';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACTUALITZAR_SERVEI = 'ACTUALITZAR_SERVEI';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useActions = (refresh: () => void) => {
    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('serveiResource');
    const { temporalMessageShow } = useBaseAppContext();

    const executarAccio = (id: any, code: string, missatge: string) => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(id, { code })
            .then(() => {
                refresh();
                temporalMessageShow(null, missatge, 'success');
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.serveis.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    const actualitzar = (id: any) => executarAccio(id, ACTUALITZAR_SERVEI, t('page.serveis.accio.actualitzarOk'))

    return {
        actualitzar
    }
}

export const useServeiAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();

    const { actualitzar } = useActions(refresh)

    return [
        {
            label: t('page.serveis.accio.actualitzar'),
            icon: icons.sincronitza,
            showInMenu: true,
            action: ACTUALITZAR_SERVEI,
            onClick: actualitzar,
        },
    ];
};

export default useServeiAccions;
