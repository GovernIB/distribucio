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

export const PropietatsProps: React.FC<{ groupId?: any, grups: any[], configs: any[], highlight?: string }> = (props) => {
    const { groupId, grups, configs, highlight } = props;
    const { t } = useTranslation();

    const group = useMemo(() => grups?.find((g:any) => g.id === groupId ), [groupId, grups])
    const confProps = configs?.filter((p:any) => p.group?.id == groupId)
    const children = grups?.filter((p:any) => p.parent?.id == groupId)

    return (
        <Load value={group} noEffect>
            <DetailCard title={group?.description} variant={'h6'} headerProps={{ color: 'white', backgroundColor: 'primary.main' }}>
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
                            <Icon fontSize="large" color="disabled">
                                block
                            </Icon>
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
