import React, {useMemo} from 'react';
import { useTranslation } from 'react-i18next';
import {Grid} from '@mui/material';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import {DetailCard} from "../../components/CardData.tsx";
import Load from "../../components/Load.tsx";
import {PropietatsForm} from "./PropietatsForm.tsx";
import { icons } from '../../util/icons';

export const PropietatsProps: React.FC<{ groupId?: any, grups: any[], configs: any[], highlight?: string }> = (props) => {
    // Mostra només les propietats del grup seleccionat; els subgrups es naveguen des de l'arbre del menú.
    // Ocupa tota l'alçada, amb la capçalera fixa i només el contingut amb scroll.
    const { groupId, grups, configs, highlight } = props;
    const { t } = useTranslation();

    const group = useMemo(() => grups?.find((g:any) => g.id === groupId ), [groupId, grups])
    const confProps = configs?.filter((p:any) => p.group?.id == groupId)

    return (
        <Load value={group} noEffect>
            {/* Encapçalament: el grup seleccionat penja del títol de la pantalla (h1) */}
            <DetailCard title={group?.description} variant={'h6'} titleComponent={'h2'}
                headerProps={{ color: 'white', backgroundColor: 'primary.main' }}
                gridProps={{ sx: { height: '100%' } }}
                cardProps={{ height: '100%', display: 'flex', flexDirection: 'column' }}
                contentProps={{ flex: 1, minHeight: 0, overflow: 'auto' }}>
                <Grid size={12}>
                <List component={Paper}>
                    {confProps?.length ? (
                        confProps.map((c:any) => (<PropietatsForm key={`item-${c.id}`} id={c.id} highlight={highlight}/>))
                    ) : (
                        <Box
                            sx={{
                                width: '100%',
                                textAlign: 'center',
                                px: 2,
                                py: 4,
                            }}>
                            <Icon fontSize="large" color="disabled">{icons.senseDades}</Icon>
                            <Typography variant="h5" color="text.secondary">
                                {t('page.propietats.empty')}
                            </Typography>
                        </Box>
                    )}
                </List>
                </Grid>
            </DetailCard>
        </Load>
    );
};
