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

// Grups del menú, niuats: cada grup penja del seu pare (la clau `undefined` són els de nivell superior)
const GroupTreeItems: React.FC<{ parentId?: string; childrenByParent: Map<string | undefined, any[]> }> = (props) => {
    const { parentId, childrenByParent } = props;
    return (
        <>
            {childrenByParent.get(parentId)?.map((g) => (
                <TreeItem key={g.key} itemId={g.id} label={g.description}>
                    {childrenByParent.has(g.id) && <GroupTreeItems parentId={g.id} childrenByParent={childrenByParent} />}
                </TreeItem>
            ))}
        </>
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
    const [expandedGroupIds, setExpandedGroupIds] = React.useState<string[]>([]);
    React.useEffect(() => {
        if (isGroupReady) {
            const args = {
                // Hi ha grups germans amb la mateixa posició: la descripció desempata l'ordre
                sorts: ['position,asc', 'description,asc'],
                unpaged: true,
            };
            apiGroupFind(args).then((response) => {
                setConfigGroups(response.rows);
            });
        }
    }, [isGroupReady]);

    const groupsById = useMemo(() => new Map<string, any>((configGroups ?? []).map((g) => [g.id, g])), [configGroups]);

    // Grups visibles: els que tenen propietats i tots els seus ancestres, a qualsevol nivell
    const filteredGroups = useMemo(() => {
        if (!configGroups || filteredProps.length === 0) {
            return [];
        }
        const visibleIds = new Set<string>();
        filteredProps.forEach((p) => {
            let id: string | undefined = p.group?.id;
            while (id != null && !visibleIds.has(id)) {
                visibleIds.add(id);
                id = groupsById.get(id)?.parent?.id;
            }
        });
        return configGroups.filter((g) => visibleIds.has(g.id));
    }, [filteredProps, configGroups, groupsById]);

    // Grups visibles agrupats pel pare; els grups de nivell superior pengen de la clau `undefined`
    const childrenByParent = useMemo(() => {
        const map = new Map<string | undefined, any[]>();
        filteredGroups.forEach((g) => {
            const parentId = g.parent?.id;
            map.set(parentId, [...(map.get(parentId) ?? []), g]);
        });
        return map;
    }, [filteredGroups]);

    const groupIdsWithProps = useMemo(() => new Set<string>(filteredProps.map((p) => p.group?.id)), [filteredProps]);

    // Grup mateix o, si no té propietats, el primer descendent que en tingui (perquè el panell no quedi buit)
    const resolveGroupWithProps = (groupId: string): string | undefined => {
        if (groupIdsWithProps.has(groupId)) {
            return groupId;
        }
        for (const child of childrenByParent.get(groupId) ?? []) {
            const resolved = resolveGroupWithProps(child.id);
            if (resolved != null) {
                return resolved;
            }
        }
        return undefined;
    };

    // Els pares sense propietats pròpies són només contenidors: en fer-hi clic s'expandeixen o es pleguen, però no es seleccionen
    const isContainer = (groupId: string) => childrenByParent.has(groupId) && !groupIdsWithProps.has(groupId);

    // Manté sempre seleccionat un grup visible i amb propietats (càrrega inicial, cercador, vista per entitat)
    React.useEffect(() => {
        if (filteredGroups.length === 0) {
            return;
        }
        const isSelectedVisible = filteredGroups.some((g) => g.id === selectedGroupId);
        const candidates = isSelectedVisible ? [selectedGroupId!] : (childrenByParent.get(undefined) ?? []).map((g) => g.id);
        for (const candidate of candidates) {
            const resolved = resolveGroupWithProps(candidate);
            if (resolved != null) {
                if (resolved !== selectedGroupId) {
                    setSelectedGroupId(resolved);
                }
                return;
            }
        }
    }, [filteredGroups, selectedGroupId]);

    // En seleccionar un grup només queda oberta la seva branca; si no en té, es plega tot el menú.
    React.useEffect(() => {
        const ancestorIds: string[] = [];
        let parentId = selectedGroupId != null ? groupsById.get(selectedGroupId)?.parent?.id : undefined;
        while (parentId != null && !ancestorIds.includes(parentId)) {
            ancestorIds.push(parentId);
            parentId = groupsById.get(parentId)?.parent?.id;
        }
        setExpandedGroupIds((prev) => {
            const next = quickFilter ? [...prev, ...ancestorIds.filter((id) => !prev.includes(id))] : ancestorIds;
            return next.length === prev.length && next.every((id) => prev.includes(id)) ? prev : next;
        });
    }, [selectedGroupId, groupsById, quickFilter]);

    // Amb el cercador actiu es desplega tot el que coincideix
    React.useEffect(() => {
        if (!quickFilter) {
            return;
        }
        const parentIds = Array.from(childrenByParent.keys()).filter((id): id is string => id != null);
        setExpandedGroupIds((prev) =>
            parentIds.every((id) => prev.includes(id)) ? prev : [...prev, ...parentIds.filter((id) => !prev.includes(id))]
        );
    }, [quickFilter, childrenByParent]);

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
                            selectedItems={selectedGroupId ?? null}
                            onSelectedItemsChange={(_event, id) => {
                                if (id && !isContainer(id)) {
                                    setSelectedGroupId(id);
                                }
                            }}
                            expandedItems={expandedGroupIds}
                            onExpandedItemsChange={(_event, ids) => setExpandedGroupIds(ids)}
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
                            <GroupTreeItems childrenByParent={childrenByParent} />
                        </SimpleTreeView>
                    </Box>
                    <Box sx={{ flex: '1 1 0', minWidth: 0 }}>
                        <PropietatsProps
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
