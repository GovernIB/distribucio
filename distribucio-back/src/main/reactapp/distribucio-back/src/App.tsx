import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import { CssBaseline, useMediaQuery } from '@mui/material';
import { ThemeProvider, useTheme } from '@mui/material/styles';
import { envVar, OidcAuthProvider, ContainerAuthProvider, ResourceApiProvider } from 'reactlib';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import distribucioLogo from './assets/DIR_DRA_COL.svg';
import { getThemeForTema } from './theme';
import { UserPreferencesProvider, useUserPreferences } from './components/UserProfile';
import { DistribucioProvider } from './components/DistribucioProvider';
import { useDistribucioContext } from './components/DistribucioContext';
import { filtrarEntradesMenu, type MenuEntryAmbPantalla } from './util/pantalles';
import { SessionStorageProvider } from './components/SessionStorageContext';

export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
    VITE_AUTH_URL: import.meta.env.VITE_AUTH_URL,
    VITE_AUTH_REALM: import.meta.env.VITE_AUTH_REALM,
    VITE_AUTH_CLIENTID: import.meta.env.VITE_AUTH_CLIENTID,
    VITE_APP_VERSION: import.meta.env.VITE_APP_VERSION,
};

const getAuthConfig = () => ({
    url: envVar('VITE_AUTH_URL', envVars),
    realm: envVar('VITE_AUTH_REALM', envVars),
    clientId: envVar('VITE_AUTH_CLIENTID', envVars),
});

export const getEnvApiUrl = () => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return envApiPublicUrl ?? envApiUrl;
    } else {
        const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
        const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
        if (envApiBaseUrl) {
            return envApiBaseUrl + envApiSuffix;
        } else {
            const port = window.location.port ? ':' + window.location.port : '';
            return window.location.protocol + '//' + window.location.hostname + port + envApiSuffix;
        }
    }
};

// El SPA reutilitza per defecte la sessió que ja gestiona Spring Security (ContainerAuthProvider,
// same-origin). Només es fa servir OIDC client-side si es configura explícitament VITE_AUTH_URL
// (p.ex. per executar el front en un origen separat del backend).
const isAuthUrlPresent = envVar('VITE_AUTH_URL', envVars) != null;
const AuthProvider = isAuthUrlPresent ? OidcAuthProvider : ContainerAuthProvider;
const version = import.meta.env.VITE_APP_VERSION ?? '0.0.0';

// Mides de la capçalera. MENU_WIDTH és l'amplada del menú lateral obert (el valor per defecte
// del Drawer de la llibreria); APPBAR_PADDING_LEFT desplaça el botó de menú fins a la columna
// de les icones del menú, i LOGO_BOX_LEFT és on comença la caixa del logo amb aquest padding.
const MENU_WIDTH = 240;
const APPBAR_PADDING_LEFT = 30;
const LOGO_BOX_LEFT = 82;

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const mode = theme.palette.mode;

    const { currentRole } = useDistribucioContext();
    // La pantalla de cada entrada determina a quins rols es mostra (veure PANTALLA_ROLS a
    // util/pantalles.ts): el menú i les guardes de ruta surten de la mateixa declaració.
    const menuEntries: MenuEntryAmbPantalla[] = [
        {
            id: 'home',
            title: t('app.menu.home'),
            to: 'home',
            icon: 'home',
            pantalla: 'home',
        },
        {
            id: 'entitats',
            title: t('app.menu.entitats'),
            to: 'entitat',
            icon: 'account_balance',
            pantalla: 'entitat',
        },
        {
            id: 'avisos',
            title: t('app.menu.avisos'),
            to: 'avis',
            icon: 'campaign',
            pantalla: 'avis',
        },
    ];

    const bgColor= mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor= bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    // DIR_DRA_COL.svg té els colors fixats (verd corporatiu i gris fosc), així que serveix per
    // als dos modes; si algun dia cal una variant per a fons foscos, tornar a fer el ternari.
    const logoColor = distribucioLogo;
    const { estilMenu } = useUserPreferences();

    return (
        <BaseApp
            code="DISTRIBUCIO"
            logo={mode === 'light' ? goibLogoLight : goibLogoDark}
            logoStyle={{
                '& img': { height: '49px' },
                pl: 1,
                // El separador vertical ha de caure sobre la vora dreta del menú obert. El botó
                // de menú duu un marge esquerre de -12, així que ocupa de 18 a 66, i amb els seus
                // 16 de marge dret la caixa del logo arrenca a LOGO_BOX_LEFT. Fixant-ne l'amplada
                // (en comptes de deixar que la mida del logo mani) la vora cau sempre a MENU_WIDTH.
                width: MENU_WIDTH - LOGO_BOX_LEFT + 'px',
                boxSizing: 'border-box',
                borderRight: `1px solid ${theme.palette.divider}`,
            }}
            title={<img style={{ marginLeft: '8px', height: '49px', verticalAlign: 'middle' }} src={logoColor} alt="Distribucio"/>}
            version={version}
            menuEntries={filtrarEntradesMenu(menuEntries, currentRole)}
            menuAppearance={estilMenu}
            appbarBackgroundColor={bgColor}
            // El botó de menú duu ml -12, així que amb 30 de padding queda centrat a 42px, la
            // mateixa columna que les icones del menú lateral.
            appbarStyle={{ color: textColor, paddingLeft: APPBAR_PADDING_LEFT + 'px' }}
            footerHeight={36}
            footer={
                <div style={{ height: '36px' }}>
                    <DrassanaFooter title="DISTRIBUCIÓ" backgroundColor="#5F5D5D" style={{ position: 'fixed', width: '100%', bottom: 0 }}/>
                </div>
            }
        >
            <Outlet />
        </BaseApp>
    );
};

const ThemedApp: React.FC = () => {
    const { temaAplicacio } = useUserPreferences();
    const prefersDarkMode = useMediaQuery('(prefers-color-scheme: dark)');
    const theme = getThemeForTema(temaAplicacio, prefersDarkMode);
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <InnerApp />
        </ThemeProvider>
    );
};

export const App = () => {
    const authConfig = getAuthConfig();
    return (
        <AuthProvider
            appBaseUrl={import.meta.env.BASE_URL}
            logoutUrl={import.meta.env.BASE_URL}
            config={authConfig}
            mandatory
        >
            <ResourceApiProvider apiUrl={getEnvApiUrl()}>
                <UserPreferencesProvider>
                    <DistribucioProvider>
                        <SessionStorageProvider>
                            <ThemedApp />
                        </SessionStorageProvider>
                    </DistribucioProvider>
                </UserPreferencesProvider>
            </ResourceApiProvider>
        </AuthProvider>
    );
};

export default App;
