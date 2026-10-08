import React from 'react';
import { useTranslation } from 'react-i18next';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Tooltip from '@mui/material/Tooltip';
import Link from '@mui/material/Link';
import Accordion from '@mui/material/Accordion';
import AccordionSummary from '@mui/material/AccordionSummary';
import AccordionDetails from '@mui/material/AccordionDetails';
import { formattedFieldValue, useBaseAppContext, useResourceApiService } from 'reactlib';
import { formatDate } from '../../../util/dateUtils';
import { AnnexArxiuEstatCell } from '../AnnexGrid';
import {
    useDescarregarAnnex,
    REPORT_DESCARREGAR_ORIGINAL,
    REPORT_DESCARREGAR_IMPRIMIBLE,
    REPORT_DESCARREGAR_FIRMA,
} from '../AnnexAccions';
import CampDetall from '../../../components/CampDetall';
import { ROLE_ADMIN, useDistribucioContext } from '../../../components/DistribucioContext';
import { Typography } from '@mui/material';

/**
 * Claus de metadades ENI/NTI (`annex.metaDadesMap`) -&gt; sufix de la clau d'i18n, portat de
 * `registreAnnex.jsp` (bloc `c:forEach`/`c:choose` sobre `metaDadesMap`).
 */
const ENI_METADATA_LABELS: Record<string, string> = {
    'eni:codi_procediment': 'codiProcediment',
    'eni:resolucion': 'resolucion',
    'eni:profundidad_color': 'profundidadColor',
    'cm:title': 'titol',
    'eni:idioma': 'idioma',
    'eni:descripcion': 'descripcio',
    'eni:app_tramite_doc': 'appTramitDoc',
    'eni:organo': 'organ',
    'eni:origen': 'origen',
    'eni:estado_elaboracion': 'estatElaboracio',
    'eni:tipo_doc_ENI': 'tipusDocEni',
    'eni:cod_clasificacion': 'codiClassificacio',
    'eni:csv': 'csv',
    'eni:def_csv': 'defCsv',
    'eni:id': 'id',
    'eni:id_origen': 'idOrigen',
    'eni:fecha_inicio': 'dataInici',
    'eni:nombre_formato': 'nomFormat',
    'eni:extension_formato': 'extensioFormat',
    'eni:tamano_logico': 'midaLogica',
    'eni:termino_punto_acceso': 'termePuntAcces',
    'eni:id_punto_acceso': 'idPuntAcces',
    'eni:esquema_punto_acceso': 'esquemaPuntAcces',
    'eni:soporte': 'suport',
    'eni:loc_archivo_central': 'locArxiuCentral',
    'eni:loc_archivo_general': 'arxiuGeneral',
    'eni:unidades': 'unitats',
    'eni:subtipo_doc': 'subtipusDoc',
    'eni:tipo_asiento_registral': 'tipusAsientoRegistral',
    'eni:codigo_oficina_registro': 'codiOficinaRegistre',
    'eni:fecha_asiento_registral': 'dataAsientoRegistral',
    'eni:numero_asiento_registral': 'numAsientoRegistral',
    'eni:tipoFirma': 'tipusFirma',
    'eni:perfil_firma': 'perfilFirma',
    'eni:fecha_sellado': 'dataSegellat',
    'eni:id_tramite': 'idTramite',
};

/** Estats de {@code validacioFirmaEstat}, veure ValidacioFirmaEnum al backend. */
const VALIDACIO_FIRMA_ICON: Record<string, { icon: string; color?: 'success' | 'error' | 'warning' }> = {
    SENSE_FIRMES: { icon: 'block' },
    FIRMA_VALIDA: { icon: 'edit', color: 'success' },
    FIRMA_INVALIDA: { icon: 'edit', color: 'error' },
    ERROR_VALIDANT: { icon: 'error', color: 'error' },
};

type AnnexDetailContentProps = {
    annex: any;
    onRefresh?: () => void;
};

