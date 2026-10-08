import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACTUALITZAR_SERVEI = 'ACTUALITZAR_SERVEI';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useServeiAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } =
        useResourceApiService('serveiResource');
    const executarAccio = (id: any, code: string, clauMissatgeOk: string) => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(id, { code })
            .then(() => {
                refresh();
                temporalMessageShow(null, t(clauMissatgeOk), 'success');
            })
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.serveis.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
    return [
        {
            label: t('page.serveis.accio.actualitzar'),
            icon: 'sync',
            showInMenu: true,
            action: ACTUALITZAR_SERVEI,
            onClick: (id: any) => executarAccio(id, ACTUALITZAR_SERVEI, 'page.serveis.accio.actualitzarOk'),
        },
    ];
};

export default useServeiAccions;
