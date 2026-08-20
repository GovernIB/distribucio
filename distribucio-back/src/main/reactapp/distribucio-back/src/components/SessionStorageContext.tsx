import React, { createContext, useContext, useEffect, useState } from 'react';

/**
 * Estat compartit damunt del `sessionStorage` del navegador.
 *
 * Port reduït de `src/components/SessionStorageContext.tsx` de RIPEA: només s'hi ha portat
 * la part de `sessionStorage` (`SessionStorageProvider` + `useSession`), que és la que
 * necessita {@link ./StyledMuiFilter.tsx} per a recordar les dades del filtre de cada
 * llistat mentre dura la sessió del navegador. La resta del fitxer original (SessionProvider
 * / useSessionContext / useSessionList) es podrà portar quan alguna pantalla ho requereixi.
 */

type SessionData = Record<string, any>;

interface SessionStorageContextProps {
    data: SessionData;
    setValue: (key: string, value: any) => void;
    removeValue: (key: string) => void;
}

const SessionStorageContext = createContext<SessionStorageContextProps | undefined>(undefined);

export const SessionStorageProvider = ({ children }: { children: React.ReactNode }) => {
    const [data, setData] = useState<SessionData>(() => {
        const allKeys = Object.keys(sessionStorage);
        const initialData: SessionData = {};
        allKeys.forEach((key) => {
            try {
                initialData[key] = JSON.parse(sessionStorage.getItem(key)!);
            } catch {
                initialData[key] = sessionStorage.getItem(key);
            }
        });
        return initialData;
    });

    const setValue = (key: string, value: any) => {
        setData((prev) => {
            if (prev[key] === value) return prev; // no canvia -> evita re-render
            sessionStorage.setItem(key, JSON.stringify(value));
            return { ...prev, [key]: value };
        });
    };

    const removeValue = (key: string) => {
        sessionStorage.removeItem(key);
        setData((prev) => {
            const newData = { ...prev };
            delete newData[key];
            return newData;
        });
    };

    // Detectar canvis externs al sessionStorage (p. ex. altres pestanyes)
    useEffect(() => {
        const handleStorage = (e: StorageEvent) => {
            if (e.storageArea === sessionStorage && e.key) {
                const newValue = e.newValue ? JSON.parse(e.newValue) : null;
                setData((prev) => {
                    if (prev[e.key!] === newValue) return prev; // no canvia -> evita re-render
                    return { ...prev, [e.key!]: newValue };
                });
            }
        };
        window.addEventListener('storage', handleStorage);
        return () => window.removeEventListener('storage', handleStorage);
    }, []);

    return (
        <SessionStorageContext.Provider value={{ data, setValue, removeValue }}>
            {children}
        </SessionStorageContext.Provider>
    );
};

const useSessionStorage = () => {
    const ctx = useContext(SessionStorageContext);
    if (!ctx) throw new Error('useSessionStorage s’ha d’utilitzar dins de <SessionStorageProvider>');
    return ctx;
};

const initialized: Map<string, boolean> = new Map();

export const useSession = (key: string) => {
    const { data, setValue, removeValue } = useSessionStorage();
    return {
        value: data[key],
        isInitialized: () => !!initialized.get(key) || !!data[key],
        save: (val: any) => {
            initialized.set(key, !!val);
            setValue(key, val);
        },
        remove: () => {
            initialized.set(key, false);
            removeValue(key);
        },
    };
};
