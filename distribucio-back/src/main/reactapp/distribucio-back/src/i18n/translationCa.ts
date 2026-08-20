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
                },
            },
            form: {
                titleCreate: 'Nova entitat',
                titleUpdate: 'Modificar entitat',
            },
        },
    },
    component: {
        Offline: {
            message: 'No s\'ha pogut connectar amb el servidor',
            retry: 'Reintentar',
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
