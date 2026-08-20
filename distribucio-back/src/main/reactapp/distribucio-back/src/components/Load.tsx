import React from 'react';
import { BasePage } from 'reactlib';
import { Box, CircularProgress } from '@mui/material';

/**
 * Mostra els fills només quan `value` ja té valor; mentrestant, un indicador de càrrega
 * (o res, amb `noEffect`).
 *
 * Port de `src/components/Load.tsx` de RIPEA.
 */
export type LoadProps = React.PropsWithChildren & {
    value: any;
    noEffect?: boolean;
};

const Load: React.FC<LoadProps> = (props: LoadProps) => {
    const { value, noEffect, children } = props;

    if (value) {
        return children;
    }

    if (noEffect) {
        return <></>;
    }

    return (
        <BasePage>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                }}
            >
                <CircularProgress />
            </Box>
        </BasePage>
    );
};

export default Load;
