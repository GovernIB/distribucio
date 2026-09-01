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
        save: "Guarda",
        actualitza: "Actualitza",
        cancel: "Cancel·la",
        close: 'Tanca',
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
            avisos: 'Avisos',
            consultar: "Consultar",
            serveis: 'Serveis',
        },
        avisos: {
            mostra: "Mostra el detall de l'avís",
            amaga: "Amaga el detall de l'avís",
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
                    // Única capçalera que no és cap atribut del recurs: és la columna del botó
                    // que obre el llistat de permisos. La resta les aporta el backend (_prompt).
                    permisos: 'Permisos',
                },
            },
            permis: {
                title: "Permisos de l'entitat {{nom}}",
                grid: {
                    buit: 'Aquesta entitat no té cap permís',
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
                new: "Nova entitat",
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
        avisos: {
            grid: {
                title: "Avisos",
            },
            form: {
                titleCreate: 'Nou avis',
                titleUpdate: 'Modifica avis',
                resourceTitle: 'avis',
            },
            accio: {
                new: "Nou avis",
                modificar: 'Modifica',
                activar: 'Activa',
                desactivar: 'Desactiva',
                esborrar: 'Esborra',
                crearOk: "L'avis s'ha creat correctament",
                modificarOk: "L'avis s'ha modificat correctament",
                esborrarOk: "L'avis s'ha esborrat correctament",
                activarOk: "L'avis s'ha activat correctament",
                desactivarOk: "L'avis s'ha desactivat correctament",
                error: "No s'ha pogut executar l'acció",
            },
        },
        serveis: {
            grid: {
                title: "Serveis",
            },
            accio: {
                actualitzarTotsButton: 'Actualitza tots els serveis',
                actualitzar: 'Actualitza el servei',
                esborrar: 'Esborra',
                actualitzarOk: "El servei s'ha actualitzat correctament",
                actualitzarTotsOk: "Tots els serveis s'han actualitzat correctament",
                esborrarOk: "El servei s'ha esborrat correctament",
                error: "No s'ha pogut executar l'acció",
                actualitzarTots: {
                    title: "Actualització de serveis",
                    confirmacio: "Voleu actualitzar els serveis amb la informació de ROLSAC?",
                    estat: "Estat",
                    total: "Número de serveis totals",
                    processats: "Número de serveis processats",
                    estats: {
                        INICIALITZANT: "Inicialitzant",
                        ACTUALITZANT: "Actualitzant",
                        FINALITZAT: "Finalitzat",
                        ERROR: "Error",
                    },
                    close: {
						check: "Estau segur que voleu tancar aquesta finestra?",
						description: "L'importació continuarà en segon pla i podreu consultar el resultat a l'expedient més tard.",
					},
					cancel: {
						check: "Esteu segur que voleu cancel·lar la importació?",
						description: "Els documents importats fins a aquest moment es conservaran a l’expedient.",
					},
                },
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
