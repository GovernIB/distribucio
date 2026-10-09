import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { AuthContext } from 'reactlib';

/**
 * Autenticació de la interfície REACT amb la sessió de servidor, com fa RIPEA
 * (`RipeaAuthProvider`).
 *
 * El navegador no gestiona cap token: les crides a l'API viatgen amb la galeta de la mateixa
 * sessió HTTP que la interfície JSP (adaptador Keycloak de JBoss en mode EAR, `oauth2Login` de
 * Spring en mode Spring Boot), i la renovació del token de Keycloak, si n'hi ha, la fa el servidor
 * dins d'aquesta sessió. Substitueix l'`OidcAuthProvider` de base-react, que guardava un refresh
 * token per pestanya: com que totes les pestanyes compartien la mateixa sessió de client de
 * Keycloak, la renovació d'una invalidava la de les altres ("Stale token"), i la que fallava es
 * recarregava sencera cada ~4 minuts.
 *
 * El codi, el nom, el correu i els rols de l'usuari surten de `api/sessioUsuari`
 * (`SessioUsuariController`), que serveix també per a comprovar si la sessió continua viva. No es
 * consulta periòdicament, perquè cada consulta allargaria la sessió i un usuari inactiu no
 * arribaria mai a caducar (a la JSP sí que caduca). Es consulta en tornar a la pestanya, amb
 * l'activitat de l'usuari (com a molt un cop per minut) i quan cau la connexió SSE.
 *
 * Si no hi ha sessió (o ha caducat) es va directament al login, i en acabar es torna a la mateixa
 * URL: directament si la sessió de Keycloak encara és viva, i si no després de la pantalla de
 * login. Si la pàgina la serveix el backend n'hi ha prou amb recarregar-la; amb `npm run dev` es
 * passa per `sessioUsuari/login` del backend, que torna a la URL de Vite (veure
 * `SessioUsuariController.login`).
 */

type SessioUsuari = {
    codi: string;
    nom?: string;
    email?: string;
    /** Rols de l'usuari que la interfície pot oferir (inclòs el rol base "tothom"). */
    rols: string[];
};

type EstatSessio = 'carregant' | 'autenticat' | 'caducada' | 'error';

type SessioUsuariContextType = {
    usuari?: SessioUsuari;
    /** Comprova si la sessió continua viva i, si ha caducat, torna a iniciar-la. */
    comprovarSessio: () => void;
};

const SessioUsuariContext = React.createContext<SessioUsuariContextType | undefined>(undefined);

export const useSessioUsuari = (): SessioUsuariContextType => {
    const context = React.useContext(SessioUsuariContext);
    if (context === undefined) {
        throw new Error('useSessioUsuari must be used within a DistribucioAuthProvider');
    }
    return context;
};

/** Temps mínim entre dues comprovacions provocades per l'activitat de l'usuari. */
const INTERVAL_ACTIVITAT_MS = 60000;
/** Temps mínim entre dues comprovacions provocades per la pestanya o per l'SSE. */
const INTERVAL_MINIM_MS = 10000;
/**
 * Marca (sessionStorage) del darrer intent d'iniciar la sessió. Si en tornar la sessió encara no
 * hi és, no es torna a intentar (evita un bucle de redireccions, p. ex. si la galeta de sessió no
 * arriba al backend) i es mostra la pantalla de sessió caducada.
 */
const INICI_SESSIO_KEY = 'distribucio.sessioCaducada.intent';
const INICI_SESSIO_MARGE_MS = 60000;

const ESDEVENIMENTS_ACTIVITAT = ['pointerdown', 'keydown'] as const;

/** Arrel de l'aplicació al servidor (context path), a partir de la URL de l'API. */
const getBackendBaseUrl = (apiUrl: string) =>
    new URL(apiUrl, window.location.href).href.replace(/\/+$/, '').replace(/\/api$/, '');

/**
 * Consulta l'usuari de la sessió. Retorna null si no hi ha sessió: el servidor respon amb una
 * redirecció cap al login (adaptador de JBoss, `invalidSessionUrl` de Spring), que amb
 * `redirect: 'manual'` arriba com a `opaqueredirect`, o bé amb un 401. Qualsevol altre error
 * (servidor aturat, xarxa) es propaga: no vol dir que la sessió hagi caducat.
 */
