const translationCa = {
    comu: {
        empty: {
            option: 'Selecciona...',
        },
    },
    // Claus compartides pels components portats de RIPEA (StyledMuiFilter...). Es manté el
    // nom "common" que fan servir aquests components perquè els propers ports hi encaixin
    // sense retocs.
    common: {
        clear: 'Neteja',
        filter: 'Filtra',
        filterCount_one: '{{num}} filtre aplicat',
        filterCount_other: '{{num}} filtres aplicats',
        advancedSearch: 'Cerca avançada',
        advancedSearchOpen: 'Obre la cerca avançada',
        advancedSearchClose: 'Tanca la cerca avançada',
    },
    app: {
        loading: 'Iniciant DISTRIBUCIO',
        menu: {
            home: 'Inici',
            entitats: 'Entitats',
        },
        interficie: {
            classica: 'Interfície clàssica',
        },
    },
    page: {
        forbidden: {
            message: 'No teniu accés a aquesta pàgina amb el rol actual',
        },
        notFound: {
            message: 'Pàgina no trobada',
        },
        home: {
            toolbar: {
                title: 'DISTRIBUCIO',
                subtitle: "Distribució d'anotacions de registre a les diferents bústies dels organismes.",
            },
        },
        entitats: {
            grid: {
                title: 'Entitats',
                column: {
                    codi: 'Codi',
                    nom: 'Nom',
                    descripcio: 'Descripció',
                    cif: 'CIF',
                    codiDir3: 'Codi DIR3',
                    activa: 'Activa',
                    permisos: 'Permisos',
                },
            },
            permis: {
                title: "Permisos de l'entitat {{nom}}",
                grid: {
                    buit: 'Aquesta entitat no té cap permís',
                    column: {
                        principalTipus: 'Tipus',
                        principalNom: 'Principal',
                        administracio: 'Administració',
                        adminLectura: 'Admin (Lectura)',
                        usuari: 'Usuari',
                    },
                },
                // Valors de PrincipalTipusEnumDto, els mateixos que la JSP mostra a la columna
                // "Tipus" (principal.tipus.enum.* de messages_ca.properties).
                principalTipus: {
                    USUARI: 'Usuari',
                    ROL: 'Rol',
                },
                form: {
                    titleCreate: 'Nou permís',
                    titleUpdate: 'Modifica el permís',
                },
                esborrar: {
                    title: 'Confirmació',
                    confirm: 'Estau segur que voleu esborrar aquest permís?',
                },
                accio: {
                    gestionar: 'Gestiona els permisos',
                    nou: 'Nou permís',
                    modificar: 'Modifica',
                    esborrar: 'Esborra',
                    guardarOk: "El permís s'ha desat correctament",
                    esborrarOk: "El permís s'ha esborrat correctament",
                    error: "No s'ha pogut executar l'acció",
                },
            },
            form: {
                titleCreate: 'Nova entitat',
                titleUpdate: 'Modifica entitat',
                // El diàleg de la graella compon el títol amb el verb de l'acció ("Crea" o
                // "Modifica") més aquest nom de recurs.
                resourceTitle: 'entitat',
            },
            accio: {
                modificar: 'Modifica',
                activar: 'Activa',
                desactivar: 'Desactiva',
                esborrar: 'Esborra',
                crearOk: "L'entitat s'ha creat correctament",
                modificarOk: "L'entitat s'ha modificat correctament",
                esborrarOk: "L'entitat s'ha esborrat correctament",
                activarOk: "L'entitat s'ha activat correctament",
                desactivarOk: "L'entitat s'ha desactivat correctament",
                error: "No s'ha pogut executar l'acció",
            },
        },
    },
    component: {
        Offline: {
            message: 'No s\'ha pogut connectar amb el servidor',
            retry: 'Torna-ho a provar',
        },
        UserProfile: {
            perfil: 'El meu perfil',
            seccioDades: 'Dades',
            seccioConfig: 'Configuració',
            rols: 'Rols',
            entitatPerDefecte: 'Entitat per defecte',
            bustiaPerDefecte: 'Bústia per defecte',
            tema: {
                label: 'Tema',
                clar: 'Clar',
                obscur: 'Obscur',
                dracula: 'Dracula',
                sistema: 'Sistema',
            },
            estilMenu: {
                label: 'Estil del menú',
                tema: 'Tema',
                temaInvertit: 'Tema invertit',
                peu: 'Fix',
            },
        },
        EntitatRolSelector: {
            rol: {
                DIS_SUPER: 'Superusuari',
                DIS_ADMIN: 'Administrador Entitat',
                DIS_ADMIN_LECTURA: 'Admin (Lectura)',
                tothom: 'Usuari',
            },
        },
    },
};

export default translationCa;
