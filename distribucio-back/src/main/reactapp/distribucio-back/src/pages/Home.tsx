import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Typography from '@mui/material/Typography';
import { GridPage } from 'reactlib';
import ScreenFrame from '../components/ScreenFrame';
import { icons } from '../util/icons';

const Home: React.FC = () => {
    const { t } = useTranslation();
    return (
        <GridPage>
            <ScreenFrame
                fitContent
                icon={icons.inici}
                title={t('page.home.toolbar.title')}
                actions={
                    <Chip
                        size="small"
                        variant="outlined"
                        color="primary"
                        label={`v${import.meta.env.VITE_APP_VERSION}`}
                        sx={(theme) => ({
                            bgcolor: theme.palette.primarySoft,
                            ...(theme.palette.mode === 'dark' && { color: theme.palette.text.primary }),
                        })}
                    />
                }
            >
                <Typography variant="body1" color="text.secondary">
                    {t('page.home.toolbar.subtitle')}
                </Typography>
            </ScreenFrame>
        </GridPage>
    );
};

export default Home;
