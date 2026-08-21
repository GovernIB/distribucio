import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import { useAuthContext, useResourceApiContext, useResourceApiService } from 'reactlib';
import {
    DistribucioContext,
    ROLE_PREFIX,
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_LECTURA,
    ROLE_USER,
} from './DistribucioContext';

const ALLOWED_ROLES = [ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_USER].reverse();

export const distribucioChannel = new BroadcastChannel('distribucio');
type CurrentSession = Readonly<{
    role?: string;
    entitatId?: number;
}>;

const useBroadcastSession = () => {

    const [session, setSessionState] = React.useState<CurrentSession>({});

    const setSession = React.useCallback(
        (
            update:
                | Partial<CurrentSession>
                | ((previous: CurrentSession) => Partial<CurrentSession>),
            broadcast = true
        ) => {

            setSessionState(previous => {

                const changes =
                    typeof update === "function"
                        ? update(previous)
                        : update;

                const next = {
                    ...previous,
                    ...changes,
                };

                if (broadcast) {
                    distribucioChannel.postMessage(next);
                }

                return next;
            });

        },
        []
    );

    React.useEffect(() => {

        const listener = ({ data }: MessageEvent<CurrentSession>) => {
            if (!data) {
                return;
            }
            setSession(data, false);
        };

        distribucioChannel.addEventListener("message", listener);

        return () =>
            distribucioChannel.removeEventListener("message", listener);

    }, [setSession]);

    return {
        session,
        setSession,
    };

};

type BroadcastSession = ReturnType<typeof useBroadcastSession>;

const decodeJwt = (token: string) => {

    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

const useSessionStorage = (...keyParts: any[]) => {

    const key = keyParts.map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p))).join('|');
    const getValue = () => sessionStorage.getItem(key);
    const setValue = (value: string | null) => {
        if (value == null) {
            sessionStorage.removeItem(key);
            return;
        }
        sessionStorage.setItem(key, value);
    };
    return {getValue, setValue,};
};

const useCurrentUser = () => {

    const {isReady: apiIsReady, find: apiFind,} = useResourceApiService('usuariResource');
    const [currentUser, setCurrentUser] = React.useState<any>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        void apiFind({ unpaged: true }).then((response) => {
            if (response.rows.length) {
                setCurrentUser(response.rows[0]);
            }
        });
    }, [apiIsReady]);
    return { currentUser, setCurrentUser };
};

const useCurrentRole = (broadcast: BroadcastSession, currentUser: any) => {

    const {isReady: authIsReady, getUserId: authGetUserId, getToken: authGetToken,} = useAuthContext();
    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const {
        session,
        setSession
    } = broadcast;

    const currentRole = session.role;

    const setCurrentRole = (role?: string) => setSession({role, entitatId: undefined});
    const { getValue: roleSessionGetValue, setValue: roleSessionSetValue } = useSessionStorage(currentUserId, 'currentRole');
    React.useEffect(() => {
        // Obté els rols disponibles del token JWT o de __AUTH_ROLES__
        if (!authIsReady) {
            return;
        }
        const userId = authGetUserId();
        setCurrentUserId(userId);
        const token = authGetToken();
        if (token == null) {
            return;
        }
        const tokenDecoded = decodeJwt(token);
        // Els rols vénen del token (mode OIDC) o de __AUTH_ROLES__ (mode contenidor, on el token
        // no duu realm_access), i sempre s'hi afegeix ROLE_USER: "tothom" no és un rol de Keycloak
        // sinó el rol base que el backend concedeix a tot usuari autenticat (veure
        // WebSecurityConfig.filterAllowedGrantedAuthorities), igual que a RIPEA.
        const rolsIdp: string[] =
            tokenDecoded.realm_access != null
                ? tokenDecoded.realm_access?.roles?.filter(
                      (r: string) => r === ROLE_USER || r.startsWith(ROLE_PREFIX)
                  ) ?? []
                : (window as any).__AUTH_ROLES__ ?? [];
        setRolesAvailable(ALLOWED_ROLES.filter((a) => a === ROLE_USER || rolsIdp.includes(a)));
    }, [authIsReady]);

    React.useEffect(() => {
        // Rol inicial, per aquest ordre: el de la pestanya actual (sessionStorage), el darrer rol
        // amb què l'usuari va operar (dis_usuari.rol_actual, el mateix camp que la interfície JSP)
        // i, si cap dels dos no està disponible, el rol base "tothom". S'espera currentUser per no
        // decidir abans de conèixer el rol desat (isReady ja l'espera igualment).
        if (rolesAvailable == null || currentUser == null || currentRole != null) {
            return;
        }
        const rolDisponible = (rol?: string) => rol != null && rolesAvailable.includes(rol);
        const rolInicial =
            [roleSessionGetValue() ?? undefined, currentUser.rolActual].find(rolDisponible) ??
            (rolDisponible(ROLE_USER) ? ROLE_USER : rolesAvailable[0]);
        if (rolInicial != null) {
            setCurrentRole(rolInicial);
        }
    }, [rolesAvailable, currentRole, currentUser]);

    React.useEffect(() => {
        // Configura el session storage i la capçalera HTTP amb el rol actual quan aquest canvia
        if (currentRole === undefined) {
            return;
        }
        roleSessionSetValue(currentRole);
        if (currentRole) {
            apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
        }
    }, [currentRole]);
    const currentRoleFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Role' in h)?.['X-App-Role'];
    const roleHttpHeaderInitialized = currentRole != null && currentRole === currentRoleFromHttpHeader;
    return {
        currentUserId,
        currentRole,
        currentRoleReady: roleHttpHeaderInitialized,
        rolesAvailable,
        setCurrentRole,
    };
};