const GestioDocumental: React.FC<{ annex: any }> = ({ annex }) => {
    const { t } = useTranslation();

    return (
        <CampDetall
            label={t('page.annex.detall.camp.gestioDocumental')}
            value={
                <Box>
                    <Box>
                        {t('page.annex.detall.gestioDocumental.identificador')}: {annex?.gesdocDocumentId}
                    </Box>
                    <Box sx={{ mt: 0.5 }}>
                        {t('page.annex.detall.gestioDocumental.firmes')}:
                        {!annex?.gesdocFirmes?.length ? (
                            ` ${t('page.annex.detall.gestioDocumental.cap')}`
                        ) : (
                            <Table
                                size="small"
                                sx={{
                                    my: 1,
                                    '& td:not(:last-of-type), & th:not(:last-of-type)': {
                                        borderRight: '1px solid rgba(224, 224, 224, 1)',
                                    },
                                }}
                            >
                                <TableBody>
                                    <TableRow>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.tipus')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.perfil')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.fitxer')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.camp.fitxerTipusMime')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.csvRegulacio')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.autofirma')}
                                        </TableCell>
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.gesdocFirmaId')}
                                        </TableCell>
                                    </TableRow>
                                    {annex.gesdocFirmes.map((firma: any, index: number) => (
                                        <TableRow key={index}>
                                            <TableCell>{firma.tipus}</TableCell>
                                            <TableCell>{firma.perfil}</TableCell>
                                            <TableCell>{firma.fitxerNom}</TableCell>
                                            <TableCell>{firma.tipusMime}</TableCell>
                                            <TableCell>{firma.csvRegulacio}</TableCell>
                                            <TableCell>{firma.autofirma ? 'Si' : 'No'}</TableCell>
                                            <TableCell>{firma.gesdocFirmaId}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                        )}
                    </Box>
                </Box>
            }
        />
    );
};

const ACTION_VALIDAR_FIRMES = 'VALIDAR_FIRMES';
const ValidacioFirma: React.FC<{ annex: any; onRefresh?: () => void }> = ({ annex, onRefresh }) => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const { temporalMessageShow } = useBaseAppContext();
    const { artifactAction } = useResourceApiService('registreAnnexResource');
    const [loading, setLoading] = React.useState(false);

    const validacioFirmaIcon = annex?.validacioFirmaEstat ? VALIDACIO_FIRMA_ICON[annex.validacioFirmaEstat] : undefined;

    const validarFirmes = () => {
        setLoading(true);
        artifactAction(annex.id, { code: ACTION_VALIDAR_FIRMES })
            .then((result: any) => {
                onRefresh?.();
                (result.missatges ?? []).forEach((m: any) =>
                    temporalMessageShow(null, t(`page.annex.accio.validarFirmes.${m.key}`), m.severitat)
                );
            })
            .catch((error: any) =>
                temporalMessageShow(t('page.annex.accio.error'), error?.description ?? error?.message, 'error')
            )
            .finally(() => setLoading(false));
    };

    return (
        <CampDetall
            label={t('page.annex.detall.camp.validacioFirmaEstat')}
            value={
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'flex-start',
                        flexWrap: 'nowrap',
                        justifyContent: 'space-between',
                    }}
                >
                    <Box sx={{ alignSelf: 'start' }}>
                        {validacioFirmaIcon && (
                            <Icon fontSize="small" color={validacioFirmaIcon.color} sx={{ mr: 1, fontSize: '16px' }}>
                                {validacioFirmaIcon.icon}
                            </Icon>
                        )}
                        {t(`page.annex.detall.validacioFirmaEstat.${annex?.validacioFirmaEstat ?? 'NO_VALIDAT'}`)}
                        {annex?.validacioFirmaError ? `: ${annex.validacioFirmaError}` : ''}
                    </Box>
                    {isAdmin && (
                        <Button
                            size="small"
                            variant="outlined"
                            startIcon={<Icon>sync</Icon>}
                            onClick={validarFirmes}
                            loading={loading}
                            loadingPosition="start"
                            sx={{ alignSelf: 'end' }}
                        >
                            {t(
                                annex?.arxiuEstat === 'DEFINITIU'
                                    ? 'page.annex.detall.action.validarFirmes'
                                    : 'page.annex.detall.action.validarICustodiar'
                            )}
                        </Button>
                    )}
                </Box>
            }
        />
    );
};

const Fitxer: React.FC<{ annex: any }> = ({ annex }) => {
    const { t } = useTranslation();
    const [loadingImprimible, setLoadingImprimible] = React.useState(false);
    const [loadingOriginal, setLoadingOriginal] = React.useState(false);

    const descarregar = useDescarregarAnnex();

    return (
        <CampDetall
            label={t('page.annex.detall.camp.fitxer')}
            value={
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        flexWrap: 'wrap',
                        gap: 1,
                    }}
                >
                    <span>
                        {annex?.fitxerNom} ({annex?.fitxerTamany} bytes)
                    </span>
                    <Box>
                        <Button
                            size="small"
                            variant="outlined"
                            startIcon={<Icon>download</Icon>}
                            loading={loadingOriginal}
                            loadingPosition="start"
                            onClick={() =>
                                descarregar(annex?.id, REPORT_DESCARREGAR_ORIGINAL, undefined, setLoadingOriginal)
                            }
                        >
                            {t('page.annex.accio.descarregarOriginal')}
                        </Button>
                        {annex?.potGenerarVersioImprimible && (
                            <Button
                                size="small"
                                variant="outlined"
                                startIcon={<Icon>print</Icon>}
                                loading={loadingImprimible}
                                loadingPosition="start"
                                onClick={() =>
                                    descarregar(
                                        annex?.id,
                                        REPORT_DESCARREGAR_IMPRIMIBLE,
                                        undefined,
                                        setLoadingImprimible
                                    )
                                }
                            >
                                {t('page.annex.accio.descarregarImprimible')}
                            </Button>
                        )}
                    </Box>
                </Box>
            }
        />
    );
};

