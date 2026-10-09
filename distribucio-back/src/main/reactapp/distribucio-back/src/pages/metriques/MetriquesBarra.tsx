import React from 'react';
import Box from '@mui/material/Box';
import type { SxProps, Theme } from '@mui/material/styles';

type MetriquesBarraProps = {
    /** Ample del segment, en % de la barra. */
    ample: number;
    color: 'success' | 'error' | 'info';
    /** Segment ratllat (temps mig), com el `.mitja` de l'antiga interfície. */
    ratllat?: boolean;
    children?: React.ReactNode;
};

/** Segment d'una barra de mètriques: ample proporcional, color del tema i text a dins. */
export const MetriquesBarraSegment: React.FC<MetriquesBarraProps> = ({ ample, color, ratllat, children }) => (
    <Box
        sx={(theme) => ({
            width: `${Math.max(0, Math.min(100, ample))}%`,
            minWidth: 0,
            overflow: 'hidden',
            whiteSpace: 'nowrap',
            textAlign: 'center',
            lineHeight: '20px',
            fontSize: '12px',
            color: theme.palette[color].contrastText,
            backgroundColor: theme.palette[color].main,
            backgroundImage: ratllat
                ? 'linear-gradient(45deg, rgba(255,255,255,0.25) 25%, transparent 25%, transparent 50%, rgba(255,255,255,0.25) 50%, rgba(255,255,255,0.25) 75%, transparent 75%, transparent)'
                : undefined,
            backgroundSize: ratllat ? '40px 40px' : undefined,
        })}
    >
        {children}
    </Box>
);

/** Contenidor d'una barra (un o diversos segments). */
export const MetriquesBarra: React.FC<{ children: React.ReactNode; sx?: SxProps<Theme> }> = ({ children, sx }) => (
    <Box
        sx={[
            (theme) => ({
                display: 'flex',
                width: '100%',
                height: '20px',
                borderRadius: '4px',
                overflow: 'hidden',
                backgroundColor: theme.palette.action.hover,
            }),
            ...(Array.isArray(sx) ? sx : [sx]),
        ]}
    >
        {children}
    </Box>
);
