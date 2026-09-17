import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import { iniciaDescargaBlob } from '../../util/downloadUtils';
import { ROLE_ADMIN, useDistribucioContext } from '../../components/DistribucioContext';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';
import { useExecucioMassivaGrid } from '../execucioMassiva/ExecucioMassivaGrid';

const ACTION_GUARDAR_DEFINITIU = 'GUARDAR_DEFINITIU';
const ACTION_COMPROVAR_PENDENTS = 'COMPROVAR_PENDENTS';
export const REPORT_DESCARREGAR_ORIGINAL = 'DESCARREGAR_ORIGINAL';
export const REPORT_DESCARREGAR_IMPRIMIBLE = 'DESCARREGAR_IMPRIMIBLE';
export const REPORT_DESCARREGAR_FIRMA = 'DESCARREGAR_FIRMA';

/**
 * Descàrrega d'un annex (original/imprimible) o d'una firma individual (`data: { firmaIndex }`);
 * compartit entre el menú de la graella i el diàleg de detalls.
 */
export const useDescarregarAnnex = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { artifactReport: apiArtifactReport } = useResourceApiService('registreAnnexResource');

    return (id: any, code: string, data?: any, setLoading?: (loading: boolean) => void) => {
        setLoading?.(true);
        apiArtifactReport(id, { code, fileType: 'PDF', data })
            .then((result: any) => iniciaDescargaBlob(result))
            .catch((error: any) =>
                temporalMessageShow(t('page.annex.accio.error'), error?.description ?? error?.message, 'error')
            )
            .finally(() => setLoading?.(false));
    };
};

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useAnnexAccions = (mostrarDetall: (id: any, row: any) => void, refresh?: () => void): AccionsFila => {
    const { t } = useTranslation();
    const descarregar = useDescarregarAnnex();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const { temporalMessageShow } = useBaseAppContext();
    const { artifactAction } = useResourceApiService('registreAnnexResource');

    const guardarDefinitiu = (id: any) => {
        artifactAction(id, { code: ACTION_GUARDAR_DEFINITIU })
            .then((result: any) => {
                refresh?.();
                temporalMessageShow(
                    null,
                    t(`page.annex.accio.guardarDefinitiu.${result.keyMessage}`, {
                        titol: result.annexTitol,
                        numero: result.anotacioNumero,
                    }),
                    result.ok ? 'success' : result.error ? 'error' : 'warning'
                );
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.annex.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    return [
        {
            label: t('page.annex.accio.detallsAnotacio'),
            icon: 'adjust',
            showInMenu: true,
            hidden: () => !isAdmin,
            onClick: () => alert("TODO: Pendent d'implementar!!"),
        },
        {
            label: t('page.annex.accio.detalls'),
            icon: 'info',
            showInMenu: true,
            onClick: (id: any, row: any) => mostrarDetall(id, row),
        },
        {
            label: t('page.annex.accio.concsv'),
            icon: 'open_in_new',
            showInMenu: true,
            linkTo: (row: any) => row?.concsvUrl,
            linkTarget: '_blank',
            hidden: (row: any) => !row?.concsvUrl,
        },
        {
            label: t('page.annex.accio.descarregarOriginal'),
            icon: 'download',
            showInMenu: true,
            onClick: (id: any) => descarregar(id, REPORT_DESCARREGAR_ORIGINAL),
        },
        {
            label: t('page.annex.accio.descarregarImprimible'),
            icon: 'print',
            showInMenu: true,
            onClick: (id: any) => descarregar(id, REPORT_DESCARREGAR_IMPRIMIBLE),
            hidden: (row: any) => !row?.potGenerarVersioImprimible,
        },
        {
            label: t('page.annex.accio.guardarDefinitiu.label'),
            icon: 'edit_note',
            showInMenu: true,
            hidden: (row: any) => !isAdmin || row?.arxiuEstat === 'DEFINITIU',
            onClick: (id: any) => guardarDefinitiu(id),
        },
    ];
};

/**
 * Acció massiva "Custòdia" de la barra d'eines.
 * No reprocessa res: crea una ExecucioMassivaDto de tipus CUSTODIAR amb els annexos seleccionats,
 * igual que `ExecucioMassivaController.crearExecucioMassivaAnnexos` a la JSP, i delega el seguiment
 * al diàleg genèric d'Execucions Massives ja migrat.
 */
export const useAnnexMassiveActions = (): { actions: MassiveActionProps[]; components: ReactNode } => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const { temporalMessageShow } = useBaseAppContext();
    const { getOne: getAnnex } = useResourceApiService('registreAnnexResource');
    const { create: crearExecucio, artifactAction: execucioMassivaAction } =
        useResourceApiService('execucioMassivaResource');
    const { create: crearContingut } = useResourceApiService('execucioMassivaContingutResource');
    const { handleOpen: handleExecucioMassiva, component: componentExecucioMassiva } = useExecucioMassivaGrid();

    // Si algun dels annexos seleccionats ja forma part d'una execució massiva pendent/en curs, es bloqueja
    // la creació d'una de nova i es mostren els noms dels elements en conflicte.
    const custodiarMassiu = (ids: any[]) => {
        if (!ids?.length) {
            return;
        }
        execucioMassivaAction(undefined, { code: ACTION_COMPROVAR_PENDENTS, data: { elementIds: ids } })
            .then((elementsPendents: any) => {
                if (elementsPendents?.length) {
                    temporalMessageShow(
                        null,
                        t('page.annex.accio.guardarDefinitiuMultiple.duplicat', {
                            elements: elementsPendents.join(', '),
                        }),
                        'warning'
                    );
                    return;
                }
                return Promise.all(ids.map((id) => getAnnex(id)))
                    .then((annexos: any[]) =>
                        crearExecucio({ data: { tipus: 'CUSTODIAR' } }).then((execucio: any) =>
                            Promise.all(
                                annexos.map((annex) =>
                                    crearContingut({
                                        data: {
                                            elementId: annex.id,
                                            elementNom: annex.fitxerNom,
                                            elementTipus: 'ANNEX',
                                            execucioMassiva: { id: execucio.id },
                                        },
                                    })
                                )
                            )
                        )
                    )
                    .then(() => handleExecucioMassiva());
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.annex.accio.error'), error?.description ?? error?.message, 'error')
            );
    };

    const actions: MassiveActionProps[] = [
        {
            label: t('page.annex.accio.guardarDefinitiuMultiple.label'),
            icon: 'edit_note',
            showInMenu: true,
            hidden: !isAdmin,
            onClick: custodiarMassiu,
        },
    ];

    return {
        actions,
        components: componentExecucioMassiva,
    };
};

export default useAnnexAccions;