/** Bloc "Firmes": Les columnes "Tipus firma"/"Perfil firma" només es mostren a ROLE_ADMIN. */
const Firmes: React.FC<{ annex: any }> = ({ annex }) => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const descarregar = useDescarregarAnnex();

    if (!annex?.ambFirma) {
        return null;
    }

    const files: { firma: any; detall: any; firmaIndex: number }[] = [];
    (annex?.firmes ?? []).forEach((firma: any, firmaIndex: number) => {
        (firma.detalls?.length ? firma.detalls : [undefined]).forEach((detall: any) => {
            files.push({ firma, detall, firmaIndex });
        });
    });

    return (
        <TableRow>
            <TableCell colSpan={2} sx={{ p: 0, borderBottom: 'none' }}>
                <Accordion disableGutters elevation={1} sx={{ px: 1, '&:before': { display: 'none' } }}>
                    <AccordionSummary expandIcon={<Icon>expand_more</Icon>} sx={{ px: 0, minHeight: 0 }}>
                        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                            <Icon fontSize="small" sx={{ fontSize: '16px' }}>
                                verified
                            </Icon>
                            <Typography>{t('page.annex.detall.firmes.mostrar')}</Typography>
                        </Box>
                    </AccordionSummary>
                    <AccordionDetails sx={{ p: 0 }}>
                        <Table
                            size="small"
                            sx={{
                                '& td:not(:last-of-type), & th:not(:last-of-type)': {
                                    borderRight: '1px solid rgba(224, 224, 224, 1)',
                                },
                            }}
                        >
                            <TableBody>
                                <TableRow>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.camp.firma')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.firmes.column.nom')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.firmes.column.nif')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.firmes.column.data')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.firmes.column.emissor')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.camp.fitxer')}
                                    </TableCell>
                                    <TableCell sx={{ fontWeight: 'bold' }}>
                                        {t('page.annex.detall.gestioDocumental.column.csvRegulacio')}
                                    </TableCell>
                                    {isAdmin && (
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.tipus')}
                                        </TableCell>
                                    )}
                                    {isAdmin && (
                                        <TableCell sx={{ fontWeight: 'bold' }}>
                                            {t('page.annex.detall.gestioDocumental.column.perfil')}
                                        </TableCell>
                                    )}
                                </TableRow>
                                {files.map(({ firma, detall, firmaIndex }, index) => {
                                    const ambFitxer = !['PADES', 'CADES_ATT', 'XADES_ENV', 'XADES_DET'].includes(
                                        firma.tipus
                                    );
                                    return (
                                        <TableRow key={index}>
                                            <TableCell>
                                                {t('page.annex.detall.camp.firma')} {index + 1}
                                                {firma.autofirma && (
                                                    <Tooltip title={t('page.annex.detall.firmes.autofirma.info')}>
                                                        <Icon
                                                            fontSize="small"
                                                            sx={{ verticalAlign: 'middle', ml: 0.5, fontSize: '16px' }}
                                                        >
                                                            info
                                                        </Icon>
                                                    </Tooltip>
                                                )}
                                            </TableCell>
                                            <TableCell>{detall?.responsableNom}</TableCell>
                                            <TableCell>{detall?.responsableNif}</TableCell>
                                            <TableCell>
                                                {detall?.data
                                                    ? formatDate(detall.data, 'DD/MM/YYYY HH:mm:ss')
                                                    : t('page.annex.detall.firmes.data.nd')}
                                            </TableCell>
                                            <TableCell>{detall?.emissorCertificat}</TableCell>
                                            <TableCell>
                                                {ambFitxer && (
                                                    <Box
                                                        sx={{
                                                            display: 'flex',
                                                            alignItems: 'center',
                                                            justifyContent: 'space-between',
                                                            gap: 1,
                                                        }}
                                                    >
                                                        <span>{firma.fitxerNom}</span>
                                                        <Tooltip title={t('page.annex.detall.action.descarregarFirma')}>
                                                            <IconButton
                                                                size="small"
                                                                onClick={() =>
                                                                    descarregar(annex?.id, REPORT_DESCARREGAR_FIRMA, {
                                                                        firmaIndex,
                                                                    })
                                                                }
                                                            >
                                                                <Icon fontSize="small">download</Icon>
                                                            </IconButton>
                                                        </Tooltip>
                                                    </Box>
                                                )}
                                            </TableCell>
                                            <TableCell>{firma.csvRegulacio}</TableCell>
                                            {isAdmin && (
                                                <TableCell>
                                                    {firma.tipus
                                                        ? t('page.annex.detall.firmes.tipus.' + firma.tipus)
                                                        : ''}
                                                </TableCell>
                                            )}
                                            {isAdmin && <TableCell>{firma.perfil}</TableCell>}
                                        </TableRow>
                                    );
                                })}
                            </TableBody>
                        </Table>
                    </AccordionDetails>
                </Accordion>
            </TableCell>
        </TableRow>
    );
};

