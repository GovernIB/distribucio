import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import { formatDate } from '../../../util/dateUtils';
import { Box } from '@mui/material';
import { CardData } from '../../../components/CardData';

type ContingutAuditoriaTabProps = {
    contingutRow: any;
};

export const ContingutAuditoriaTab: React.FC<ContingutAuditoriaTabProps> = ({ contingutRow }) => {
    const { t } = useTranslation();
    const data = (value?: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm') : '-');

    return (
        <Grid container spacing={2}>
            <CardData
                size={6}
                title={t('page.contingut.historial.auditoria.creacio')}
                headerProps={{ backgroundColor: 'greyBackground' }}
            >
                <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.contingut.historial.auditoria.usuari')}:{' '}
                        <Typography component={'span'}>
                            {contingutRow?.lastModifiedByFullName ??
                                contingutRow?.lastModifiedBy ??
                                '-'}
                        </Typography>
                    </Typography>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.contingut.historial.auditoria.data')}:{' '}
                        <Typography component={'span'}>
                            {data(contingutRow?.lastModifiedDate)}
                        </Typography>
                    </Typography>
                </Box>
            </CardData>
            <CardData
                size={6}
                title={t('page.contingut.historial.auditoria.modificacio')}
                headerProps={{ backgroundColor: 'greyBackground' }}
            >
                <Box sx={{ display: 'flex', flexDirection: 'column' }}>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.contingut.historial.auditoria.usuari')}:{' '}
                        <Typography component={'span'}>
                            {contingutRow?.lastModifiedByFullName ??
                                contingutRow?.lastModifiedBy ??
                                '-'}
                        </Typography>
                    </Typography>
                    <Typography sx={{ fontWeight: 'bold' }}>
                        {t('page.contingut.historial.auditoria.data')}:{' '}
                        <Typography component={'span'}>
                            {data(contingutRow?.lastModifiedDate)}
                        </Typography>
                    </Typography>
                </Box>
            </CardData>
        </Grid>
    );
};

export default ContingutAuditoriaTab;
