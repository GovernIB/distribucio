import { ReactNode } from 'react';
import {Box} from "@mui/material";

export const ErrorArea = ({ children, ...other }: { children: ReactNode, [key: string]: any }) => {
    return <Box
        {...other}
        sx={{
            border: 'solid 1px',
            borderRadius: '4px',
            display: 'block',
            overflow: 'auto',
            whiteSpace: 'pre',
            fontFamily: 'monospace', // opcional para parecer <pre>
            p: 1,
            ...other.sx,
        }}
    >
        {children}
    </Box>
}