import Load from "../../components/Load.tsx";
import React, {useEffect, useMemo, useState} from "react";
import {FormApi, GridPage, useBaseAppContext, useMuiFormDialogApiRef, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {ROLE_ADMIN, useDistribucioContext} from "../../components/DistribucioContext.ts";
import {TreeView} from "../../components/TreeView.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import { MAIN_GAP, SCREEN_GRID_MIN_HEIGHT } from '../../util/layout';
import BustiaFilter from "./BustiaFilter.tsx";
import {Icon, IconButton} from "@mui/material";
import Box from "@mui/material/Box";
import {BustiaFormDialog, BustiaOrganigramaForm} from "./BustiaForm.tsx";
import {Link} from "../../components/BaseApp.tsx";
import {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {useSimpleTreeViewApiRef} from "@mui/x-tree-view";
import {MenuActionButton} from "../../components/MenuButton.tsx";
import {useActions, useBustiaActions} from "./detail/BustiaActions.tsx";
import {ResourceApiError} from "../../../lib/components/ResourceApiProvider.tsx";

export const useOrganigrama = (props:any) => {
    const { t } = useTranslation();
    // `height`: alçada de l'arbre. Per defecte 56vh (diàlegs); la pantalla passa 100% per omplir l'alçada que sobra.
    const {quickFilter, filter, namedQueries, perspectives, onClick, disabled, height = '56vh', ...other} = props
    const { currentEntitat } = useDistribucioContext();
    const apiRef = useSimpleTreeViewApiRef();

    const [busties, setBusties] = useState<any>();
    const [unitats, setUnitats] = useState<any>();

    const {
        isReady: apiBustiaIsReady,
        find: apiBustiaFind,
    } = useResourceApiService('bustiaResource');
    const {
        isReady: apiUnitatIsReady,
        find: apiUnitatFind,
    } = useResourceApiService('unitatOrganitzativaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const refresh = () => {
        apiBustiaFind({quickFilter, filter, namedQueries, perspectives, unpaged: true, /*sorts: ['codi,asc']*/})
            .then((app) => setBusties(app?.rows))
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    useEffect(() => {
        if (apiBustiaIsReady) {
            refresh()
        }
    }, [apiBustiaIsReady, quickFilter, filter, namedQueries]);

    useEffect(() => {
        if(apiUnitatIsReady){
            apiUnitatFind({unpaged: true, sorts: ['nom,asc']})
                .then((app) => setUnitats(app?.rows))
                .catch((error) => {
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
    }, [apiUnitatIsReady]);

    const structureUnitats = (pareId:string | null, unitats:any, busties:any) => {
        return unitats
            ?.filter((u:any) => u?.unitatSuperior?.id == pareId)
            ?.map((u:any) => {
                // const children = [structureUnitats(u.id, unitats, busties), structureBusties(u.id, busties)]
                const children = [
                    ...(structureUnitats(u.id, unitats, busties) || []),
                    ...(structureBusties(u.id, busties) || [])
                ]
                return {
                    id: u.codi,
                    label: `${u.denominacio} (${u.codi})`,
                    icon: pareId == null ?'home' :'folder',
                    children: (children?.length > 0) ?children :undefined,
                    componentProps: {
                        disableSelection: true
                    },
                    class: 'unitat',
                    data: u
                }
            })
            ?.filter((u:any) => u?.children != undefined)
    }

    const structureBusties = (unitatId:string, busties:any) => {
        return busties
            ?.filter((u:any) => u?.unitatOrganitzativa?.id == unitatId)
            ?.map((u:any) => {
                return {
                    id: u.id,
                    label: <>{u.nom} {u.perDefecte && <strong>({t('page.bustia.grid.principal')})</strong>}</>,
                    icon: icons.bustia,
                    onClick: () => onClick?.(u.id, u),
                    componentProps: {
                        sx: {
                            color: !u.activa ?'lightgrey'  :'inherit'
                        },
                        disableSelection: disabled?.(u) || false,
                    },
                    class: 'bustia',
                    data: u
                }
            })
    }

    const organigrama:any = useMemo(() => {
        if (busties && unitats) {
            return structureUnitats(null, unitats, busties)
        }
        return undefined;
    }, [busties, unitats]);

    const content = (
        <Box height={'max'} sx={{
            height,
            overflow: 'auto',
            border: '1px solid #e0e0e0',
            borderRadius: 1,
        }}>
            <Load value={organigrama}>
                <TreeView
                    apiRef={apiRef}
                    defaultExpandedItems={[currentEntitat?.codiDir3]}
                    list={organigrama}
                    {...other}
                />
            </Load>
        </Box>
    );

    return {
        apiRef,
        busties,
        unitats,
        refresh,
        content
    }
}

export const BustiaOrganigrama = () => {
    const { t } = useTranslation();
    const { currentEntitatId, currentRole } = useDistribucioContext();
    const isAdmin = currentRole == ROLE_ADMIN

    const [entity, setEntity] = useState<any>();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const formDialogApiRef = useMuiFormDialogApiRef();
    const formApiRef = React.useRef<FormApi | any>({});

    const {
        delete: apiDelete
    } = useResourceApiService('bustiaResource');
    const {temporalMessageShow, t: tLib} = useBaseAppContext();

    const {apiRef, busties, content, refresh} = useOrganigrama({
        filter: springFilter,
        namedQueries: namedQueries,
        height: '100%',
        onClick: (_id:any, row:any) => setEntity(row),
        renderCell: (item:any) => {
            if (item.class == 'unitat') {
                return <>
                    <Box display={'flex'} alignItems={'center'} gap={1} onClick={item?.onClick}>
                        <Icon>{item.icon}</Icon>{item.label}

                        {item.data.estat != "V" && <Icon title={t('component.BustiaObsoleta.unitat')} color={'error'}>{icons.advertencia}</Icon>}
                    </Box>
                </>
            }
        }
    })

    useEffect(() => {
        if (entity != null && busties!= null) {
            const bustia = busties.find((b:any) => b.id === entity.id)
            setEntity(bustia || undefined)
        }
    }, [busties]);

    const {actions, components} = useBustiaActions(refresh);
    const { usersBustia } = useActions()

    const additionalActions = [
        ...actions,
        {
            label: t('common.delete'),
            icon: icons.esborra,
            onClick: () => {
                apiDelete(entity.id)
                    .then(() => {
                        setEntity(undefined)
                        refresh()
                        temporalMessageShow(null, tLib('form.delete.success'), 'success');
                    })
                    .catch((error: ResourceApiError) => {
                        temporalMessageShow(tLib('form.delete.error'), error.message, 'error');
                    });
            },
        },
    ];

    const create = () => {
        formDialogApiRef.current?.show(undefined, {entitat: {id: currentEntitatId}})
            .then((response:any) => {
                setEntity(response)
                refresh()

                // TODO: revisar seleción al crear
                const input = apiRef.current?.getItemDOMElement(response.id);
                input?.focus();
            })
    }

    const update = () => {
        formApiRef.current?.save()
            .then(() => {
                refresh()
            })
    }

    return (
        <GridPage>
            <ScreenFrame
                icon={icons.bustia}
                title={t('page.bustia.grid.title')}
                actions={
                    <>
                        <ToolbarButton
                            icon={icons.llista}
                            variant={'contained'}
                            component={Link}
                            to={'/bustiaAdmin'}
                        >{t('page.bustia.vista')}</ToolbarButton>
                        <ToolbarButton
                            icon={icons.informe}
                            variant={'contained'}
                            color={'success'}
                            onClick={() => usersBustia(springFilter, namedQueries)}
                        >{t('page.bustia.accio.usuarisBustia.label')}</ToolbarButton>
                        <ToolbarButton
                            title={t('common.create')}
                            icon={icons.crea}
                            onClick={create}
                            color={'primary'}
                            hidden={!isAdmin}
                        >
                            {t('page.bustia.accio.new.label')}
                        </ToolbarButton>
                    </>
                }
            >
                <BustiaFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />
                <BustiaFormDialog formDialogApiRef={formDialogApiRef} />

                {/* Arbre i formulari ocupen l'alçada que sobra a la pantalla (cada un fa el seu scroll per dins). */}
                <Box sx={{ display: 'flex', gap: MAIN_GAP + 'px', flex: '1 1 0', minHeight: SCREEN_GRID_MIN_HEIGHT + 'px' }}>
                    <Box sx={{ flex: '0 0 33%', minWidth: 0 }}>
                        {content}
                    </Box>
                    <Box sx={{ flex: '1 1 0', minWidth: 0, overflow: 'auto' }}>
                        <Load value={entity} noEffect>
                            <BustiaOrganigramaForm apiRef={formApiRef} entity={entity} toolbarElementsWithPositions={isAdmin?[
                                {
                                    position: 2,
                                    element: <IconButton title={t('common.update')} onClick={update}><Icon>{icons.desa}</Icon></IconButton>,
                                },
                                {
                                    position: 2,
                                    element: <MenuActionButton
                                        id={entity?.id}
                                        entity={entity}
                                        ButtonComponent={IconButton}
                                        buttonLabel={<Icon>{icons.mesAccions}</Icon>}
                                        actions={additionalActions}
                                    />,
                                }
                            ]:[]}/>
                        </Load>
                        {components}
                    </Box>
                </Box>
            </ScreenFrame>
        </GridPage>
    );
}