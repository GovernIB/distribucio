import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import { useTheme } from '@mui/material/styles';
import { envVar, ResourceApiProvider } from 'reactlib';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import distribucioLogo from './assets/DIR_DRA_COL.svg';
import { UserPreferencesProvider, useUserPreferences } from './components/UserProfile';
import { TemaProvider } from './components/TemaProvider';
import { DistribucioProvider } from './components/DistribucioProvider';
import { DistribucioAuthProvider } from './components/DistribucioAuthProvider';
import { useDistribucioContext } from './components/DistribucioContext';
import { filtrarEntradesMenu, type MenuEntryAmbPantalla } from './util/pantalles';
import { icons } from './util/icons';
import {
    APPBAR_PADDING_LEFT,
    FOOTER_HEIGHT,
    HEADER_HEIGHT,
    LOGO_BOX_LEFT,
    LOGO_HEIGHT,
    LOGO_SEPARATOR_GAP,
    MENU_WIDTH,
} from './util/layout';
import { SessionStorageProvider } from './components/SessionStorageContext';
import TitolPagina from './components/TitolPagina';
import {SnackbarProvider} from "notistack";

// eslint-disable-next-line react-refresh/only-export-components
export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
    VITE_APP_VERSION: import.meta.env.VITE_APP_VERSION,
};

// eslint-disable-next-line react-refresh/only-export-components
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
            // Sense cap URL configurada, l'API és al mateix servidor que serveix la pàgina i sota
            // el mateix context path: el pare del base path del SPA ("/distribucioback/reactapp"
            // -> "/distribucioback"). Així el build no depèn del nom ni del port del servidor on
            // es desplega.
            const contextPath = import.meta.env.BASE_URL.replace(/\/+$/, '').replace(/\/[^/]*$/, '');
            return window.location.origin + contextPath + envApiSuffix;
        }
    }
};

const version = import.meta.env.VITE_APP_VERSION ?? '0.0.0';

