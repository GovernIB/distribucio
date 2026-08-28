import React from 'react';
import {
    useNavigate,
    useLocation,
    useBlocker,
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
} from 'react-router-dom';
import { saveAs } from 'file-saver';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { useTheme, type Theme } from '@mui/material/styles';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { MuiBaseApp, type MenuEntry, useBaseAppContext, useMuiFormDialogApiRef } from 'reactlib';
import i18n from '../i18n/i18n';
import reactlibCa from '../i18n/reactlibCa';
import reactlibEs from '../i18n/reactlibEs';
import Offline from './Offline';
import { UserProfileMenu, UserProfileFormDialog, useUserPreferences } from './UserProfile';
import { EntitatSelector, RolSelector, getRolBadgeIcon } from './EntitatRolSelector';
import { InterficieClassicaButton } from './InterficieClassica';
import { useDistribucioContext } from './DistribucioContext';
import { MenuEstil } from '../theme';

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    logo?: string;
    logoStyle?: any;
    title?: string | React.ReactElement;
    version: string;
    menuEntries?: MenuEntry[];
    menuAppearance?: MenuEstil;
    footer?: React.ReactElement;
    footerHeight?: number;
    appbarBackgroundColor?: string;
    appbarStyle?: any;
};

type MenuColorSet = {
    background: string;
    textPrimary: string;
    textSecondary: string;
    divider: string;
    accent: string;
    selectedBackground: string;
    hoverBackground: string;
};

// `TEMA` no sobreescriu cap color: el menú reutilitza directament la paleta activa de MUI.
const getMenuColorSet = (theme: Theme, appearance: MenuEstil): MenuColorSet | undefined => {
    if (appearance === MenuEstil.PEU) {
        return {
            background: '#5F5D5D',
            textPrimary: '#F6F6F6',
            textSecondary: '#E5E5E5',
            divider: '#807D7D',
            accent: '#FFFFFF',
            selectedBackground: 'rgba(255, 255, 255, 0.12)',
            hoverBackground: 'rgba(255, 255, 255, 0.08)',
        };
    }
    if (appearance !== MenuEstil.TEMA_INVERTIT) {
        return undefined;
    }
    if (theme.palette.mode === 'dark') {
        return {
            background: '#FFFFFF',
            textPrimary: '#1F2937',
            textSecondary: '#4B5563',
            divider: '#D1D5DB',
            accent: '#1976D2',
            selectedBackground: 'rgba(25, 118, 210, 0.12)',
            hoverBackground: 'rgba(0, 0, 0, 0.04)',
        };
    }
    return {
        background: '#1E293B',
        textPrimary: '#F8FAFC',
        textSecondary: '#CBD5E1',
        divider: '#475569',
        accent: '#60A5FA',
        selectedBackground: 'rgba(96, 165, 250, 0.18)',
        hoverBackground: 'rgba(255, 255, 255, 0.08)',
    };
};

// Espai de noms on base-react registra les seves traduccions (LIB_I18N_NS a lib/components/
// BaseApp.tsx, que la llibreria no exporta) i sobreescriptures de l'aplicació per idioma.
// base-react també hi registra l'anglès, que aquí no es fa servir i per això no té paquet.
const REACTLIB_I18N_NS = 'reactlib';
const REACTLIB_OVERRIDES: Record<string, any> = { ca: reactlibCa, es: reactlibEs };

export const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const useLocationPath = () => {
    const location = useLocation();
    return location.pathname;
};

