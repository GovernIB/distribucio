import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import { icons } from '../../util/icons';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACTUALITZAR_PROCEDIMENT = 'ACTUALITZAR_PROCEDIMENT';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useActions = (refresh: () => void) => {
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

    const actualitzar = (id: any) => executarAccio(id, ACTUALITZAR_PROCEDIMENT, t('page.procediments.accio.actualitzarOk'))

    return {
        actualitzar
    }
}

export const useProcedimentAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();

    const { actualitzar } = useActions(refresh)

    return [
        {
            label: t('page.procediments.accio.actualitzar'),
            icon: icons.sincronitza,
            showInMenu: true,
            action: ACTUALITZAR_PROCEDIMENT,
            onClick: actualitzar,
        },
    ];
};

export default useProcedimentAccions;
