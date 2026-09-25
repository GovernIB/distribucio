import {useBaseAppContext, useDetailContext, useMuiContentDialog, MuiDetail, useResourceApiService} from "reactlib";
import {Badge, Box, Grid, Icon, IconButton, Tooltip} from "@mui/material";
import TabComponent from "../../../components/TabComponent.tsx";
import React, {useMemo} from "react";
import {useCommentDialog} from "../../CommentDialog.tsx";
import {DetailCard, DetailCardContent, DetailExpandCard, DetailField} from "../../../components/CardData.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import * as builder from '../../../util/springFilterUtils';
import Load from "../../../components/Load.tsx";
import AnnexDetailContent from "../../annex/actions/AnnexDetailContent.tsx";
import {useTranslation} from "react-i18next";
import {ErrorArea} from "../../../components/ErrorArea.tsx";
import {useConfig} from "../../../components/ConfigProvider.tsx";
import useVisualitzar from "../../annex/actions/AnnexVisualitzar.tsx";

const AnnexTab = ({entity}:any) => {
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('registreAnnexResource');
    const [annexos, setAnnexos] = React.useState<any>();

    const refresh = () => {
        apiFind({ filter: builder.and(
                builder.eq('registre.id', entity.id),
                builder.neq('id', `'${entity.justificant?.id}'`),
            ),perspectives: annexPerspectives, sorts: ['id,asc'], unpaged: true })
            .then((response => setAnnexos(response.rows)));
    };

    React.useEffect(() => {
        if (apiIsReady && entity) {
            refresh();
        }
    }, [apiIsReady, entity]);

    return <Grid container columnSpacing={1} rowSpacing={1}>
        <Load value={annexos}>
            {annexos?.map((annex:any) => <>
                <DetailExpandCard key={annex.id} title={annex.titol} headerProps={{backgroundColor: 'greyBackground'}}>
                    <AnnexDetailContent annex={annex}/>
                </DetailExpandCard>
            </>)}
        </Load>
    </Grid>
}

const annexColumns = [
    { field: 'titol', flex: 1 },
    { field: 'ntiTipusDocument', flex: 1 },
    { field: 'observacions', flex: 1 },
    { field: 'dataCaptura', flex: 1 },
    { field: 'origenCiutadaAdmin', flex: 1 },
    { field: 'ntiElaboracioEstat', flex: 1 },
]
const annexPerspectives = ['FIRMES']
const annexSortModel:any = [{ field: 'id', sort: 'asc' }]
const AnnexGrid = ({entity}:any) => {
    const { t } = useTranslation();

    const {handleOpen, component} = useVisualitzar()

    return (<>
        <StyledMuiGrid
            resourceName={'registreAnnexResource'}
            columns={annexColumns}
            filter={builder.and(
                builder.eq('registre.id', entity.id),
                builder.neq('id', `'${entity.justificant?.id}'`),
            )}
            perspectives={annexPerspectives}
            fixedSortModel={annexSortModel}

            onRowClick={(params) => handleOpen(params.row)}

            getDetailPanelHeight={() => 'auto'}
            getDetailPanelContent={(params) => {
                let index = 0;
                return (
                    <Box p={1}>
                        {params.row.firmes?.map((firma: any) => <>
                            {firma?.detalls?.map((detall: any) =>
                                <DetailExpandCard title={t('page.annex.detall.camp.firma')} variant={'body1'}
                                                  headerProps={{backgroundColor: 'greyBackground'}} expanded>
                                    <DetailCardContent size={3} title={t('page.annex.detall.camp.firma')}>
                                        {t('page.annex.detall.camp.firma')} {++index}
                                        {firma.autofirma && <Box display={'flex'} alignItems={'center'}>
                                            ({t('page.annex.detall.gestioDocumental.column.autofirma')}
                                            <Tooltip title={t('page.annex.detall.firmes.autofirma.info')}>
                                                <Icon fontSize={'small'}>info</Icon>
                                            </Tooltip>)
                                        </Box>}
                                    </DetailCardContent>
                                    <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.nom')}>{detall.responsableNom}</DetailCardContent>
                                    <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.nif')}>{detall.responsableNif}</DetailCardContent>
                                    <DetailCardContent size={3} title={t('page.annex.detall.firmes.column.data')}>{formatDate(detall.data) || t('page.annex.firmes.data.nd')}</DetailCardContent>
                                    {/*<DetailCardContent title={"Emisor"}>{detall.emissorCertificat}</DetailCardContent>*/}
                                    {firma.tipus != 'PADES' && firma.tipus != 'CADES_ATT' && firma.tipus != 'XADES_ENV' && firma.tipus !='XADES_DET'
                                        && <DetailCardContent size={6} title={t('page.annex.detall.firmes.column.fitxerNom')}>{firma.fitxerNom}</DetailCardContent>}
                                    <DetailCardContent size={6} title={t('page.annex.detall.firmes.column.csvRegulacio')} hidden={!firma.csvRegulacio}>{firma.csvRegulacio}</DetailCardContent>
                                    {/*<DetailCardContent title={"Tipus firma"}>{firma.tipus}</DetailCardContent>*/}
                                    {/*<DetailCardContent title={"Perfil"}>{firma.perfil}</DetailCardContent>*/}
                                </DetailExpandCard>
                            )}
                        </>)}
                    </Box>
                )
            }}
            toolbarHide
            readOnly
        />
        {component}
    </>)
}

