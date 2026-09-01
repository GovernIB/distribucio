import React from 'react';
import CssBaseline from '@mui/material/CssBaseline';
import useMediaQuery from '@mui/material/useMediaQuery';
import { ThemeProvider } from '@mui/material/styles';
import { useAuthContext } from 'reactlib';
import { getThemeForTema, TemaAplicacio } from '../theme';

/**
 * Tema visual actiu de l'aplicació.
 *
 * La font de veritat és el perfil de l'usuari (dis_usuari.tema_aplicacio), que arriba amb
 * DistribucioProvider; però aquest no pinta res fins a tenir-lo carregat, i la seva pantalla de
 * càrrega quedava fora del ThemeProvider: un usuari amb tema obscur veia un instant en clar a
 * cada recàrrega. Per evitar-ho el proveïdor va per damunt de tot i arrenca amb l'últim tema
 * conegut, desat al localStorage, fins que UserPreferencesProvider hi puja el del perfil.
 *
 * La memòria cau es desa per usuari: en un navegador compartit, arrencar amb el tema de l'usuari
 * anterior seria el mateix parpelleig que es vol evitar.
 */
const CLAU_TEMA_CACHE = 'DISTRIBUCIO_TEMA';

const clauTemaCache = (usuariId: string) => CLAU_TEMA_CACHE + '_' + usuariId.toUpperCase();

const llegirTemaCache = (usuariId: string): TemaAplicacio | undefined => {
    try {
        const desat = localStorage.getItem(clauTemaCache(usuariId));
        return desat != null && desat in TemaAplicacio ? (desat as TemaAplicacio) : undefined;
    } catch {
        return undefined;
    }
};

/**
 * Recorda el tema del perfil per al proper arrencada. Només s'hi ha de desar el valor desat a la
 * base de dades, mai la previsualització del diàleg de perfil: un tema que s'ha descartat no ha
 * de sobreviure a la recàrrega.
 */
export const desarTemaCache = (usuariId?: string, tema?: TemaAplicacio) => {
    if (usuariId == null) {
        return;
    }
    try {
        if (tema != null) {
            localStorage.setItem(clauTemaCache(usuariId), tema);
        } else {
            localStorage.removeItem(clauTemaCache(usuariId));
        }
    } catch {
        // Navegació privada o emmagatzematge ple: el tema del perfil s'aplicarà igualment, només
        // es perd l'estalvi del parpelleig.
    }
};

const TemaContext = React.createContext<((tema?: TemaAplicacio) => void) | undefined>(undefined);

export const TemaProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { getUserId: authGetUserId } = useAuthContext();
    // Els fills d'AuthProvider només es pinten un cop autenticat (`mandatory`), per tant aquí ja
    // se sap de quin usuari s'ha de llegir la memòria cau.
    const [tema, setTema] = React.useState<TemaAplicacio | undefined>(() =>
        llegirTemaCache(authGetUserId())
    );
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const theme = getThemeForTema(tema, prefersDarkMode);
    return (
        <TemaContext.Provider value={setTema}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {children}
            </ThemeProvider>
        </TemaContext.Provider>
    );
};

/** Fixa el tema actiu. L'usa UserPreferencesProvider per aplicar-hi el del perfil. */
export const useSetTemaAplicacio = () => {
    const context = React.useContext(TemaContext);
    if (context === undefined) {
        throw new Error('useSetTemaAplicacio must be used within a TemaProvider');
    }
    return context;
};

export default TemaProvider;
