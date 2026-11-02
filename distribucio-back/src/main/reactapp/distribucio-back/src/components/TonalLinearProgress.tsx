import React from 'react';
import Box from '@mui/material/Box';
import LinearProgress, { type LinearProgressProps } from '@mui/material/LinearProgress';
import type { SxProps, Theme } from '@mui/material/styles';
import { type TonalColor, tonalProgress } from '../util/tonal';

type TonalLinearProgressProps = Omit<LinearProgressProps, 'color' | 'variant'> & {
    color?: TonalColor;
    /** Estils del contenidor (posició, marges...). */
    boxSx?: SxProps<Theme>;
    /**
     * Text sobre la barra, normalment el percentatge. Obligatori a la pràctica: la barra tonal no arriba al
     * contrast de 3:1 i el nivell no es pot transmetre només amb el color (vegeu util/tonal).
     */
    children?: React.ReactNode;
};

/**
 * Barra de progrés en color tonal amb el valor escrit a sobre, en el color de text del tema. El valor ja
 * l'anuncia el lector de pantalla a través de l'`aria-valuenow` de la barra; el text és només visual.
 */
export const TonalLinearProgress: React.FC<TonalLinearProgressProps> = (props) => {
    const { color = 'success', boxSx, sx, children, ...other } = props;

    return (
        <Box position="relative" width="100%" sx={boxSx}>
            <LinearProgress
                variant="determinate"
                {...other}
                sx={[
                    { width: '100%', borderRadius: '4px' },
                    tonalProgress(color),
                    ...(sx === undefined ? [] : Array.isArray(sx) ? sx : [sx]),
                ]}
            />
            <Box
                color="text.primary"
                fontWeight={500}
                position="absolute"
                top={0}
                left={0}
                width="100%"
                height="100%"
                display="flex"
                alignItems="center"
                justifyContent="center"
                aria-hidden
            >
                {children}
            </Box>
        </Box>
    );
};

export default TonalLinearProgress;
