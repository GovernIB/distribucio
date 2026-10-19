/**
 * Catàleg d'icones (Material Icons) per **concepte**. És l'única font d'icones de l'aplicació:
 * l'entrada de menú, el títol de la pantalla i els botons que representen el mateix concepte han de
 * fer servir sempre la mateixa icona, i el mateix concepte ha de tenir la mateixa icona a totes les
 * aplicacions de Drassana. Per això les seccions d'accions, filtre i selecció són comunes amb les
 * altres aplicacions (ConCSV, RIPEA...) i només canvien les entrades de pantalla. Els conceptes de
 * pantalla compartits amb RIPEA duen la mateixa clau i la mateixa icona que al seu
 * `src/util/icons.ts` (p. ex. `anotacio`, `consulta`).
 *
 * Si una pantalla o acció nova necessita una icona, s'hi afegeix aquí per concepte; no es posa el nom
 * de la icona en línia al component.
 */
export const icons = {
    // --- Pantalles (entrades de menú i títol de la pantalla) ---------------------------------
    inici: 'home',
    anotacio: 'email',
    entitat: 'account_balance',
    configuracio: 'settings',
    // Grup de menú «Configurar» (ajustos de l'aplicació): la mateixa que fa servir ConCSV per a la configuració.
    ajustos: 'tune',
    avis: 'campaign',
    monitoritzacio: 'monitor',
    consulta: 'search',
    // Mateixa icona que les opcions principals de RIPEA "Procediments i serveis" i "Acció massiva".
    procediment: 'integration_instructions',
    servei: 'integration_instructions',
    massiva: 'list_alt',
    // La mateixa que fa servir RIPEA als botons de permisos (p. ex. pages/entitat/EntitatGrid.tsx).
    permis: 'key',
    // Entrades de submenú amb icona pròpia de DISTRIBUCIO (a RIPEA no hi són o no en duen).
    bustia: 'inbox',
    unitatOrganitzativa: 'account_tree',
    backoffice: 'wifi',
    config: 'display_settings',
    limitCanviEstat: 'date_range',
    usuariCanviCodis: 'manage_accounts',
    annex: 'attach_file',
    contingut: 'archive',
    integracio: 'sync_alt',
    monitorSistema: 'monitor_heart',
    metriques: 'stacked_bar_chart',
    pendentsArxiu: 'query_stats',
    excepcio: 'bug_report',
    vistaMoviments: 'move_down',
    metaDada: 'data_object',
    domini: 'domain',
    regla: 'rule',

    // --- Accions -----------------------------------------------------------------------------
    crea: 'add',
    modifica: 'edit',
    esborra: 'delete',
    esborraTot: 'delete_sweep',
    desa: 'save',
    desfes: 'undo',
    cancela: 'close',
    accepta: 'check',
    activa: 'check',
    desactiva: 'close',
    executa: 'bolt',
    refresca: 'refresh',
    sincronitza: 'cached',
    copia: 'content_copy',
    descarrega: 'file_download',
    carrega: 'file_upload',
    detall: 'info',
    informe: 'description',
    llista: 'list',
    diagnostic: 'monitor_heart',
    envia: 'send',
    imprimeix: 'print',
    obreNovaFinestra: 'open_in_new',
    mou: 'turn_right',
    avanca: 'fast_forward',
    reprenExecucio: 'play_circle',
    pausaExecucio: 'pause',
    desaDefinitiu: 'edit_note',
    afegeixUsuari: 'person_add',
    bloqueja: 'lock',
    desbloqueja: 'lock_open_right',
    mostra: 'visibility',
    amunt: 'arrow_upward',
    avall: 'arrow_downward',
    anterior: 'keyboard_double_arrow_left',
    seguent: 'keyboard_double_arrow_right',
    plega: 'expand_less',
    desplega: 'expand_more',
    mesAccions: 'more_vert',
    buida: 'clear',
    interficieClassica: 'fast_rewind',

    // --- Estats i avisos ---------------------------------------------------------------------
    advertencia: 'warning',
    error: 'error',
    problema: 'report_problem',
    correcte: 'check_circle',
    incorrecte: 'cancel',
    programat: 'schedule',
    senseDades: 'block',
    favorit: 'star',
    alertes: 'note_stack',
    historial: 'history',
    comentaris: 'forum',
    documentacioFisica: 'inventory_2',
    documentacioElectronica: 'code_xml',

    // --- Objectes i vistes -------------------------------------------------------------------
    correu: 'mail',
    rol: 'badge',
    grup: 'group',
    taula: 'table',
    llistaPunts: 'format_list_bulleted',
    detallAnotacio: 'adjust',

    // --- Filtre i cerca ----------------------------------------------------------------------
    cerca: 'search',
    filtre: 'filter_alt',
    neteja: 'filter_alt_off',
    cercaAvancadaObre: 'zoom_in',
    cercaAvancadaTanca: 'zoom_out',

    // --- Selecció (accions massives) ---------------------------------------------------------
    seleccionaTot: 'check_box',
    deseleccionaTot: 'check_box_outline_blank',
    opcioMarcada: 'check_box',
    opcioDesmarcada: 'check_box_outline_blank',
} as const;

export type IconName = (typeof icons)[keyof typeof icons];
