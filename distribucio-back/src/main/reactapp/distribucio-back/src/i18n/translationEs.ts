const translationEs = {
    comu: {
        empty: {
            option: 'Selecciona...',
        },
    },
    // Claves compartidas por los componentes portados de RIPEA (StyledMuiFilter...). Se mantiene
    // el nombre "common" que usan estos componentes para que los próximos ports encajen sin
    // retoques.
    common: {
        clear: 'Limpia',
        filter: 'Filtra',
        filterCount_one: '{{num}} filtro aplicado',
        filterCount_other: '{{num}} filtros aplicados',
        advancedSearch: 'Búsqueda avanzada',
        advancedSearchOpen: 'Abrir la búsqueda avanzada',
        advancedSearchClose: 'Cerrar la búsqueda avanzada',
    },
    app: {
        loading: 'Iniciando DISTRIBUCIO',
        menu: {
            home: 'Inicio',
            entitats: 'Entidades',
        },
        interficie: {
            classica: 'Interfaz clásica',
        },
    },
    page: {
        forbidden: {
            message: 'No tiene acceso a esta página con el rol actual',
        },
        notFound: {
            message: 'Página no encontrada',
        },
        home: {
            toolbar: {
                title: 'DISTRIBUCIO',
                subtitle: 'Distribución de anotaciones de registro a los diferentes buzones de los organismos.',
            },
        },
        entitats: {
            grid: {
                title: 'Entidades',
                column: {
                    codi: 'Código',
                    nom: 'Nombre',
                    descripcio: 'Descripción',
                    cif: 'CIF',
                    codiDir3: 'Código DIR3',
                    activa: 'Activa',
                },
            },
            form: {
                titleCreate: 'Nueva entidad',
                titleUpdate: 'Modificar entidad',
            },
        },
    },
    component: {
        Offline: {
            message: 'No se ha podido conectar con el servidor',
            retry: 'Reintentar',
        },
        UserProfile: {
            perfil: 'Mi perfil',
            seccioDades: 'Datos',
            seccioConfig: 'Configuración',
            rols: 'Roles',
            entitatPerDefecte: 'Entidad por defecto',
            bustiaPerDefecte: 'Buzón por defecto',
            tema: {
                label: 'Tema',
                clar: 'Claro',
                obscur: 'Oscuro',
                dracula: 'Dracula',
                sistema: 'Sistema',
            },
            estilMenu: {
                label: 'Estilo del menú',
                tema: 'Tema',
                temaInvertit: 'Tema invertido',
                peu: 'Fijo',
            },
        },
        EntitatRolSelector: {
            rol: {
                DIS_SUPER: 'Superusuario',
                DIS_ADMIN: 'Administrador Entidades',
                DIS_ADMIN_LECTURA: 'Admin (Lectura)',
                tothom: 'Usuario',
            },
        },
    },
};

export default translationEs;
