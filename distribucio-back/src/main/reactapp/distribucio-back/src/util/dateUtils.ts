import dayjs from 'dayjs';

/**
 * Utilitats de format de dates per als filtres.
 *
 * Port de `src/util/dateUtils.ts` de RIPEA adaptat a dayjs (DISTRIBUCIO no té moment entre
 * les dependències). Els literals "T", "00:00:00" i "23:59:59" van entre claudàtors perquè
 * dayjs no els interpreti com a tokens de format.
 */

export const formatDate = (date: string, format: string = 'DD/MM/YYYY HH:mm:ss'): string | null => {
    return date ? dayjs(date).format(format) : null;
};

export const formatIso = (date: string) => {
    return formatDate(date, 'YYYY-MM-DD[T]HH:mm:ss');
};

export const formatStartOfDay = (date: string) => {
    return formatDate(date, 'YYYY-MM-DD[T00:00:00]');
};

export const formatEndOfDay = (date: string) => {
    return formatDate(date, 'YYYY-MM-DD[T23:59:59]');
};
