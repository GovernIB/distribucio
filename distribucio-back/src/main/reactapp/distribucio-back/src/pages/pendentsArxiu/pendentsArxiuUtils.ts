/** Estat de la tasca en una execució (PendentsArxiuResource.Entrada al backend). */
export type EntradaHistograma = {
    data?: string | number;
    pendentArxiu: number;
    processats: number;
    errors: number;
    /** Temps mig de processament d'una anotació, en mil·lisegons. */
    tempsMitjaMs: number;
};

/** Resultat de l'acció OBTENIR_HISTOGRAMA (PendentsArxiuResource.Histograma al backend). */
export type Histograma = {
    numeroThreads?: number;
    expressioInactivitat?: string;
    entrades?: EntradaHistograma[];
};

/** Data i hora completes (capçalera), en l'idioma actual. */
export const formatDataHora = (data: string | number | undefined, idioma: string): string | undefined =>
    data == null
        ? undefined
        : new Date(data).toLocaleString(idioma, {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit',
              hour: '2-digit',
              minute: '2-digit',
          });

/** Hora i minut (etiqueta de l'eix X del gràfic), en l'idioma actual. */
export const formatHora = (data: string | number | undefined, idioma: string): string =>
    data == null ? '' : new Date(data).toLocaleTimeString(idioma, { hour: '2-digit', minute: '2-digit' });

export const msASegons = (ms: number): number => ms / 1000;
