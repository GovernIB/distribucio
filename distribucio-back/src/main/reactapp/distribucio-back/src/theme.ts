import { createTheme, alpha } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';

// Estenem les interfícies de MUI per admetre el nou color
declare module '@mui/material/styles' {
    interface Palette {
        customBackground: string;
        greyBackground: string;
    }
    interface PaletteOptions {
        customBackground?: string;
        greyBackground?: string;
    }
}

export enum TemaAplicacio {
    CLAR = 'CLAR',
    OBSCUR = 'OBSCUR',
    DRACULA = 'DRACULA',
    SISTEMA = 'SISTEMA',
}

export enum MenuEstil {
    TEMA = 'TEMA',
    TEMA_INVERTIT = 'TEMA_INVERTIT',
    PEU = 'PEU',
}

// Compartit per tots els temes: el MuiTreeItem s'adapta automàticament al `theme` actiu que rep
// per paràmetre, per tant no cal repetir-lo a cada tema.
const commonComponents = {
    MuiCssBaseline: {
        styleOverrides: {
            // Contenidor del filtre dels llistats (veure components/StyledMuiFilter.tsx).
            // El padding horitzontal és el mateix desplaçament que la graella aplica a les
            // seves files, de manera que els camps del filtre queden alineats amb les
            // columnes i el botó "Filtra" no arriba a tocar la vora dreta de la pàgina.
            // (A RIPEA aquest padding està comentat perquè allà el filtre va dins d'un CardPage
            // que ja el proporciona.)
            '.styledFilter': {
                marginBottom: '16px',
                // 16px a dalt (no els 11px de RIPEA) perquè les etiquetes flotants dels camps
                // no quedin enganxades a la capçalera: aquí el filtre no va dins de cap card.
                paddingTop: '16px',
                paddingBottom: '16px',
                paddingLeft: '16px',
                paddingRight: '16px',
                borderRadius: '4px',
                backgroundColor: 'inherit',
            },
        },
    },
    // Els botons mostren l'etiqueta tal com està escrita a les traduccions ("Filtra", "Guarda"),
    // no en majúscules: MUI aplica `textTransform: uppercase` per defecte a tots els botons i
    // aquí es desactiva perquè els botons es llegeixin igual que les entrades dels menús.
    MuiButton: {
        styleOverrides: {
            root: {
                textTransform: 'none' as const,
            },
        },
    },
    // Ordre dels botons de totes les finestres modals: el d'acció ("Desa", "Accepta",
    // "Executa"...) a l'esquerra i el de sortida ("Cancel·la") a la dreta.
    //
    // base-react els declara sempre en l'ordre contrari ([cancel·lar, acció], veure
    // lib/components/AppButtons.tsx) i els pinta dins un MuiDialogActions; com que la
    // llibreria no es pot tocar, la inversió es fa aquí, a nivell de tema, i val per a
    // qualsevol diàleg de l'aplicació.
    //
    // Amb `row-reverse` l'inici de l'eix principal passa a ser la dreta, per això el
    // `justifyContent` ha de ser `flex-start` perquè el bloc de botons continuï alineat a la
    // dreta. La separació es passa a `gap` perquè el marge esquerre que MUI posa entre germans
    // quedaria a la vora exterior del bloc, no entre els botons.
    MuiDialogActions: {
        styleOverrides: {
            root: {
                flexDirection: 'row-reverse' as const,
                justifyContent: 'flex-start' as const,
                gap: '8px',
                '& > :not(style) ~ :not(style)': {
                    marginLeft: 0,
                },
            },
        },
    },
    MuiTreeItem: {
        styleOverrides: {
            content: ({ theme }: { theme: any }) => ({
                '&:hover': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.1),
                },
                '&.Mui-selected': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.15),
                    '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.2),
                    },
                    '&.Mui-focused': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.25),
                    },
                },
            }),
        },
    },
};

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: '#439798' },
        customBackground: '#f5f5f5',
        greyBackground: '#f5f5f5',
    },
    components: commonComponents,
});

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { main: '#86e56c' },
        customBackground: '#121212',
        greyBackground: '#222222',
    },
    components: commonComponents,
});

// Tema Dracula (https://draculatheme.com/) -- palet fosc d'alt contrast, alternatiu al tema fosc
// per defecte.
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { main: '#bd93f9', contrastText: '#282a36' },
        secondary: { main: '#f8f8f2' },
        background: { default: '#282a36', paper: '#303341' },
        text: { primary: '#f8f8f2', secondary: '#d6d6c2' },
        error: { main: '#ff5555' },
        warning: { main: '#ffb86c' },
        success: { main: '#50fa7b' },
        info: { main: '#8be9fd' },
        divider: '#44475a',
        customBackground: '#282a36',
        greyBackground: '#303341',
    },
    components: commonComponents,
});

export const getThemeForTema = (temaAplicacio: TemaAplicacio | undefined, prefersDarkMode: boolean) => {
    switch (temaAplicacio) {
        case TemaAplicacio.CLAR:
            return lightTheme;
        case TemaAplicacio.OBSCUR:
            return darkTheme;
        case TemaAplicacio.DRACULA:
            return draculaTheme;
        case TemaAplicacio.SISTEMA:
        default:
            return prefersDarkMode ? darkTheme : lightTheme;
    }
};

export default lightTheme;
