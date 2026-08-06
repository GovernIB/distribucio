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
        primary: { main: '#497e3a' },
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
