import type { MenuEntry } from 'reactlib';
import {
    ROLE_ADMIN,
    ROLE_ADMIN_LECTURA,
    ROLE_SUPER,
    ROLE_USER,
} from '../components/DistribucioContext';

/**
 * Rols amb els quals es pot operar a la interfície REACT: els mateixos que ofereix el selector de
 * rol (veure ALLOWED_ROLES a DistribucioProvider).
 */
export const ROLS_APLICACIO = [ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_USER];

/** Identificador de cada pantalla amb control d'accés. Una pantalla nova s'ha d'afegir aquí. */
export type Pantalla = 'home'
    | 'entitat'
    | 'avis'
    | 'servei'
    | 'unitatOrganitzativa'
    | 'bustiaAdmin'
    | 'bustiaAdminOrganigrama'
    | 'limitCanviEstat'
    | 'procediment'
    | 'permis'
    | 'contingut'
    | 'config'
    | 'backoffice'
    | 'massiva'
    | 'integracio'
    | 'annex'
    | 'registre'
    | 'vistaMoviments'
    | 'regla';

/**
 * Rols autoritzats per pantalla. És l'única font de veritat del control d'accés de la interfície:
 * la fan servir tant el filtre del menú lateral (App.tsx) com les guardes de ruta (router.tsx), de
 * manera que amagar una entrada de menú i barrar-ne la ruta no poden divergir. El tipus Pantalla
 * obliga a declarar-hi tota pantalla nova.
 *
 * El mapatge reprodueix el de la interfície JSP, que aplica interceptors per prefix d'URL (veure
 * WebMvcConfig): SUPER_PATHS -- entre els quals "/entitat**" -- només per al rol actual DIS_SUPER
 * (AccesSuperInterceptor), ADMIN_PATHS per a DIS_ADMIN i DIS_ADMIN_LECTURA (AccesAdminInterceptor)
 * i USER_PATHS per a "tothom".
 *
 * Compte: això és usabilitat, no seguretat. Qui protegeix de veritat és el backend amb els
 * @ResourceAccessConstraint de cada classe *Resource (a EntitatResource, per exemple, l'escriptura
 * és només de DIS_SUPER i la lectura de tots els rols, perquè el selector d'entitat la necessita).
 * Tota pantalla nova ha de declarar les seves restriccions al recurs encara que aquí ja s'amagui.
 */
export const PANTALLA_ROLS: Record<Pantalla, string[]> = {
    home: ROLS_APLICACIO,
    entitat: [ROLE_SUPER],
    avis: [ROLE_SUPER],
    servei: [ROLE_ADMIN],
    unitatOrganitzativa: [ROLE_ADMIN],
    limitCanviEstat: [ROLE_SUPER],
    bustiaAdmin: [ROLE_ADMIN],
    bustiaAdminOrganigrama: [ROLE_ADMIN],
    permis: [ROLE_ADMIN],
    procediment: [ROLE_ADMIN],
    contingut: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    config: [ROLE_SUPER],
    backoffice: [ROLE_ADMIN],
    massiva: [ROLE_ADMIN, ROLE_USER],
    integracio: [ROLE_SUPER],
    annex: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    registre: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_USER],
    vistaMoviments: [ROLE_USER],
    regla: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
};

export const isPantallaPermesa = (pantalla: Pantalla, rol?: string): boolean =>
    rol != null && PANTALLA_ROLS[pantalla].includes(rol);

/**
 * Pantalla d'inici per rol -- l'equivalent del HomeRedirect de RIPEA. Reprodueix la redirecció de
 * l'arrel de la interfície JSP (DistribucioController.get): DIS_SUPER va a "integracio",
 * DIS_ADMIN i DIS_ADMIN_LECTURA a "registreAdmin" i "tothom" a "registreUser", que a REACT són
 * la mateixa pantalla "registre". La ruta que s'hi indiqui ha d'estar autoritzada per al rol a
 * PANTALLA_ROLS; un rol sense entrada va a RUTA_INICIAL_DEFECTE.
 */
const RUTA_INICIAL_PER_ROL: Partial<Record<string, string>> = {
    [ROLE_SUPER]: '/integracio',
    [ROLE_ADMIN]: '/registre',
    [ROLE_ADMIN_LECTURA]: '/registre',
    [ROLE_USER]: '/registre',
};

export const RUTA_INICIAL_DEFECTE = '/home';

export const rutaInicialPerRol = (rol?: string): string =>
    (rol != null ? RUTA_INICIAL_PER_ROL[rol] : undefined) ?? RUTA_INICIAL_DEFECTE;

/**
 * Entrada de menú amb la pantalla a la qual dona accés. Les entrades sense pantalla (separadors,
 * agrupadors o enllaços externs) es mostren a tots els rols.
 */
export type MenuEntryAmbPantalla = MenuEntry & {
    pantalla?: Pantalla;
    children?: MenuEntryAmbPantalla[];
};

/**
 * Deixa al menú només les entrades que el rol pot obrir. Els submenús es filtren en profunditat i
 * els que es queden sense fills visibles i no són cap enllaç desapareixen.
 */
export const filtrarEntradesMenu = (
    entrades: MenuEntryAmbPantalla[],
    rol?: string
): MenuEntry[] =>
    entrades.reduce<MenuEntry[]>((visibles, entrada) => {
        const { pantalla, children, ...entradaMenu } = entrada;
        if (pantalla != null && !isPantallaPermesa(pantalla, rol)) {
            return visibles;
        }
        if (children != null) {
            const fills = filtrarEntradesMenu(children, rol);
            if (fills.length === 0 && entradaMenu.to == null) {
                return visibles;
            }
            visibles.push({ ...entradaMenu, children: fills });
        } else {
            visibles.push(entradaMenu);
        }
        return visibles;
    }, []);
