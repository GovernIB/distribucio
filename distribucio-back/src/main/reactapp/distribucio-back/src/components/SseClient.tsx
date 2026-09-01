import React from 'react';
import { EventSource } from 'eventsource';
import { useAuthContext, useResourceApiContext } from 'reactlib';
import { useDistribucioContext } from './DistribucioContext';

/**
 * Client dels esdeveniments SSE (Server-Sent Events) que envia el servidor.
 *
 * Manté una connexió oberta contra `api/sse/subscribe/user/{codi}` (veure
 * `SseResourceController`) i deixa el que hi arriba a l'abast de tota l'aplicació. De moment
 * l'únic esdeveniment són els avisos: en connectar el servidor n'envia l'estat actual, i el
 * torna a enviar cada vegada que la taula `dis_avis` canvia.
 *
 * L'estat es guarda en memòria i no al `sessionStorage`: el servidor envia els avisos tot d'una
 * en connectar, així que guardar-los només serviria per a pintar durant un instant una llista
 * possiblement caducada.
 */

/** Noms dels esdeveniments, tal com els envia el servidor (UserEventType). */
const EVENT_USER_CONNECT = 'user_connect';
const EVENT_AVISOS = 'avisos';

/** Temps d'espera abans de tornar a connectar quan la connexió cau. */
const RECONNECT_DELAY_MS = 5000;

export type AvisSse = {
    id: number;
    assumpte: string;
    missatge: string;
    avisNivell: string;
    dataInici?: string;
    dataFinal?: string;
};

type SseState = {
    /** Cert mentre la connexió amb el servidor està establerta. */
    connected: boolean;
    /** Avisos actius que ha de veure l'usuari amb el rol i l'entitat actuals. */
    avisos: AvisSse[];
};

const SSE_STATE_INICIAL: SseState = { connected: false, avisos: [] };

const SseContext = React.createContext<SseState>(SSE_STATE_INICIAL);

/** Estat de la connexió SSE i esdeveniments rebuts. */
export const useSseClient = () => React.useContext(SseContext);

/** Avisos actius rebuts per SSE. */
export const useAvisosSse = () => useSseClient().avisos;

/**
 * Construeix la URL de subscripció.
 *
 * El rol i l'entitat hi van com a paràmetres perquè l'EventSource no permet afegir capçaleres a
 * la petició, que és per on viatgen normalment (`X-App-Role` i `X-App-Session`). El servidor
 * comprova que siguin de l'usuari abans d'obrir la connexió.
 */
const getSubscribeUrl = (apiUrl: string, usuariCodi: string, rol?: string, entitatId?: number) => {
    const base = apiUrl.endsWith('/') ? apiUrl.slice(0, -1) : apiUrl;
    const params = new URLSearchParams();
    if (rol != null) {
        params.set('rol', rol);
    }
    if (entitatId != null) {
        params.set('entitatId', String(entitatId));
    }
    const queryString = params.toString();
    return `${base}/sse/subscribe/user/${encodeURIComponent(usuariCodi)}${queryString ? '?' + queryString : ''}`;
};

export const SseProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { apiUrl } = useResourceApiContext();
    const {
        isAuthenticated: authIsAuthenticated,
        bearerTokenActive: authBearerTokenActive,
        getToken: authGetToken,
    } = useAuthContext();
    const { currentUser, currentRole, currentEntitatId } = useDistribucioContext();
    const [state, setState] = React.useState<SseState>(SSE_STATE_INICIAL);
    const usuariCodi = currentUser?.id;

    React.useEffect(() => {
        if (usuariCodi == null) {
            return;
        }
        let eventSource: EventSource | null = null;
        let reconnectTimeout: ReturnType<typeof setTimeout> | undefined;
        // Evita que una reconnexió ja programada s'executi després de desmuntar el component o
        // de canviar de rol o d'entitat, que és quan es torna a connectar des de zero.
        let cancelled = false;

        const isLive = () => eventSource != null && eventSource.readyState !== EventSource.CLOSED;

        const connect = () => {
            if (cancelled || isLive()) {
                return;
            }
            eventSource?.close();
            // No es fa servir l'EventSource natiu del navegador: aquell no deixa posar capçaleres,
            // i amb el front servit des d'un origen diferent del backend (`npm run dev` amb OIDC)
            // l'autenticació viatja al Bearer i no a la galeta de sessió. Amb aquesta
            // implementació la petició es fa amb un fetch propi, que admet totes dues coses. La
            // condició per a posar-hi el Bearer és la mateixa que fa servir base-react per a les
            // crides a l'API (veure ResourceApiProvider.refreshKettingClient).
            eventSource = new EventSource(getSubscribeUrl(apiUrl, usuariCodi, currentRole, currentEntitatId), {
                fetch: (input, init) => {
                    const token = authGetToken();
                    const bearer = authIsAuthenticated && authBearerTokenActive && token != null;
                    return fetch(input, {
                        ...init,
                        credentials: 'include',
                        headers: bearer
                            ? { ...init?.headers, Authorization: `Bearer ${token}` }
                            : init?.headers,
                    });
                },
            });

            eventSource.addEventListener(EVENT_USER_CONNECT, () => {
                setState((previous) => ({ ...previous, connected: true }));
            });

            eventSource.addEventListener(EVENT_AVISOS, (event) => {
                try {
                    const data = JSON.parse(event.data);
                    setState((previous) => ({ ...previous, avisos: data?.avisos ?? [] }));
                } catch (error) {
                    console.error(`Error processant l'esdeveniment SSE '${EVENT_AVISOS}'`, error);
                }
            });

            eventSource.onerror = () => {
                setState((previous) => ({ ...previous, connected: false }));
                eventSource?.close();
                eventSource = null;
                if (!cancelled) {
                    reconnectTimeout = setTimeout(connect, RECONNECT_DELAY_MS);
                }
            };
        };

        // Reconnexió en tornar a la pestanya: cobreix els reinicis del servidor i les connexions
        // que el navegador atura mentre la pestanya està en segon pla.
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible' && !isLive()) {
                connect();
            }
        };
        document.addEventListener('visibilitychange', handleVisibilityChange);
        connect();

        return () => {
            cancelled = true;
            document.removeEventListener('visibilitychange', handleVisibilityChange);
            clearTimeout(reconnectTimeout);
            eventSource?.close();
            eventSource = null;
            setState(SSE_STATE_INICIAL);
        };
    }, [apiUrl, usuariCodi, currentRole, currentEntitatId]);

    return <SseContext.Provider value={state}>{children}</SseContext.Provider>;
};

export default SseProvider;
