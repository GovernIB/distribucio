import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/**
 * Accions del menú de cada fila del llistat d'entitats: les mateixes, i en el mateix ordre, que
 * el desplegable "Accions" de la interfície JSP (entitatList.jsp).
 *
 * Cada acció declara com es fa visible, i la comprovació sempre és del servidor:
 * - `rowLink` ("update", "delete") només mostra l'acció si la fila duu l'enllaç HAL corresponent,
 *   és a dir si l'usuari té el permís necessari sobre el recurs.
 * - `action` només la mostra si el recurs publica aquest artefacte per a l'usuari actual (les
 *   accions sense accessConstraints pròpies demanen el permís WRITE, o sia DIS_SUPER).
 *
 * Modificar i esborrar els resol la pròpia graella (diàleg de modificació i diàleg de confirmació
 * + esborrat, tots dos amb refresc inclòs); activar i desactivar criden l'acció del recurs i
 * refresquen la graella des d'aquí.
 */
export const useEntitatAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } =
        useResourceApiService('entitatResource');
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
                    t('page.entitats.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
    return [
        {
            label: t('page.entitats.accio.modificar'),
            icon: 'edit',
            showInMenu: true,
            rowLink: 'update',
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.entitats.accio.activar'),
            icon: 'check',
            showInMenu: true,
            action: ACCIO_ACTIVAR,
            hidden: (row: any) => row?.activa,
            onClick: (id: any) => executarAccio(id, ACCIO_ACTIVAR, 'page.entitats.accio.activarOk'),
        },
        {
            label: t('page.entitats.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_DESACTIVAR,
            hidden: (row: any) => !row?.activa,
            onClick: (id: any) =>
                executarAccio(id, ACCIO_DESACTIVAR, 'page.entitats.accio.desactivarOk'),
        },
        {
            label: t('page.entitats.accio.esborrar'),
            icon: 'delete',
            showInMenu: true,
            rowLink: 'delete',
            clickTriggerDelete: true,
        },
    ];
};

export default useEntitatAccions;
