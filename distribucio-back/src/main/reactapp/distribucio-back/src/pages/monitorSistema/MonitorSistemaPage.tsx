import React from 'react';
import { useTranslation } from 'react-i18next';
import type { Theme } from '@mui/material/styles';
import { GridPage } from 'reactlib';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import TabComponent from '../../components/TabComponent';
import MonitorSistemaSistema from './MonitorSistemaSistema';
import MonitorSistemaFils from './MonitorSistemaFils';
import MonitorSistemaTasques from './MonitorSistemaTasques';
import { useInformacioSistema } from './useInformacioSistema';

export const MonitorSistemaPage: React.FC = () => {
    const { t } = useTranslation();
    const [tab, setTab] = React.useState<string>('sistema');
    const { informacio, loading, refresh } = useInformacioSistema();

    const tabs = [
        {
            value: 'sistema',
            label: t('page.monitorSistema.tabs.sistema'),
            content: <MonitorSistemaSistema informacio={informacio} />,
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
            <ScreenFrame
                icon={icons.monitorSistema}
                title={t('page.monitorSistema.title')}
                actions={
                    tab === 'sistema' ? (
                        <IconButton
                            title={t('page.monitorSistema.accio.refrescar')}
                            disabled={loading}
                            onClick={refresh}
                            sx={{ color: (theme: Theme) => theme.palette.primary.contrastText }}
                        >
                            <Icon>{icons.refresca}</Icon>
                        </IconButton>
                    ) : undefined
                }
            >
                <Box sx={{ flex: '1 1 0', minHeight: '420px' }}>
                    <TabComponent tabs={tabs} value={tab} onChange={setTab} />
                </Box>
            </ScreenFrame>
        </GridPage>
    );
};

export default MonitorSistemaPage;
