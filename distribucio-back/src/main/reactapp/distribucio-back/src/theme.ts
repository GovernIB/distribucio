import { createTheme, alpha, lighten, darken, type Theme } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';
import type {} from '@mui/x-data-grid/themeAugmentation';
import {
    HEADER_HEIGHT,
    ICON_SIZE,
    MENU_COLLAPSED_BELOW,
    MENU_BORDER_WIDTH,
    MENU_ICON_COLUMN,
    MENU_ITEM_PADDING_LEFT,
    MENU_WIDTH_COLLAPSED,
} from './util/layout';

// Estenem les interfícies de MUI per admetre el nou color
declare module '@mui/material/styles' {
    interface Palette {
        customBackground: string;
        greyBackground: string;
        /** Atenuat del color base de l'aplicació: fons de la capçalera de les graelles. */
        primarySoft: string;
    }
    interface PaletteOptions {
        customBackground?: string;
        greyBackground?: string;
        primarySoft?: string;
    }
}

// Identitat visual de DISTRIBUCIÓ (colors de Drassana). Per portar el disseny a una altra aplicació
// només cal canviar aquests tres valors:
//  - MAIN: color base. Capçaleres de pantalla, diàlegs i botons d'acció.
//  - DARK: "torrat", és el `primary.dark` de MUI: hover dels botons i, als temes clars, badges,
//    paginació, barres de desplaçament i icona seleccionada del menú (veure accentColor).
//  - SOFT: atenuat, és `palette.primarySoft`. Fons de la capçalera de les graelles al tema clar.
// Als temes foscos l'atenuat i el torrat s'inverteixen i es deriven de la base: l'atenuat és un to
// molt fosc (`darken`) i el torrat un to clar (`PRIMARY_LIGHT`, és el `primary.light` de MUI), perquè
// un torrat sobre fons fosc no té contrast. No hi ha cap altre color d'identitat.
const PRIMARY_MAIN = '#439798';
const PRIMARY_DARK = '#2f6a6a';
const PRIMARY_SOFT = '#EBF4F4';
const PRIMARY_LIGHT = lighten(PRIMARY_MAIN, 0.3);

/**
 * Color "torrat" de la identitat: badges, paginació, barres de desplaçament, icona seleccionada del
 * menú... És `primary.dark` als temes clars; als foscos, un torrat sobre fons fosc no té contrast i
 * es fa servir `primary.light`. Tot el que ha de sortir "torrat" passa per aquí.
 */
export const accentColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.light : theme.palette.primary.dark;

/**
 * Color de farciment (fons) dels elements primaris petits amb text a sobre: pàgina seleccionada de la
 * paginació, badges i xips. Als temes clars és el mateix «torrat»; als foscos, `accentColor` (to clar)
 * no dona prou contrast amb text blanc, així que es fa servir el color base de l'aplicació.
 */
export const accentFillColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.main : theme.palette.primary.dark;

// Files de les graelles als temes foscos (Obscur i Dracula comparteixen la base): el hover és un to
// suau; la fila seleccionada, un to fosc, i amb el cursor a sobre una mica més viu.
const GRID_ROW_HOVER_DARK = alpha(lighten(PRIMARY_MAIN, 0.05), 0.24);
const GRID_ROW_SELECTED_DARK = alpha(darken(PRIMARY_MAIN, 0.4), 0.8);
const GRID_ROW_SELECTED_HOVER_DARK = alpha(darken(PRIMARY_MAIN, 0.2), 0.9);
const GRID_ROW_CHECKED_DARK = PRIMARY_SOFT;

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

// Vora inferior de la capçalera de les graelles: degradat del color base al torrat. El fons de la
// capçalera no es fixa aquí sinó a `palette.DataGrid.headerBg` de cada tema (és la via de MUI X, que
// també acoloreix el filler i el buit de la barra de desplaçament).
//
// Es declara a part, i no dins `baseComponentStyles`, perquè cada tema redefineix
// `MuiDataGrid.styleOverrides` sencer i l'ha de referenciar explícitament.
const gridColumnHeadersOverride = ({ theme }: { theme: Theme }) => ({
    position: 'relative' as const,
    '&::after': {
        content: '""',
        position: 'absolute' as const,
        left: 0,
        right: 0,
        bottom: 0,
        height: '3px',
        background: `linear-gradient(90deg, ${theme.palette.primary.main} 25%, ${accentColor(theme)} 75%)`,
        pointerEvents: 'none' as const,
    },
});