export const AnnexDetailContent: React.FC<AnnexDetailContentProps> = ({ annex, onRefresh }) => {
    const { t } = useTranslation();
    const { currentFields } = useResourceApiService('registreAnnexResource');

    const metaDadesEntries: [string, string][] = annex?.metaDadesMap ? Object.entries(annex.metaDadesMap) : [];

    const arxiuEstatField = currentFields?.find((f: any) => f.name === 'arxiuEstat');
    const arxiuEstatFormatted = formattedFieldValue(annex?.arxiuEstat, arxiuEstatField);

    return (
        <Table size="small">
            <TableBody>
                <CampDetall
                    label={t('page.annex.detall.camp.dataCaptura')}
                    value={annex?.dataCaptura ? formatDate(annex.dataCaptura, 'DD/MM/YYYY HH:mm:ss') : undefined}
                />
                <CampDetall label={t('page.annex.detall.camp.origen')} value={annex?.origenCiutadaAdmin} />
                <CampDetall
                    label={t('page.annex.detall.camp.ntiElaboracioEstat')}
                    value={
                        annex?.ntiElaboracioEstat
                            ? t('page.annex.detall.ntiElaboracioEstat.' + annex.ntiElaboracioEstat)
                            : undefined
                    }
                />
                <CampDetall
                    label={t('page.annex.detall.camp.ntiTipusDocument')}
                    value={
                        annex?.ntiTipusDocument
                            ? t('page.annex.detall.ntiTipusDocument.' + annex.ntiTipusDocument)
                            : undefined
                    }
                />
                <CampDetall
                    label={t('page.annex.detall.camp.sicresTipusDocument')}
                    value={
                        annex?.sicresTipusDocument
                            ? t('page.annex.detall.sicresTipusDocument.' + annex.sicresTipusDocument)
                            : undefined
                    }
                />
                {annex?.localitzacio && (
                    <CampDetall label={t('page.annex.detall.camp.localitzacio')} value={annex.localitzacio} />
                )}
                {annex?.observacions && (
                    <CampDetall label={t('page.annex.detall.camp.observacions')} value={annex.observacions} />
                )}
                <CampDetall
                    label={t('page.annex.detall.camp.fitxerArxiuUuid')}
                    value={
                        annex?.fitxerArxiuUuid ?? (
                            <Tooltip title={t('page.annex.grid.arxiuEstat.buitAvis')}>
                                <Icon fontSize="small" color="warning" sx={{ verticalAlign: 'middle' }}>
                                    warning
                                </Icon>
                            </Tooltip>
                        )
                    }
                />
                {annex?.firmaCsv && (
                    <CampDetall
                        label={t('page.annex.detall.camp.firmaCsv')}
                        value={
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                {annex.firmaCsv}
                                {annex.concsvUrl && (
                                    <Tooltip title={t('page.annex.detall.camp.concsvUrl')}>
                                        <Link href={annex.concsvUrl} target="_blank" rel="noopener">
                                            <Icon fontSize="small" sx={{ verticalAlign: 'middle' }}>
                                                open_in_new
                                            </Icon>
                                        </Link>
                                    </Tooltip>
                                )}
                            </Box>
                        }
                    />
                )}
                {metaDadesEntries.map(([key, value]) => (
                    <CampDetall
                        key={key}
                        label={
                            ENI_METADATA_LABELS[key]
                                ? t('page.annex.detall.metaData.' + ENI_METADATA_LABELS[key])
                                : key.toUpperCase()
                        }
                        value={value}
                    />
                ))}
                <CampDetall label={t('page.annex.detall.camp.fitxerTipusMime')} value={annex?.fitxerTipusMime} />
                <ValidacioFirma annex={annex} onRefresh={onRefresh} />

                <CampDetall
                    label={t('page.annex.detall.camp.arxiuEstat')}
                    value={
                        annex ? (
                            <AnnexArxiuEstatCell params={{ row: annex, formattedValue: arxiuEstatFormatted }} />
                        ) : undefined
                    }
                />
                {annex?.arxiuEstat === 'ESBORRANY' && <GestioDocumental annex={annex} />}
                <Fitxer annex={annex} />
                <Firmes annex={annex} />
            </TableBody>
        </Table>
    );
};

export default AnnexDetailContent;