// Les mides de la capçalera, del menú i del peu són a util/layout.ts: MENU_WIDTH és l'amplada del
// menú lateral obert, APPBAR_PADDING_LEFT desplaça el botó de menú fins a la columna de les icones
// del menú, i LOGO_BOX_LEFT és on comença la caixa del logo amb aquest padding.

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const theme = useTheme();
    const mode = theme.palette.mode;

    const { currentRole } = useDistribucioContext();
    // La pantalla de cada entrada determina a quins rols es mostra (veure PANTALLA_ROLS a
    // util/pantalles.ts): el menú i les guardes de ruta surten de la mateixa declaració.
    // Icones a util/icons.ts: la mateixa icona que fa servir el títol de cada pantalla (ScreenFrame).
    const menuEntries: MenuEntryAmbPantalla[] = [
        {
            id: 'home',
            title: t('app.menu.home'),
            to: 'home',
            icon: icons.inici,
            pantalla: 'home',
        },
        {
            id: 'registre',
            title: t('app.menu.registre'),
            to: 'registre',
            icon: icons.anotacio,
            pantalla: 'registre',
        },
        {
            id: 'vistaMoviments',
            title: t('app.menu.vistaMoviments'),
            to: 'vistaMoviments',
            icon: icons.vistaMoviments,
            pantalla: 'vistaMoviments',
        },
        {
            id: 'entitats',
            title: t('app.menu.entitats'),
            to: 'entitat',
            icon: icons.entitat,
            pantalla: 'entitat',
        },
        {
            id: 'configuracio',
            title: t('app.menu.configuracio'),
            icon: icons.configuracio,
            children: [
                {
                    id: 'bustia',
                    title: t('app.menu.bustia'),
                    to: 'bustiaAdminOrganigrama',
                    icon: icons.bustia,
                    pantalla: 'bustiaAdminOrganigrama'
                },
                {
                    id: 'uo',
                    title: t('app.menu.unitatOrganitzativa'),
                    to: 'unitatOrganitzativa',
                    icon: icons.unitatOrganitzativa,
                    pantalla: 'unitatOrganitzativa'
                },
                {
                    id: 'regla',
                    title: t('app.menu.regla'),
                    to: 'regla',
                    icon: icons.regla,
                    pantalla: 'regla'
                },
                {
                    id: 'backoffice',
                    title: t('page.backoffice.title'),
                    to: 'backoffice',
                    icon: icons.backoffice,
                    pantalla: 'backoffice'
                },
                {
                    id: 'permis',
                    title: t('page.entitats.permis.title'),
                    to: 'permis',
                    icon: icons.permis,
                    pantalla: 'permis'
                },
                {
                    id: 'metaDada',
                    title: t('page.metadada.title'),
                    to: 'metaDada',
                    icon: icons.metaDada,
                    pantalla: 'metaDada',
                },
                {
                    id: 'domini',
                    title: t('page.domini.title'),
                    to: 'domini',
                    icon: icons.domini,
                    pantalla: 'domini',
                },
            ],
        },
        {
            id: 'configurar',
            title: t('app.menu.configurar'),
            icon: icons.ajustos,
            children: [
                {
                    id: 'config',
                    title: t('app.menu.config'),
                    to: 'config',
                    icon: icons.config,
                    pantalla: 'config'
                },
                {
                    id: 'limitCanviEstat',
                    title: t('app.menu.limitCanviEstat'),
                    to: 'limitCanviEstat',
                    icon: icons.limitCanviEstat,
                    pantalla: 'limitCanviEstat'
                },
                {
                    id: 'usuariCanviCodis',
                    title: t('app.menu.usuariCanviCodis'),
                    to: 'usuariCanviCodis',
                    icon: icons.usuariCanviCodis,
                    pantalla: 'usuariCanviCodis'
                },
            ],
        },
        {
            id: 'avisos',
            title: t('app.menu.avisos'),
            to: 'avis',
            icon: icons.avis,
            pantalla: 'avis',
        },
        {
            id: 'monitoritzar',
            title: t('app.menu.monitoritzar'),
            icon: icons.monitoritzacio,
            children: [
                {
                    id: 'integracio',
                    title: t('app.menu.integracio'),
                    to: 'integracio',
                    icon: icons.integracio,
                    pantalla: 'integracio',
                },
                {
                    id: 'excepcio',
                    title: t('app.menu.excepcio'),
                    to: 'excepcio',
                    icon: icons.excepcio,
                    pantalla: 'excepcio',
                },
                {
                    id: 'metriques',
                    title: t('app.menu.metriques'),
                    to: 'metriques',
                    icon: icons.metriques,
                    pantalla: 'metriques',
                },
                {
                    id: 'pendentsArxiu',
                    title: t('app.menu.pendentsArxiu'),
                    to: 'pendentsArxiu',
                    icon: icons.pendentsArxiu,
                    pantalla: 'pendentsArxiu',
                },
                {
                    id: 'monitor',
                    title: t('app.menu.monitor'),
                    to: 'monitor',
                    icon: icons.monitorSistema,
                    pantalla: 'monitor',
                },
            ],
        },
        {
            id: 'consultar',
            title: t('app.menu.consultar'),
            icon: icons.consulta,
            children: [
                {
                    id: 'contingut',
                    title: t('app.menu.contingut'),
                    to: 'contingut',
                    icon: icons.contingut,
                    pantalla: 'contingut',
                },
                {
                    id: 'annex',
                    title: t('app.menu.annex'),
                    to: 'annexosAdmin',
                    icon: icons.annex,
                    pantalla: 'annex',
                },
                {
                    id: 'procediment',
                    title: t('app.menu.procediment'),
                    to: 'procediment',
                    icon: icons.procediment,
                    pantalla: 'procediment',
                },
                {
                    id: 'serveis',
                    title: t('app.menu.serveis'),
                    to: 'servei',
                    icon: icons.servei,
                    pantalla: 'servei',
                },
                // { divider: true },
                {
                    id: 'massiva',
                    title: t('app.menu.massiva'),
                    to: 'massiva',
                    icon: icons.massiva,
                    pantalla: 'massiva',
                },
            ],
        },
    ];

    const bgColor = mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor = bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    // DIR_DRA_COL.svg té els colors fixats (verd corporatiu i gris fosc), així que serveix per
    // als dos modes; si algun dia cal una variant per a fons foscos, tornar a fer el ternari.
    const logoColor = distribucioLogo;
    const { estilMenu } = useUserPreferences();

    return (
        <BaseApp
            code="DISTRIBUCIO"
            logo={mode === 'light' ? goibLogoLight : goibLogoDark}
            logoStyle={{
                '& img': { height: LOGO_HEIGHT + 'px', display: 'block' },
                // El separador vertical ha de caure sobre la vora dreta del menú obert, i omplir
                // l'alçada de la capçalera. El botó de menú duu un marge esquerre de -12 i un dret de
                // 16, així que la caixa del logo arrenca a LOGO_BOX_LEFT. Fixant-ne l'amplada (en
                // comptes de deixar que la mida del logo mani) la vora cau sempre a MENU_WIDTH.
                width: MENU_WIDTH - LOGO_BOX_LEFT + 'px',
                boxSizing: 'border-box',
                alignSelf: 'stretch',
                borderRight: `1px solid ${theme.palette.divider}`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'flex-end',
                // Substitueix el mr/pt/pr de la llibreria (l'sx s'hi fusiona al final).
                mr: 0,
                pt: 0,
                pl: 0,
                pr: LOGO_SEPARATOR_GAP + 'px',
            }}
            title={
                <img
                    style={{
                        marginLeft: LOGO_SEPARATOR_GAP + 'px',
                        height: LOGO_HEIGHT + 'px',
                        verticalAlign: 'middle',
                    }}
                    src={logoColor}
                    alt="Distribucio"
                />
            }
            version={version}
            menuEntries={filtrarEntradesMenu(menuEntries, currentRole)}
            menuAppearance={estilMenu}
            appbarBackgroundColor={bgColor}
            // El botó de menú duu ml -12, així que amb APPBAR_PADDING_LEFT queda centrat a la mateixa
            // columna que les icones del menú lateral. L'alçada mínima substitueix els 64px que la
            // llibreria dona a la barra.
            appbarStyle={{
                color: textColor,
                paddingLeft: APPBAR_PADDING_LEFT + 'px',
                minHeight: HEADER_HEIGHT + 'px',
            }}
            footerHeight={FOOTER_HEIGHT}
            // El peu va per flux, no fixat: l'arrel de l'aplicació ocupa l'alçada de la finestra
            // (veure frameSx a BaseApp.tsx), així que queda sempre a baix i sense tapar el contingut.
            footer={
                <div style={{ height: FOOTER_HEIGHT + 'px', overflow: 'hidden' }}>
                    <DrassanaFooter title="DISTRIBUCIÓ" backgroundColor="#5F5D5D" />
                </div>
            }
        >
            {/* Va per davant de l'<Outlet> perquè una pàgina que es posi el títol pel seu
                compte (useTitolPagina) sobreescrigui el que declara la ruta. */}
            <TitolPagina />
            <Outlet />
        </BaseApp>
    );
};

