import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useConfirmDialogButtons, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';

/** Codis de les accions declarades a EntitatResource (@ResourceArtifact de tipus ACTION). */
const ACCIO_ACTIVAR = 'ACTIVAR';
const ACCIO_DESACTIVAR = 'DESACTIVAR';
/** Codi de l'acció massiva declarada a AvisResource (@ResourceArtifact de tipus ACTION, requiresId=false). */
const ACCIO_MASSIVA = 'ACCIO_MASSIVA';

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
            hidden: (row: any) => row?.actiu,
            onClick: (id: any) => executarAccio(id, ACCIO_ACTIVAR, 'page.avisos.accio.activarOk'),
        },
        {
            label: t('page.avisos.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_DESACTIVAR,
            hidden: (row: any) => !row?.actiu,
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

/** Accions massives del manteniment d'avisos (activar/desactivar/esborrar sobre els avisos seleccionats). */
export const useAvisMassiveAccions = (refresh: () => void): { actions: MassiveActionProps[]; components: React.ReactElement } => {
    const { t } = useTranslation();
    // `tLib`: el `t` de useBaseAppContext (namespace `reactlib`), on viuen les claus genèriques `datacommon.*` de la llibreria.
    const { temporalMessageShow, messageDialogShow, t: tLib } = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons().reverse();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } =
        useResourceApiService('avisResource');

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
                temporalMessageShow(
                    t('page.avisos.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
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
                executarAccioMassiva(ids, 'eliminar', 'page.avisos.accio.esborrarMassiuOk');
            }
        });
    };

    const actions: MassiveActionProps[] = [
        {
            label: t('page.avisos.accio.activar'),
            icon: 'check',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: (ids: any[]) => executarAccioMassiva(ids, 'activar', 'page.avisos.accio.activarMassiuOk'),
        },
        {
            label: t('page.avisos.accio.desactivar'),
            icon: 'close',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: (ids: any[]) => executarAccioMassiva(ids, 'desactivar', 'page.avisos.accio.desactivarMassiuOk'),
        },
        {
            label: t('page.avisos.accio.esborrar'),
            icon: 'delete',
            showInMenu: true,
            action: ACCIO_MASSIVA,
            onClick: esborrarMassiu,
        },
    ];

    return { actions, components: <></> };
};

export default useAvisAccions;
