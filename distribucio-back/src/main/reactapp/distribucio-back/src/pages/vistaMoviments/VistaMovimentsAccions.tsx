import { useTranslation } from 'react-i18next';
import useContingutHistorialDialog from '../contingut/actions/ContingutHistorialDialog.tsx';

/** Menú d'accions de la "Vista de moviments" */
export const useVistaMovimentsAccions = () => {
    const { t } = useTranslation();

    const { show: handleHistoric, component: componentHistoric } = useContingutHistorialDialog();

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
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.alerta.label')}`);
            },
        },
        {
            label: t('page.vistaMoviments.accio.reenviar.label'),
            icon: 'send',
            showInMenu: true,
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.vistaMoviments.accio.reenviar.label')}`);
            },
        },
        {
            label: t('page.vistaMoviments.accio.email.label'),
            icon: 'mail',
            showInMenu: true,
            disabled: (row: any) => row.procesEstat === 'ARXIU_PENDENT',
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.vistaMoviments.accio.email.label')}`);
            },
        },
        {
            label: t('page.vistaMoviments.accio.descarregarOriginal.label'),
            icon: 'download',
            showInMenu: true,
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.vistaMoviments.accio.descarregarOriginal.label')}`);
            },
        },
        {
            label: t('page.vistaMoviments.accio.descarregarFirma.label'),
            icon: 'verified',
            showInMenu: true,
            onClick: () => {
                alert(`TODO: pendent d'implementar -> ${t('page.vistaMoviments.accio.descarregarFirma.label')}`);
            },
        },
    ];

    const components = <>{componentHistoric}</>;

    return {
        actions,
        components,
    };
};

export default useVistaMovimentsAccions;
