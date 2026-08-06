import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { BasePage } from 'reactlib';

const NotFound: React.FC = () => {
    const { t } = useTranslation();
    return (
        <BasePage expandHeight>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100%',
                }}
            >
                <Typography variant="h4">{t('page.notFound.message')}</Typography>
            </Box>
        </BasePage>
    );
};

export default NotFound;
