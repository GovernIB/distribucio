import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import { iniciaDescargaBlob } from '../../util/downloadUtils';
import { ROLE_ADMIN, useDistribucioContext } from '../../components/DistribucioContext';
import type { MassiveActionProps } from '../../components/MassiveActionSelector';

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

    return (id: any, code: string, data?: any) => {
        apiArtifactReport(id, { code, fileType: 'PDF', data })
            .then((result: any) => iniciaDescargaBlob(result))
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.annex.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
};

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/**
 * Accions del menú per fila (annexosAdminList.jsp: tota la columna d'accions només es mostra si
 * `isRolActualAdministrador`, però aquí només apliquem aquesta restricció a les accions noves
 * ("Detalls de l'anotació" / "Custòdia") -- les ja existents es mantenen visibles també per
 * ROLE_ADMIN_LECTURA fins que es decideixi replicar la restricció completa de la JSP).
 */
export const useAnnexAccions = (mostrarDetall: (id: any, row: any) => void): AccionsFila => {
    const { t } = useTranslation();
    const descarregar = useDescarregarAnnex();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;

    return [
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
            label: t('page.annex.accio.detallsAnotacio'),
            icon: 'dot_circle',
            showInMenu: true,
            hidden: () => !isAdmin,
            onClick: () => alert('TODO: Pendent d\'implementar!!'),
        },
        {
            label: t('page.annex.accio.guardarDefinitiu'),
            icon: 'edit_note',
            showInMenu: true,
            hidden: (row: any) => !isAdmin || row?.arxiuEstat === 'DEFINITIU',
            onClick: () => alert('TODO: Pendent d\'implementar!!'),
        },
    ];
};

/**
 * Acció massiva "Custòdia" de la barra d'eines (annexosAdminList.jsp: botó "Guardar com a definitiu
 * (múltiple)" dins el desplegable d'accions massives, només visible si `isRolActualAdministrador`).
 */
export const useAnnexMassiveActions = (): MassiveActionProps[] => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;

    return [
        {
            label: t('page.annex.accio.guardarDefinitiuMultiple'),
            icon: 'edit_note',
            showInMenu: true,
            hidden: !isAdmin,
            onClick: () => alert('TODO: Pendent d\'implementar!!'),
        },
    ];
};

export default useAnnexAccions;
