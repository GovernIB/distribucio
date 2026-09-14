import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';

type IntegracioEstatProps = {
    value?: string;
};

export const IntegracioEstat: React.FC<IntegracioEstatProps> = ({ value }) => {
    if (value === 'OK') {
        return (
            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 0.5,
                    p: '4px',
                    px: 1,
                    color: 'success.contrastText',
                    backgroundColor: 'success.main',
                    borderRadius: '4px',
                }}
            >
                <Icon fontSize="small">check_circle</Icon>
                {value}
            </Box>
        );
    }
    if (value === 'ERROR') {
        return (
            <Box
                sx={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 0.5,
                    p: '4px',
                    px: 1,
                    color: 'error.contrastText',
                    backgroundColor: 'error.main',
                    borderRadius: '4px',
                }}
            >
                <Icon fontSize="small">warning</Icon>
                {value}
            </Box>
        );
    }
    return <>{value}</>;
};

export default IntegracioEstat;
