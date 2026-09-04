import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACTUALITZAR_PROCEDIMENT = 'ACTUALITZAR_PROCEDIMENT';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useProcedimentAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } =
        useResourceApiService('procedimentResource');
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
                    t('page.procediments.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
    return [
        {
            label: t('page.procediments.accio.actualitzar'),
            icon: 'sync',
            showInMenu: true,
            action: ACTUALITZAR_PROCEDIMENT,
            onClick: (id: any) => executarAccio(id, ACTUALITZAR_PROCEDIMENT, 'page.procediments.accio.actualitzarOk'),
        },
    ];
};

export default useProcedimentAccions;