// Barres de desplaçament de tota l'aplicació (i de les graelles, que hereten la propietat) al color
// "torrat" del tema. `scrollbar-color` és herència CSS, per això n'hi ha prou amb posar-lo a `html`.
const scrollbarOverrides = (thumbColor: string) => ({
    html: { scrollbarColor: `${thumbColor} transparent` },
});

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
    // Menú lateral. La llibreria en té la geometria cablejada (espaiador superior de 64px, plegat a
    // 57px, icones de 20px) i no es pot tocar; s'ajusta aquí, al tema, i no des de BaseApp.tsx, perquè
    // en pantalles estretes la llibreria pinta el menú en un portal, fora del <nav> i de qualsevol
    // embolcall de l'aplicació: només els overrides de tema hi arriben. Les mides surten de
    // util/layout.ts. El color de les entrades segueix a BaseApp.tsx (depèn del perfil de l'usuari).
    MuiDrawer: {
        styleOverrides: {
            // Menú permanent: la llibreria el plega a 57px i en reserva aquest espai al layout
            docked: { minWidth: `${MENU_WIDTH_COLLAPSED}px` },
            paper: {
                minWidth: `${MENU_WIDTH_COLLAPSED}px`,
                // El menú ha de començar sota la capçalera, que és més alta que els 64px de la llibreria
                '& > .MuiBox-root:first-of-type': { marginTop: `${HEADER_HEIGHT}px` },
                '& .menu-item-icon .MuiIcon-root': { fontSize: `${ICON_SIZE}px` },
                // Cada entrada amaga el text només quan el menú és estret, sense haver de saber-ne
                // l'estat: en plegat el text només es fa transparent, però continua ocupant lloc, i
                // les entrades llargues fan dues línies i les icones quedarien a altures diferents.
                // El contenidor de la consulta és la llista i NO el paper: un contenidor crea un bloc
                // contenidor per als descendents `position: fixed`, i el panell flotant dels submenús
                // del menú plegat (veure més avall) quedaria tallat dins el paper.
                '& .MuiList-root': { containerType: 'inline-size' as const },
                // Plegat: la llibreria hi centra les icones a `espai útil / 2`, sense dependre de
                // la mida de la icona, que és la columna menys mig píxel per la vora del menú
                // (MENU_BORDER_WIDTH). La icona du un marge esquerre de -8px que aquí se li treu
                // un píxel perquè el centre caigui just a MENU_ICON_COLUMN.
                [`@container (max-width: ${MENU_COLLAPSED_BELOW}px)`]: {
                    '& .MuiListItemText-root': { display: 'none' },
                    '& > div > .MuiList-root > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${-8 + MENU_BORDER_WIDTH}px`,
                    },
                },
                // Obert: la llibreria deixa la icona a `padding + marge` (24 + 8 = 32px de vora
                // esquerra), i amb la mida nova el centre cauria a 44px. Es retoca el marge de les
                // entrades de nivell 0 perquè el centre sigui a MENU_ICON_COLUMN, com en plegat.
                [`@container (min-width: ${MENU_COLLAPSED_BELOW + 1}px)`]: {
                    '& > .MuiList-root > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${MENU_ICON_COLUMN - ICON_SIZE / 2 - MENU_ITEM_PADDING_LEFT}px`,
                    },
                },
                // Menú plegat: el panell flotant amb els fills d'una entrada (`position: fixed`, germà de la
                // llista) la llibreria el col·loca a 64px de dalt i 57px d'esquerra (capçalera i menú plegat
                // de la llibreria): s'ajusta a la capçalera i al menú plegat d'aquesta aplicació.
                '& > div > .MuiBox-root': {
                    top: `${HEADER_HEIGHT}px`,
                    left: `${MENU_WIDTH_COLLAPSED}px`,
                },
            },
        },
    },
    // Identitat: paginació, badges, xips primaris i icona seleccionada del menú en el color "torrat"
    // (veure accentColor). El botó, l'enllaç i la resta de components primaris es queden al base.
    MuiPaginationItem: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.Mui-selected': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                    '&:hover': { backgroundColor: accentFillColor(theme), filter: 'brightness(1.15)' },
                },
            }),
        },
    },
    MuiBadge: {
        styleOverrides: {
            badge: ({ theme }: { theme: Theme }) => ({
                '&.MuiBadge-colorPrimary': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
    MuiChip: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.MuiChip-colorPrimary:not(.MuiChip-outlined)': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
    // Text de les pestanyes tal com s'escriu (MUI el posa tot en majúscules). Pestanya seleccionada: als
    // temes foscos el color base no té prou contrast amb el text sobre el fons fosc; es fa servir el to
    // clar (accentColor). Al clar es manté el del tema.
    MuiTab: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                textTransform: 'none',
                ...(theme.palette.mode === 'dark' && { '&.Mui-selected': { color: accentColor(theme) } }),
            }),
        },
    },
    MuiListItemButton: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                // `menu-item-icon` és la classe que la llibreria posa al ListItemIcon del menú
                '&.Mui-selected .menu-item-icon': { color: accentColor(theme) },
                // Vora esquerra de l'entrada seleccionada, del color de la barra de títol de ScreenFrame
                // (als estils de menú amb colors propis la pren de l'accent, veure BaseApp.tsx)
                '&.Mui-selected': { boxShadow: `inset 4px 0 0 ${theme.palette.primary.main}` },
                // Guia vertical dels submenús (la dibuixa la llibreria en blau fix): al color de la vora
                '&.Mui-selected :before': { background: theme.palette.primary.main },
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

