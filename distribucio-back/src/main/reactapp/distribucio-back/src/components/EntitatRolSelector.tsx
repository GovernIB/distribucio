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
import { useResourceApiContext } from 'reactlib';

// Han de coincidir amb es.caib.distribucio.logic.intf.config.BaseConfig.
const ROLE_SUPER = 'DIS_SUPER';
const ROLE_ADMIN = 'DIS_ADMIN';

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

type EntitatOption = { id: number; nom: string };

type EntitatRolActual = {
    entitatActualId?: number;
    rolActual?: string;
    rolsDisponibles: string[];
};

type EntitatRolState = EntitatRolActual & {
    entitats: EntitatOption[];
    loading: boolean;
};

type EntitatRolContextValue = EntitatRolState & {
    canviEntitat: (id: number) => void;
    canviRol: (rol: string) => void;
};

const EntitatRolContext = React.createContext<EntitatRolContextValue | undefined>(undefined);

// Reflecteix -- via l'endpoint /api/usuariPreferencies/entitatRolActual -- l'entitat i el rol
// actuals de la sessió: els mateixos atributs de HttpSession que ja fa servir la interfície JSP
// (EntitatHelper/RolHelper), de manera que JSP i REACT sempre mostren i respecten la mateixa
// selecció, es faci el canvi des d'on es faci.
export const EntitatRolProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { apiUrl, requestHref, isReady } = useResourceApiContext();
    const [state, setState] = React.useState<EntitatRolState>({
        entitats: [],
        rolsDisponibles: [],
        loading: true,
    });

    const fetchActual = React.useCallback(
        (query?: string) =>
            requestHref(apiUrl + 'usuariPreferencies/entitatRolActual' + (query ?? ''))
                .then((resultState) => {
                    const data = (resultState.data ?? {}) as EntitatRolActual;
                    setState((prev) => ({
                        ...prev,
                        entitatActualId: data.entitatActualId,
                        rolActual: data.rolActual,
                        rolsDisponibles: data.rolsDisponibles ?? [],
                        loading: false,
                    }));
                })
                .catch(() => setState((prev) => ({ ...prev, loading: false }))),
        [apiUrl, requestHref]
    );

    // El client Ketting (i, per tant, requestHref) no queda operatiu fins que ResourceApiProvider
    // acaba d'inicialitzar-se (isReady) -- cridar-lo abans llança "Ketting client not initialized".
    React.useEffect(() => {
        if (!isReady) {
            return;
        }
        requestHref(apiUrl + 'usuariPreferencies/entitats')
            .then((resultState) =>
                setState((prev) => ({ ...prev, entitats: (resultState.data as EntitatOption[]) ?? [] }))
            )
            .catch(() => setState((prev) => ({ ...prev, entitats: [] })));
        fetchActual();
    }, [apiUrl, isReady]);

    const canviEntitat = (id: number) => fetchActual('?canviEntitat=' + encodeURIComponent(String(id)));
    const canviRol = (rol: string) => fetchActual('?canviRol=' + encodeURIComponent(rol));

    return (
        <EntitatRolContext.Provider value={{ ...state, canviEntitat, canviRol }}>
            {children}
        </EntitatRolContext.Provider>
    );
};

export const useEntitatRol = (): EntitatRolContextValue => {
    const context = React.useContext(EntitatRolContext);
    if (context === undefined) {
        throw new Error('useEntitatRol must be used within an EntitatRolProvider');
    }
    return context;
};

// Mostra el selector d'entitat només si el rol actual no és DIS_SUPER (els superusuaris
// administren totes les entitats, no "actuen dins" de cap en concret) -- igual que
// `${!isRolActualSuperusuari}` al decorador JSP. Amb una única entitat accessible, mostra només
// l'etiqueta (sense desplegable), igual que `${hiHaMesEntitats}`.
export const EntitatSelector: React.FC = () => {
    const { entitats, entitatActualId, rolActual, canviEntitat } = useEntitatRol();
    if (entitats.length === 0 || rolActual === ROLE_SUPER) {
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
            value={entitatActualId ?? ''}
            onChange={(event) => canviEntitat(Number(event.target.value))}
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
// `selected`) -- mateixa lògica de visibilitat que el bloc de rol del decorador JSP.
export const RolSelector: React.FC = () => {
    const { t } = useTranslation();
    const { rolsDisponibles, rolActual, canviRol } = useEntitatRol();
    const [expanded, setExpanded] = React.useState(false);
    const label = (rol: string) => t(`component.EntitatRolSelector.rol.${rol}`, rol);
    if (rolsDisponibles.length === 0 || !rolActual) {
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
                <ListItemText>{label(rolActual)}</ListItemText>
            </MenuItem>
        );
    }
    return (
        <>
            <MenuItem onClick={() => setExpanded((prev) => !prev)}>
                <ListItemIcon>
                    <Icon fontSize="small">badge</Icon>
                </ListItemIcon>
                <ListItemText>{label(rolActual)}</ListItemText>
                <Icon fontSize="small">{expanded ? 'expand_less' : 'expand_more'}</Icon>
            </MenuItem>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                {rolsDisponibles.map((rol) => (
                    <MenuItem
                        key={rol}
                        selected={rol === rolActual}
                        sx={{ pl: 4 }}
                        onClick={() => {
                            canviRol(rol);
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
