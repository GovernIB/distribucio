import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useAvisAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } =
        useResourceApiService('avisResource');
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
                    t('page.avisos.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
    return [
        {
            label: t('page.avisos.accio.modificar'),
            icon: 'edit',
            showInMenu: true,
            rowLink: 'update',
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.avisos.accio.activar'),
            icon: 'check',
            showInMenu: true,
            action: ACCIO_ACTIVAR,
            hidden: (row: any) => row?.activa,
            onClick: (id: any) => executarAccio(id, ACCIO_ACTIVAR, 'page.avisos.accio.activarOk'),
        },
        {
            label: t('page.avisos.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_DESACTIVAR,
            hidden: (row: any) => !row?.activa,
            onClick: (id: any) =>
                executarAccio(id, ACCIO_DESACTIVAR, 'page.avisos.accio.desactivarOk'),
        },
        {
            label: t('page.avisos.accio.esborrar'),
            icon: 'delete',
            showInMenu: true,
            rowLink: 'delete',
            clickTriggerDelete: true,
        },
    ];
};

export default useAvisAccions;
