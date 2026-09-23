import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useConfirmDialogButtons, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';

const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';
const ACCIO_MASSIVA = 'ACCIO_MASSIVA';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/**
 * Mostra un avís que l'acció encara no s'ha desenvolupat, en lloc de trucar l'API. Substitueix el
 * `ActionExecutor` real fins que es migri la lògica de cada acció .
 */
const accioPendent = (label: string) => () => {
    alert(`TODO: pendent d'implementar -> ${label}`);
};

/** Menú d'accions de fila del manteniment de regles. Modificar, Esborrar, Activar i Desactivar són reals. */
export const useReglaAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('reglaResource');

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
                temporalMessageShow(t('page.regla.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    return [
        {
            label: t('page.regla.accio.modificar'),
            icon: 'edit',
            showInMenu: true,
            rowLink: 'update',
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.regla.accio.aplicarManualment'),
            icon: 'settings',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.aplicarManualment')),
        },
        {
            label: t('page.regla.accio.amunt'),
            icon: 'arrow_upward',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.amunt')),
        },
        {
            label: t('page.regla.accio.avall'),
            icon: 'arrow_downward',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.avall')),
        },
        {
            label: t('page.regla.accio.activar'),
            icon: 'check',
            showInMenu: true,
            action: ACCIO_ACTIVAR,
            hidden: (row: any) => row?.activa,
            onClick: (id: any) => executarAccio(id, ACCIO_ACTIVAR, 'page.regla.accio.activarOk'),
        },
        {
            label: t('page.regla.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_DESACTIVAR,
            hidden: (row: any) => !row?.activa,
            onClick: (id: any) => executarAccio(id, ACCIO_DESACTIVAR, 'page.regla.accio.desactivarOk'),
        },
        {
            label: t('page.regla.accio.esborrar'),
            icon: 'delete',
            showInMenu: true,
            rowLink: 'delete',
            clickTriggerDelete: true,
        },
    ];
};

/** Accions massives del manteniment de regles: activar/desactivar/esborrar sobre les regles seleccionades. */
export const useReglaMassiveAccions = (
    refresh: () => void
): {
    actions: MassiveActionProps[];
    components: React.ReactElement;
} => {
    const { t } = useTranslation();
    const { temporalMessageShow, messageDialogShow, t: tLib } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('reglaResource');

    const executarAccioMassiva = (ids: any[], accio: string, clauMissatgeOk: string) => {
        if (!apiIsReady) {
            return;
        }
        apiArtifactAction(undefined, { code: ACCIO_MASSIVA, data: { accio, ids } })
            .then(() => {
                refresh();
                temporalMessageShow(null, t(clauMissatgeOk), 'success');
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.regla.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    const esborrarMassiu = (ids: any[]) => {
        messageDialogShow(
            tLib('datacommon.delete.multiple.label'),
            tLib('datacommon.delete.multiple.confirm', { count: ids.length }),
            confirmDialogButtons,
            { maxWidth: 'sm', fullWidth: true }
        ).then((value: any) => {
            if (value) {
                executarAccioMassiva(ids, 'eliminar', 'page.regla.accio.esborrarMassiuOk');
            }
        });
    };

    const actions: MassiveActionProps[] = [
        {
            label: t('page.regla.accio.activar'),
            icon: 'check',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: (ids: any[]) => executarAccioMassiva(ids, 'activar', 'page.regla.accio.activarMassiuOk'),
        },
        {
            label: t('page.regla.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: (ids: any[]) => executarAccioMassiva(ids, 'desactivar', 'page.regla.accio.desactivarMassiuOk'),
        },
        {
            label: t('page.regla.accio.esborrarMassiu'),
            icon: 'delete',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: esborrarMassiu,
        },
    ];

    return { actions, components: <></> };
};

export default useReglaAccions;
