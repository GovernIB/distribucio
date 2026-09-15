import { useTranslation } from 'react-i18next';
import type { MuiDataGridProps } from 'reactlib';

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

/**
 * Accions del menú de cada fila del llistat de continguts: de moment només "Historial"
 * "Detalls" encara no esta migrat -- veure ContingutAdminController legacy
 */
export const useContingutAccions = (
    mostrarHistorial: (id: any, row: any) => void
): AccionsFila => {
    const { t } = useTranslation();
    return [
        {
            label: t('page.contingut.accio.historial.label'),
            icon: 'format_list_bulleted',
            showInMenu: true,
            onClick: (id: any, row: any) => mostrarHistorial(id, row),
        },
    ];
};

export default useContingutAccions;
