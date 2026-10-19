import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage } from 'reactlib';
import Box from '@mui/material/Box';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
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
            <ScreenFrame icon={icons.monitorSistema} title={t('page.monitorSistema.title')}>
                <Box sx={{ flex: '1 1 0', minHeight: '420px' }}>
                    <TabComponent tabs={tabs} />
                </Box>
            </ScreenFrame>
        </GridPage>
    );
};

export default MonitorSistemaPage;
