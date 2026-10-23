import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import { useDistribucioContext } from './DistribucioContext';
import {formatDate} from "../util/dateUtils.ts";
import Load from "./Load.tsx";

export const UserLastConection = () => {
    const { t } = useTranslation();
    const { currentUser: user } = useDistribucioContext();

    return <Load value={user?.darreraConnexio} noEffect>
        <MenuItem
            title={t('component.UserLastConection.lastConnection')}
            disableRipple
            sx={{
                '&.MuiButtonBase-root:hover': {
                    bgcolor: 'transparent',
                    cursor: 'default',
                },
                pt: 0,
                pb: 0,
            }}
            key="darreraConnexio"
        >
            <ListItemIcon>
                <Icon>schedule</Icon>
            </ListItemIcon>
            <ListItemText>
                <Typography color="textSecondary" variant={'body2'} sx={{ fontSize: 12, mr: "1ch" }}>
                    {t('component.UserLastConection.darreraConnexio')}
                </Typography>
                <Typography color="textSecondary" variant={'body2'}>
                    {formatDate(user?.darreraConnexio, 'DD/MM/YY HH:mm')}
                </Typography>
            </ListItemText>
        </MenuItem>
        <Divider key="darreraConnexioDivider"></Divider>
    </Load>
}