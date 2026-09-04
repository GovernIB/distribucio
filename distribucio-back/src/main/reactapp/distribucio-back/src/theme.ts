import { createTheme, alpha, lighten } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';
import type {} from '@mui/x-data-grid/themeAugmentation';

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

// Patró de línies en diàgonal per al fons
const hatchPattern = (lineColor: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${lineColor} 1.25px, transparent 1.75px, transparent 3px)`;

// Compartit per tots els temes: el MuiTreeItem s'adapta automàticament al `theme` actiu que rep
// per paràmetre, per tant no cal repetir-lo a cada tema.
const baseComponentStyles = {
    MuiCssBaseline: {
        styleOverrides: {
            // Contenidor del filtre dels llistats (veure components/StyledMuiFilter.tsx).
            '.styledFilter': {
                paddingTop: '11px',
                paddingBottom: '16px',
                borderRadius: '4px',
                backgroundColor: 'inherit',
            },
            'input:-webkit-autofill, input:-webkit-autofill:hover, input:-webkit-autofill:focus, input:-webkit-autofill:active': {
                WebkitBoxShadow: '0 0 0 100px transparent inset !important',
                caretColor: 'inherit !important',
                transition: 'background-color 5000s ease-in-out 0s !important',
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
                '&:not(.MuiButtonGroup-grouped)': { marginLeft: '10px' },
                '& .MuiButton-startIcon': { marginRight: '0' },
                '&.Mui-disabled': {
                    opacity: 0.6,
                    cursor: 'not-allowed',
                },
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
                // flexDirection: 'row-reverse' as const,
                // justifyContent: 'flex-start' as const,
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
    MuiTypography: {
        styleOverrides: {
            h5: { fontSize: '1.8rem', lineHeight: 1.2, fontWeight: 400 },
            h4: { fontSize: '1.5rem', lineHeight: 1.2, fontWeight: 400 },
            // body1: { fontWeight: 500 },
            // overline: { fontSize: '1.2rem', letterSpacing: '0em', textTransform: 'none' },
        },
    },
    MuiDialogTitle: {
        styleOverrides: {
            root: {
                padding: '5px 24px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                fontSize: '1.5rem',
            },
        },
    },
    MuiCardHeader: {
        styleOverrides: {
            root: {
                paddingTop: '8px',
                paddingBottom: '8px',
                paddingLeft: '16px',
                paddingRight: '16px',
            },
        },
    },
    MuiDialogContent: {
        styleOverrides: {
            root: {
                paddingTop: '20px !important',
            },
        },
    },
    MuiDataGrid: {
        styleOverrides: {
            root: {
                // '& [class^="row-with-color-"] .MuiDataGrid-cellCheckbox': {
                //     width: '48px !important',
                //     maxWidth: '48px !important',
                //     minWidth: '48px !important',
                //     marginLeft: '-4px !important',
                // },
                // '& .MuiDataGrid-cell': {
                //     display: 'flex',
                // },
                // '& .MuiDataGrid-treeDataGroupingCell': {
                //     '--DataGrid-t-spacing-unit': '16px',
                // },
                // '& .MuiDataGrid-treeDataGroupingCell > *': {
                //     display: 'flex',
                //     alignItems: 'center',
                // },
                // '& .MuiDataGrid-treeDataGroupingCellToggle': {
                //     marginRight: 0,
                // },
            },
            row: {
                minHeight: '45px !important',
            },
            cell: {
                '&.MuiDataGrid-cell--withRenderer': {
                    alignItems: 'flex-start !important',
                },
            },
            columnHeader: {
                '&.MuiDataGrid-columnHeaderCheckbox': {
                    alignItems: 'flex-end !important',
                    paddingTop: '4px !important',
                },
            },
            checkboxInput: {
                transform: 'scale(0.8)',
            },
        },
    },
    // Evita que Chrome pinti de groc els camps autocompletats (autofill).
    // El color de fons que substitueix el groc es defineix a cada tema,
    // perquè ha de coincidir amb `background.paper` de la palette.
    MuiOutlinedInput: {
        styleOverrides: {},
    },
};

const LIGHT_PRIMARY_MAIN = '#439798';
const LIGHT_PRIMARY_CONTRAST_TEXT = '#ffffff';
const LIGHT_SECONDARY_MAIN = '#2E2E2E';
const LIGHT_CUSTOM_BACKGROUND = '#f5f5f5';
const LIGHT_GREY_BACKGROUND = '#e7e7e7';

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: LIGHT_PRIMARY_MAIN, },
        customBackground: LIGHT_CUSTOM_BACKGROUND,
        greyBackground: LIGHT_GREY_BACKGROUND,
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                    backgroundColor: LIGHT_CUSTOM_BACKGROUND,
                    backgroundImage: hatchPattern('#e1e1e1'), // Color del patró per al tema clar
                    color: '#000000de',
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    // Només el botó de tancar (fill directe del paper, sobre la capçalera acolorida).
                    // Sense el combinador '>' també s'aplicava a les icones del cos del diàleg.
                    '& > .MuiIconButton-root .MuiIcon-root': {
                        color: LIGHT_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDialogTitle?.styleOverrides?.root as object),
                    backgroundColor: LIGHT_PRIMARY_MAIN,
                    color: LIGHT_PRIMARY_CONTRAST_TEXT,
                    // borderBottom: '1px solid #e3e3e3',
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: alpha(LIGHT_SECONDARY_MAIN, 0.08),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(LIGHT_PRIMARY_MAIN, 0.4)} !important` },
                },
            },
        },
    },
});

