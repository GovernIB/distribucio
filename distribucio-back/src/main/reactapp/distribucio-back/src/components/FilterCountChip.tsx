import { Chip, Icon } from '@mui/material';
import { useTranslation } from 'react-i18next';

/**
 * Xip amb el nombre de filtres aplicats, per a la barra d'eines dels llistats.
 *
 * Port de la part corresponent de `src/components/StyledMuiGrid.tsx` de RIPEA (countTopLevel /
 * resolveFilterCount / FilterCountChip). A DISTRIBUCIO encara no hi ha `StyledMuiGrid`, així que
 * viu en un fitxer propi i les pantalles l'injecten a la graella per `toolbarElementsWithPositions`.
 */

/**
 * Compta els operands de primer nivell d'una cadena Spring Filter: treu els parèntesis que
 * envolten tota l'expressió, ignora el contingut dels parèntesis interiors i compta els
 * AND/OR que queden a fora.
 */
export const countTopLevel = (filter?: string): number => {
    if (!filter || !filter?.trim()) return 0;
    let str = filter.trim();

    if (str.startsWith('(') && str.endsWith(')')) {
        let bal = 0;
        for (let i = 0; i < str.length; i++) {
            bal += str[i] === '(' ? 1 : str[i] === ')' ? -1 : 0;
            if (bal === 0 && i < str.length - 1) {
                bal = 1;
                break;
            }
        }
        if (bal === 0) str = str.slice(1, -1);
    }

    let depth = 0;
    let flat = '';
    for (let i = 0; i < str.length; i++) {
        const c = str[i];
        if (c === '(') depth++;
        else if (c === ')') depth--;
        else if (depth === 0) flat += c;
    }

    const matches = flat.match(/(?<=\s)(AND|OR)(?=\s)/gi);
    return matches ? matches.length + 1 : 1;
};

type FilterCount = number | ((num: number) => number);

/** Nombre de filtres aplicats: el compte del filtre spring, ajustat si es rep una funció. */
export const resolveFilterCount = (filter?: string, filterCount?: FilterCount): number =>
    typeof filterCount === 'function' ? filterCount(countTopLevel(filter)) : (filterCount ?? countTopLevel(filter));

export const FilterCountChip = (props: { filter?: string; filterCount?: FilterCount; sx?: any }) => {
    const { filter, filterCount, sx } = props;
    const { t } = useTranslation();
    const num = resolveFilterCount(filter, filterCount);

    if (!num) {
        return null;
    }

    return (
        <Chip
            // `count` és el que fa servir i18next per a triar la forma singular/plural;
            // `num` és el que interpola el text de la clau.
            label={t('common.filterCount', { num, count: num })}
            size="small"
            icon={<Icon>filter_alt</Icon>}
            color="primary"
            sx={{ px: 0.5, ...sx }}
        />
    );
};

export default FilterCountChip;
