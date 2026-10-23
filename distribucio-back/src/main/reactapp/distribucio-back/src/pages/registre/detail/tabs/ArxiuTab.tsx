import { useBaseAppContext } from 'reactlib';
import { Grid } from '@mui/material';
import { DetailCard, DetailCardContent } from '../../../../components/CardData.tsx';
import { formatDate } from '../../../../util/dateUtils.ts';
import Load from '../../../../components/Load.tsx';

export const ArxiuDetall = ({ entity }: any) => {
    const { t } = useBaseAppContext();
    // Si l'estat no té traducció, t() retorna la clau: es mostra el valor tal com arriba
    const clauEstat = `page.registre.arxiu.estat.${entity?.eniEstat}`;
    const estatArxiu = entity?.eniEstat && (t(clauEstat) === clauEstat ? entity.eniEstat : t(clauEstat));

    return (
        <Load value={entity}>
            <Grid container columnSpacing={1} rowSpacing={2}>
                <DetailCard>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.identificador')}>
                        {entity.identificador}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.nom')}>
                        {entity.nom}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.serieDocumental')}>
                        {entity.serieDocumental}
                    </DetailCardContent>
                </DetailCard>
                <DetailCard>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniVersio')}>
                        {entity.eniVersio}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniIdentificador')}>
                        {entity.eniIdentificador}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniOrgans')}>
                        {entity.eniOrgans}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniDataObertura')}>
                        {formatDate(entity.eniDataObertura)}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniClassificacio')}>
                        {entity.eniClassificacio}
                    </DetailCardContent>
                    <DetailCardContent titleSize={2} textSize={10} title={t('page.registre.arxiu.eniEstat')}>
                        {estatArxiu}
                    </DetailCardContent>
                </DetailCard>
            </Grid>
        </Load>
    );
};