const InteressatDetail = () => {
    const { t } = useBaseAppContext();
    const {data} = useDetailContext()
    return (<>
        <DetailCard>
            <DetailField name={"pais"} size={6} formatterValue={(_v:any, data:any) => <>{data.pais} ({data.paisCodi})</>} inline/>
            <DetailField name={"email"} size={6} inline/>
            <DetailField name={"provincia"} size={6} formatterValue={(_v:any, data:any) => <>{data.provincia} ({data.provinciaCodi})</>} inline/>
            <DetailField name={"telefon"} size={6} inline/>
            <DetailField name={"municipi"} size={6} formatterValue={(_v:any, data:any) => <>{data.municipi} ({data.municipiCodi})</>} inline/>
            <DetailField name={"emailHabilitat"} size={6} inline/>
            <DetailField name={"adresa"} size={6} inline/>
            <DetailField name={"canalPreferent"} size={6} inline/>
            <DetailField name={"codiPostal"} size={6} inline/>
            <DetailField name={"observacions"} size={6} inline/>
            <DetailField name={"codiDire"} size={6} inline/>
        </DetailCard>

        {data?.representant &&
            <DetailExpandCard title={t('component.RegistreDetail.titles.representant')} headerProps={{backgroundColor: 'greyBackground'}}>
                <MuiDetail
                    id={data?.representant?.id}
                    resourceName={'registreInteressatResource'}
                    hiddenToolbar
                    componentProps={{ sx: { mt: 0 } }}
                >
                    <Grid container>
                        <DetailField name={"tipus"} size={4}/>
                        <DetailField name={"documentTipus"}
                                     formatterValue={(formattedValue:any, data:any) => <>
                                         {formattedValue}: {data.documentNum}</>}
                                     size={4}/>
                        <DetailField name={"nomComplet"} size={4}/>
                        <InteressatDetail/>
                    </Grid>
                </MuiDetail>
            </DetailExpandCard>}
    </>);
}

const interessatColumns = [
    { field: 'tipus', flex: 1 },
    { field: 'documentTipus', flex: 1,
        renderCell: (params:any) => <>
            {params.formattedValue}: {params.row.documentNum}
        </>
    },
    { field: 'nomComplet', flex: 1},
]
const InteressatsGrid = ({id}:any) => {
    return (<>
        <StyledMuiGrid
            resourceName={'registreInteressatResource'}
            columns={interessatColumns}
            filter={builder.and(
                builder.eq('registre.id', id),
                builder.eq('representat', null),
            )}

            getDetailPanelHeight={() => 'auto'}
            getDetailPanelContent={(params) => (
                <MuiDetail
                    id={params?.id}
                    resourceName={'registreInteressatResource'}
                    hiddenToolbar
                    componentProps={{ sx: { mt: 0, p:1 } }}
                >
                    <Grid container columnSpacing={1} rowSpacing={1}>
                        <InteressatDetail/>
                    </Grid>
                </MuiDetail>
            )}
            toolbarHide
            autoHeight
            readOnly
        />
    </>)
}

