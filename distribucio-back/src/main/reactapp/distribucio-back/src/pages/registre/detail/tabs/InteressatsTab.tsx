import {useBaseAppContext, useDetailContext, MuiDetail} from "reactlib";
import {Grid} from "@mui/material";
import {DetailCard, DetailExpandCard, DetailField} from "../../../../components/CardData.tsx";
import StyledMuiGrid from "../../../../components/StyledMuiGrid.tsx";
import * as builder from '../../../../util/springFilterUtils';

export const InteressatDetail = () => {
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
export const InteressatsGrid = ({id}:any) => {
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