const CustomLocalizationProvider = ({ children }: React.PropsWithChildren) => {
    const { currentLanguage } = useBaseAppContext();
    const adapterLocale = React.useMemo(() => {
        const languageTwoChars = currentLanguage?.substring(0, 2).toLowerCase();
        return languageTwoChars === 'ca' || languageTwoChars === 'es' ? languageTwoChars : 'ca';
    }, [currentLanguage]);
    return (
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale={adapterLocale}>
            {children}
        </LocalizationProvider>
    );
};

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        logoStyle,
        title,
        version,
        menuEntries,
        menuAppearance,
        footer,
        footerHeight,
        appbarBackgroundColor,
        appbarStyle,
        children,
    } = props;
    const theme = useTheme();
    const menuColorSet = getMenuColorSet(theme, menuAppearance ?? MenuEstil.TEMA);
    const menuColorSetSx = menuColorSet
        ? {
              '& nav .MuiDrawer-root': {
                  '& .MuiPaper-root, & .MuiList-root': {
                      backgroundColor: menuColorSet.background,
                      color: menuColorSet.textPrimary,
                      '& > div .MuiBox-root': {
                          backgroundColor: menuColorSet.background,
                          borderColor: menuColorSet.divider,
                      },
                      '& > div > .MuiBox-root': { borderLeft: `1px solid ${menuColorSet.divider}` },
                      '& p': { color: menuColorSet.textPrimary },
                      '& h6': { color: menuColorSet.accent },
                  },
                  '& .menu-item-icon': { color: menuColorSet.textSecondary },
                  '& .MuiListItemButton-root': {
                      '&.Mui-selected': { backgroundColor: menuColorSet.selectedBackground },
                      '&.Mui-selected:hover': { backgroundColor: menuColorSet.selectedBackground },
                      '&:hover': { backgroundColor: menuColorSet.hoverBackground },
                  },
              },
          }
        : undefined;
    const navigate = useNavigate();
    const location = useLocation();
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    };
    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
        // base-react registra aquí les seves traduccions. Com que no s'hi pot tocar, els seus
        // textos es corregeixen fusionant-hi a sobre el paquet de sobreescriptures de
        // l'aplicació (veure i18n/reactlibCa.ts): fusió profunda -- només se substitueixen les
        // claus declarades -- i amb sobreescriptura, perquè la clau ja existeix.
        const overrides = namespace === REACTLIB_I18N_NS ? REACTLIB_OVERRIDES[language] : undefined;
        if (overrides != null) {
            i18n.addResourceBundle(language, namespace, overrides, true, true);
        }
    };
    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (anyHistoryEntryExist()) {
            navigate(-1);
        } else if (fallback != null) {
            navigate(fallback);
        }
    };
    const formDialogApiRef = useMuiFormDialogApiRef();
    const { currentRole: rolActual } = useDistribucioContext();
    // L'idioma surt del perfil de l'usuari (dis_usuari.idioma), que DistribucioProvider ja té
    // carregat abans de pintar res. i18n.language -- el que detecta el navegador -- només fa de
    // recurs si el perfil no en duu cap. Fer-ho aquí, i no des del diàleg de perfil, és el que
    // evita que obrir el perfil canviï l'idioma i recarregui la pantalla.
    const { idioma } = useUserPreferences();
    return (
        <Box sx={menuColorSetSx}>
        <MuiBaseApp
            code={code}
            headerTitle={title}
            headerLogo={logo}
            headerLogoStyle={logoStyle}
            headerAppbarBackgroundColor={appbarBackgroundColor}
            headerAppbarStyle={appbarStyle}
            headerVersion={version}
            headerAdditionalComponents={[
                <Box key="interficie_classica" sx={{ display: 'flex', alignItems: 'center', mr: 2 }}>
                    <InterficieClassicaButton />
                </Box>,
                <Box key="entitat_selector" sx={{ display: 'flex', alignItems: 'center', mr: 2 }}>
                    <EntitatSelector />
                </Box>,
            ]}
            headerAuthBadgeIcon={getRolBadgeIcon(rolActual)}
            headerAdditionalAuthComponents={[
                <Box key="user_profile" sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
                    <UserProfileMenu formDialogApiRef={formDialogApiRef} />
                </Box>,
                <RolSelector key="rol_selector" />,
            ]}
            offline={<Offline />}
            footer={footer}
            footerHeight={footerHeight}
            // Sense `persistentLanguage`: la llibreria guardaria l'idioma al localStorage i el
            // rellegiria en arrencar, competint amb el perfil. Amb dues fonts, un canvi no desat
            // sobrevivia a la recàrrega i el perfil l'havia de desfer després, amb el refresc
            // corresponent. La preferència desada a la base de dades és l'única font.
            i18nUseTranslation={useTranslation}
            i18nCurrentLanguage={idioma ?? i18n.language}
            i18nHandleLanguageChange={i18nHandleLanguageChange}
            i18nAddResourceBundleCallback={i18nAddResourceBundleCallback}
            routerGoBack={goBack}
            routerNavigate={navigate}
            routerUseBlocker={useBlocker}
            routerUseLocationPath={useLocationPath}
            routerAnyHistoryEntryExist={anyHistoryEntryExist}
            linkComponent={Link}
            marginsDisabled={false}
            saveAs={saveAs}
            menuEntries={menuEntries}
        >
            <CustomLocalizationProvider>
                <UserProfileFormDialog formDialogApiRef={formDialogApiRef} />
                {children}
            </CustomLocalizationProvider>
        </MuiBaseApp>
        </Box>
    );
};

export default BaseApp;
