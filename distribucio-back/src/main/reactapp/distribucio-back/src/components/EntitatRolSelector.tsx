import React from 'react';
import { useTranslation } from 'react-i18next';
import Collapse from '@mui/material/Collapse';
import Divider from '@mui/material/Divider';
import Icon from '@mui/material/Icon';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import { useNavigate } from 'react-router-dom';
import { useResourceApiService } from 'reactlib';
import { useDistribucioContext, ROLE_SUPER, ROLE_ADMIN } from './DistribucioContext';
import { rutaInicialPerRol } from '../util/pantalles';
import { icons } from '../util/icons';

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

// Ítem del menú desplegable de l'usuari (headerAdditionalAuthComponents), no un camp de formulari:
// amb una única opció només mostra l'etiqueta; amb més d'una, un ítem plegable que en desplegar-se
// mostra totes les opcions (l'actual remarcada amb `selected`).
type OpcioMenu<T> = { value: T; label: string };
const MenuItemDesplegable = <T,>(props: {
    icon: string;
    value: T;
    opcions: OpcioMenu<T>[];
    onSelect: (value: T) => void;
}) => {
    const { icon, value, opcions, onSelect } = props;
    const [expanded, setExpanded] = React.useState(false);
    const label = opcions.find((opcio) => opcio.value === value)?.label ?? '';
    if (opcions.length === 1) {
        return (
            <MenuItem
                disableRipple
                sx={{ '&.MuiButtonBase-root:hover': { bgcolor: 'transparent', cursor: 'default' } }}
            >
                <ListItemIcon>
                    <Icon fontSize="small">{icon}</Icon>
                </ListItemIcon>
                <ListItemText>{label}</ListItemText>
            </MenuItem>
        );
    }
    return (
        <>
            <MenuItem onClick={() => setExpanded((prev) => !prev)}>
                <ListItemIcon>
                    <Icon fontSize="small">{icon}</Icon>
                </ListItemIcon>
                <ListItemText>{label}</ListItemText>
                <Icon fontSize="small">{expanded ? 'expand_less' : 'expand_more'}</Icon>
            </MenuItem>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                {opcions.map((opcio) => (
                    <MenuItem
                        key={String(opcio.value)}
                        selected={opcio.value === value}
                        sx={{ pl: 4 }}
                        onClick={() => {
                            setExpanded(false);
                            onSelect(opcio.value);
                        }}
                    >
                        <ListItemText>{opcio.label}</ListItemText>
                    </MenuItem>
                ))}
            </Collapse>
        </>
    );
};

// El selector d'entitat només es mostra si el rol actual no és DIS_SUPER (els superusuaris
// administren totes les entitats, no "actuen dins" de cap en concret).
const useMostraEntitat = () => {
    const { entitatsAvailable, currentRole } = useDistribucioContext();
    return (entitatsAvailable?.length ?? 0) > 0 && currentRole !== ROLE_SUPER;
};

const useMostraRol = () => {
    const { rolesAvailable, currentRole } = useDistribucioContext();
    return (rolesAvailable?.length ?? 0) > 0 && !!currentRole;
};

const EntitatSelector: React.FC = () => {
    const { entitatsAvailable, currentEntitatId, setCurrentEntitatId } = useDistribucioContext();
    if (!useMostraEntitat() || currentEntitatId == null) {
        return null;
    }
    return (
        <MenuItemDesplegable
            icon={icons.domini}
            value={currentEntitatId}
            opcions={(entitatsAvailable ?? []).map((entitat) => ({ value: entitat.id, label: entitat.nom }))}
            onSelect={setCurrentEntitatId}
        />
    );
};

const RolSelector: React.FC = () => {
    const { t } = useTranslation();
    const { rolesAvailable, currentRole, setCurrentRole, currentUser, setCurrentUser } =
        useDistribucioContext();
    const { isReady: usuariApiIsReady, patch: usuariApiPatch } = useResourceApiService('usuariResource');
    const navigate = useNavigate();
    // Desa el rol triat a dis_usuari.rol_actual perquè el proper inici de sessió hi torni (veure
    // la resolució del rol inicial a DistribucioProvider). Només es desa des d'aquí, que és
    // l'acció explícita de l'usuari: les altres pestanyes reben el canvi pel BroadcastChannel i
    // no l'han de reescriure (dues escriptures simultànies xocarien amb el control de versió).
    const desarRolActual = (rol: string) => {
        if (!usuariApiIsReady || currentUser?.id == null) {
            return;
        }
        usuariApiPatch(currentUser.id, { data: { rolActual: rol } })
            .then(() => setCurrentUser({ ...currentUser, rolActual: rol }))
            .catch((error: any) => console.error("No s'ha pogut desar el rol actual", error));
    };
    if (!useMostraRol() || !currentRole) {
        return null;
    }
    return (
        <MenuItemDesplegable
            icon={icons.rol}
            value={currentRole}
            opcions={(rolesAvailable ?? []).map((rol) => ({
                value: rol,
                label: t(`component.EntitatRolSelector.rol.${rol}`, rol),
            }))}
            onSelect={(rol) => {
                setCurrentRole(rol);
                desarRolActual(rol);
                // Amb el rol nou la pantalla actual pot quedar prohibida (p.ex. sortir de
                // DIS_SUPER estant a /entitat): s'hi va a la d'inici del rol, com fa RIPEA amb el
                // navigate('/') posterior al canvi de rol.
                navigate(rutaInicialPerRol(rol));
            }}
        />
    );
};

// Grup del menú de l'usuari amb l'entitat i el rol actuals, separat de la resta per un divisor
// (el de sota el posa base-react abans de "Surt"), com el menú d'usuari de RIPEA
// (ripea-back/src/main/jsapp/ripea-back/src/pages/user/UserMenu.tsx).
export const EntitatRolMenu: React.FC = () => {
    const mostraEntitat = useMostraEntitat();
    const mostraRol = useMostraRol();
    if (!mostraEntitat && !mostraRol) {
        return null;
    }
    return (
        <>
            <Divider />
            <EntitatSelector />
            <RolSelector />
        </>
    );
};
