import { useResourceApiService } from 'reactlib';
import {Box, Grid, Icon, Tooltip, Typography} from '@mui/material';
import React, { useMemo } from 'react';
import { DetailCardContent, DetailExpandCard } from '../../../../components/CardData.tsx';
import { formatDate } from '../../../../util/dateUtils.ts';
import StyledMuiGrid, {ToolbarButton} from '../../../../components/StyledMuiGrid.tsx';
import * as builder from '../../../../util/springFilterUtils';
import Load from '../../../../components/Load.tsx';
import AnnexDetailContent from '../../../annex/actions/AnnexDetailContent.tsx';
import { useTranslation } from 'react-i18next';
import useVisualitzar from '../../../annex/actions/AnnexVisualitzar.tsx';
import {
    REPORT_DESCARREGAR_FIRMA,
    REPORT_DESCARREGAR_IMPRIMIBLE,
    useDescarregarAnnex
} from '../../../annex/AnnexAccions.tsx';
import { icons } from '../../../../util/icons';
import Alert from "@mui/material/Alert";
import {ROLE_ADMIN, ROLE_USER, useDistribucioContext} from "../../../../components/DistribucioContext.ts";
import {useActions} from "../RegistreActions.tsx";
import IconButton from "@mui/material/IconButton";

const annexPerspectives = ['FIRMES'];

export const AnnexTab = ({ entity }: any) => {
    const {t} = useTranslation()
    const {currentRole} = useDistribucioContext()
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('registreAnnexResource');
    const [annexos, setAnnexos] = React.useState<any>();

    const isAdmin = currentRole == ROLE_ADMIN;
    const isUser = currentRole == ROLE_USER;

    const refresh = () => {
        apiFind({
            filter: builder.and(builder.eq('registre.id', entity.id), builder.neq('id', `'${entity.justificant?.id}'`)),
            perspectives: annexPerspectives,
            sorts: ['id,asc'],
            unpaged: true,
        }).then((response) => setAnnexos(response.rows));
    };

    const {processarAnnexos, reenviarBackoffice} = useActions(refresh)

    React.useEffect(() => {
        if (apiIsReady && entity) {
            refresh();
        }
    }, [apiIsReady, entity]);

    const numNoGordat = useMemo(() => {
        return annexos?.filter((a:any) => !a?.fitxerArxiuUuid)?.length
    },[annexos])

    const numEsborrany = useMemo(() => {
        return annexos?.filter((a:any) => a?.arxiuEstat === 'ESBORRANY')?.length
    },[annexos])

    const numFirmaInvalida = useMemo(() => {
        return annexos?.filter((a:any) => a?.validacioFirmaEstat === 'FIRMA_INVALIDA' || a?.validacioFirmaEstat === 'ERROR_VALIDANT')?.length
    },[annexos])

    return (
        <Grid container columnSpacing={1} rowSpacing={1}>
            {numEsborrany > 0 && <Grid size={12}>
                <Alert icon={<Icon>{icons.error}</Icon>}
                       severity={'warning'}>{t('page.annex.detall.alert.esborranys', {num: numEsborrany})}</Alert>
            </Grid>}
            {numFirmaInvalida > 0 && <Grid size={12}>
                <Alert icon={<Icon>edit_square</Icon>}
                       severity={'warning'}>{t('page.annex.detall.alert.firmaInvalida', {num: numFirmaInvalida})}</Alert>
            </Grid>}
            {numNoGordat > 0 && <Grid size={12}>
                <Alert severity={'warning'} action={
                    <ToolbarButton icon={icons.refresca} onClick={() => processarAnnexos(entity.id)} hidden={!isAdmin}>
                        {t('page.registre.accio.processarAnnexos.label')}
                    </ToolbarButton>
                }>{t('page.annex.detall.alert.noGodatsAxiu', {num: numNoGordat})}</Alert>
            </Grid>}

            {(isAdmin || (isUser && entity.potModificar))
                && (entity.procesEstat == 'BACK_PENDENT' && entity.procesError == null && entity.procesIntents >= 0)
                && <Grid size={12}>
                    <ToolbarButton icon={icons.refresca} onClick={() => reenviarBackoffice(entity.id)}>
                        {t('page.registre.accio.reenviarBackoffice.label')}
                    </ToolbarButton>
                </Grid>}

            <Load value={annexos}>
                {annexos?.map((annex: any) => (
                    <DetailExpandCard
                        key={annex.id}
                        header={<Box display={'flex'} alignItems={'center'} gap={1}>
                            <AnnexIcon fitxerExtension={annex?.fitxerExtension}/>

                            <Typography mt={0.5} variant={'overline'} sx={{fontWeight: 600, color: 'primary.dark'}}>
                                {annex.titol}
                            </Typography>

                            {!annex?.fitxerArxiuUuid && (
                                <Tooltip title={t('page.annex.grid.arxiuEstat.buitAvis')}>
                                    <Icon fontSize="small" color="warning" sx={{verticalAlign: 'middle'}}>
                                        {icons.advertencia}
                                    </Icon>
                                </Tooltip>
                            )}
                            {(annex?.validacioFirmaEstat === 'FIRMA_INVALIDA' || annex?.validacioFirmaEstat === 'ERROR_VALIDANT') && (
                                <Tooltip title={t('page.annex.grid.arxiuEstat.firmaInvalida')}>
                                    <Icon fontSize="small" color="warning" sx={{verticalAlign: 'middle'}}>
                                        edit_square
                                    </Icon>
                                </Tooltip>
                            )}
                            {annex?.arxiuEstat === 'ESBORRANY' && (
                                <Tooltip title={t('page.annex.grid.arxiuEstat.esborrany')}>
                                    <Icon fontSize="small" color="warning" sx={{verticalAlign: 'middle'}}>
                                        {icons.error}
                                    </Icon>
                                </Tooltip>
                            )}
                        </Box>}
                    >
                        <AnnexDetailContent annex={annex}/>
                    </DetailExpandCard>
                ))}
            </Load>
        </Grid>
    );
};

