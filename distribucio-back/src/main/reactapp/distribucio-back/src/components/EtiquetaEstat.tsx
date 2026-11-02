import React from 'react';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import { isTonalColor, tonal, tonalNeutre } from '../util/tonal';

type EtiquetaEstatProps = {
    /** Color semàntic. Sense color (o amb un que no és tonal) es pinta la variant neutra. */
    color?: string;
    /** Nom de la icona (Material Icons) que precedeix el text. */
    icon?: string;
    /** Text del tooltip. Per defecte, el text de l'etiqueta. */
    title?: string;
    children?: React.ReactNode;
};

/**
 * Etiqueta d'estat en color tonal (vegeu util/tonal): icona + text petit sobre un fons suau. Mateix aspecte que
 * l'`EstatMessage` (StyledLabel + classe `.myLabel`) de RIPEA.
 */
export const EtiquetaEstat: React.FC<EtiquetaEstatProps> = ({ color, icon, title, children }) => {
    const theme = useTheme();
    return (
        <Typography
            variant="caption"
            title={title ?? (typeof children === 'string' ? children : undefined)}
            sx={[
                {
                    display: 'flex',
                    alignItems: 'center',
                    width: 'max-content',
                    p: '4px 8px',
                    borderRadius: '5px',
                },
                isTonalColor(color) ? tonal(color)(theme) : tonalNeutre(theme),
            ]}
        >
            {icon && (
                <Icon sx={{ fontSize: 18, mr: children != null ? 1 : 0 }}>
                    {icon}
                </Icon>
            )}
            {children}
        </Typography>
    );
};

export default EtiquetaEstat;
