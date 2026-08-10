import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Icon from '@mui/material/Icon';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import Typography from '@mui/material/Typography';
import { useDistribucioContext, ROLE_SUPER, ROLE_ADMIN } from './DistribucioContext';

// Icona de distintiu sobre l'avatar de l'usuari segons el rol actual (cap distintiu per a la
// resta de rols). S'usa a headerAuthBadgeIcon de MuiBaseApp.
export const getRolBadgeIcon = (rolActual?: string): string | undefined => {
    if (rolActual === ROLE_SUPER) {
        return 'shield';
    }
    if (rolActual === ROLE_ADMIN) {
        return 'admin_panel_settings';
    }
    return undefined;
};

// Mostra el selector d'entitat només si el rol actual no és DIS_SUPER (els superusuaris
// administren totes les entitats, no "actuen dins" de cap en concret). Amb una única entitat
// accessible, mostra només l'etiqueta (sense desplegable).
export const EntitatSelector: React.FC = () => {
    const { entitatsAvailable, currentEntitatId, currentRole, setCurrentEntitatId } = useDistribucioContext();
    const entitats = entitatsAvailable ?? [];
    if (entitats.length === 0 || currentRole === ROLE_SUPER) {
        return null;
    }
    if (entitats.length === 1) {
        return (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, px: 1 }}>
                <Icon fontSize="small">domain</Icon>
                <Typography variant="body2">{entitats[0].nom}</Typography>
            </Box>
        );
    }
    return (
        <Select
            size="small"
            value={currentEntitatId ?? ''}
            onChange={(event) => setCurrentEntitatId(Number(event.target.value))}
            renderValue={(value) => (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                    <Icon fontSize="small">domain</Icon>
                    {entitats.find((entitat) => entitat.id === value)?.nom ?? ''}
                </Box>
            )}
            sx={{ minWidth: 140 }}
        >
            {entitats.map((entitat) => (
                <MenuItem key={entitat.id} value={entitat.id}>
                    {entitat.nom}
                </MenuItem>
            ))}
        </Select>
    );
};

// Pensat per viure dins el menú desplegable de l'usuari (headerAdditionalAuthComponents), no com
// un camp de formulari: amb un únic rol disponible només mostra l'etiqueta; amb més d'un, un ítem
// de menú plegable que en desplegar-se mostra la resta d'opcions (la actual remarcada amb
// `selected`).
export const RolSelector: React.FC = () => {
    const { t } = useTranslation();
    const { rolesAvailable, currentRole, setCurrentRole } = useDistribucioContext();
    const [expanded, setExpanded] = React.useState(false);
    const rolsDisponibles = rolesAvailable ?? [];
    const label = (rol: string) => t(`component.EntitatRolSelector.rol.${rol}`, rol);
    if (rolsDisponibles.length === 0 || !currentRole) {
        return null;
    }
    if (rolsDisponibles.length === 1) {
        return (
            <MenuItem
                disableRipple
                sx={{ '&.MuiButtonBase-root:hover': { bgcolor: 'transparent', cursor: 'default' } }}
            >
                <ListItemIcon>
                    <Icon fontSize="small">badge</Icon>
                </ListItemIcon>
                <ListItemText>{label(currentRole)}</ListItemText>
            </MenuItem>
        );
    }
    return (
        <>
            <MenuItem onClick={() => setExpanded((prev) => !prev)}>
                <ListItemIcon>
                    <Icon fontSize="small">badge</Icon>
                </ListItemIcon>
                <ListItemText>{label(currentRole)}</ListItemText>
                <Icon fontSize="small">{expanded ? 'expand_less' : 'expand_more'}</Icon>
            </MenuItem>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                {rolsDisponibles.map((rol) => (
                    <MenuItem
                        key={rol}
                        selected={rol === currentRole}
                        sx={{ pl: 4 }}
                        onClick={() => {
                            setCurrentRole(rol);
                            setExpanded(false);
                        }}
                    >
                        <ListItemText>{label(rol)}</ListItemText>
                    </MenuItem>
                ))}
            </Collapse>
        </>
    );
};
