import { useTranslation } from 'react-i18next';

export const useLimitCanviEstatAccions = () => {
    const { t } = useTranslation();

    return [
        {
            label: t('page.entitats.accio.modificar'),
            icon: 'edit',
            showInMenu: true,
            rowLink: 'update',
            clickShowUpdateDialog: true,
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

export default useLimitCanviEstatAccions;
