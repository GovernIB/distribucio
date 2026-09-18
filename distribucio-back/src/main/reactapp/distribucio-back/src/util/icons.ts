/**
 * Icones estàndard (noms de lligadura de Material Icons) que es fan servir de manera transversal a
 * l'aplicació. Centralitzar-les aquí garanteix la coherència visual entre pantalles i evita literals
 * escampats. Els conceptes compartits amb RIPEA duen la mateixa clau i la mateixa icona que al seu
 * `src/util/icons.ts` (p. ex. `anotacio`, `consulta`).
 */
export const icons = {
    inici: 'home',
    anotacio: 'email',
    entitat: 'account_balance',
    configuracio: 'settings',
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
    limitCanviEstat: 'display_settings',
    annex: 'attach_file',
    contingut: 'archive',
} as const;
