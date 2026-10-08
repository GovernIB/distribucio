import i18n from '../i18n/i18n';

/**
 * El perfil desa l'idioma com el nom de la constant d'IdiomaEnumDto ("CA"/"ES") i pot arribar
 * en minúscules de l'alta automàtica d'usuaris, mentre que i18next i la capçalera
 * Accept-Language volen el codi de dues lletres en minúscules. Sense normalitzar, "ES" i "es"
 * (o "es-ES") es considerarien idiomes diferents i es rellançarien consultes sense necessitat.
 */
export const normalitzaIdioma = (idioma?: string): string | undefined =>
    idioma != null && idioma.length > 0 ? idioma.substring(0, 2).toLowerCase() : undefined;

/**
 * Idioma amb què treballa l'aplicació (textos de la interfície i Accept-Language de l'API): el
 * del perfil de l'usuari (dis_usuari.idioma) i, si no en té, el que detecta i18next.
 *
 * Tant DistribucioProvider (que l'aplica a l'API abans de pintar l'aplicació) com BaseApp (que el
 * passa a base-react) l'han de calcular amb aquesta funció: si en sortissin valors diferents,
 * base-react el tornaria a canviar un cop pintada l'aplicació, i recarregar l'índex de l'API la
 * desmuntaria i la tornaria a muntar.
 */
export const idiomaAplicacio = (idiomaPerfil?: string): string =>
    normalitzaIdioma(idiomaPerfil) ?? normalitzaIdioma(i18n.language) ?? 'ca';
