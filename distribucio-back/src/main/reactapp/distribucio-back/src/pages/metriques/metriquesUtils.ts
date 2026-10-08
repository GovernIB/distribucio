/** Dades d'un timer del registre de Dropwizard (format del JSON de MetricsModule). */
export type TimerData = {
    count: number;
    min: number;
    mean: number;
    max: number;
    stddev: number;
    p50: number;
    p75: number;
    p95: number;
    p98: number;
    p99: number;
    p999: number;
    mean_rate: number;
    m1_rate: number;
    m5_rate: number;
    m15_rate: number;
    duration_units?: string;
    rate_units?: string;
};

/** Mètriques de l'aplicació (MetriquesResource.Metriques al backend); és el mateix format que el fitxer metrics.json. */
export type MetriquesData = {
    version?: string;
    timers: Record<string, TimerData>;
    [grup: string]: unknown;
};

/** Timer ja preparat per a la llista, amb els percentatges d'ample de les barres. */
export type TimerResum = {
    name: string;
    count: number;
    data: TimerData;
    pes: number;
    mitja: number;
    maxim: number;
    pctPes: number;
    pctMitja: number;
    pctMaxim: number;
};

const arrodoneix2 = (valor: number) => Math.round(valor * 100) / 100;

/** Percentatge de `valor` sobre `max`; 0 si no es pot calcular. */
export const percentatge = (valor: number, max: number): number => {
    const pct = (valor * 100) / max;
    return Number.isFinite(pct) ? Math.round(pct) : 0;
};

/** Valor d'una barra de detall: un decimal. */
export const formatNumero = (valor: number | undefined): string =>
    typeof valor === 'number' && !Number.isNaN(valor) ? valor.toFixed(1) : '1';

export const capitalitza = (text?: string): string => (text ? text.charAt(0).toUpperCase() + text.slice(1) : '');

/**
 * Llista de timers ordenada per pes descendent (pes = temps mig x nombre d'execucions): 
 * el pes es compara amb el pes màxim i els temps mig i màxim amb el temps màxim de tots els timers.
 */
export const construeixTimers = (dades?: MetriquesData): TimerResum[] => {
    const timers = Object.entries(dades?.timers ?? {}).map(([name, data]) => ({
        name,
        count: data.count,
        data,
        weight: data.mean * data.count,
    }));
    const maxPes = Math.max(0, ...timers.map((timer) => timer.weight));
    const maxMax = Math.max(0, ...timers.map((timer) => timer.data.max));
    const pctAmple = (valor: number, max: number) => {
        const pct = (valor * 100) / max;
        return Number.isFinite(pct) ? pct : 0;
    };
    return timers
        .sort((a, b) => b.weight - a.weight)
        .map(({ name, count, data, weight }) => ({
            name,
            count,
            data,
            pes: arrodoneix2(weight),
            mitja: arrodoneix2(data.mean),
            maxim: arrodoneix2(data.max),
            pctPes: pctAmple(weight, maxPes),
            pctMitja: pctAmple(data.mean, maxMax),
            pctMaxim: pctAmple(data.max - data.mean, maxMax),
        }));
};

/** Comprova que el contingut d'un fitxer importat té la forma de les mètriques. */
export const esMetriquesValides = (dades: unknown): dades is MetriquesData => {
    const timers = (dades as { timers?: unknown } | null)?.timers;
    return typeof dades === 'object' && !!timers && typeof timers === 'object';
};
