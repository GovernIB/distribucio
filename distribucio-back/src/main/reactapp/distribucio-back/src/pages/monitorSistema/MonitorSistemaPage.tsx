import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage } from 'reactlib';
import Box from '@mui/material/Box';
import { CardPage } from '../../components/CardData';
import TabComponent from '../../components/TabComponent';
import MonitorSistemaSistema from './MonitorSistemaSistema';
import MonitorSistemaFils from './MonitorSistemaFils';
import MonitorSistemaTasques from './MonitorSistemaTasques';

export const MonitorSistemaPage: React.FC = () => {
    const { t } = useTranslation();

    const tabs = [
        {
            value: 'sistema',
            label: t('page.monitorSistema.tabs.sistema'),
            content: <MonitorSistemaSistema />,
        },
        {
            value: 'fils',
            label: t('page.monitorSistema.tabs.fils'),
            content: <MonitorSistemaFils />,
        },
        {
            value: 'tasques',
            label: t('page.monitorSistema.tabs.tasques'),
            content: <MonitorSistemaTasques />,
        },
    ];

    return (
        <GridPage>
            <CardPage title={t('page.monitorSistema.title')}>
                {/* Alçada fixa per a les pestanyes amb graella: les graelles s'adapten a l'alçada del contenidor. */}
                <Box sx={{ height: 'calc(100vh - 230px)', minHeight: '420px' }}>
                    <TabComponent tabs={tabs} />
                </Box>
            </CardPage>
        </GridPage>
    );
};

export default MonitorSistemaPage;
