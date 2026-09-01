import React, { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useMatches } from 'react-router-dom';

/**
 * Nom de l'aplicació que encapçala sempre el títol de la pestanya del navegador. La interfície JSP
 * fa el mateix des del decorador de SiteMesh (webapp/WEB-INF/jsp/decorators/default.jsp:63, que
 * pinta "Distribucio - " + el títol de la pàgina); aquí es manté la grafia en majúscules que fa
 * servir la resta de la interfície REACT (index.html, "Iniciant DISTRIBUCIO"...).
 */
export const APP_TITOL = 'DISTRIBUCIO';

/**
 * Títol que una ruta declara al seu handle: la clau de traducció del text que ha de sortir a la
 * pestanya. Veure router.tsx, on cada ruta duu el mateix títol que la seva pantalla mostra a la
 * barra d'eines -- igual que a la UI JSP, on el <title> de la JSP és també el títol de la pàgina.
 */
export type TitolRouteHandle = { titol?: string };

/**
 * Fixa el títol de la pestanya. Pensat per a títols dinàmics que no es poden declarar a la ruta
 * (el nom de l'element que s'està editant, per exemple); per als títols fixos n'hi ha prou amb el
 * handle de la ruta.
 */
export const setTitolPagina = (titol?: string) => {
    document.title = titol ? `${APP_TITOL} - ${titol}` : APP_TITOL;
};

/**
 * Versió en forma de hook de setTitolPagina, perquè una pàgina pugui posar el seu propi títol. Es
 * pot cridar sense por des d'una pàgina que ja tingui títol declarat a la ruta: el component
 * TitolPagina va per davant de l'<Outlet> a l'arbre, així que el seu efecte s'executa abans i el
 * títol de la pàgina és el que acaba manant.
 */
export const useTitolPagina = (titol?: string) => {
    useEffect(() => {
        setTitolPagina(titol);
    }, [titol]);
};

/**
 * Manté el títol de la pestanya sincronitzat amb la ruta activa. De totes les rutes que encaixen
 * amb la URL agafa el títol de la més concreta que en declari un (el handle d'una ruta filla, per
 * tant, sobreescriu el de la seva pare), i el torna a resoldre quan es canvia d'idioma.
 *
 * Equivalent del TitleHeaderConfigurator de RIPEA
 * (ripea-back/src/main/jsapp/ripea-back/src/TitleHeaderConfigurator.tsx), però prenent el títol
 * del handle de cada ruta en comptes d'un mapa de paths a part: així no hi ha cap llista de rutes
 * duplicada que es pugui desincronitzar del router.
 */
export const TitolPagina: React.FC = () => {
    const { t, i18n } = useTranslation();
    const matches = useMatches();
    const clauTitol = matches.reduce<string | undefined>(
        (titol, match) => (match.handle as TitolRouteHandle | undefined)?.titol ?? titol,
        undefined
    );
    useEffect(() => {
        setTitolPagina(clauTitol != null ? t(clauTitol) : undefined);
        // i18n.language hi és perquè el títol es torni a traduir en canviar d'idioma sense sortir
        // de la pàgina.
    }, [clauTitol, t, i18n.language]);
    return null;
};

export default TitolPagina;
