import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';

type IntegracioEstatProps = {
    value?: string;
    excepcioMessage?: string;
};

export const IntegracioEstat: React.FC<IntegracioEstatProps> = ({ value, excepcioMessage }) => {
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
                <Icon fontSize="small" sx={{ fontSize: '18px' }}>
                    check_circle
                </Icon>
                {value}
            </Box>
        );
    }
    if (value === 'ERROR') {
        return (
            <Box
                title={excepcioMessage}
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
                <Icon fontSize="small" sx={{ fontSize: '18px' }}>
                    warning
                </Icon>
                {value}
            </Box>
        );
    }
    return <>{value}</>;
};

export default IntegracioEstat;
