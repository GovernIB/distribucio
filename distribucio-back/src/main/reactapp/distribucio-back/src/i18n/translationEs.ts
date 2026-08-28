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
        save: "Guarda",
        cancel: "Cancela",
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
            avisos: 'Avisos',
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
                    // Única cabecera que no es un atributo del recurso: es la columna del botón
                    // que abre el listado de permisos. El resto las aporta el backend (_prompt).
                    permisos: 'Permisos',
                },
            },
            permis: {
                title: 'Permisos de la entidad {{nom}}',
                grid: {
                    buit: 'Esta entidad no tiene ningún permiso',
                },
                form: {
                    titleCreate: 'Nuevo permiso',
                    titleUpdate: 'Modifica el permiso',
                },
                esborrar: {
                    title: 'Confirmación',
                    confirm: '¿Está seguro de que desea eliminar este permiso?',
                },
                accio: {
                    gestionar: 'Gestiona los permisos',
                    nou: 'Nuevo permiso',
                    modificar: 'Modifica',
                    esborrar: 'Elimina',
                    guardarOk: 'El permiso se ha guardado correctamente',
                    esborrarOk: 'El permiso se ha eliminado correctamente',
                    error: 'No se ha podido ejecutar la acción',
                },
            },
            form: {
                titleCreate: 'Nueva entidad',
                titleUpdate: 'Modifica entidad',
                resourceTitle: 'entidad',
            },
            accio: {
                new: "Nueva entidad",
                modificar: 'Modifica',
                activar: 'Activa',
                desactivar: 'Desactiva',
                esborrar: 'Elimina',
                crearOk: 'La entidad se ha creado correctamente',
                modificarOk: 'La entidad se ha modificado correctamente',
                esborrarOk: 'La entidad se ha eliminado correctamente',
                activarOk: 'La entidad se ha activado correctamente',
                desactivarOk: 'La entidad se ha desactivado correctamente',
                error: 'No se ha podido ejecutar la acción',
            },
        },
        avisos: {
            grid: {
                title: "Avisos",
            },
            form: {
                titleCreate: 'Nuevo aviso',
                titleUpdate: 'Modifica aviso',
                resourceTitle: 'aviso',
            },
            accio: {
                new: "Nuevo aviso",
                modificar: 'Modifica',
                activar: 'Activa',
                desactivar: 'Desactiva',
                esborrar: 'Elimina',
                crearOk: 'El aviso se ha creado correctamente',
                modificarOk: 'El aviso se ha modificado correctamente',
                esborrarOk: 'El aviso se ha eliminado correctamente',
                activarOk: 'El aviso se ha activado correctamente',
                desactivarOk: 'El aviso se ha desactivado correctamente',
                error: 'No se ha podido ejecutar la acción',
            },
        },
    },
    component: {
        Offline: {
            message: 'No se ha podido conectar con el servidor',
            retry: 'Reinténtalo',
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
