import React, {useEffect, useMemo} from 'react';
import {GridPage, useBaseAppContext, useDebounce, useResourceApiService} from "reactlib";
import {CardPage} from "../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import {TextField, Grid, InputAdornment, Icon, IconButton, Button} from "@mui/material";
import * as builder from "../../util/springFilterUtils.ts";
import {SimpleTreeView} from "@mui/x-tree-view/SimpleTreeView";
import {TreeItem} from "@mui/x-tree-view/TreeItem";
import Box from "@mui/material/Box";
import {PropietatsProps} from "./PropietatsProps.tsx";
import {useParams} from "react-router-dom";

const PropietatsQuickFilter: React.FC<{ onChange: (quickFilter: string | undefined) => void }> = (
    props
) => {
    const { onChange } = props;
    // const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
        <TextField
            value={quickFilter}
            onChange={(event) => setQuickFilter(event.target.value)}
            // label={t('page.propietats.find')}
            variant="outlined"
            size="small"
            slotProps={{
                input: {
                    startAdornment: (
                        <InputAdornment position="start">
                            <Icon fontSize="small">search</Icon>
                        </InputAdornment>
                    ),
                    endAdornment: quickFilter && (
                        <InputAdornment position="end">
                            <IconButton size="small" onClick={() => setQuickFilter('')}>
                                <Icon fontSize="inherit">clear</Icon>
                            </IconButton>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

export const Propietats = () => {
    const { t } = useTranslation();
    const { id: entitatId } = useParams();
    const { temporalMessageShow } = useBaseAppContext();
    const [quickFilter, setQuickFilter] = React.useState<string>();

    const {
        isReady: isEntitatReady,
        getOne: apiEntitatGet,
    } = useResourceApiService('entitatResource');
    const [entitat, setEntitat] = React.useState<any>();

    useEffect(() => {
        if (isEntitatReady) {
            if (entitatId) {
                apiEntitatGet(entitatId).then(setEntitat)
            } else {
                setEntitat(undefined)
            }
        }
    }, [isEntitatReady, entitatId]);

    const {
        isReady: isConfigReady,
        find: apiConfigFind,
        artifactAction: apiConfigAction,
    } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    React.useEffect(() => {
        if (isConfigReady) {
            const args = {
                quickFilter,
                filter: (
                    entitatId != null
                        ? builder.eq('entitat.id', `'${entitatId}'`)
                        : builder.eq('entitatCodi', null)
                ),
                sorts: ['position,asc', 'description,desc'],
                unpaged: true,
            };
            apiConfigFind(args).then((response) => {
                const configs = response.rows.filter(() => true);
                setConfigs(configs);
            });
        }
    }, [isConfigReady, entitatId]);

    const filteredProps = useMemo(() => {
        if (!configs) {
            return [];
        }
        if (!quickFilter) {
            return configs;
        }

        return configs.filter(c =>
            c.key?.toLowerCase().includes(quickFilter.toLowerCase())
            || c.value?.toLowerCase().includes(quickFilter.toLowerCase())
            || c.description?.toLowerCase().includes(quickFilter.toLowerCase())
        );
    }, [quickFilter, configs]);

    const {
        isReady: isGroupReady,
        find: apiGroupFind,
    } = useResourceApiService('configGroupResource');
    const [configGroups, setConfigGroups] = React.useState<any[]>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<string>();
    React.useEffect(() => {
        if (isGroupReady) {
            const args = {
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiGroupFind(args).then((response) => {
                const configGroups = response.rows;
                setSelectedGroupId(undefined);
                setConfigGroups(configGroups);
                if (configGroups.length) {
                    const isSelectedGroupIdInConfigGroups = configGroups.find(
                        (g) => g.id === selectedGroupId
                    );
                    if (!isSelectedGroupIdInConfigGroups) {
                        setSelectedGroupId(configGroups[0].id);
                    }
                }
            });
        }
    }, [isGroupReady]);

    const filteredGroups = useMemo(() => {
        if (!configGroups || !filteredProps || filteredProps.length === 0) {
            return [];
        }

        const temp = configGroups.filter(g =>
            filteredProps.some(p =>
                    g.id === p.group.id
            )
        );

        return configGroups.filter(g =>
            temp.some(g2 => g2.id == g.id || g2.parent?.id == g.id)
        );
    }, [filteredProps, configGroups]);

    const restart = () => {
        apiConfigAction(undefined, { code: 'RESTART_TASKS' })
            .then(() => {
                temporalMessageShow(null, t('page.config.accio.restart.ok'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(null, error.message, 'error')
            );
    }

    const sync = () => {
        apiConfigAction(undefined, { code: 'SYNC_JBOSS' })
            .then(() => {
                temporalMessageShow(null, t('page.config.accio.sync.ok'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(null, error.message, 'error')
            );
    }

    return <GridPage>
        <CardPage title={t('page.config.title') + (entitat ?` - ${entitat?.nom}` :'')}>
            <Grid container spacing={2}>
                <Grid size={12} sx={{ px: 1 }} display={'flex'} justifyContent={'end'}>
                    <PropietatsQuickFilter onChange={setQuickFilter} />
                    <Button variant="outlined" size="small" sx={{ borderRadius: '4px' }} onClick={restart}>
                        <Icon>cached</Icon>{t('page.config.accio.restart.label')}
                    </Button>
                    <Button variant="outlined" size="small" sx={{ borderRadius: '4px' }} onClick={sync}>
                        <Icon>cached</Icon>{t('page.config.accio.sync.label')}
                    </Button>
                </Grid>
                <Grid size={3}>
                    <Box height={'max'} sx={{
                        height: '68vh',
                        overflow: 'auto',
                        border: '1px solid #e0e0e0',
                        borderRadius: 1,
                    }}>
                        <SimpleTreeView
                            selectedItems={selectedGroupId}
                            onSelectedItemsChange={(_event, id) => {
                                setSelectedGroupId(id || undefined)
                            }}
                            sx={{
                                '& .MuiTreeItem-content': {
                                    paddingY: 1,
                                },
                            }}>
                            {filteredGroups?.filter(g => g.parent == null)?.map(g =>
                                <TreeItem key={g.key} itemId={g.id} label={g.description}/>
                            )}
                        </SimpleTreeView>
                    </Box>
                </Grid>
                <Grid size={9}>
                    <Box height={'max'} sx={{
                        height: '68vh',
                        overflow: 'auto',
                    }}>
                        <PropietatsProps groupId={selectedGroupId} grups={filteredGroups} configs={filteredProps} highlight={quickFilter} />
                    </Box>
                </Grid>
            </Grid>
        </CardPage>
    </GridPage>
}