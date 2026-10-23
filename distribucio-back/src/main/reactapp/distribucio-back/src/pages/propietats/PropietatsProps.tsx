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

export const PropietatsProps: React.FC<{ groupId?: any, grups: any[], configs: any[], highlight?: string, root?: boolean }> = (props) => {
    // root: grup de nivell superior; ocupa tota l'alçada, amb la capçalera fixa i només el contingut amb scroll.
    // Els subgrups no ho són i flueixen dins el contingut del pare.
    const { groupId, grups, configs, highlight, root = false } = props;
    const { t } = useTranslation();

    const group = useMemo(() => grups?.find((g:any) => g.id === groupId ), [groupId, grups])
    const confProps = configs?.filter((p:any) => p.group?.id == groupId)
    const children = grups?.filter((p:any) => p.parent?.id == groupId)

    return (
        <Load value={group} noEffect>
            {/* Encapçalaments: el grup arrel penja del títol de la pantalla (h1) i els subgrups, d'ell */}
            <DetailCard title={group?.description} variant={'h6'} titleComponent={root ? 'h2' : 'h3'}
                headerProps={{ color: 'white', backgroundColor: 'primary.main' }}
                {...(root && {
                    gridProps: { sx: { height: '100%' } },
                    cardProps: { height: '100%', display: 'flex', flexDirection: 'column' },
                    contentProps: { flex: 1, minHeight: 0, overflow: 'auto' },
                })}>
                <Grid size={12}>
                <List component={Paper}>
                    {(confProps?.length || children?.length) ? (<>
                        {confProps?.map((c:any) => (<PropietatsForm key={`item-${c.id}`} id={c.id} highlight={highlight}/>))}
                        {children?.map((c:any) => (
                            <Box key={c.key} sx={{ px: 1 }}>
                                <PropietatsProps groupId={c.id} grups={grups} configs={configs} highlight={highlight}/>
                            </Box>
                        ))}
                    </>) : (
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
