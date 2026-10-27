import { useTranslation } from 'react-i18next';
import { type MuiDataGridProps } from 'reactlib';
import { icons } from '../../util/icons';
import {useActions as useServeiActions} from "../servei/ServeiAccions.tsx";
import {useActions as useProcedimentActions } from "../procediment/ProcedimentAccions.tsx";

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useSiaAccions = (refresh: () => void): AccionsFila => {
    const { t } = useTranslation();

    const { actualitzar: actualitzarProcediment } = useProcedimentActions(refresh)
    const { actualitzar: actualitzarServei } = useServeiActions(refresh)

    return [
        {
            label: t('page.procediments.accio.actualitzar'),
            icon: icons.sincronitza,
            showInMenu: true,
            // action: ACTUALITZAR_PROCEDIMENT,
            onClick: actualitzarProcediment,
            hidden: (row:any) => row.tipus != 'PROCEDIMENT'
        },
        {
            label: t('page.serveis.accio.actualitzar'),
            icon: icons.sincronitza,
            showInMenu: true,
            // action: ACTUALITZAR_SERVEI,
            onClick: actualitzarServei,
            hidden: (row:any) => row.tipus != 'SERVEI'
        },
    ];
};

export default useSiaAccions;