const useCurrentEntitat = (
    broadcast: BroadcastSession,
    currentUserId: string | undefined,
    currentRole: string | undefined,
    currentRoleReady: boolean
) => {

    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const {isReady: apiIsReady, find: apiFind, getOne: apiGetOne} = useResourceApiService('entitatResource');
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentEntitatLoading, setCurrentEntitatLoading] = React.useState<boolean>();
    const [currentEntitat, setCurrentEntitat] = React.useState<any>();
    const { getValue: sessionSessionGetValue, setValue: sessionSessionSetValue } = useSessionStorage(currentUserId, 'currentSession');
    const {
        session,
        setSession
    } = broadcast;

    const currentEntitatId = session.entitatId;

    const setCurrentEntitatId = (id?: number) => setSession({ entitatId: id });

    React.useEffect(() => {
        if (!apiIsReady || !currentRoleReady || currentRole == null) {
            return;
        }
        setEntitatsAvailable(undefined);
        setCurrentEntitat(undefined);
        setCurrentEntitatId(undefined);

        if (currentRole === ROLE_SUPER) {
            setEntitatsAvailable([]);
            return;
        }
        apiFind({ unpaged: true }).then((response) => {
            const entitatsAvailable = response.rows;
            setEntitatsAvailable(entitatsAvailable);

            const storedSession = sessionSessionGetValue();

            const parsedSession = storedSession ? JSON.parse(storedSession) : {};
            const sessionValue = parsedSession.e;
            const isSessionValueInEntitatsAvailable = entitatsAvailable.map((e) => e.id).includes(sessionValue);
            if (isSessionValueInEntitatsAvailable) {
                setCurrentEntitatId(sessionValue);
            } else if (entitatsAvailable?.length && currentEntitatId == null) {
                setCurrentEntitatId(entitatsAvailable[0].id);
            }
        });
    }, [apiIsReady, currentRoleReady, currentRole]);

    React.useEffect(() => {
        if (currentRole == null || currentRole === ROLE_SUPER || currentEntitatId == null) {
            return;
        }

        const sessionJson = JSON.stringify({ e: currentEntitatId });
        sessionSessionSetValue(sessionJson);

        apiSetHttpHeaders([
            { "X-App-Role": currentRole },
            { "X-App-Session": sessionJson },
        ]);
    }, [currentRole, currentEntitatId]);

    React.useEffect(() => {
        if (!apiIsReady || currentEntitatId == null || entitatsAvailable == null) {
            return;
        }
        const entitatExisteix = entitatsAvailable.some(e => e.id === currentEntitatId);
        if (!entitatExisteix) {
            return;
        }
        setCurrentEntitatLoading(true);
        apiGetOne(currentEntitatId)
        .then(setCurrentEntitat)
        .finally(() => setCurrentEntitatLoading(false));

    }, [apiIsReady, currentEntitatId, entitatsAvailable,]);

    const currentSessionFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Session' in h)?.['X-App-Session'];
    const currentEntitatIdFromHttpHeader = currentSessionFromHttpHeader != null ? JSON.parse(currentSessionFromHttpHeader).e : undefined;
    const entitatIdHttpHeaderInitialized = currentRole === ROLE_SUPER
        || (currentEntitatId == null && currentEntitatIdFromHttpHeader == null)
        || currentEntitatId === currentEntitatIdFromHttpHeader;
    const currentEntitatReady = apiIsReady && entitatsAvailable != null && entitatIdHttpHeaderInitialized;
    return {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
    };
};

const DistribucioProviderLoading: React.FC = () => {

    const { t } = useTranslation();
    return (
        <Box sx={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh',}}>
            <CircularProgress size={70} />
            <Typography sx={{ mt: 1 }}>{t('app.loading')}</Typography>
        </Box>
    );
};

export const DistribucioProvider: React.FC<React.PropsWithChildren> = ({ children }) => {

    const { offline: apiOffline } = useResourceApiContext();
    const broadcast = useBroadcastSession();
    const { currentUser, setCurrentUser } = useCurrentUser();
    const { currentUserId, currentRole, currentRoleReady, rolesAvailable, setCurrentRole } = useCurrentRole(broadcast, currentUser);
    const {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
    } = useCurrentEntitat(broadcast, currentUserId, currentRole, currentRoleReady);
    const isReady = apiOffline || (currentRoleReady && currentEntitatReady && currentUser != null);
    const contextValue = {
        isReady,
        currentUser,
        setCurrentUser,
        rolesAvailable,
        currentRole,
        setCurrentRole,
        entitatsAvailable,
        currentEntitatId,
        setCurrentEntitatId,
        currentEntitat,
        currentEntitatLoading,
    };
    return (
        <DistribucioContext.Provider value={contextValue}>
            {isReady ? children : <DistribucioProviderLoading />}
        </DistribucioContext.Provider>
    );
};

export default DistribucioProvider;
