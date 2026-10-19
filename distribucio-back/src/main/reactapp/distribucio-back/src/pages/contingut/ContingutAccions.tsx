import { useTranslation } from 'react-i18next';
import type { MuiDataGridProps } from 'reactlib';
import { icons } from '../../util/icons';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/** Accions del menú de cada fila del llistat de continguts: "Detalls" i "Historial" */
export const useContingutAccions = (
    mostrarDetall: (id: any, row: any) => void,
    mostrarHistorial: (id: any, row: any) => void
): AccionsFila => {
    const { t } = useTranslation();
    return [
        {
            label: t('page.contingut.accio.detalls.label'),
            icon: icons.detall,
            showInMenu: true,
            onClick: (id: any, row: any) => mostrarDetall(id, row),
        },
        {
            label: t('page.contingut.accio.historial.label'),
            icon: icons.llistaPunts,
            showInMenu: true,
            onClick: (id: any, row: any) => mostrarHistorial(id, row),
        },
    ];
};

export default useContingutAccions;