const DARK_PRIMARY_MAIN = '#439798';
const DARK_PRIMARY_LIGHT = '#52b9bb';
const DARK_PRIMARY_CONTRAST_TEXT = '#ffffff';
const DARK_CUSTOM_BACKGROUND = '#121212';
const DARK_GREY_BACKGROUND = '#222222';
const DARK_TEXT_SECONDARY = '#bbbbbb';

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: {light: DARK_PRIMARY_LIGHT, main: DARK_PRIMARY_MAIN },
        customBackground: DARK_CUSTOM_BACKGROUND,
        greyBackground: DARK_GREY_BACKGROUND,
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                   backgroundColor: DARK_CUSTOM_BACKGROUND,
                    backgroundImage: hatchPattern('#2c2c2c'),
                    color: DARK_PRIMARY_CONTRAST_TEXT,
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: DARK_PRIMARY_LIGHT,
                        borderWidth: '2px',
                    },
                    '&.Mui-disabled .MuiOutlinedInput-notchedOutline': {
                        borderColor: alpha(DARK_TEXT_SECONDARY, 0.4),
                    },
                },
            },
        },
        MuiDialog: {
            styleOverrides: {
                paper: {
                    // Només el botó de tancar (fill directe del paper, sobre la capçalera acolorida).
                    // Sense el combinador '>' també s'aplicava a les icones del cos del diàleg.
                    '& > .MuiIconButton-root .MuiIcon-root': {
                        color: DARK_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDialogTitle: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDialogTitle?.styleOverrides?.root as object),
                    backgroundColor: DARK_PRIMARY_MAIN,
                    color: DARK_PRIMARY_CONTRAST_TEXT,
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: lighten('#2e2e2e', 0.05),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.28)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(DARK_PRIMARY_MAIN, 0.5)} !important` },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: DARK_PRIMARY_CONTRAST_TEXT,
                                borderColor: DARK_PRIMARY_CONTRAST_TEXT,
                            },
                        },
                    ],
                },
            },
        },
    },
});

const DRACULA_PRIMARY_LIGHT = '#52b9bb';
const DRACULA_PRIMARY_MAIN = '#439798';
const DRACULA_PRIMARY_CONTRAST_TEXT = '#ffffff';
// const DRACULA_CUSTOM_BACKGROUND = '#121212';
// const DRACULA_GREY_BACKGROUND = '#222222';
const DRACULA_TEXT_SECONDARY = '#D6D6C2';

// Tema Dracula (https://draculatheme.com/) -- palet fosc d'alt contrast, alternatiu al tema fosc
// per defecte.
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: DRACULA_PRIMARY_LIGHT, main: DRACULA_PRIMARY_MAIN, contrastText: DRACULA_PRIMARY_CONTRAST_TEXT },
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
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                body: {
                   backgroundColor: '#282a36',
                    backgroundImage: hatchPattern('#44475a'),
                    color: '#f8f8f2',
                },
            },
        },
        MuiOutlinedInput: {
            styleOverrides: {
                root: {
                    '&.Mui-focused .MuiOutlinedInput-notchedOutline': {
                        borderColor: DRACULA_PRIMARY_LIGHT,
                        borderWidth: '2px',
                    },
                    '&.Mui-disabled .MuiOutlinedInput-notchedOutline': {
                        borderColor: alpha(DRACULA_TEXT_SECONDARY, 0.4),
                    },
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiButton?.styleOverrides?.root as object),
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: '#F8F8F2',
                                borderColor: DRACULA_PRIMARY_CONTRAST_TEXT,
                            },
                        },
                    ],
                    '.MuiDialog-paper &.MuiButton-outlinedPrimary': {
                         borderColor: DRACULA_PRIMARY_CONTRAST_TEXT,
                    },
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.root as object),
                    '& .MuiDataGrid-row.even:not(.Mui-selected)': {
                        backgroundColor: lighten('#282A36', 0.05),
                    },
                    '& .MuiDataGrid-row:hover': {
                        backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.15)} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.2)} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${alpha(DRACULA_PRIMARY_MAIN, 0.3)} !important` },
                },
            },
        },
    },
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
