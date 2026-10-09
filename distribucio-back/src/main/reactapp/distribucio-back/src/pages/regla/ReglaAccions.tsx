import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useConfirmDialogButtons, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';

const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';
const ACCIO_MASSIVA = 'ACCIO_MASSIVA';
const ACCIO_AMUNT = 'AMUNT';
const ACCIO_AVALL = 'AVALL';
const ACCIO_MOURE = 'MOURE';
const ACCIO_APLICAR_MANUALMENT = 'APLICAR_MANUALMENT';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useReglaAccions = (refresh: () => void, aplicarManualment: (id: any) => void): AccionsFila => {
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
            action: ACCIO_APLICAR_MANUALMENT,
            disabled: (row: any) => !row?.activa,
            onClick: (id: any) => aplicarManualment(id),
        },
        {
            label: t('page.regla.accio.amunt'),
            icon: 'arrow_upward',
            showInMenu: true,
            action: ACCIO_AMUNT,
            hidden: (row: any) => row?.ordre === 0,
            onClick: (id: any) => executarAccio(id, ACCIO_AMUNT, 'page.regla.accio.amuntAvallOk'),
        },
        {
            label: t('page.regla.accio.avall'),
            icon: 'arrow_downward',
            showInMenu: true,
            action: ACCIO_AVALL,
            hidden: (row: any) => row?.ordre === (row?.totalRegles ?? 1) - 1,
            onClick: (id: any) => executarAccio(id, ACCIO_AVALL, 'page.regla.accio.amuntAvallOk'),
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

/**
 * Handler de `onRowOrderChange` de la graella (drag&drop de files): tradueix l'`targetIndex` (relatiu a
 * la pàgina visible) a una posició absoluta i crida l'acció `MOURE`. `datagridApiRef` es fa servir només
 * per llegir la pàgina/mida de pàgina actuals en el moment de mollar la fila.
 */
export const useReglaRowOrderChange = (refresh: () => void, datagridApiRef: any) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('reglaResource');

    return (params: { row: any; targetIndex: number }) => {
        if (!apiIsReady) {
            refresh();
            return;
        }
        const paginationModel = datagridApiRef.current?.state?.pagination?.paginationModel;
        const page = paginationModel?.page ?? 0;
        const pageSize = paginationModel?.pageSize ?? 0;
        const posicio = page * pageSize + params.targetIndex;
        apiArtifactAction(params.row.id, { code: ACCIO_MOURE, data: { posicio } })
            .then(() => refresh())
            .catch((error: any) => {
                temporalMessageShow(t('page.regla.accio.error'), error?.description ?? error?.message, 'error');
                refresh();
            });
    };
};

export default useReglaAccions;
