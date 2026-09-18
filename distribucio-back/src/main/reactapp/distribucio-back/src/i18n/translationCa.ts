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
        update: 'Modifica',
        delete: 'Esborra',
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
        copiat: 'Copiat correctament',
    },
    app: {
        loading: 'Iniciant DISTRIBUCIO',
        menu: {
            home: 'Inici',
            entitats: 'Entitats',
            avisos: 'Avisos',
            consultar: "Consultar",
            serveis: 'Serveis',
            configuracio: 'Configuració',
            configurar: 'Configurar',
            limitCanviEstat: "Limits canvis d'estat",
            bustia: 'Bústies',
            unitatOrganitzativa: 'Unitats Organitzatives',
            procediment: 'Procediments',
            contingut: 'Continguts',
            annex: 'Annexos',
            config: 'Propietats configurables',
            massiva: "Consultar accions massives",
            monitoritzar: 'Monitoritzar',
            integracio: 'Integracions',
            registre: "Anotacions",
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
                adminAllowed: 'Administració',
                perm0Allowed: 'Admin (Lectura)',
                readAllowed: 'Usuaris',
            },
            permis: {
                title: "Permisos de l'entitat",
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
						description: "L'acció continuarà en segon pla i podreu consultar el resultat més tard.",
					},
                },
            },
        },
        procediments: {
            grid: {
                title: "Procediments",
            },
            accio: {
                actualitzarTotsButton: "Actualitza tots els procediments",
                actualitzar: "Actualitza el procediment",
                actualitzarOk: "El procediment s'ha actualitzat correctament",
                actualitzarTotsOk: "Tots els procediments s'han actualitzat correctament",
                error: "No s'ha pogut executar l'acció",
                actualitzarTots: {
                    title: "Actualització de procediments",
                    confirmacio: "Voleu actualitzar els procediments amb la informació de ROLSAC?",
                    estat: "Estat",
                    total: "Número de procediments totals",
                    processats: "Número de procediments processats",
                    estats: {
                        INICIALITZANT: "Inicialitzant",
                        ACTUALITZANT: "Actualitzant",
                        FINALITZAT: "Finalitzat",
                        ERROR: "Error",
                    },
                    close: {
						check: "Estau segur que voleu tancar aquesta finestra?",
						description: "L'acció continuarà en segon pla i podreu consultar el resultat més tard.",
					},
                },
            },
        },
        contingut: {
            grid: {
                title: "Localitzador de continguts",
                icona: {
                    bustia: "Bústia",
                    registre: "Anotació de registre",
                    unitat: "Unitat organitzativa",
                    esborrat: "Esborrat",
                    alerta: "Aquest registre s'ha distribuit amb regles",
                },
            },
            accio: {
                detalls: {
                    label: "Detalls",
                },
                historial: {
                    label: "Històric d'accions",
                },
            },
            detall: {
                title: "Detall de l'element",
                camp: {
                    nom: "Nom",
                    entitat: "Entitat",
                    unitatOrganitzativa: "Unitat organitzativa",
                    activa: "Activa",
                    perDefecte: "Per defecte",
                    permisos: "Permisos",
                },
            },
            historial: {
                title: "Històric d'accions de l'element",
                tab: {
                    accions: "Accions",
                    moviments: "Moviments",
                    auditoria: "Auditoria",
                },
                accio: {
                    informe: {
                        label: "Informe",
                    },
                    veureDetall: "Veure detalls",
                },
                detall: {
                    title: "Detall de l'acció",
                    objecte: "Objecte: {{nom}}",
                    params: "Paràmetres",
                    accioPare: "Acció pare",
                    moviment: "Moviment",
                    movimentOrigen: "Origen",
                    movimentDesti: "Destí",
                },
                auditoria: {
                    creacio: "Creació",
                    modificacio: "Darrera modificació",
                    usuari: "Usuari",
                    data: "Data",
                },
            },
        },
        annex: {
            grid: {
                title: "Localitzador d'annexos",
                registre: {
                    original: 'original',
                    copia: 'còpia {{num}}',
                },
                arxiuEstat: {
                    buitAvis: "Aquest annex no està guardat dins l'Arxiu",
                },
            },
            accio: {
                detalls: "Detalls de l'annex",
                detallsAnotacio: "Detalls de l'anotació",
                concsv: 'Enllaç al ConCSV',
                descarregarOriginal: 'Descàrrega original',
                descarregarImprimible: 'Descàrrega imprimible',
                guardarDefinitiu: {
                    label: 'Custòdia',
                    jaDefinitiu: "L'annex \"{{titol}}\" de l'anotació {{numero}} ja consta com a Definitiu a Distribució",
                    expedientTancat: "L'expedient que conté l'annex \"{{titol}}\" de l'anotació {{numero}} està tancat a l'arxiu",
                    definitiuArxiu: "El document annex \"{{titol}}\" de l'anotació {{numero}} ja estava com a definitiu a l'arxiu",
                    mogutBackoffice: "El document annex \"{{titol}}\" de l'anotació {{numero}} s'ha mogut a un expedient del backoffice",
                    updated: "L'annex \"{{titol}}\" de l'anotació {{numero}} s'ha marcat com a Definitiu",
                    errorUpdate: "Ha ocorregut un error en el moment de crear/modificar a l'arxiu l'annex \"{{titol}}\" de l'anotació {{numero}}",
                    errorArxiu: "Error no controlat marcant l'annex \"{{titol}}\" de l'anotació {{numero}} com a definitiu",
                    errorFirma: "Hi ha hagut un error validant la firma de l'annex {{titol}} de l'anotació {{numero}}",
                    senseFirma: "Error no controlat amb la firma de l'annex \"{{titol}}\" de l'anotació {{numero}}",
                },
                guardarDefinitiuMultiple: {
                    label: 'Custòdia',
                    duplicat: 'Ja existeix una execució massiva pendent dels elements seleccionats: {{elements}}',
                },
                validarFirmes: {
                    valides: "Les firmes de l'annex són vàlides",
                    noValides: 'Les firmes no són vàlides',
                    errorValidant: "Les firmes no s'han pogut validar",
                    custodiat: 'Annex custodiat correctament',
                },
                error: "No s'ha pogut executar l'acció",
            },
            detall: {
                title: "Detalls de l'annex",
                camp: {
                    dataCaptura: 'Data de captura (ENI)',
                    origen: 'Origen (ENI)',
                    ntiElaboracioEstat: "Estat d'elaboració (ENI)",
                    ntiTipusDocument: 'Tipus documental (ENI)',
                    sicresTipusDocument: 'Tipus de document annexat',
                    localitzacio: 'Localitzacio',
                    observacions: 'Observacions',
                    fitxerArxiuUuid: "Identificador dins l'Arxiu",
                    firmaCsv: 'Firma CSV',
                    concsvUrl: 'Enllaç a ConCSV',
                    fitxerTipusMime: 'Tipus MIME',
                    validacioFirmaEstat: 'Validació de firma',
                    arxiuEstat: "Estat a l'Arxiu",
                    gestioDocumental: 'Gestió documental',
                    fitxer: 'Fitxer',
                    firma: 'Firma',
                    firmes: 'Firmes',
                },
                firmes: {
                    mostrar: 'Mostrar firmes',
                    column: {
                        nom: 'Nom',
                        nif: 'NIF',
                        data: 'Data',
                        emissor: 'Emissor',
                    },
                    data: {
                        nd: 'N/D',
                    },
                    autofirma: {
                        info: "DISTRIBUCIÓ ha afegit automàticament aquesta firma a l'annex de l'anotació de registre per desar-lo com a definitiu a l'arxiu",
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
                    firmes: 'Firmes',
                    cap: '(cap)',
                    column: {
                        tipus: 'Tipus firma',
                        perfil: 'Perfil firma',
                        fitxer: 'Fitxer',
                        csvRegulacio: 'CSV regulació',
                        autofirma: "Firmat per DISTRIBUCIÓ",
                        gesdocFirmaId: 'Gestió documental firma',
                    },
                },
                ntiElaboracioEstat: {
                    ORIGINAL: 'Original',
                    COPIA_ELECT_AUTENTICA_CANVI_FORMAT: 'Còpia electrònica autèntica amb canvi de format',
                    COPIA_ELECT_AUTENTICA_PAPER: 'Còpia electrònica autèntica de document paper',
                    COPIA_ELECT_AUTENTICA_PARCIAL: 'Còpia electrònica parcial autèntica',
                    ALTRES: 'Altres',
                },
                ntiTipusDocument: {
                    RESOLUCIO: 'Resolució',
                    ACORD: 'Acord',
                    CONTRACTE: 'Contracte',
                    CONVENI: 'Conveni',
                    DECLARACIO: 'Declaració',
                    COMUNICACIO: 'Comunicació',
                    NOTIFICACIO: 'Notificació',
                    PUBLICACIO: 'Publicació',
                    ACUS_REBUT: 'Justificant de recepció',
                    ACTE: 'Acta',
                    CERTIFICAT: 'Certificat',
                    DILIGENCIA: 'Diligència',
                    INFORME: 'Informe',
                    SOLICITUD: "Sol·licitud",
                    DENUNCIA: 'Denúncia',
                    ALEGACIONS: 'Alegació',
                    RECURSOS: 'Recursos',
                    COMUNICACIO_CIUTADA: 'Comunicació ciutadà',
                    FACTURA: 'Factura',
                    ALTRES_INCAUTATS: 'Altres confiscats',
                    LLEI: 'Llei',
                    MOCIO: 'Moció',
                    INSTRUCCIO: 'Instrucció',
                    CONVOCATORIA: 'Convocatòria',
                    ORDRE_DIA: 'Ordre del dia',
                    INFORME_PONENCIA: 'Informe de ponència',
                    DICTAMEN_COMISSIO: 'Dictamen de comissió',
                    INICIATIVA_LEGISLATIVA: 'Iniciativa legislativa',
                    PREGUNTA: 'Pregunta',
                    INTERPELACIO: "Interpel·lació",
                    RESPOSTA: 'Resposta',
                    PROPOSICIO_NO_LLEI: 'Proposició no de llei',
                    ESQUEMA: 'Esmena',
                    PROPOSTA_RESOLUCIO: 'Proposta de resolució',
                    COMPAREIXENSA: 'Compareixença',
                    SOLICITUD_INFORMACIO: "Sol·licitud d'informació",
                    ESCRIT: 'Escrit',
                    INICIATIVA__LEGISLATIVA: 'Iniciativa legislativa',
                    PETICIO: 'Petició',
                    ALTRES: 'Altres tipus de documents',
                },
                sicresTipusDocument: {
                    FORM: 'Formulari',
                    FORM_ADJUNT: 'Document adjunt al formulari',
                    INTERN: 'Fitxer tècnic intern',
                },
                metaData: {
                    codiProcediment: 'Codi de procediment',
                    resolucion: 'Resolució',
                    profundidadColor: 'Profunditat color',
                    titol: 'Títol',
                    idioma: 'Idioma',
                    descripcio: 'Descripció',
                    appTramitDoc: 'App tràmit document',
                    organ: 'Òrgan',
                    origen: 'Origen',
                    estatElaboracio: 'Estat elaboració',
                    tipusDocEni: 'Tipus doc ENI',
                    codiClassificacio: 'Codi classificació',
                    csv: 'Csv',
                    defCsv: 'Def. csv',
                    id: 'Id',
                    idOrigen: 'Id origen',
                    dataInici: 'Data inici',
                    nomFormat: 'Nom format',
                    extensioFormat: 'Extensió format',
                    midaLogica: 'Mida lógica',
                    termePuntAcces: 'Terme punt accés',
                    idPuntAcces: 'Id punt accés',
                    esquemaPuntAcces: 'Esquema punt accés',
                    suport: 'Suport',
                    locArxiuCentral: 'Loc. arxiu central',
                    arxiuGeneral: 'Arxiu general',
                    unitats: 'Unitats',
                    subtipusDoc: 'Subtipus doc.',
                    tipusAsientoRegistral: 'Tipus asiento registral',
                    codiOficinaRegistre: 'Codi oficina registre',
                    dataAsientoRegistral: 'Data asiento registral',
                    numAsientoRegistral: 'Nombre asiento registral',
                    tipusFirma: 'Tipus firma',
                    perfilFirma: 'Perfil firma',
                    dataSegellat: 'Data segellat',
                    idTramite: 'Id tràmit',
                },
                validacioFirmaEstat: {
                    NO_VALIDAT: 'No validat',
                    SENSE_FIRMES: 'Sense firmes',
                    FIRMA_VALIDA: 'Firma vàlida',
                    FIRMA_INVALIDA: 'Firma invàlida',
                    ERROR_VALIDANT: 'Error en la validació',
                },
                action: {
                    validarFirmes: 'Validar firmes',
                    validarICustodiar: 'Validar i custodiar',
                    descarregarFirma: 'Descarregar firma',
                },
            },
        },
        integracio: {
            grid: {
                title: "Monitor d'integracions",
                column: {
                    data: 'Data',
                    descripcio: 'Descripció',
                    tipus: 'Tipus',
                    codiUsuari: 'Usuari',
                    entitat: 'Entitat',
                    numeroRegistre: 'Número de registre',
                    tempsResposta: 'Temps resposta',
                    estat: 'Estat',
                },
            },
            pipella: {
                USUARIS: 'Usuaris',
                UNITATS: 'Unitats admin.',
                ARXIU: 'Arxiu digital',
                DADESEXT: 'Dades ext.',
                SIGNATURA: 'Signatura',
                VALIDASIG: 'Valida sig.',
                GESDOC: 'Gestió documental',
                BUSTIAWS: 'Bústia WS',
                PROCEDIMENT: 'Procediments',
                SERVEI: 'Serveis',
                DISTRIBUCIO: 'Distribució',
                BACKOFFICE: 'Backoffice',
            },
            detail: {
                title: 'Detall',
                tipus: {
                    ENVIAMENT: 'Enviament',
                    RECEPCIO: 'Recepció',
                },
                errorDescripcio: 'Descripció de l\'error',
                excepcioMessage: 'Missatge',
                excepcioStacktrace: 'Traça',
                copyTooltip: 'Copia la traça al portapapeles',
            },
        },
        unitatOrganitzativa: {
            grid: {
                title: "Gestió d'unitats organitzatives",
                dataSinc: "Data sincronització",
                dataDarrerSinc: "Data darrera actualització",
            },
            accio: {
                organigrama: {
                    label: "Mostrar arbre d'unitats vigents",
                    title: "Arbre d'unitats vigents",
                },
                descarregarPdf: {
                    label: "Descarrega pdf",
                },
                sincronitzar: {
                    label: "Sincronitzar",
                    title: "Predicció de sincronització",
                    sincronitzar: "Sincronitza",
                    forzar: "Força sincronització",
                    ok: "Sincronització realitzada amb èxit",
                    info: {
                        first: 'Primera sincronización',
                        empty: {
                            title: "No hi ha canvis",
                            label: "Les unitats organitzatives estan actualizades",
                            unitat: "No s'ha trobat cap unitat vigent amb aquesta unitat pare",
                        },
                        noves: 'NOVES',
                        divisions: 'DIVISIONS',
                        fusions: 'FUSIONS',
                        substitucio: 'SUBSTITUCIONS',
                        canvi: 'CANVIS EN ATRIBUTS',
                        reglesAfectades: 'REGLES AFECTADES PER LA SINCRONITZACIÓ',
                    }
                },
            },
        },
        limitCanviEstat: {
            grid: {
                title: "Límits de canvis d'estat",
            },
            form: {
                resourceTitle: "limit de canvi d'estat",
            },
            accio: {
                new: "Nou limit de canvi d'estat",
                modificar: 'Modifica',
                esborrar: 'Esborra',
                crearOk: "El nou limit de canvi d'estat s'ha creat correctament",
                modificarOk: "El limit de canvi d'estat s'ha modificat correctament",
                esborrarOk: "El limit de canvi d'estat s'ha esborrat correctament",
            },
        },
        bustia: {
            title: "Bústia",
            vista: "Canvi de vista",
            grid: {
                title: "Gestió de bústies",
                writeAllowed: "Accés",
                principal: "proncipal",
                favorit: "Favorit"
            },
            permisos: {
                readAllowed: "Només lectura",
                writeAllowed: "Complet",
            },
            accio: {
                new: {
                    label: "Nova bústia",
                },
                update: {
                    title: "Modifica bústia",
                },
                moureAnotacions: {
                    title : "Moure les anotacions de registre de la bústia '{{nom}}'",
                    label: 'Mou anotacions'
                },
                perDefecte: {
                    label: 'Marca com a principal',
                    ok: "La bústia s'ha marcat com a principal",
                },
                activar: {
                    label: 'Activa',
                    ok: "La bústia s'ha activat correctament",
                },
                desactivar: {
                    label: 'Desactiva',
                    ok: "La bústia s'ha desactivat correctament",
                },
                usuarisBustia: {
                    label: 'Usuaris',
                    ok: "El document s'ha descarregat correctament",
                }
            },
        },
        config: {
            title: "Propietats de configuració",
            accio: {
                restart: {
                    label: "Reinicia tasques en segon pla",
                    ok: "S'han reiniciat les tasques en segon pla. Pot comprovar l'estat d'execució de cada tasca en el monitor del sistema.",
                },
                sync: {
                    label: "Sincronitza",
                    ok: "Sincronització realitzada amb èxit",
                },
            }
        },
        backoffice: {
            title: "Backoffices",
            form: {
                resourceTitle: "backoffice",
                codi: "Codi per identificar amb quin Backoffice s'ha distribuit l'anotació",
                url: "Clau de el fitxer de propietats corresponent a la url per a la connexió amb format ${clau.fitxer.propietats.url} o url directament",
                usuari: "Clau de el fitxer de propietats corresponent a el nom d'usuari per a la connexió amb format ${clau.fitxer.propietats.usuari} o usuari directament",
                contrasenya: "Clau de el fitxer de propietats corresponent a la contrasenya per a la connexió amb format ${clau.fitxer.propietats.contrasenya} o contrasenya directament",
            },
            accio: {
                new: {
                    label: "Nou backoffice",
                },
                prova: {
                    label: "Prova",
                }
            }
        },
        massiva: {
            title: "Execucions massives globals",
            refresh: "Refresca cada {{segons}}s.",
            contingut: "Contingut",
            accio: {
                play: {
                    label: "Reprèn",
                    ok: "L'acció s'ha reprès correctament",
                },
                pause: {
                    label: "Pausa",
                    ok: "L'acció s'ha pausat correctament",
                },
                cancel: {
                    label: "Cancel·la",
                    ok: "L'acció s'ha cancel·lat correctament",
                },
                download: {
                    label: "Descarrega",
                    notFound: "Document no disponible",
                    ok: "Document descarregat correctament",
                }
            }
        },
        registre: {
            title: "Anotacions de registre",
            remitentEmpty: "Remitent buit",
            grid: {
                avisos: "Avisos",
                remitent: "Remitent",
            },
            estat: {
                regla: "L'anotació està pendent de regla sense regla, cal reprocessar-la",
                maxReintents: "S'han realitzat {{num}} intents d'un màxim de {{max}}",
            },
            avisos: {
                sobreescriure: "Marcat per a sobreescriure",
                enviatPerEmail: "Enviat per correu electrònic",
                documentacio: {
                    _1: "Documentació adjunta en suport PAPER (o altres suports)",
                    _2: "Documentació adjunta digitalitzada i complementàriament en paper",
                    _3: "Documentació adjunta digitalitzada",
                },
                estatEsborrany: "L'anotació té annexos en estat d'esborrany a l'Arxiu",
                alerta: "Aquest registre s'ha distribuït amb regles",
                procesError: {
                    ARXIU_PENDENT: "Error en desar l'anotació de registre a l'Arxiu",
                    REGLA_PENDENT: "Error en aplicar la regla de distribució",
                    BACK_PENDENT: "Error en enviar l'anotació al backoffice",
                    BACK_ERROR: "Processada al backoffice amb errors",
                    default: "Anotació amb error",
                },
                pendentExecucioMassiva: "Inclosa en una acció massiva pendent d'execució",
            },
            accio: {
                classifica: {
                    label: "Classifíca...",
                    title: "Classificació de l'anotació de registre",
                    warning: "La classificació d'una anotació de registre pot disparar l'execució de regles, la qual cosa pot provocar que l'anotació desaparegui de la bústia actual.",
                    ok: {
                        PROCEDIMENT: "L'anotació de registre '{{numero}}' s'ha classificat correctament dins del procediment '{{sia}}'",
                        SERVEI: "L'anotació de registre '{{numero}}' s'ha classificat correctament dins del servei '{{sia}}'",
                    },
                },
                email: {
                    label: "Envía via email...",
                    title: "Enviar via email",
                    titleMassive: "Enviar via email {{num}} anotacions",
                    ok: "La anotación \"{{numero}}\" se ha enviado correctamente",
                    form: {
                        destinatari: "Introduïu els destinataris separats per coma o per espai",
                    }
                },
                reenviar: {
                    label: "Reenvía...",
                    title: "Reenviar",
                    titleMassive: "Reenviar {{num}} anotacions",
                    ok: "La anotación \"{{numero}}\" se ha reenviat correctamente",
                },
                marcarProcessada: {
                    label: "Marca com a processada...",
                    title: "Marcar com a processada",
                    titleMassive: "Marcar com a processades {{num}} anotacions",
                    ok: "L'anotació \"{{numero}}\" s'ha marcat com a processada",
                },
                marcarPendent: {
                    label: "Marca com a pendent...",
                    title: "Marcar com a pendent",
                    titleMassive: "Marcar com a pendents {{num}} anotacions",
                    ok: "L'anotació \"{{numero}}\" s'ha marcat com a pendent",
                },
            },
        },
        alerta: {
            label: "Llistat d'alertes",
            title: "Llistat d'alertes",
            accio: {
                llegida: {
                    label: "Llegida",
                    ok: "L'alerta s'ha marcat com a llegida",
                    confirm: {
                        message: "Segur que vol marcar l'alerta com a llegida?"
                    }
                }
            }
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
            interficieUsuari: {
                // Etiqueta de l'opció buida: sense valor mana la propietat del sistema.
                perDefecte: 'Per defecte del sistema',
            },
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
        AclPermissionManager: {
            title: "Permisos",
            resourceTitle: "permís",
        },
        MassiveActionSelector: {
            options: "Opcions",
            all: "Selecciona-ho tot",
            clear: "Neteja la selecció",
        },
        CommentDialog: {
            title: "Comentaris del contingut: {{name}}",
            label: "Comentaris",
            envia: "Envia",
        },
        RegistreSelector:{
            title: "Anotacions seleccionades",
        },
        RegistreReenviar: {
            quickfilter: "Bústia destí",
            favoritfilter: "Mostrar només bústies favorites",
            busties: "Bústies seleccionades",
            coneixement: {
                marcar: "Afegir a coneixement",
                desmarcar: "Llevar de coneixement",
            },
            favorit: {
                marcar: "Afegir a favorits",
                desmarcar: "Llevar de favorits",
            },
        }
    },
};

export default translationCa;
