import React from 'react';
import { useTranslation } from 'react-i18next';
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
            >
                <Typography variant="body1" color="text.secondary">
                    {t('page.home.toolbar.subtitle')}
                </Typography>
            </ScreenFrame>
        </GridPage>
    );
};

export default Home;
