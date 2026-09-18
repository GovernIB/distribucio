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
        update: 'Modifica',
        delete: 'Borra',
        actualitza: "Actualiza",
        cancel: "Cancela",
        close: 'Cierra',
        clear: 'Limpia',
        filter: 'Filtra',
        filterCount_one: '{{num}} filtro aplicado',
        filterCount_other: '{{num}} filtros aplicados',
        advancedSearch: 'Búsqueda avanzada',
        advancedSearchOpen: 'Abrir la búsqueda avanzada',
        advancedSearchClose: 'Cerrar la búsqueda avanzada',
        copiat: 'Copiado correctamente',
    },
    app: {
        loading: 'Iniciando DISTRIBUCIO',
        menu: {
            home: 'Inicio',
            entitats: 'Entidades',
            avisos: 'Avisos',
            consultar: "Consultar",
            serveis: 'Servicios',
            configuracio: "Configuración",
            configurar: "Configurar",
            limitCanviEstat: "Limites de cambios de estado",
            bustia: 'Buzones',
            unitatOrganitzativa: 'Unidades Organizativas',
            procediment: 'Procedimientos',
            contingut: 'Contenidos',
            annex: 'Anexos',
            config: 'Propiedades configurables',
            monitoritzar: 'Monitorizar',
            integracio: 'Integraciones',
        },
        avisos: {
            mostra: 'Muestra el detalle del aviso',
            amaga: 'Oculta el detalle del aviso',
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
                adminAllowed: "Administración",
                perm0Allowed: "Admin (Lectura)",
                readAllowed: "Usuarios",
            },
            permis: {
                title: 'Permisos de la entidad',
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
                // El diálogo de la cuadrícula compone el título con el verbo de la acción ("Crea" o
                // "Modifica") más este nombre de recurso.
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
        serveis: {
            grid: {
                title: "Servicios",
            },
            accio: {
                actualitzarTotsButton: 'Actualizar todos los servicios',
                actualitzar: 'Actualiza el servicio',
                esborrar: 'Borra',
                actualitzarOk: "El servicio se ha actualizado correctamente",
                actualitzarTotsOk: "Todos los servicios se han actualizado correctamente",
                esborrarOk: "El servicio se ha borrado correctamente",
                error: "No se ha podido ejecutar la acción",
                actualitzarTots: {
                    title: "Actualización de servicios",
                    confirmacio: "¿Quiere actualizar los servicios con la información de ROLSAC?",
                    estat: "Estado",
                    total: "Número de servicios totales",
                    processats: "Número de servicios processados",
                    estats: {
                        INICIALITZANT: "Inicializando",
                        ACTUALITZANT: "Actualizando",
                        FINALITZAT: "Finalizado",
                        ERROR: "Error",
                    },
                    close: {
					    check: "¿Está seguro de que desea cerrar esta ventana?",
					    description: "La acción continuará en segundo plano y podrá consultar el resultado más tarde.",
					},
                },
            },
        },
        procediments: {
            grid: {
                title: "Procedimientos",
            },
            accio: {
                actualitzarTotsButton: "Actualizar todos los procedimientos",
                actualitzar: "Actualiza el procedimiento",
                actualitzarOk: "El procedimiento se ha actualizado correctamente",
                actualitzarTotsOk: "Todos los procedimientos se han actualizado correctamente",
                error: "No se ha podido ejecutar la acción",
                actualitzarTots: {
                    title: "Actualización de procedimientos",
                    confirmacio: "¿Quiere actualizar los procedimientos con la información de ROLSAC?",
                    estat: "Estado",
                    total: "Número de procedimientos totales",
                    processats: "Número de procedimientos processados",
                    estats: {
                        INICIALITZANT: "Inicializando",
                        ACTUALITZANT: "Actualizando",
                        FINALITZAT: "Finalizado",
                        ERROR: "Error",
                    },
                    close: {
					    check: "¿Está seguro de que desea cerrar esta ventana?",
					    description: "La acción continuará en segundo plano y podrá consultar el resultado más tarde.",
					},
                },
            },
        },
        contingut: {
            grid: {
                title: "Localizador de contenidos",
                icona: {
                    bustia: "Buzón",
                    registre: "Anotación de registro",
                    unitat: "Unidad organizativa",
                    esborrat: "Eliminado",
                    alerta: "Este registro se ha distribuido con reglas",
                },
            },
            accio: {
                detalls: {
                    label: "Detalles",
                },
                historial: {
                    label: "Histórico de acciones",
                },
            },
            detall: {
                title: "Detalle del elemento",
                camp: {
                    nom: "Nombre",
                    entitat: "Entidad",
                    unitatOrganitzativa: "Unidad organizativa",
                    activa: "Activa",
                    perDefecte: "Por defecto",
                    permisos: "Permisos",
                },
            },
            historial: {
                title: "Histórico de acciones del elemento",
                tab: {
                    accions: "Acciones",
                    moviments: "Movimientos",
                    auditoria: "Auditoría",
                },
                accio: {
                    informe: {
                        label: "Informe",
                    },
                    veureDetall: "Ver detalles",
                },
                detall: {
                    title: "Detalle de la acción",
                    objecte: "Objeto: {{nom}}",
                    params: "Parámetros",
                    accioPare: "Acción padre",
                    moviment: "Movimiento",
                    movimentOrigen: "Origen",
                    movimentDesti: "Destino",
                },
                auditoria: {
                    creacio: "Creación",
                    modificacio: "Última modificación",
                    usuari: "Usuario",
                    data: "Fecha",
                },
            },
        },
        annex: {
            grid: {
                title: 'Localizador de anexos',
                registre: {
                    original: 'original',
                    copia: 'copia {{num}}',
                },
                arxiuEstat: {
                    buitAvis: 'Este anexo no está guardado dentro del Arxiu',
                },
            },
            accio: {
                detalls: 'Detalles del anexo',
                detallsAnotacio: 'Detalles de la anotación',
                concsv: 'Enlace a ConCSV',
                descarregarOriginal: 'Descarga original',
                descarregarImprimible: 'Descarga imprimible',
                guardarDefinitiu: {
                    label: 'Custodia',
                    jaDefinitiu: 'El anexo "{{titol}}" de la anotación {{numero}} ya consta como Definitivo en Distribución',
                    expedientTancat: 'El expediente que contiene el anexo "{{titol}}" de la anotación {{numero}} está cerrado en el archivo',
                    definitiuArxiu: 'El documento anexo "{{titol}}" de la anotación {{numero}} ya estaba como definitivo en el archivo',
                    mogutBackoffice: 'El documento anexo "{{titol}}" de la anotación {{numero}} se ha movido a un expediente del backoffice',
                    updated: 'El anexo "{{titol}}" de la anotación {{numero}} se ha marcado como Definitivo',
                    errorUpdate: 'Ha ocurrido un error al crear/modificar en el archivo el anexo "{{titol}}" de la anotación {{numero}}',
                    errorArxiu: 'Error no controlado marcando el anexo "{{titol}}" de la anotación {{numero}} como definitivo',
                    errorFirma: 'Ha habido un error validando la firma del anexo {{titol}} de la anotación {{numero}}',
                    senseFirma: 'Error no controlado con la firma del anexo "{{titol}}" de la anotación {{numero}}',
                },
                guardarDefinitiuMultiple: {
                    label: 'Custodia',
                    duplicat: 'Ya existe una ejecución masiva pendiente de los elementos seleccionados: {{elements}}',
                },
                validarFirmes: {
                    valides: 'Las firmas del anexo son válidas',
                    noValides: 'Las firmas no son válidas',
                    errorValidant: 'Las firmas no se han podido validar',
                    custodiat: 'Anexo custodiado correctamente',
                },
                error: 'No se ha podido ejecutar la acción',
            },
            detall: {
                title: 'Detalles del anexo',
                camp: {
                    dataCaptura: 'Fecha de captura (ENI)',
                    origen: 'Origen (ENI)',
                    ntiElaboracioEstat: 'Estado de elaboración (ENI)',
                    ntiTipusDocument: 'Tipo documental (ENI)',
                    sicresTipusDocument: 'Tipo de documento anexado',
                    localitzacio: 'Localización',
                    observacions: 'Observaciones',
                    fitxerArxiuUuid: 'Identificador dentro del Arxiu',
                    firmaCsv: 'Firma CSV',
                    concsvUrl: 'Enlace a ConCSV',
                    fitxerTipusMime: 'Tipo MIME',
                    validacioFirmaEstat: 'Validación de firma',
                    arxiuEstat: 'Estado en el Arxiu',
                    gestioDocumental: 'Gestión documental',
                    fitxer: 'Fichero',
                    firma: 'Firma',
                    firmes: 'Firmas',
                },
                firmes: {
                    mostrar: 'Mostrar firmas',
                    column: {
                        nom: 'Nombre',
                        nif: 'NIF',
                        data: 'Fecha',
                        emissor: 'Emisor',
                    },
                    data: {
                        nd: 'N/D',
                    },
                    autofirma: {
                        info: 'DISTRIBUCIÓN ha añadido automaticamente esta firma al anexo de la anotación de registro para guardarlo como definitivo al archivo',
                    },
                    tipus: {
                        CSV: 'CSV',
                        XADES_DET: 'XAdES internally detached signature',
                        XADES_ENV: 'XAdES enveloped signature',
                        CADES_DET: 'CAdES detached/explicit signature',
                        CADES_ATT: 'CAdES attached/implicit signature',
                        PADES: 'PAdES',
                        SMIME: 'S/MIME',
                        ODT: 'ODT',
                        OOXML: 'OOXML',
                    },
                },
                gestioDocumental: {
                    identificador: 'Identificador',
                    firmes: 'Firmas',
                    cap: '(ninguna)',
                    column: {
                        tipus: 'Tipo firma',
                        perfil: 'Perfil firma',
                        fitxer: 'Fichero',
                        csvRegulacio: 'CSV regulación',
                        autofirma: 'Firmado por DISTRIBUCIÓN',
                        gesdocFirmaId: 'Gestión documental firma',
                    },
                },
                ntiElaboracioEstat: {
                    ORIGINAL: 'Original',
                    COPIA_ELECT_AUTENTICA_CANVI_FORMAT: 'Copia electrónica auténtica con cambio de formato',
                    COPIA_ELECT_AUTENTICA_PAPER: 'Copia electrónica auténtica de documento papel',
                    COPIA_ELECT_AUTENTICA_PARCIAL: 'Copia electrónica parcial auténtica',
                    ALTRES: 'Otros',
                },
                ntiTipusDocument: {
                    RESOLUCIO: 'Resolución',
                    ACORD: 'Acuerdo',
                    CONTRACTE: 'Contrato',
                    CONVENI: 'Convenio',
                    DECLARACIO: 'Declaración',
                    COMUNICACIO: 'Comunicación',
                    NOTIFICACIO: 'Notificación',
                    PUBLICACIO: 'Publicación',
                    ACUS_REBUT: 'Justificante de recepción',
                    ACTE: 'Acta',
                    CERTIFICAT: 'Certificado',
                    DILIGENCIA: 'Diligéncia',
                    INFORME: 'Informe',
                    SOLICITUD: 'Solicitud',
                    DENUNCIA: 'Denuncia',
                    ALEGACIONS: 'Alegación',
                    RECURSOS: 'Recursos',
                    COMUNICACIO_CIUTADA: 'Comunicación ciudadano',
                    FACTURA: 'Factura',
                    ALTRES_INCAUTATS: 'Otros confiscados',
                    LLEI: 'Ley',
                    MOCIO: 'Moción',
                    INSTRUCCIO: 'Instrucción',
                    CONVOCATORIA: 'Convocatoria',
                    ORDRE_DIA: 'Orden del dia',
                    INFORME_PONENCIA: 'Informe de ponencia',
                    DICTAMEN_COMISSIO: 'Dictamen de comisión',
                    INICIATIVA_LEGISLATIVA: 'Iniciativa legislativa',
                    PREGUNTA: 'Pregunta',
                    INTERPELACIO: 'Interpelación',
                    RESPOSTA: 'Respuesta',
                    PROPOSICIO_NO_LLEI: 'Proposición no de ley',
                    ESQUEMA: 'Enmienda',
                    PROPOSTA_RESOLUCIO: 'Propuesta de resolución',
                    COMPAREIXENSA: 'Comparecencia',
                    SOLICITUD_INFORMACIO: 'Solicitud de información',
                    ESCRIT: 'Escrito',
                    INICIATIVA__LEGISLATIVA: 'Iniciativa legislativa',
                    PETICIO: 'Petición',
                    ALTRES: 'Otros tipo de documentos',
                },
                sicresTipusDocument: {
                    FORM: 'Formulario',
                    FORM_ADJUNT: 'Documento adjunto al formulario',
                    INTERN: 'Fichero técnico interno',
                },
                metaData: {
                    codiProcediment: 'Código de procedimiento',
                    resolucion: 'Resolución',
                    profundidadColor: 'Profundidad color',
                    titol: 'Título',
                    idioma: 'Idioma',
                    descripcio: 'Descripción',
                    appTramitDoc: 'App trámite documento',
                    organ: 'Órgano',
                    origen: 'Origen',
                    estatElaboracio: 'Estado elaboración',
                    tipusDocEni: 'Tipo doc ENI',
                    codiClassificacio: 'Código clasificación',
                    csv: 'Csv',
                    defCsv: 'Def. csv',
                    id: 'Id',
                    idOrigen: 'Id origen',
                    dataInici: 'Fecha inicio',
                    nomFormat: 'Nombre formato',
                    extensioFormat: 'Extensión formato',
                    midaLogica: 'Tamaño lógico',
                    termePuntAcces: 'Término punto acceso',
                    idPuntAcces: 'Id punto acceso',
                    esquemaPuntAcces: 'Esquema punto acceso',
                    suport: 'Soporte',
                    locArxiuCentral: 'Loc. archivo central',
                    arxiuGeneral: 'Archivo general',
                    unitats: 'Unidades',
                    subtipusDoc: 'Subtipo doc.',
                    tipusAsientoRegistral: 'Tipo asiento registral',
                    codiOficinaRegistre: 'Código oficina registro',
                    dataAsientoRegistral: 'Fecha asiento registral',
                    numAsientoRegistral: 'Número asiento registral',
                    tipusFirma: 'Tipo firma',
                    perfilFirma: 'Perfil firma',
                    dataSegellat: 'Fecha sellado',
                    idTramite: 'Id trámite',
                },
                validacioFirmaEstat: {
                    NO_VALIDAT: 'No validado',
                    SENSE_FIRMES: 'Sin firmas',
                    FIRMA_VALIDA: 'Firma válida',
                    FIRMA_INVALIDA: 'Firma inválida',
                    ERROR_VALIDANT: 'Error en la validación',
                },
                action: {
                    validarFirmes: 'Validar firmas',
                    validarICustodiar: 'Validar y custodiar',
                    descarregarFirma: 'Descargar firma',
                },
            },
        },
        integracio: {
            grid: {
                title: 'Monitor de integraciones',
                column: {
                    data: 'Fecha',
                    descripcio: 'Descripción',
                    tipus: 'Tipo',
                    codiUsuari: 'Usuario',
                    entitat: 'Entidad',
                    numeroRegistre: 'Número de registro',
                    tempsResposta: 'Tiempo respuesta',
                    estat: 'Estado',
                },
            },
            pipella: {
                USUARIS: 'Usuarios',
                UNITATS: 'Unidades admin.',
                ARXIU: 'Archivo digital',
                DADESEXT: 'Datos ext.',
                SIGNATURA: 'Signatura',
                VALIDASIG: 'Valida firma',
                GESDOC: 'Gestión documental',
                BUSTIAWS: 'Buzón WS',
                PROCEDIMENT: 'Procedimientos',
                SERVEI: 'Servicios',
                DISTRIBUCIO: 'Distribución',
                BACKOFFICE: 'Backoffice',
            },
            detail: {
                title: 'Detalle',
                tipus: {
                    ENVIAMENT: 'Enviado',
                    RECEPCIO: 'Recepción',
                },
                errorDescripcio: 'Descripción del error',
                excepcioMessage: 'Mensaje',
                excepcioStacktrace: 'Traza',
                copyTooltip: 'Copia la traza al portapapeles',
            },
        },
        unitatOrganitzativa: {
            grid: {
                title: "Gestión de unidades organizativas",
                dataSinc: "Fecha de sincronización",
                dataDarrerSinc: "Fecha de la última actualización",
            },
            accio: {
                organigrama: {
                    label: "Mostrar árbol de unidades vigentes",
                    title: "Árbol de unidades vigentes",
                },
                descarregarPdf: {
                    label: "Descarga PDF",
                },
                sincronitzar: {
                    label: "Sincronizar",
                    title: "Predicción de sincronización",
                    sincronitzar: "Sincroniza",
                    forzar: "Fuerza sincronización",
                    ok: "Sincronización realizada correctamente",
                    info: {
                        first: 'Primera sincronización',
                        empty: {
                            title: "No hay cambios",
                            label: "Las unidades organizativas están actualizadas",
                            unitat: "No se ha encontrado ninguna unidad vigente con esta unidad superior",
                        },
                        noves: 'NUEVAS',
                        divisions: 'DIVISIONES',
                        fusions: 'FUSIONES',
                        substitucio: 'SUSTITUCIONES',
                        canvi: 'CAMBIOS EN ATRIBUTOS',
                        reglesAfectades: 'REGLAS AFECTADAS POR LA SINCRONIZACIÓN',
                    }
                },
            },
        },
        limitCanviEstat: {
            grid: {
                title: "Límites de cambios de estado",
            },
            form: {
                resourceTitle: "limite de cambio de estado",
            },
            accio: {
                new: "Nuevo límite de cambio de estado",
                modificar: 'Modifica',
                esborrar: 'Borra',
                crearOk: "El nuevo límite de cambio de estado se ha creado correctamente",
                modificarOk: "El límite de cambio de estado se ha modificado correctamente",
                esborrarOk: "El límite de cambio de estado se ha borrado correctamente",
            },
        },
        bustia: {
            title: "Buzón",
            vista: "Cambiar vista",
            grid: {
                title: "Gestión de buzones",
                writeAllowed: "Acceso",
                principal: "Principal",
                favorit: "Favorito"
            },
            permisos: {
                readAllowed: "Solo lectura",
                writeAllowed: "Completo",
            },
            accio: {
                new: {
                    label: "Nuevo buzón",
                },
                update: {
                    title: "Modificar buzón",
                },
                moureAnotacions: {
                    title: "Mover las anotaciones de registro del buzón '{{nom}}'",
                    label: "Mueve anotaciones"
                },
                perDefecte: {
                    label: "Marca como principal",
                    ok: "El buzón se ha marcado como principal",
                },
                activar: {
                    label: "Activa",
                    ok: "El buzón se ha activado correctamente",
                },
                desactivar: {
                    label: "Desactiva",
                    ok: "El buzón se ha desactivado correctamente",
                },
                usuarisBustia: {
                    label: "Usuarios",
                    ok: "El documento se ha descargado correctamente",
                }
            },
        },
        config: {
            title: "Propiedades de configuración",
            accio: {
                restart: {
                    label: "Reinicia tareas en segundo plano",
                    ok: "Se han reiniciado las tareas en segundo plano. Puede comprobar el estado de ejecución de cada tarea en el monitor del sistema.",
                },
                sync: {
                    label: "Sincroniza",
                    ok: "Sincronización realizada correctamente",
                },
            }
        },
        backoffice: {
            title: "Backoffices",
            form: {
                resourceTitle: "backoffice",
                codi: "Código para identificar con qué Backoffice se ha distribuido la anotación",
                url: "Clave del fichero de propiedades correspondiente a la URL para la conexión con formato ${clau.fitxer.propietats.url} o URL directamente",
                usuari: "Clave del fichero de propiedades correspondiente al nombre de usuario para la conexión con formato ${clau.fitxer.propietats.usuari} o usuario directamente",
                contrasenya: "Clave del fichero de propiedades correspondiente a la contraseña para la conexión con formato ${clau.fitxer.propietats.contrasenya} o contraseña directamente",
            },
            accio: {
                new: {
                    label: "Nuevo backoffice",
                },
                prova: {
                    label: "Prueba",
                }
            }
        },
        massiva: {
            title: "Ejecuciones masivas globales",
            refresh: "Actualizar cada {{segons}}s.",
            contingut: "Contenido",
            accio: {
                play: {
                    label: "Reanudar",
                    ok: "La acción se ha reanudado correctamente",
                },
                pause: {
                    label: "Pausar",
                    ok: "La acción se ha pausado correctamente",
                },
                cancel: {
                    label: "Cancelar",
                    ok: "La acción se ha cancelado correctamente",
                },
                download: {
                    label: "Descargar",
                    notFound: "Documento no disponible",
                    ok: "Documento descargado correctamente",
                }
            }
        },
        registre: {
            title: "Anotaciones de registro",
            remitentEmpty: "Remitente vacío",
            grid: {
                avisos: "Avisos",
                remitent: "Remitente",
            },
            estat: {
                regla: "La anotación está pendiente de una regla sin regla, es necesario reprocesarla",
                maxReintents: "Se han realizado {{num}} intentos de un máximo de {{max}}",
            },
            avisos: {
                sobreescriure: "Marcada para sobrescribir",
                enviatPerEmail: "Enviada por correo electrónico",
                documentacio: {
                    _1: "Documentación adjunta en soporte PAPEL (u otros soportes)",
                    _2: "Documentación adjunta digitalizada y complementariamente en papel",
                    _3: "Documentación adjunta digitalizada",
                },
                estatEsborrany: "La anotación tiene anexos en estado de borrador en el Archivo",
                alerta: "Este registro se ha distribuido con reglas",
                procesError: {
                    ARXIU_PENDENT: "Error al guardar la anotación de registro en el Archivo",
                    REGLA_PENDENT: "Error al aplicar la regla de distribución",
                    BACK_PENDENT: "Error al enviar la anotación al backoffice",
                    BACK_ERROR: "Procesada en el backoffice con errores",
                    default: "Anotación con error",
                },
                pendentExecucioMassiva: "Incluida en una acción masiva pendiente de ejecución",
            },
            accio: {
                classifica: {
                    label: "Clasifíca...",
                    title: "Clasificación de la anotación de registro",
                    warning: "La clasificación de una anotación de registro puede desencadenar la ejecución de reglas, lo que puede provocar que la anotación desaparezca del buzón actual.",
                    ok: {
                        PROCEDIMENT: "La anotación de registro '{{numero}}' se ha clasificado correctamente dentro del procedimiento '{{sia}}'",
                        SERVEI: "La anotación de registro '{{numero}}' se ha clasificado correctamente dentro del servicio '{{sia}}'",
                    },
                },
                email: {
                    label: "Envía per correu electrònic...",
                    title: "Enviar per correu electrònic",
                    titleMassive: "Enviar per correu electrònic {{num}} anotacions",
                    ok: "L'anotació \"{{numero}}\" s'ha enviat correctament",
                    form: {
                        destinatari: "Introduïu els destinataris separats per coma o espai",
                    }
                },
                reenviar: {
                    label: "Reenvía...",
                    title: "Reenviar",
                    titleMassive: "Reenviar {{num}} anotaciones",
                    ok: "La anotación \"{{numero}}\" se ha reenviado correctamente",
                },
                marcarProcessada: {
                    label: "Marca como procesada...",
                    title: "Marcar como procesada",
                    titleMassive: "Marcar como procesadas {{num}} anotaciones",
                    ok: "La anotación \"{{numero}}\" se ha marcado como procesada",
                },
                marcarPendent: {
                    label: "Marca como pendiente...",
                    title: "Marcar como pendiente",
                    titleMassive: "Marcar como pendientes {{num}} anotaciones",
                    ok: "La anotación \"{{numero}}\" se ha marcado como pendiente",
                },
            },
        },
        alerta: {
            label: "Listado de alertas",
            title: "Listado de alertas",
            accio: {
                llegida: {
                    label: "Leída",
                    ok: "La alerta se ha marcado como leída",
                    confirm: {
                        message: "¿Está seguro de que desea marcar la alerta como leída?"
                    }
                }
            }
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
            interficieUsuari: {
                // Etiqueta de l'opció buida: sense valor mana la propietat del sistema.
                perDefecte: 'Por defecto del sistema',
            },
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
        AclPermissionManager: {
            title: "Permisos",
            resourceTitle: "permiso",
        },
        MassiveActionSelector: {
            options: "Opciones",
            all: "Seleccionarlo todo",
            clear: "Limpiar la selección",
        },
        CommentDialog: {
            title: "Comentarios del contenido: {{name}}",
            label: "Comentarios",
            envia: "Enviar",
        },
        RegistreSelector:{
            title: "Anotaciones seleccionadas",
        },
        RegistreReenviar: {
            quickfilter: "Buzón destino",
            favoritfilter: "Mostrar solo buzones favoritos",
            busties: "Buzones seleccionados",
            coneixement: {
                marcar: "Añadir a conocimiento",
                desmarcar: "Quitar de conocimiento",
            },
            favorit: {
                marcar: "Añadir a favoritos",
                desmarcar: "Quitar de favoritos",
            },
        }
    },
};

export default translationEs;
