import {useBaseAppContext} from "reactlib";
import {Grid} from "@mui/material";
import {DetailCard, DetailCardContent} from "../../../../components/CardData.tsx";
import {formatDate} from "../../../../util/dateUtils.ts";
import Load from "../../../../components/Load.tsx";

export const ArxiuDetall = ({entity}:any) => {
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
