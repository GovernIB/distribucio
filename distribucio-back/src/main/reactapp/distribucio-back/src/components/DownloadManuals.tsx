import {iniciaDescarga} from "../util/downloadUtils.ts";
import {Icon, MenuItem, ListItemIcon, ListItemText} from "@mui/material";
import {ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_SUPER, ROLE_USER, useDistribucioContext} from "./DistribucioContext.ts";
import {useTranslation} from "react-i18next";

export const DownloadManuals = () => {
    const {t} = useTranslation()
    const {currentRole} = useDistribucioContext()
    const isAdmin = currentRole === ROLE_SUPER || currentRole === ROLE_ADMIN || currentRole === ROLE_ADMIN_LECTURA;
    const isUser = currentRole === ROLE_SUPER || currentRole == ROLE_USER

    return <>
        {isUser && <MenuItem onClick={() => {
            const url = 'https://github.com/GovernIB/distribucio/raw/dis-dev/doc/pdf/02_Distribucio_Manual_Usuari.pdf';
            iniciaDescarga(url, '02_Distribucio_Manual_Usuari.pdf')
        }}>
            <ListItemIcon><Icon fontSize={"small"}>download</Icon></ListItemIcon>
            <ListItemText>{t('component.DownloadManuals.manual')}</ListItemText>
        </MenuItem>}
        {isAdmin && <MenuItem onClick={() => {
            const url = 'https://github.com/GovernIB/distribucio/raw/dis-dev/doc/pdf/02_Distribucio_Manual_Administrador.pdf';
            iniciaDescarga(url, '02_Distribucio_Manual_Administrador.pdf')
        }}>
            <ListItemIcon><Icon fontSize={"small"}>download</Icon></ListItemIcon>
            <ListItemText>{t('component.DownloadManuals.manualAdmin')}</ListItemText>
        </MenuItem>}
    </>
}