const LIGHT_PRIMARY_MAIN = PRIMARY_MAIN;
const LIGHT_PRIMARY_CONTRAST_TEXT = '#ffffff';
const LIGHT_SECONDARY_MAIN = '#2E2E2E';
const LIGHT_BACKGROUND_DEFAULT = '#ffffff';
const LIGHT_CUSTOM_BACKGROUND = '#f5f5f5';
const LIGHT_GREY_BACKGROUND = '#e7e7e7';

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: LIGHT_PRIMARY_MAIN, dark: PRIMARY_DARK },
        error: { main: '#d32f2f', contrastText: '#f8f8f2' },
        warning: { main: '#ed6c02', contrastText: '#f8f8f2' },
        success: { main: '#2e7d32', contrastText: '#f8f8f2' },
        info: { main: '#0288d1', contrastText: '#f8f8f2' },
        customBackground: LIGHT_CUSTOM_BACKGROUND,
        greyBackground: LIGHT_GREY_BACKGROUND,
        primarySoft: PRIMARY_SOFT,
        DataGrid: { headerBg: PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...scrollbarOverrides(PRIMARY_DARK),
                body: {
                    backgroundColor: LIGHT_BACKGROUND_DEFAULT,
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
                columnHeaders: gridColumnHeadersOverride,
            },
        },
    },
});

const DARK_PRIMARY_MAIN = PRIMARY_MAIN;
const DARK_PRIMARY_LIGHT = PRIMARY_LIGHT;
const DARK_PRIMARY_CONTRAST_TEXT = '#ffffff';
const DARK_CUSTOM_BACKGROUND = '#121212';
const DARK_GREY_BACKGROUND = '#222222';
const DARK_TEXT_SECONDARY = '#bbbbbb';
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DARK_PRIMARY_SOFT = darken(PRIMARY_MAIN, 0.7);

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: DARK_PRIMARY_LIGHT, main: DARK_PRIMARY_MAIN, dark: PRIMARY_DARK },
        error: { main: '#c62828', contrastText: '#f8f8f2' },
        warning: { main: '#e65100', contrastText: '#f8f8f2' },
        success: { main: '#1b5e20', contrastText: '#f8f8f2' },
        info: { main: '#01579b', contrastText: '#f8f8f2' },
        customBackground: DARK_CUSTOM_BACKGROUND,
        greyBackground: DARK_GREY_BACKGROUND,
        primarySoft: DARK_PRIMARY_SOFT,
        DataGrid: { headerBg: DARK_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...scrollbarOverrides(DARK_PRIMARY_LIGHT),
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
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                    // Casella marcada d'una fila seleccionada: gairebé blanca, es veu sobre el fons fosc
                    '& .MuiDataGrid-row.Mui-selected .MuiCheckbox-root.Mui-checked': {
                        color: `${GRID_ROW_CHECKED_DARK} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
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

const DRACULA_PRIMARY_LIGHT = PRIMARY_LIGHT;
const DRACULA_PRIMARY_MAIN = PRIMARY_MAIN;
const DRACULA_PRIMARY_CONTRAST_TEXT = '#ffffff';
// const DRACULA_CUSTOM_BACKGROUND = '#121212';
// const DRACULA_GREY_BACKGROUND = '#222222';
const DRACULA_TEXT_SECONDARY = '#D6D6C2';
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DRACULA_PRIMARY_SOFT = darken(PRIMARY_MAIN, 0.6);

// Tema Dracula (https://draculatheme.com/) -- palet fosc d'alt contrast, alternatiu al tema fosc
// per defecte.
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: {
            light: DRACULA_PRIMARY_LIGHT,
            main: DRACULA_PRIMARY_MAIN,
            dark: PRIMARY_DARK,
            contrastText: DRACULA_PRIMARY_CONTRAST_TEXT,
        },
        secondary: { main: '#f8f8f2' },
        background: { default: '#282a36', paper: '#303341' },
        text: { primary: '#f8f8f2', secondary: '#d6d6c2' },
        error: { main: '#c62828', contrastText: '#f8f8f2' },
        warning: { main: '#e65100', contrastText: '#f8f8f2' },
        success: { main: '#1b5e20', contrastText: '#f8f8f2' },
        info: { main: '#01579b', contrastText: '#f8f8f2' },
        divider: '#44475a',
        customBackground: '#282a36',
        greyBackground: '#303341',
        primarySoft: DRACULA_PRIMARY_SOFT,
        DataGrid: { headerBg: DRACULA_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...(baseComponentStyles.MuiCssBaseline?.styleOverrides as object),
                ...scrollbarOverrides(DRACULA_PRIMARY_LIGHT),
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
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                    // Casella marcada d'una fila seleccionada: gairebé blanca, es veu sobre el fons fosc
                    '& .MuiDataGrid-row.Mui-selected .MuiCheckbox-root.Mui-checked': {
                        color: `${GRID_ROW_CHECKED_DARK} !important`,
                    },
                },
                row: {
                    ...(baseComponentStyles.MuiDataGrid?.styleOverrides?.row as object),
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
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
