import React from 'react';
import { useTranslation } from 'react-i18next';
import { Icon, Tooltip } from '@mui/material';
import { useFormContext } from 'reactlib';
import GridFormField from './GridFormField';

type GridUnitatFieldProps = React.ComponentProps<typeof GridFormField> & {
    /** Estat de la unitat quan el valor no ve d'una selecció (p. ex. un valor ja desat): 'V' = vigent. */
    estatPerDefecte?: string;
};

const ICONA_OBSOLETA_SX = { ml: 0.5, verticalAlign: 'middle' };

/** Estat vigent d'una unitat organitzativa. Qualsevol altre (E, A, T) es considera obsolet. */
const isObsoleta = (estat?: string) => estat != null && estat !== 'V';

/**
 * Selector d'unitat organitzativa que marca amb una icona d'avís les unitats no vigents, tant a les
 * opcions del desplegable com al valor seleccionat (equivalent a `formatSelectUnitatItem` de les JSP).
 */
export const GridUnitatField: React.FC<GridUnitatFieldProps> = (props) => {
    const { estatPerDefecte, componentProps, ...otherProps } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();

    const avis = t('page.regla.unitatObsoleta');
    const estatSeleccionat = data?.[props.name]?.data?.estat ?? estatPerDefecte;

    return (
        <GridFormField
            {...otherProps}
            optionDataFields={['estat']}
            componentProps={{
                ...componentProps,
                renderOption: (liProps: any, option: any) => (
                    <li {...liProps} key={option.id}>
                        {option.description}
                        {isObsoleta(option.data?.estat) && (
                            <Tooltip title={avis}>
                                <Icon fontSize="small" color="inherit" sx={ICONA_OBSOLETA_SX}>
                                    warning
                                </Icon>
                            </Tooltip>
                        )}
                    </li>
                ),
                slotProps: {
                    ...componentProps?.slotProps,
                    input: {
                        ...componentProps?.slotProps?.input,
                        endAdornment:
                            data?.[props.name] != null && isObsoleta(estatSeleccionat) ? (
                                <Tooltip title={avis}>
                                    <Icon fontSize="small" color="warning">
                                        warning
                                    </Icon>
                                </Tooltip>
                            ) : (
                                componentProps?.slotProps?.input?.endAdornment
                            ),
                    },
                },
            }}
        />
    );
};

export default GridUnitatField;