const ProcessBack = ({entity}:any) => {
    const { t } = useBaseAppContext();

    return <Grid container columnSpacing={1} rowSpacing={1}>
        {(entity.procesEstat == 'BACK_COMUNICADA' || entity.procesEstat == 'BACK_REBUTJADA' || entity.procesEstat == 'BACK_ERROR') && <>
            <Grid size={12} sx={{ textAlign: 'end' }}>
                <ToolbarButton icon={'refresh'}>Reintentar enviament al backoffice</ToolbarButton>
            </Grid>
        </>}

        <DetailCard>
            <DetailField name={"procesEstat"} inline/>
            <DetailField name={"backCodi"} inline/>
            <DetailField name={"backPendentData"} inline>{formatDate(entity.backPendentData)}</DetailField>
            <DetailField name={"backRebudaData"} inline>{formatDate(entity.backRebudaData)}</DetailField>

            {entity.procesEstat == 'BACK_PROCESSADA' && <DetailField name={"backProcesRebutjErrorData"}
                             title={t('page.registre.grid.backProcesData')}
                             inline>{formatDate(entity.backProcesRebutjErrorData)}</DetailField>}

            {entity.procesEstat == 'BACK_REBUTJADA' && <DetailField name={"backProcesRebutjErrorData"}
                             title={t('page.registre.grid.backRebutjData')}
                             inline>{formatDate(entity.backProcesRebutjErrorData)}</DetailField>}

            {entity.procesEstat == 'BACK_ERROR' && <DetailField name={"backProcesRebutjErrorData"}
                             title={t('page.registre.grid.backErrorData')}
                             inline>{formatDate(entity.backProcesRebutjErrorData)}</DetailField>}
        </DetailCard>

        <Grid size={12}>
            {entity.procesError &&
                <ErrorArea sx={{backgroundColor: 'customBackground', fontSize: 12}}>{entity.procesError}</ErrorArea>}
            {entity.backObservacions &&
                <ErrorArea sx={{backgroundColor: 'customBackground', fontSize: 12}}>{entity.backObservacions}</ErrorArea>}
        </Grid>
    </Grid>
}

// const dadesColumns = [
//     { field: 'valor', flex: 1 },
// ]
const metaDadesColumns = [
    { field: 'nom', flex: 1 },
]
const metaDadesSortModel:any = [{ field: 'id', sort: 'asc' }]
// L'entitat només la necessita el panell de detall comentat: quan es reactivi, cal tornar a
// desestructurar-la ({entity}:any) i descomentar dadesColumns.
const MetaDadesGrid = (_props:any) => {
    return (<>
        <StyledMuiGrid
            resourceName={'metaDadaResource'}
            columns={metaDadesColumns}
            sortModel={metaDadesSortModel}

            // getDetailPanelHeight={() => 'auto'}
            // getDetailPanelContent={(params) => (
            //     <Grid container columnSpacing={1} rowSpacing={1} p={1}>
            //         <StyledMuiGrid
            //             resourceName={'dadaResource'}
            //             columns={dadesColumns}
            //
            //             filter={builder.and(
            //                 builder.eq('registre.id', `'${entity.id}'`),
            //                 builder.eq('metaDada.id', `'${params.id}'`),
            //             )}
            //             sortModel={metaDadesSortModel}
            //
            //             toolbarHideRefresh
            //             autoHeight
            //         />
            //     </Grid>
            // )}

            toolbarHide
            autoHeight
            readOnly
        />
    </>)
}

