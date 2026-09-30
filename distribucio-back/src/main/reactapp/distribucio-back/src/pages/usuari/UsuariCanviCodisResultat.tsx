import React from 'react';
import { useTranslation } from 'react-i18next';
import {
    Alert,
    AlertTitle,
    Box,
    Divider,
    Icon,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography,
} from '@mui/material';

export type CanviCodisEstat =
    | 'OK'
    | 'ERROR'
    | 'FORMAT_INCORRECTE'
    | 'ANTIC_NO_EXISTEIX'
    | 'NOU_EXISTEIX_SALTAT'
    | 'DUPLICAT';

export type CanviCodisLinia = {
    numLinia: number;
    codiAntic?: string;
    codiNou?: string;
    estat: CanviCodisEstat;
    registresModificats?: number;
    durada?: number;
    missatge?: string;
};

/** Resposta de l'acció CANVI_CODIS (CanviCodisResultat del backend). */
export type CanviCodisResultatData = {
    linies: CanviCodisLinia[];
    duradaTotal: number;
};

/** Durada en ms com a text llegible: "532 ms", "12,4 s" o "3 min 5 s". */
const formatDurada = (ms: number): string => {
    if (ms < 1000) {
        return `${ms} ms`;
    }
    if (ms < 60000) {
        return `${(ms / 1000).toFixed(1).replace('.', ',')} s`;
    }
    const minuts = Math.floor(ms / 60000);
    const segons = Math.round((ms % 60000) / 1000);

    return `${minuts} min ${segons} s`;
};

const ICONA_ESTAT: Record<CanviCodisEstat, { icon: string; color: 'success' | 'error' | 'warning' }> = {
    OK: { icon: 'check_circle', color: 'success' },
    ERROR: { icon: 'cancel', color: 'error' },
    FORMAT_INCORRECTE: { icon: 'cancel', color: 'error' },
    ANTIC_NO_EXISTEIX: { icon: 'cancel', color: 'error' },
    NOU_EXISTEIX_SALTAT: { icon: 'warning', color: 'warning' },
    DUPLICAT: { icon: 'cancel', color: 'error' },
};

type UsuariCanviCodisResultatProps = {
    /** Línies ja processades, en ordre; durant l'execució va creixent d'una en una. */
    linies: CanviCodisLinia[];
    /** Indica si s'ha marcat la unificació amb els usuaris nous que ja existien. */
    unifica: boolean;
    /** Cert quan s'han processat totes les línies: només llavors es mostren els alerts de resum. */
    finalitzat: boolean;
    /** Durada total del procés en ms; només té sentit un cop finalitzat. */
    duradaTotal?: number;
};

/**
 * Resultat d'una execució del canvi de codis: una fila per línia processada (que va apareixent a mesura que es fa) i, en acabar, 
 * el resum global (línies, correctes/errors/omeses, registres i temps total) i els textos informatius sobre el canvi que s'ha fet.
 */
export const UsuariCanviCodisResultat: React.FC<UsuariCanviCodisResultatProps> = ({
    linies,
    unifica,
    finalitzat,
    duradaTotal = 0,
}) => {
    const { t } = useTranslation();
    const taulaRef = React.useRef<HTMLDivElement>(null);

    React.useEffect(() => {
        const taula = taulaRef.current;
        if (!finalitzat && taula != null) {
            taula.scrollTop = taula.scrollHeight;
        }
    }, [linies.length, finalitzat]);

    if (linies.length === 0 && !finalitzat) {
        return null;
    }

    const ok = linies.filter((l) => l.estat === 'OK');
    const omeses = linies.filter((l) => l.estat === 'NOU_EXISTEIX_SALTAT').length;
    const errors = linies.length - ok.length - omeses;
    const registres = ok.reduce((total, l) => total + (l.registresModificats ?? 0), 0);
    const severity = errors > 0 ? 'error' : omeses > 0 ? 'warning' : 'success';

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, minHeight: 0, flex: '0 1 auto' }}>
            <Divider textAlign="left" sx={{ my: 1 }}>
                <Typography variant="button">{t('page.usuariCanviCodis.resultat.title')}</Typography>
            </Divider>
            {finalitzat && (
                <Alert severity={severity} sx={{ flexShrink: 0 }}>
                    <AlertTitle>
                        {t('page.usuariCanviCodis.resultat.resum.titol', { durada: formatDurada(duradaTotal) })}
                    </AlertTitle>
                    {t('page.usuariCanviCodis.resultat.resum.text', {
                        total: linies.length,
                        ok: ok.length,
                        errors,
                        omeses,
                        registres,
                    })}
                </Alert>
            )}
            {finalitzat && ok.length > 0 && (
                <Alert severity="info" sx={{ flexShrink: 0 }}>
                    <Typography variant="body2">{t('page.usuariCanviCodis.resultat.canvi')}</Typography>
                    {unifica && (
                        <Typography variant="body2" sx={{ mt: 0.5 }}>
                            {t('page.usuariCanviCodis.resultat.unificats')}
                        </Typography>
                    )}
                </Alert>
            )}
            {linies.length === 0 ? (
                <Alert severity="info" sx={{ flexShrink: 0 }}>
                    {t('page.usuariCanviCodis.resultat.senseLinies')}
                </Alert>
            ) : (
                <TableContainer
                    ref={taulaRef}
                    sx={{
                        flex: '0 1 auto',
                        minHeight: 160,
                        overflow: 'auto',
                        border: 1,
                        borderColor: 'divider',
                        borderRadius: 1,
                    }}
                >
                    <Table
                        size="small"
                        stickyHeader
                        sx={{ '& tbody tr:nth-of-type(odd)': { backgroundColor: 'action.hover' } }}
                    >
                        <TableHead>
                            <TableRow>
                                <TableCell>{t('page.usuariCanviCodis.resultat.columna.linia')}</TableCell>
                                <TableCell>{t('page.usuariCanviCodis.resultat.columna.canvi')}</TableCell>
                                <TableCell>{t('page.usuariCanviCodis.resultat.columna.resultat')}</TableCell>
                                <TableCell>{t('page.usuariCanviCodis.resultat.columna.durada')}</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {linies.map((linia) => {
                                const { icon, color } = ICONA_ESTAT[linia.estat];
                                return (
                                    <TableRow key={linia.numLinia}>
                                        <TableCell>{linia.numLinia}</TableCell>
                                        <TableCell>
                                            {linia.codiAntic != null ? (
                                                <strong>
                                                    {linia.codiAntic} → {linia.codiNou}
                                                </strong>
                                            ) : (
                                                linia.missatge
                                            )}
                                        </TableCell>
                                        <TableCell>
                                            <Icon
                                                color={color}
                                                fontSize="small"
                                                sx={{ verticalAlign: 'middle', mr: 1 }}
                                            >
                                                {icon}
                                            </Icon>
                                            {linia.estat !== 'OK'
                                                ? t(`page.usuariCanviCodis.resultat.estat.${linia.estat}`)
                                                : (linia.registresModificats ?? 0) === 0
                                                  ? t('page.usuariCanviCodis.resultat.estat.OK_sense')
                                                  : t('page.usuariCanviCodis.resultat.estat.OK', {
                                                        count: linia.registresModificats,
                                                    })}
                                            {linia.estat === 'ERROR' && linia.missatge ? `: ${linia.missatge}` : ''}
                                        </TableCell>
                                        <TableCell>{linia.durada != null ? formatDurada(linia.durada) : ''}</TableCell>
                                    </TableRow>
                                );
                            })}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}
        </Box>
    );
};

export default UsuariCanviCodisResultat;
