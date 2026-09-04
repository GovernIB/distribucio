import Load from "../../components/Load.tsx";
import React, {useEffect, useMemo, useState} from "react";
import {FormApi, GridPage, useBaseAppContext, useMuiFormDialogApiRef, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import {TreeView} from "../../components/TreeView.tsx";
import {CardPage} from "../../components/CardData.tsx";
import BustiaFilter from "./BustiaFilter.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import {Grid, Icon, IconButton} from "@mui/material";
import Box from "@mui/material/Box";
import {BustiaFormDialog, BustiaOrganigramaForm} from "./BustiaForm.tsx";
import {Link} from "../../components/BaseApp.tsx";
import {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {useSimpleTreeViewApiRef} from "@mui/x-tree-view";
import {MenuActionButton} from "../../components/MenuButton.tsx";

export const useOrganigrama = ({filter, namedQueries, onClick}:any) => {
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
        apiBustiaFind({filter, namedQueries, unpaged: true, sorts: ['codi,asc']})
            .then((app) => setBusties(app?.rows))
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    useEffect(() => {
        if(apiBustiaIsReady){
            refresh()
        }
    }, [apiBustiaIsReady]);

    useEffect(() => {
        if(apiUnitatIsReady){
            apiUnitatFind({filter: builder.eq('entitat.id', currentEntitat.id), unpaged: true, sorts: ['nom,asc']})
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
                    children: (children?.length > 0) ?children :undefined
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
                    label: <>{u.nom} {u.perDefecte && <strong>({'principal'})</strong>}</>,
                    icon: 'inbox',
                    onClick: () => onClick?.(u.id, u),
                    componentProps: !u.activa ?{
                        sx: {
                            color: 'lightgrey'
                        }
                    } :{},
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
            height: '56vh',
            overflow: 'auto',
            border: '1px solid #e0e0e0',
            borderRadius: 1,
        }}>
            <Load value={organigrama}>
                <TreeView
                    apiRef={apiRef}
                    defaultExpandedItems={[currentEntitat?.codiDir3]}
                    list={organigrama}
                />
            </Load>
        </Box>
    );

    return {
        apiRef,
        refresh,
        content
    }
}

export const BustiaOrganigrama = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = useDistribucioContext();

    const [entity, setEntity] = useState<any>();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const formDialogApiRef = useMuiFormDialogApiRef();

    const formApiRef = React.useRef<FormApi | any>({});

    const actions = [
        {
            label: t('page.bustia.accio.moureAnotacions.label'),
            icon: 'turn_right',
        },
        {
            label: t('page.bustia.accio.perDefecte.label'),
            icon: 'check_box',
            hidden: (row:any) => row.perDefecte,
        },
        {
            label: t('page.bustia.accio.activar.label'),
            icon: 'check',
            hidden: (row:any) => row.activa,
        },
        {
            label: t('page.bustia.accio.desactivar.label'),
            icon: 'close',
            hidden: (row:any) => !row.activa,
        },
        {
            label: t('common.delete'),
            icon: 'delete',
            onClick: () => {
                formApiRef.current?.delete()
                refresh?.()
                setEntity(undefined)
            },
        },
    ];

    const {apiRef, content, refresh} = useOrganigrama({
        filter: springFilter,
        namedQueries: namedQueries,
        onClick: (_id:any, row:any) => setEntity(row)
    })

    const create = (event:any) => {
        formDialogApiRef.current?.show(undefined, {entitat: {id: currentEntitatId}})
            .then((response:any) => {
                setEntity(response)
                refresh()

                // TODO: revisar seleción al crear
                apiRef.current?.focusItem(event, response.id);
            })
    }

    const update = () => {
        formApiRef.current?.save()
        refresh()
    }

    return (
        <GridPage>
            <CardPage title={t('page.bustia.grid.title')}>
                <BustiaFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />

                <Grid container>
                    <Grid container size={4} rowSpacing={1} columnSpacing={1} pr={1}>
                        <Grid size={6}>
                            <ToolbarButton
                                icon={'list'}
                                variant={'contained'}
                                component={Link}
                                to={'/bustiaAdmin'}
                            >{t('page.bustia.vista')}</ToolbarButton>
                        </Grid>

                        <Grid size={6} display={'flex'} justifyContent={'end'}>
                            <ToolbarButton
                                title={t('common.create')}
                                icon={'add'}
                                onClick={create}
                                color={'primary'}
                            >
                                {t('page.bustia.accio.new.label')}
                            </ToolbarButton>
                            <BustiaFormDialog formDialogApiRef={formDialogApiRef} />
                        </Grid>

                        <Grid size={12}>
                            {content}
                        </Grid>
                    </Grid>
                    <Grid size={8}>
                        <Load value={entity} noEffect>
                            <BustiaOrganigramaForm apiRef={formApiRef} entity={entity} toolbarElementsWithPositions={[
                                {
                                    position: 2,
                                    element: <IconButton title={t('common.update')} onClick={update}><Icon>save</Icon></IconButton>,
                                },
                                {
                                    position: 2,
                                    element: <MenuActionButton
                                        id={entity?.id}
                                        entity={entity}
                                        ButtonComponent={IconButton}
                                        buttonLabel={<Icon>more_vert</Icon>}
                                        actions={actions}
                                    />,
                                }
                            ]}/>
                        </Load>
                    </Grid>
                </Grid>
            </CardPage>
        </GridPage>
    );
}