const consultarSessio = async (backendBaseUrl: string): Promise<SessioUsuari | null> => {
    const response = await fetch(backendBaseUrl + '/api/sessioUsuari', {
        credentials: 'include',
        redirect: 'manual',
        cache: 'no-store',
        headers: { Accept: 'application/json' },
    });
    if (response.type === 'opaqueredirect' || response.status === 401) {
        return null;
    }
    if (!response.ok) {
        throw new Error('Error consultant la sessió de l\'usuari (HTTP ' + response.status + ')');
    }
    return response.json();
};

const llegirMarcaIniciSessio = (): number => {
    try {
        return Number(sessionStorage.getItem(INICI_SESSIO_KEY)) || 0;
    } catch {
        return 0;
    }
};

const escriureMarcaIniciSessio = (valor?: number) => {
    try {
        if (valor == null) {
            sessionStorage.removeItem(INICI_SESSIO_KEY);
        } else {
            sessionStorage.setItem(INICI_SESSIO_KEY, String(valor));
        }
    } catch {
        // Sense sessionStorage no hi ha protecció contra el bucle, però l'inici de sessió funciona.
    }
};

const PantallaSessio: React.FC<{ missatge: string; accio: string; onAccio: () => void }> = ({
    missatge,
    accio,
    onAccio,
}) => (
    <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh', gap: 2 }}>
        <Typography>{missatge}</Typography>
        <Button variant="contained" onClick={onAccio}>
            {accio}
        </Button>
    </Box>
);

type DistribucioAuthProviderProps = React.PropsWithChildren & {
    /** URL de l'API REST, d'on es dedueix l'arrel de l'aplicació al servidor. */
    apiUrl: string;
};