export const App = () => {
    const apiUrl = getEnvApiUrl();
    // Autenticació amb la sessió de servidor, sense token al navegador (veure
    // DistribucioAuthProvider).
    return (
        <DistribucioAuthProvider apiUrl={apiUrl}>
            <ResourceApiProvider apiUrl={apiUrl}>
                {/* TemaProvider va per fora de tot, també de la pantalla de càrrega de
                    DistribucioProvider: arrenca amb l'últim tema conegut de l'usuari perquè no hi
                    hagi parpelleig mentre no arriba el perfil.

                    UserPreferencesProvider, en canvi, va per dins de DistribucioProvider: les
                    preferències (idioma, tema, estil de menú, mida de pàgina...) surten del perfil
                    que aquest carrega, i DistribucioProvider no pinta els fills fins a tenir-lo. */}
                <TemaProvider>
                    <SnackbarProvider maxSnack={99}>
                    <DistribucioProvider>
                        <UserPreferencesProvider>
                            <SessionStorageProvider>
                                {/* La subscripció d'esdeveniments (SseProvider) la posa
                                    DistribucioProvider, per fora de la seva pantalla de
                                    càrrega. */}
                                <InnerApp />
                            </SessionStorageProvider>
                        </UserPreferencesProvider>
                    </DistribucioProvider>
                    </SnackbarProvider>
                </TemaProvider>
            </ResourceApiProvider>
        </DistribucioAuthProvider>
    );
};

export default App;
