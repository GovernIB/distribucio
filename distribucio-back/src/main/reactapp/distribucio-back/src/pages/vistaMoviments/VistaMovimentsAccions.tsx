import { useTranslation } from 'react-i18next';
import { useBaseAppContext } from 'reactlib';
import useContingutHistorialDialog from '../contingut/actions/ContingutHistorialDialog.tsx';
import { useAlertes } from '../registre/detail/Alertes.tsx';
import useEnviarViaEmail from './actions/EnviarEmail.tsx';
import useDescarregarZip from './actions/Descarregar.tsx';
import useReenviar from './actions/Reenviar.tsx';

const REPORT_DESCARREGAR_ZIP_ORIGINAL = 'DESCARREGAR_ZIP_ORIGINAL';
const REPORT_DESCARREGAR_ZIP_CAI = 'DESCARREGAR_ZIP_CAI';

/** Menú d'accions de la "Vista de moviments" */
export const useVistaMovimentsAccions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const { show: handleHistoric, component: componentHistoric } = useContingutHistorialDialog();
    const { handleOpen: handleAlertes, component: componentAlertes } = useAlertes(refresh);
    const descarregarZip = useDescarregarZip();
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail((result: any) => {
        refresh?.();
        temporalMessageShow(null, t('page.vistaMoviments.accio.email.ok', { numero: result.numero }), 'success');
    });
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar((result: any) => {
        refresh?.();
        temporalMessageShow(null, t('page.vistaMoviments.accio.reenviar.ok', { numero: result.numero }), 'success');
    });

    const actions: any[] = [
        {
            label: t('page.vistaMoviments.accio.detalls.label'),
            icon: 'info',
            showInMenu: true,
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.vistaMoviments.accio.detalls.label')}`);
            },
        },
        {
            label: t('page.contingut.accio.historial.label'),
            icon: 'list',
            showInMenu: true,
            onClick: (_id: any, row: any) => handleHistoric(row.idRegistre, { id: row.idRegistre, nom: row.numero }),
        },
        {
            label: t('page.alerta.label'),
            icon: 'note_stack',
            showInMenu: true,
            hidden: (row: any) => !row.alertesPendents,
            // Les alertes pengen de l'anotació (idRegistre)
            onClick: (_id: any, row: any) => handleAlertes(row.idRegistre),
        },
        {
            label: t('page.vistaMoviments.accio.reenviar.label'),
            icon: 'send',
            showInMenu: true,
            onClick: (id: any) => handleReenviar(id),
        },
        {
            label: t('page.vistaMoviments.accio.email.label'),
            icon: 'mail',
            showInMenu: true,
            disabled: (row: any) => row.procesEstat === 'ARXIU_PENDENT',
            onClick: (id: any) => handleEnviarEmail(id),
        },
        {
            label: t('page.vistaMoviments.accio.descarregarOriginal.label'),
            icon: 'download',
            showInMenu: true,
            onClick: (id: any) => descarregarZip(id, REPORT_DESCARREGAR_ZIP_ORIGINAL),
        },
        {
            label: t('page.vistaMoviments.accio.descarregarCai.label'),
            icon: 'download',
            showInMenu: true,
            onClick: (id: any) => descarregarZip(id, REPORT_DESCARREGAR_ZIP_CAI),
        },
    ];

    const components = (
        <>
            {componentHistoric}
            {componentAlertes}
            {contentEnviarEmail}
            {contentReenviar}
        </>
    );

    return {
        actions,
        components,
    };
};

/**
 * Missatge de resultat d'una acció massiva: comptadors d'èxit/total i, si n'hi ha hagut, el detall
 * dels errors ("count"/"total"/"errors").
 */
const missatgeResultatMassiu = (t: any, keyPrefix: string, result: any): string => {
    const count = result?.count ?? 0;
    const total = result?.total ?? count;
    return result?.errors
        ? t(`${keyPrefix}.okMassiuAmbErrors`, { count, total, errors: result.errors })
        : t(`${keyPrefix}.okMassiu`, { count, total });
};

/** Accions múltiples de la "Vista de moviments" (només Reenviar i Enviar per email, com a la JSP antiga) */
export const useVistaMovimentsMassiveAccions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const { handleShowMassive: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail((result: any) => {
        refresh?.();
        temporalMessageShow(
            null,
            missatgeResultatMassiu(t, 'page.vistaMoviments.accio.email', result),
            result?.errors ? 'warning' : 'success'
        );
    });
    const { handleShowMassive: handleReenviar, content: contentReenviar } = useReenviar((result: any) => {
        refresh?.();
        temporalMessageShow(
            null,
            missatgeResultatMassiu(t, 'page.vistaMoviments.accio.reenviar', result),
            result?.errors ? 'warning' : 'success'
        );
    });

    const actions: any[] = [
        {
            label: t('page.vistaMoviments.accio.reenviar.label'),
            icon: 'send',
            showInMenu: true,
            onClick: (ids: any[]) => handleReenviar(ids),
        },
        {
            label: t('page.vistaMoviments.accio.email.label'),
            icon: 'mail',
            showInMenu: true,
            onClick: (ids: any[]) => handleEnviarEmail(ids),
        },
    ];

    const components = (
        <>
            {contentEnviarEmail}
            {contentReenviar}
        </>
    );

    return {
        actions,
        components,
    };
};

export default useVistaMovimentsAccions;
