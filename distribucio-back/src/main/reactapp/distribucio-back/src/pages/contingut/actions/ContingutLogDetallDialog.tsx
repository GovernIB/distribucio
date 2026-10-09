import React from 'react';
import { useTranslation } from 'react-i18next';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import { useResourceApiService, useFormContext } from 'reactlib';
import { formatDate } from '../../../util/dateUtils';

type ContingutLogDetallContentProps = {
    row: any;
};

/**
 * Contingut del diàleg de detall d'un log (accions): paràmetres, acció pare (si n'hi ha) i moviment
 * associat (si n'hi ha). El nom de l'objecte modificat (`objecteNom`) ja arriba resolt pel backend
 * mateix algorisme que fa el manteniment legacy)
 */
const ContingutLogDetallContent: React.FC<ContingutLogDetallContentProps> = () => {
    const { t } = useTranslation();
    const [pare, setPare] = React.useState<any>();
    const [moviment, setMoviment] = React.useState<any>();
    const { isReady: logApiReady, getOne: getOneLog } =
        useResourceApiService('contingutLogResource');
    const { isReady: movimentApiReady, getOne: getOneMoviment } = useResourceApiService(
        'contingutMovimentResource'
    );
    const { data } = useFormContext();

    React.useEffect(() => {
        if (data?.pare?.id != null && logApiReady) {
            getOneLog(data.pare.id).then(setPare);
        }
        if (data?.contingutMoviment?.id != null && movimentApiReady) {
            getOneMoviment(data.contingutMoviment.id).then(setMoviment);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [data, logApiReady, movimentApiReady]);

    return (
        <Stack spacing={2}>
            {data?.objecteTipus != null && (
                <Typography variant="body2">
                    {t('page.contingut.historial.detall.objecte', {
                        nom: data.objecteNom ?? `${data.objecteTipus}#${data.objecteId}`,
                    })}
                </Typography>
            )}
            {data?.params?.length > 0 && (
                <Stack>
                    <Typography variant="subtitle2">
                        {t('page.contingut.historial.detall.params')}
                    </Typography>
                    <List dense>
                        {data.params.map((param: string, index: number) => (
                            <ListItem key={index} disableGutters>
                                <ListItemText primary={param} />
                            </ListItem>
                        ))}
                    </List>
                </Stack>
            )}
            {pare && (
                <Stack>
                    <Typography variant="subtitle2">
                        {t('page.contingut.historial.detall.accioPare')}
                    </Typography>
                    <Typography variant="body2">
                        {pare.tipus} — {pare.createdByFullName} —{' '}
                        {pare.createdDate ? formatDate(pare.createdDate, 'DD/MM/YYYY HH:mm') : ''}
                    </Typography>
                </Stack>
            )}
            {moviment && (
                <Stack>
                    <Typography variant="subtitle2">
                        {t('page.contingut.historial.detall.moviment')}
                    </Typography>
                    <Typography variant="body2">
                        {t('page.contingut.historial.detall.movimentOrigen')}:{' '}
                        {moviment.origenNom ?? '-'}
                    </Typography>
                    <Typography variant="body2">
                        {t('page.contingut.historial.detall.movimentDesti')}:{' '}
                        {moviment.destiNom ?? '-'}
                    </Typography>
                </Stack>
            )}
        </Stack>
    );
};

export default ContingutLogDetallContent;
