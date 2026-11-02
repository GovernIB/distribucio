import { alpha, darken, decomposeColor, lighten, recomposeColor, type Theme } from '@mui/material/styles';

/** Colors semàntics de la paleta que admeten el tractament tonal. */
export type TonalColor = 'success' | 'error' | 'warning' | 'info' | 'primary' | 'secondary';

/**
 * Mescla `color` amb una opacitat `opacitat` sobre el fons opac `fons` i en retorna el resultat opac. Equival a
 * pintar `alpha(color, opacitat)` sobre `fons`, però el resultat ja no depèn del que hi hagi a sota.
 */
const mescla = (color: string, opacitat: number, fons: string): string => {
    const c = decomposeColor(color).values;
    const f = decomposeColor(fons).values;
    const valors = [0, 1, 2].map((i) => Math.round(c[i] * opacitat + f[i] * (1 - opacitat)));
    return recomposeColor({ type: 'rgb', values: valors as [number, number, number] });
};

/**
 * Color tonal per a etiquetes i xips d'estat: fons suau del color i text enfosquit (tema clar) o aclarit (temes
 * foscos). Més suau que el color ple i vàlid WCAG 2.1 AA (text ≥ 4,5:1) als temes clar, obscur i Dracula.
 *
 * - Tema clar: fons translúcid (alpha 0,14, com RIPEA), que s'enfosqueix amb la fila de la graella i es distingeix
 *   igual sobre les files parelles grises. Text amb darken 0,45 (RIPEA usa 0,35, però aquí warning i primary no
 *   arribaven al 4,5:1 sobre una fila seleccionada). Mínim calculat: 4,6:1 (warning, fila seleccionada + hover).
 * - Temes foscos: fons opac (el color mesclat amb `background.paper`). Sobre una fila seleccionada amb el cursor a
 *   sobre (un to mitjà del color base) un fons translúcid baixava fins a 3,2:1; opac, el contrast és el mateix
 *   sigui on sigui (mínims calculats: 9,4:1 obscur, 6,5:1 Dracula). Text amb lighten 0,65 (RIPEA usa 0,4): els
 *   temes foscos d'aquesta aplicació fan servir `main` més foscos que els de MUI per defecte.
 *
 * No s'usen palette.dark/light: els temes foscos només defineixen main i els calculats no arriben al contrast.
 *
 * Ús: sx={tonal('success')}. Si el component fa un spread de l'sx, passa-li l'objecte resolt: tonal(color)(theme).
 */
export const tonal = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: fosc ? mescla(main, 0.14, theme.palette.background.paper) : alpha(main, 0.14),
        color: fosc ? lighten(main, 0.65) : darken(main, 0.45),
        // Les icones dels xips i etiquetes agafen el color del text
        '& .MuiChip-icon, & .MuiIcon-root': { color: 'inherit' },
    };
};

/**
 * Variant neutra (estats inactius o sense color), amb el mateix aspecte que les tonals però sense color. Translúcid
 * al tema clar i opac als foscos, pel mateix motiu que `tonal` (mínims calculats: 4,6:1 clar, 8,2:1 obscur i
 * 6,7:1 Dracula).
 */
export const tonalNeutre = (theme: Theme) => ({
    bgcolor:
        theme.palette.mode === 'dark'
            ? mescla(theme.palette.text.primary, 0.08, theme.palette.background.paper)
            : alpha(theme.palette.text.primary, 0.08),
    color: theme.palette.text.secondary,
    '& .MuiChip-icon, & .MuiIcon-root': { color: 'inherit' },
});

/** Si el valor és un color semàntic de la paleta amb tractament tonal. */
export const isTonalColor = (color: unknown): color is TonalColor =>
    ['success', 'error', 'warning', 'info', 'primary', 'secondary'].includes(color as string);

/**
 * Barra de progrés en to suau. La barra translúcida no arriba al contrast de 3:1 que es demana a un element
 * gràfic (WCAG 1.4.11; calculat: 1,02-1,16:1 amb la pista) i el nivell no es pot transmetre només amb el color:
 * s'ha d'acompanyar sempre del valor en text (vegeu components/TonalLinearProgress). El text `text.primary` sobre
 * la barra i sobre la pista supera 7:1 als tres temes.
 */
export const tonalProgress = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: alpha(main, fosc ? 0.12 : 0.1),
        '& .MuiLinearProgress-bar': { bgcolor: alpha(main, fosc ? 0.3 : 0.4) },
    };
};
