import { useTranslation } from 'react-i18next';
import { icons } from '../../util/icons';
import useContingutHistorialDialog from "./actions/ContingutHistorialDialog.tsx";
import useContingutDetailDialog from "./actions/ContingutDetailDialog.tsx";

/** Accions del menú de cada fila del llistat de continguts: "Detalls" i "Historial" */
export const useContingutAccions = () => {
    const { t } = useTranslation();

    const { show: mostrarDetall, component: detailDialog } = useContingutDetailDialog();
    const { show: mostrarHistorial, component: historialDialog } = useContingutHistorialDialog();

    const actions = [
        {
            label: t('page.contingut.accio.detalls.label'),
            icon: icons.detall,
            showInMenu: true,
            onClick: mostrarDetall,
        },
        {
            label: t('page.contingut.accio.historial.label'),
            icon: icons.llistaPunts,
            showInMenu: true,
            onClick: mostrarHistorial,
        },
    ];

    const components = <>
        {historialDialog}
        {detailDialog}
    </>

    return {
        actions,
        components
    }
};

export default useContingutAccions;