const copiesColumns = (t:any, id:any) => [
    { field: 'numero', flex: 1 },
    { field: 'darrerMovimentResource.createdDate', flex: 1, headerName: t('page.registre.grid.darrerMoviment.createdDate'),
        renderCell: (params:any) => formatDate(params.formattedValue)
    },
    { field: 'procesEstat', flex: 1 },
    { field: 'darrerMovimentResource.createdBy', flex: 1, headerName: t('page.registre.grid.darrerMoviment.createdBy'),
        renderCell: (params:any) => <>
            {params.row.procesEstat == 'BUSTIA_PROCESSADA' && params.formattedValue}
        </>
    },
    { field: 'pare', flex: 2,
        renderCell: (params:any) => <Tooltip title={` / ${params.row.unitatAdministrativaDescripcio} / ${params.formattedValue}`} >
            <Box>/ <Icon>account_tree</Icon> {params.row.unitatAdministrativaDescripcio} / <Icon>inbox</Icon> {params.formattedValue}</Box>
        </Tooltip>
    },
    { field: 'numeroCopia', flex: 0.5,
        renderCell: (params:any) => <>
            {params.row.numeroCopia == 0 ?t('component.RegistreDetail.titles.original') :params.formattedValue}
        </>
    },
    { field: 'id', flex: 0.5, headerName: t('page.registre.grid.isCopia'),
        renderCell: (params:any) => <>
            {params.id == id
                ? <IconButton disabled><Icon color={"success"} >check_circle</Icon></IconButton>
                : <IconButton><Icon>open_in_new</Icon></IconButton>
            }
        </>
    },
]
const copiesPerspectives = ['DARRER_MOVIMENT']
const Copies = ({entity}:any) => {
    const { t } = useBaseAppContext();
    return <>
        <StyledMuiGrid
            resourceName="registreResource"
            columns={copiesColumns(t, entity.id)}
            filter={builder.eq("numero", `'${entity.numero}'`)}
            perspectives={copiesPerspectives}
            toolbarHide
            readOnly
        />
    </>
}

const ArxiuDetall = ({entity}:any) => {
    const { t } = useBaseAppContext();
    return <Load value={entity}>
        <Grid container columnSpacing={1} rowSpacing={1}>
            <DetailCard>
                <DetailCardContent titleSize={4} textSize={8} title={t('page.registre.arxiu.identificador')}>{entity.identificador}</DetailCardContent>
                <DetailCardContent titleSize={4} textSize={8} title={t('page.registre.arxiu.nom')}>{entity.nom}</DetailCardContent>
                <DetailCardContent titleSize={4} textSize={8} title={t('page.registre.arxiu.serieDocumental')}>{entity.serieDocumental}</DetailCardContent>
            </DetailCard>
            <DetailCard>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniVersio')}>{entity.eniVersio}</DetailCardContent>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniIdentificador')}>{entity.eniIdentificador}</DetailCardContent>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniOrgans')}>{entity.eniOrgans}</DetailCardContent>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniDataObertura')}>{formatDate(entity.eniDataObertura)}</DetailCardContent>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniClassificacio')}>{entity.eniClassificacio}</DetailCardContent>
                <DetailCardContent size={6} title={t('page.registre.arxiu.eniEstat')}>{entity.eniEstat}</DetailCardContent>
            </DetailCard>
        </Grid>
    </Load>
}

