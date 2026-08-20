import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { BasePage } from 'reactlib';

/**
 * Pàgina de missatge a tota plana. Amb `message` serveix també per al "no autoritzat" del
 * ProtectedRoute, igual que fa el NotFound de RIPEA.
 */
export type NotFoundProps = {
    message?: string;
    variant?: 'h2' | 'h3' | 'h4' | 'h5';
};

const NotFound: React.FC<NotFoundProps> = ({ message, variant = 'h4' }) => {
    const { t } = useTranslation();
    return (
        <BasePage expandHeight>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    textAlign: 'center',
                    height: '100%',
                    px: 2,
                }}
            >
                <Typography variant={variant}>{message ?? t('page.notFound.message')}</Typography>
            </Box>
        </BasePage>
    );
};

export default NotFound;
