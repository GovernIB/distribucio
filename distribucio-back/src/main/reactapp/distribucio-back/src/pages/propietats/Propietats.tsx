import React, { useEffect, useMemo } from 'react';
import { GridPage, useBaseAppContext, useDebounce, useResourceApiService } from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import { MAIN_GAP, SCREEN_GRID_MIN_HEIGHT } from '../../util/layout';
import { accentColor } from '../../theme';
import { ToolbarButton } from '../../components/StyledMuiGrid';
import { useTranslation } from 'react-i18next';
import { TextField, InputAdornment, Icon, IconButton } from '@mui/material';
import { alpha } from '@mui/material/styles';
import * as builder from '../../util/springFilterUtils.ts';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import Box from '@mui/material/Box';
import { PropietatsProps } from './PropietatsProps.tsx';
import { useParams } from 'react-router-dom';

export const QuickFilter: React.FC<{
    label?: string;
    /** Alçada de 32px, com els botons de la barra de títol de la pantalla (ScreenFrame). */
    compact?: boolean;
    onChange: (quickFilter: string | undefined) => void;
}> = (props) => {
    const { label, compact, onChange } = props;
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
        <TextField
            value={quickFilter}
            onChange={(event) => setQuickFilter(event.target.value)}
            label={label}
            variant="outlined"
            size="small"
            sx={
                compact
                    ? {
                          '& .MuiOutlinedInput-root': { height: '32px' },
                          '& .MuiOutlinedInput-input': { pt: 0, pb: 0 },
                      }
                    : undefined
            }
            slotProps={{
                // Sense `label` visible (barra de títol), el camp necessita igualment una etiqueta (OAW 1.9)
                htmlInput: label ? undefined : { 'aria-label': t('common.cerca') },
                input: {
                    startAdornment: (
                        <InputAdornment position="start">
                            <Icon fontSize="small">{icons.cerca}</Icon>
                        </InputAdornment>
                    ),
                    endAdornment: quickFilter && (
                        <InputAdornment position="end">
                            <IconButton size="small" onClick={() => setQuickFilter('')}>
                                <Icon fontSize="inherit">{icons.buida}</Icon>
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

    const { isReady: isEntitatReady, getOne: apiEntitatGet } = useResourceApiService('entitatResource');
    const [entitat, setEntitat] = React.useState<any>();

    useEffect(() => {
        if (isEntitatReady) {
            if (entitatId) {
                apiEntitatGet(entitatId).then(setEntitat);
            } else {
                setEntitat(undefined);
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
                filter:
                    entitatId != null ? builder.eq('entitat.id', `'${entitatId}'`) : builder.eq('entitatCodi', null),
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

        return configs.filter(
            (c) =>
                c.key?.toLowerCase().includes(quickFilter.toLowerCase()) ||
                c.value?.toLowerCase().includes(quickFilter.toLowerCase()) ||
                c.description?.toLowerCase().includes(quickFilter.toLowerCase())
        );
    }, [quickFilter, configs]);

    const { isReady: isGroupReady, find: apiGroupFind } = useResourceApiService('configGroupResource');
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
                    const isSelectedGroupIdInConfigGroups = configGroups.find((g) => g.id === selectedGroupId);
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

        const temp = configGroups.filter((g) => filteredProps.some((p) => g.id === p.group.id));

        return configGroups.filter((g) => temp.some((g2) => g2.id == g.id || g2.parent?.id == g.id));
    }, [filteredProps, configGroups]);

    const restart = () => {
        apiConfigAction(undefined, { code: 'RESTART_TASKS' })
            .then(() => {
                temporalMessageShow(null, t('page.config.accio.restart.ok'), 'success');
            })
            .catch((error) => temporalMessageShow(null, error.message, 'error'));
    };

    const sync = () => {
        apiConfigAction(undefined, { code: 'SYNC_JBOSS' })
            .then(() => {
                temporalMessageShow(null, t('page.config.accio.sync.ok'), 'success');
            })
            .catch((error) => temporalMessageShow(null, error.message, 'error'));
    };

    return (
        <GridPage>
            <ScreenFrame
                icon={icons.config}
                title={t('page.config.title') + (entitat ? ` - ${entitat?.nom}` : '')}
                actions={
                    <>
                        <QuickFilter compact onChange={setQuickFilter} />
                        <ToolbarButton
                            title={t('page.config.accio.restart.label')}
                            icon={icons.sincronitza}
                            color="primary"
                            onClick={restart}
                        >
                            {t('page.config.accio.restart.label')}
                        </ToolbarButton>
                        <ToolbarButton
                            title={t('page.config.accio.sync.label')}
                            icon={icons.sincronitza}
                            color="primary"
                            onClick={sync}
                        >
                            {t('page.config.accio.sync.label')}
                        </ToolbarButton>
                    </>
                }
            >
                <Box
                    sx={{
                        display: 'flex',
                        gap: MAIN_GAP + 'px',
                        flex: '1 1 0',
                        minHeight: SCREEN_GRID_MIN_HEIGHT + 'px',
                    }}
                >
                    <Box
                        sx={(theme) => ({
                            flex: '0 0 25%',
                            overflow: 'auto',
                            border: '1px solid',
                            borderColor: theme.palette.mode === 'dark' ? theme.palette.divider : '#e0e0e0',
                            borderRadius: 1,
                        })}
                    >
                        <SimpleTreeView
                            selectedItems={selectedGroupId}
                            onSelectedItemsChange={(_event, id) => {
                                setSelectedGroupId(id || undefined);
                            }}
                            sx={(theme) => ({
                                '& .MuiTreeItem-content': {
                                    paddingY: 1,
                                },
                                ...(theme.palette.mode === 'dark' && {
                                    '& .MuiTreeItem-content.Mui-selected, & .MuiTreeItem-content.Mui-selected:hover, & .MuiTreeItem-content.Mui-selected.Mui-focused':
                                        {
                                            backgroundColor: alpha(theme.palette.primary.main, 0.35),
                                            boxShadow: `inset 4px 0 0 ${accentColor(theme)}`,
                                            fontWeight: 600,
                                        },
                                }),
                            })}
                        >
                            {filteredGroups
                                ?.filter((g) => g.parent == null)
                                ?.map((g) => (
                                    <TreeItem key={g.key} itemId={g.id} label={g.description} />
                                ))}
                        </SimpleTreeView>
                    </Box>
                    <Box sx={{ flex: '1 1 0', minWidth: 0 }}>
                        <PropietatsProps
                            root
                            groupId={selectedGroupId}
                            grups={filteredGroups}
                            configs={filteredProps}
                            highlight={quickFilter}
                        />
                    </Box>
                </Box>
            </ScreenFrame>
        </GridPage>
    );
};