const InformacioRegistre = ({entity}:any) => {
    const { t } = useBaseAppContext();
    return (<Grid container columnSpacing={1} rowSpacing={1}>
        <DetailCard>
            <DetailField name={"registreTipus"} inline/>
            <DetailField name={"nom"} inline/>
            <DetailField name={"identificador"} inline/>
            <DetailField name={"data"} inline>{formatDate(entity.data)}</DetailField>
            <DetailField name={"procesEstat"} formatterValue={(v:any) => <Box display={'flex'} alignItems={'center'}>
                {v}
                {(entity.procesEstat?.includes('BACK_')) && entity.backCodi && <> - {entity.backCodi}</>}
                {entity.procesEstat == 'REGLA_PENDENT' && entity.regla && <> - {entity.regla?.description}</>}
                {entity.procesError && <Icon title={entity.procesError} color={'error'} fontSize={'small'}>warning</Icon>}
            </Box>} inline></DetailField>
            <DetailField name={"presencial"} inline>{t(`common.boolean.${entity.presencial}`)}</DetailField>
        </DetailCard>


        <DetailCard title={t('component.RegistreDetail.titles.obligatori')} headerProps={{backgroundColor: 'greyBackground'}} size={6}>
            <DetailField name={"oficinaDescripcio"} inline>{entity.oficinaDescripcio} ({entity.oficinaCodi})</DetailField>
            <DetailField name={"llibreDescripcio"} inline>{entity.llibreDescripcio} ({entity.llibreCodi})</DetailField>
            <DetailField name={"extracte"} inline/>
            <DetailField name={"documentacioFisicaDescripcio"} inline>{entity.documentacioFisicaDescripcio} ({entity.documentacioFisicaCodi})</DetailField>
            <DetailField name={"unitatAdministrativaDescripcio"} inline>{entity.unitatAdministrativaDescripcio} ({entity.unitatAdministrativaCodi})</DetailField>
            <DetailField name={"assumpteTipusDescripcio"} inline>{entity.assumpteTipusDescripcio} ({entity.assumpteTipusCodi})</DetailField>
            <DetailField name={"idiomaDescripcio"} inline>{entity.idiomaDescripcio} ({entity.idiomaCodi})</DetailField>
        </DetailCard>

        <DetailCard title={t('component.RegistreDetail.titles.opcional')} headerProps={{backgroundColor: 'greyBackground'}} size={6}>
            <DetailField name={"procediment"} inline hidden={entity.serveiCodi}/>
            <DetailField name={"servei"} inline hidden={!entity.serveiCodi}/>
            <DetailField name={"tramitNom"} inline>{entity.tramitCodi} - {entity.tramitNom}</DetailField>
            <DetailField size={6} name={"referencia"} inline/>
            <DetailField size={6} name={"expedientNumero"} inline/>
            <DetailField size={6} name={"transportTipusDescripcio"} inline>{entity.transportTipusDescripcio} ({entity.transportTipusCodi})</DetailField>
            <DetailField size={6} name={"transportNumero"} inline/>
            <DetailField size={6} name={"oficinaOrigenDescripcio"} inline>{entity.oficinaOrigenDescripcio} ({entity.oficinaOrigenCodi})</DetailField>
            <DetailField size={6} name={"assumpteDescripcio"} inline>{entity.assumpteDescripcio} ({entity.assumpteCodi})</DetailField>
            <DetailField size={6} name={"numeroOrigen"} inline/>
            <DetailField size={6} name={"dataOrigen"} inline>{formatDate(entity.dataOrigen)}</DetailField>
            <DetailField name={"observacions"} inline/>
        </DetailCard>

        <DetailExpandCard title={t('component.RegistreDetail.titles.seguiment')} headerProps={{backgroundColor: 'greyBackground'}}>
            <DetailField name={"entitat"} inline/>
            <DetailField name={"aplicacioCodi"} inline>{entity.aplicacioCodi} {entity.aplicacioVersio}</DetailField>
            <DetailField name={"usuariNom"} inline>{entity.usuariNom} ({entity.usuariCodi})</DetailField>
            <DetailField name={"createdDate"} inline>{formatDate(entity.createdDate)}</DetailField>
        </DetailExpandCard>

        <Load value={entity.justificant?.id} noEffect>
            <DetailExpandCard title={t('component.RegistreDetail.titles.justificant')} headerProps={{backgroundColor: 'greyBackground'}}>
                <Grid size={12}>
                <MuiDetail
                    id={entity.justificant?.id}
                    resourceName={'registreAnnexResource'}
                    hiddenToolbar
                    componentProps={{ sx: { mt: 0 } }}
                >
                    <DetailField name={"dataCaptura"} formatterValue={(v:any) => formatDate(v)} inline/>
                    <DetailField name={"origenCiutadaAdmin"} inline/>
                    <DetailField name={"ntiElaboracioEstat"} inline/>
                    <DetailField name={"ntiTipusDocument"} inline/>
                    <DetailField name={"fitxerNom"} formatterValue={(v:any, data:any) => <>{v} ({data.fitxerTamany} bytes)</>} inline/>
                </MuiDetail>
                </Grid>
            </DetailExpandCard>
        </Load>
    </Grid>)
}

const Resum = ({entity}:any) => {
    const { t } = useBaseAppContext();
    return (<Grid container columnSpacing={1} rowSpacing={1}>
        <DetailCard>
            <DetailField size={6} name={"identificador"} inline/>
            <DetailField size={6} name={"data"} inline>{formatDate(entity.data)}</DetailField>
            <DetailField size={6} name={"oficinaDescripcio"} inline>{entity.oficinaDescripcio}({entity.oficinaCodi})</DetailField>
            <DetailField size={6} name={"presencial"} inline>{entity.presencial ?'Si' :'No'}</DetailField>
            <DetailField size={12} name={"extracte"} inline/>
            <DetailField size={6} name={"procediment"} inline hidden={entity.serveiCodi}/>
            <DetailField size={6} name={"servei"} inline hidden={!entity.serveiCodi}/>
            <DetailField size={6} name={"expedientNumero"} inline/>
            <DetailField size={12} name={"tramitNom"} inline>{entity.tramitCodi} - {entity.tramitNom}</DetailField>
            <DetailField size={12} name={"observacions"} inline/>
            <DetailField size={12} name={"backCodi"} inline/>
            <DetailField size={4} name={"numeroOrigen"} inline/>
            <DetailField size={4} name={"dataOrigen"} inline>{formatDate(entity.dataOrigen)}</DetailField>
            <DetailField size={4} name={"oficinaOrigenDescripcio"} inline>{entity.oficinaOrigenDescripcio}({entity.oficinaOrigenCodi})</DetailField>
        </DetailCard>

        <DetailExpandCard title={t('component.RegistreDetail.tabs.interessats')} variant={'body1'} headerProps={{backgroundColor: 'greyBackground'}}>
            <InteressatsGrid id={entity.id}/>
        </DetailExpandCard>

        <DetailExpandCard title={t('component.RegistreDetail.tabs.annexos')} variant={'body1'} headerProps={{backgroundColor: 'greyBackground'}}>
            <AnnexGrid entity={entity}/>
        </DetailExpandCard>

    </Grid>)
}