export const DistribucioAuthProvider: React.FC<DistribucioAuthProviderProps> = ({ apiUrl, children }) => {
    const { t } = useTranslation();
    const backendBaseUrl = React.useMemo(() => getBackendBaseUrl(apiUrl), [apiUrl]);
    const [estat, setEstat] = React.useState<EstatSessio>('carregant');
    const [usuari, setUsuari] = React.useState<SessioUsuari>();
    const usuariRef = React.useRef<SessioUsuari>(undefined);
    const comprovantRef = React.useRef(false);
    const darreraComprovacioRef = React.useRef(0);

    const iniciarSessio = React.useCallback(() => {
        escriureMarcaIniciSessio(Date.now());
        if (window.location.href.startsWith(backendBaseUrl + '/')) {
            // La pàgina la serveix el backend: recarregar-la ja passa pel login del servidor.
            window.location.reload();
        } else {
            // Amb `npm run dev` la pàgina no passa pel backend: s'hi inicia la sessió i es torna
            // aquí.
            window.location.href =
                backendBaseUrl + '/sessioUsuari/login?retorn=' + encodeURIComponent(window.location.href);
        }
    }, [backendBaseUrl]);

    const gestionarSenseSessio = React.useCallback(() => {
        const intentRecent = Date.now() - llegirMarcaIniciSessio() < INICI_SESSIO_MARGE_MS;
        if (intentRecent) {
            setEstat('caducada');
        } else {
            iniciarSessio();
        }
    }, [iniciarSessio]);

    const comprovar = React.useCallback(
        (intervalMinim: number) => {
            if (comprovantRef.current || Date.now() - darreraComprovacioRef.current < intervalMinim) {
                return;
            }
            comprovantRef.current = true;
            darreraComprovacioRef.current = Date.now();
            consultarSessio(backendBaseUrl)
                .then((sessio) => {
                    if (sessio == null) {
                        gestionarSenseSessio();
                        return;
                    }
                    escriureMarcaIniciSessio(undefined);
                    const usuariActual = usuariRef.current;
                    if (usuariActual == null) {
                        usuariRef.current = sessio;
                        setUsuari(sessio);
                        setEstat('autenticat');
                    } else if (usuariActual.codi !== sessio.codi) {
                        // S'ha iniciat sessió amb un altre usuari (en una altra pestanya): tot el
                        // que hi ha carregat és de l'usuari anterior.
                        window.location.reload();
                    }
                })
                .catch((error) => {
                    console.error(error);
                    // Si ja s'havia carregat l'usuari, un error puntual no atura l'aplicació: les
                    // crides a l'API ja mostraran el seu propi error.
                    if (usuariRef.current == null) {
                        setEstat('error');
                    }
                })
                .finally(() => {
                    comprovantRef.current = false;
                });
        },
        [backendBaseUrl, gestionarSenseSessio]
    );

    const comprovarSessio = React.useCallback(() => comprovar(INTERVAL_MINIM_MS), [comprovar]);

    const tornarAProvar = React.useCallback(() => {
        setEstat('carregant');
        comprovar(0);
    }, [comprovar]);

    React.useEffect(() => {
        comprovar(0);
    }, [comprovar]);

    React.useEffect(() => {
        if (estat !== 'autenticat') {
            return;
        }
        const onVisibilitat = () => {
            if (document.visibilityState === 'visible') {
                comprovar(INTERVAL_MINIM_MS);
            }
        };
        const onActivitat = () => comprovar(INTERVAL_ACTIVITAT_MS);
        document.addEventListener('visibilitychange', onVisibilitat);
        ESDEVENIMENTS_ACTIVITAT.forEach((e) => window.addEventListener(e, onActivitat, { passive: true }));
        return () => {
            document.removeEventListener('visibilitychange', onVisibilitat);
            ESDEVENIMENTS_ACTIVITAT.forEach((e) => window.removeEventListener(e, onActivitat));
        };
    }, [estat, comprovar]);

    const authContext = React.useMemo(() => {
        const autenticat = estat === 'autenticat' && usuari != null;
        return {
            isLoading: !autenticat,
            isReady: autenticat,
            isAuthenticated: autenticat,
            // Sense token: les crides a l'API s'autentiquen amb la galeta de sessió.
            bearerTokenActive: false,
            getToken: () => undefined,
            // Imita els camps del token que llegeix base-react (p. ex. el botó d'usuari).
            getTokenParsed: () =>
                usuari != null
                    ? { preferred_username: usuari.codi, name: usuari.nom ?? usuari.codi, email: usuari.email }
                    : undefined,
            getUserId: () => usuari?.codi,
            getUserName: () => usuari?.nom ?? usuari?.codi,
            getUserEmail: () => usuari?.email,
            signIn: undefined,
            // El mateix enllaç que la interfície JSP (decorators/default.jsp). NO facis un fetch del
            // logout: ha d'acabar redirigint a Keycloak, i cal una navegació real perquè el
            // navegador hi enviï les seves galetes i es tanqui la sessió SSO (veure el comentari
            // equivalent a ContainerAuthProvider de base-react).
            signOut: () => {
                window.location.href = backendBaseUrl + '/usuari/logout';
            },
        };
    }, [estat, usuari, backendBaseUrl]);

    const sessioContext = React.useMemo(() => ({ usuari, comprovarSessio }), [usuari, comprovarSessio]);

    let contingut: React.ReactNode;
    if (estat === 'autenticat') {
        contingut = children;
    } else if (estat === 'caducada') {
        contingut = <PantallaSessio missatge={t('app.sessio.caducada')} accio={t('app.sessio.iniciar')} onAccio={iniciarSessio} />;
    } else if (estat === 'error') {
        contingut = <PantallaSessio missatge={t('app.sessio.error')} accio={t('app.sessio.tornarAProvar')} onAccio={tornarAProvar} />;
    } else {
        // Mentre es consulta la sessió no es pinta res, com feien els proveïdors de base-react:
        // aquest component va per fora del TemaProvider i una pantalla de càrrega hi sortiria
        // amb el tema per defecte (parpelleig amb el tema fosc).
        contingut = null;
    }
    return (
        <AuthContext.Provider value={authContext}>
            <SessioUsuariContext.Provider value={sessioContext}>{contingut}</SessioUsuariContext.Provider>
        </AuthContext.Provider>
    );
};

export default DistribucioAuthProvider;
