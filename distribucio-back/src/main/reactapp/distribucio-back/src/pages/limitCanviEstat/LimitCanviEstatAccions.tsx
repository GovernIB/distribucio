import { useTranslation } from 'react-i18next';
import { icons } from '../../util/icons';

export const useLimitCanviEstatAccions = () => {
    const { t } = useTranslation();

    return [
        {
            label: t('page.entitats.accio.modificar'),
            icon: icons.modifica,
            showInMenu: true,
            rowLink: 'update',
            clickShowUpdateDialog: true,
        },
        {
            label: t('page.entitats.accio.esborrar'),
            icon: icons.esborra,
            showInMenu: true,
            rowLink: 'delete',
            clickTriggerDelete: true,
        },
    ];
};

export default useLimitCanviEstatAccions;