const displayOpacity = (params: any) => {
    return <Box sx={{opacity: params.row?.fitxerExtension == 'pdf' ? 1 : 0.5}}>
        {params?.formattedValue}
    </Box>
}

const AnnexIcon = ({fitxerExtension, ...other}:any) => {
    const icon = fitxerExtension == 'pdf' ? 'picture_as_pdf'
        : fitxerExtension == 'xml' ?'code_xml'
        : fitxerExtension == 'jpeg' ?'image'
        // : fitxerExtension == 'msword' ?'description'
        // : fitxerExtension == 'xsig' ?'description'
        : 'description'

    return <Icon {...other}>{icon}</Icon>
}

const annexColumns = [
    {
        field: 'titol', flex: 1,
        renderCell: (params: any) => {
            return <Box sx={{
                display: 'flex',
                justifyContent: 'center',
                gap: 1,
                opacity: params.row?.fitxerExtension == 'pdf' ? 1 : 0.5
            }}>
                <AnnexIcon fitxerExtension={params.row?.fitxerExtension}/>
                {params?.formattedValue}
            </Box>
        }
    },
    { field: 'ntiTipusDocument', flex: 1, renderCell: displayOpacity },
    { field: 'observacions', flex: 1, renderCell: displayOpacity },
    { field: 'dataCaptura', flex: 1, renderCell: displayOpacity },
    { field: 'origenCiutadaAdmin', flex: 1, renderCell: displayOpacity },
    { field: 'ntiElaboracioEstat', flex: 1, renderCell: displayOpacity },
];
const annexSortModel: any = [{ field: 'id', sort: 'asc' }];
export const AnnexGrid = ({ entity }: any) => {
    const { t } = useTranslation();

    const descarregar = useDescarregarAnnex();

    const { handleOpen, component } = useVisualitzar();

    const actions = [
        {
            label: t('page.annex.accio.descarregarImprimible'),
            icon: icons.descarrega,
            report: REPORT_DESCARREGAR_IMPRIMIBLE,
            showInMenu: false,
            onClick: (id: any) => descarregar(id, REPORT_DESCARREGAR_IMPRIMIBLE),
        },
    ];

    return (
        <>
            <StyledMuiGrid
                resourceName={'registreAnnexResource'}
                columns={annexColumns}
                filter={builder.and(
                    builder.eq('registre.id', entity.id),
                    builder.neq('id', `'${entity.justificant?.id}'`)
                )}
                perspectives={annexPerspectives}
                fixedSortModel={annexSortModel}
                rowAdditionalActions={actions}
                onRowClick={(params) => {
                    if (params.row?.fitxerExtension == 'pdf') {
                        handleOpen(params.row)
                    }
                }}
                getDetailPanelHeight={() => 'auto'}
                getDetailPanelContent={(params) => {
                    let index = 0;
                    return (
                        <Box p={1}>
                            {params.row.firmes?.map((firma: any, firmaIndex: number) =>
                                firma?.detalls?.map((detall: any, detallIndex: number) => (
                                    <DetailExpandCard
                                        key={`${firmaIndex}-${detallIndex}`}
                                        title={t('page.annex.detall.camp.firma')}
                                        expanded
                                    >
                                        <DetailCardContent size={3} title={t('page.annex.detall.camp.firma')}>
                                            {t('page.annex.detall.camp.firma')} {++index}
                                            {firma.autofirma && (
                                                <Box display={'flex'} alignItems={'center'}>
                                                    ({t('page.annex.detall.gestioDocumental.column.autofirma')}
                                                    <Tooltip title={t('page.annex.detall.firmes.autofirma.info')}>
                                                        <Icon fontSize={'small'}>{icons.detall}</Icon>
                                                    </Tooltip>
                                                    )
                                                </Box>
                                            )}
                                        </DetailCardContent>
                                        <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.nom')}>
                                            {detall.responsableNom}
                                        </DetailCardContent>
                                        <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.nif')}>
                                            {detall.responsableNif}
                                        </DetailCardContent>
                                        <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.data')}>
                                            {formatDate(detall.data) || t('page.annex.detall.firmes.data.nd')}
                                        </DetailCardContent>
                                        {/*<DetailCardContent title={"Emisor"}>{detall.emissorCertificat}</DetailCardContent>*/}
                                        {firma.tipus != 'PADES' &&
                                            firma.tipus != 'CADES_ATT' &&
                                            firma.tipus != 'XADES_ENV' &&
                                            firma.tipus != 'XADES_DET' && (
                                                <DetailCardContent
                                                    size={6}
                                                    title={t('page.annex.detall.firmes.column.fitxerNom')}
                                                >
                                                    {firma.fitxerNom}
                                                </DetailCardContent>
                                            )}
                                        <DetailCardContent
                                            size={6}
                                            title={t('page.annex.detall.firmes.column.csvRegulacio')}
                                            hidden={!firma.csvRegulacio}
                                        >
                                            {firma.csvRegulacio}
                                        </DetailCardContent>
                                        {/*<DetailCardContent title={"Tipus firma"}>{firma.tipus}</DetailCardContent>*/}
                                        {/*<DetailCardContent title={"Perfil"}>{firma.perfil}</DetailCardContent>*/}

                                        {!['PADES', 'CADES_ATT', 'XADES_ENV', 'XADES_DET'].includes(firma.tipus) &&
                                            <Tooltip title={t('page.annex.detall.action.descarregarFirma')}>
                                                <IconButton
                                                    size="small"
                                                    onClick={() =>
                                                        descarregar(params.row?.id, REPORT_DESCARREGAR_FIRMA, {
                                                            firmaIndex,
                                                        })
                                                    }
                                                >
                                                    <Icon fontSize="small">{icons.descarrega}</Icon>
                                                </IconButton>
                                            </Tooltip>
                                        }
                                    </DetailExpandCard>
                                ))
                            )}
                        </Box>
                    );
                }}
                toolbarHide
                autoHeight
                readOnly
            />
            {component}
        </>
    );
};
