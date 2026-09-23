import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';

const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';

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

/** Accions massives del manteniment de regles: totes pendents de desenvolupar (incloent Esborrar). */
export const useReglaMassiveAccions = (): {
    actions: MassiveActionProps[];
    components: React.ReactElement;
} => {
    const { t } = useTranslation();

    const actions: MassiveActionProps[] = [
        {
            label: t('page.regla.accio.activar'),
            icon: 'check',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.activar')),
        },
        {
            label: t('page.regla.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.desactivar')),
        },
        {
            label: t('page.regla.accio.esborrarMassiu'),
            icon: 'delete',
            showInMenu: true,
            onClick: accioPendent(t('page.regla.accio.esborrarMassiu')),
        },
    ];

    return { actions, components: <></> };
};

export default useReglaAccions;
