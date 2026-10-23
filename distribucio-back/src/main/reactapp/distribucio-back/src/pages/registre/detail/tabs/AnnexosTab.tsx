import { useResourceApiService } from 'reactlib';
import { Box, Grid, Icon, Tooltip } from '@mui/material';
import React from 'react';
import { DetailCardContent, DetailExpandCard } from '../../../../components/CardData.tsx';
import { formatDate } from '../../../../util/dateUtils.ts';
import StyledMuiGrid from '../../../../components/StyledMuiGrid.tsx';
import * as builder from '../../../../util/springFilterUtils';
import Load from '../../../../components/Load.tsx';
import AnnexDetailContent from '../../../annex/actions/AnnexDetailContent.tsx';
import { useTranslation } from 'react-i18next';
import useVisualitzar from '../../../annex/actions/AnnexVisualitzar.tsx';
import { REPORT_DESCARREGAR_IMPRIMIBLE, useDescarregarAnnex } from '../../../annex/AnnexAccions.tsx';
import { icons } from '../../../../util/icons';

const annexPerspectives = ['FIRMES'];

export const AnnexTab = ({ entity }: any) => {
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('registreAnnexResource');
    const [annexos, setAnnexos] = React.useState<any>();

    const refresh = () => {
        apiFind({
            filter: builder.and(builder.eq('registre.id', entity.id), builder.neq('id', `'${entity.justificant?.id}'`)),
            perspectives: annexPerspectives,
            sorts: ['id,asc'],
            unpaged: true,
        }).then((response) => setAnnexos(response.rows));
    };

    React.useEffect(() => {
        if (apiIsReady && entity) {
            refresh();
        }
    }, [apiIsReady, entity]);

    return (
        <Grid container columnSpacing={1} rowSpacing={1}>
            <Load value={annexos}>
                {annexos?.map((annex: any) => (
                    <DetailExpandCard key={annex.id} title={annex.titol}>
                        <AnnexDetailContent annex={annex} />
                    </DetailExpandCard>
                ))}
            </Load>
        </Grid>
    );
};

const annexColumns = [
    { field: 'titol', flex: 1 },
    { field: 'ntiTipusDocument', flex: 1 },
    { field: 'observacions', flex: 1 },
    { field: 'dataCaptura', flex: 1 },
    { field: 'origenCiutadaAdmin', flex: 1 },
    { field: 'ntiElaboracioEstat', flex: 1 },
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
                onRowClick={(params) => handleOpen(params.row)}
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
                                            {formatDate(detall.data) || t('page.annex.firmes.data.nd')}
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
                                    </DetailExpandCard>
                                ))
                            )}
                        </Box>
                    );
                }}
                toolbarHide
                readOnly
            />
            {component}
        </>
    );
};