const RegistreDetail = () => {
    const { t } = useBaseAppContext();
    const {data} = useDetailContext()
    // console.log("data", data)

    const { getByName } = useConfig()
    const metadadesActives = getByName("es.caib.distribucio.permetre.metadades.registre")

    const { handleOpen, component } = useCommentDialog();

    const tabs = useMemo(() => [
        {
            value: 'resum',
            label: t('component.RegistreDetail.tabs.resum'),
            content: <Resum entity={data}/>
        },
        {
            value: 'info',
            label: t('component.RegistreDetail.tabs.info'),
            content: <InformacioRegistre entity={data}/>
        },
        {
            value: 'interessats',
            label: t('component.RegistreDetail.tabs.interessats'),
            content: <InteressatsGrid id={data.id}/>
        },
        {
            value: 'annexos',
            label: t('component.RegistreDetail.tabs.annexos'),
            content: <AnnexTab entity={data}/>
        },
        {
            value: 'arxiu',
            label: t('component.RegistreDetail.tabs.arxiu'),
            content: <ArxiuDetall entity={data.arxiuDetall}/>
        },
        {
            value: "dades",
            label: t('component.RegistreDetail.tabs.dades'),
            content: <MetaDadesGrid entity={data}/>,
            hidden: !metadadesActives,
        },
        {
            value: 'procesBack',
            label: t('component.RegistreDetail.tabs.procesBack'),
            content: <ProcessBack entity={data}/>,
            hidden: data.procesEstat == 'BACK_PENDENT' || !(data.procesEstat?.includes('BACK_'))
        },
        {
            value: 'copia',
            label: t('component.RegistreDetail.tabs.copia'),
            content: <Copies entity={data}/>
        },
    ], [metadadesActives])

    return (<>
        <Box display={'flex'} alignItems={'center'} flexWrap={'wrap'} bgcolor={'greyBackground'} p={1}>
            &nbsp;/&nbsp; <Icon fontSize={'small'} >account_tree</Icon> {data.unitatAdministrativaDescripcio}
            &nbsp;/&nbsp; <Icon fontSize={'small'} >inbox</Icon> {data.pare?.description}
            &nbsp;/&nbsp; {data.nom}
        </Box>

        <TabComponent
            variant="scrollable"
            headerAdditionalData={<>
                <IconButton
                    title={t('component.CommentDialog.label')}
                    onClick={() => handleOpen(data.id, data.nom) }
                >
                    <Badge badgeContent={data.numComentaris} color="primary" showZero>
                        <Icon>forum</Icon>
                    </Badge>
                </IconButton>
            </>}
            tabs={tabs}
        />
        {component}
    </>);
}

const perspectives = ['ARXIU_DETALL', 'DARRER_MOVIMENT', 'COMMENT_NUM']
export const useRegistreDetail = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (id:any, _row:any) => {
        dialogShow(
            t('page.contingut.accio.detalls.title'),
            <MuiDetail
                id={id}
                resourceName={'registreResource'}
                perspectives={perspectives}
                hiddenToolbar
                componentProps={{ sx: { mt: 0 } }}
            >
                <RegistreDetail/>
            </MuiDetail>,
            [],
            { maxWidth: 'xl', fullWidth: true }
        );
    };

    return {
        handleOpen,
        dialog: dialogComponent
    };
}