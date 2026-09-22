import {Box, Checkbox, FormControlLabel, Grid, Icon, IconButton, Tooltip, Typography} from "@mui/material";
import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import {FormField, useFormContext, useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
import {useOrganigrama} from "../../bustia/BustiaOrganigrama.tsx";
import {QuickFilter} from "../../propietats/Propietats.tsx";
import React, {useMemo} from "react";
import {useActions} from "../../bustia/detail/BustiaActions.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import Load from "../../../components/Load.tsx";

const UsuarisPermis = ({rows}:any) => {
    const { t } = useTranslation();

    const tableContent = (
        <Paper elevation={3} sx={{ maxWidth: 350 }}>
            <TableContainer>
                <Table size="small">
                    <TableHead>
                        <TableRow sx={{ bgcolor: 'primary.light' }}>
                            <TableCell sx={{ fontWeight: 'bold' }}>{t('component.RegistreReenviar.grid.userCodi')}</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>{t('component.RegistreReenviar.grid.userNom')}</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows && rows.length > 0 ? (
                            rows.map((row: any) => (
                                <TableRow key={row.value} hover>
                                    <TableCell component="th" scope="row">
                                        {row.value}
                                    </TableCell>
                                    <TableCell>{row.description}</TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={2}>
                                    <Box sx={{ width: '100%', textAlign: 'center', p: 2 }}>
                                        <Icon fontSize="large" color="disabled">
                                            block
                                        </Icon>
                                        <Typography component="div" variant="h6" color="text.secondary">
                                            {t('component.RegistreReenviar.grid.empty')}
                                        </Typography>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </TableContainer>
        </Paper>
    )

    return (
        <Tooltip
            title={tableContent}
            placement="bottom-start"
            arrow // Opcional: añade una flechita que ayuda visualmente a conectar el icono con la tabla
            disableInteractive={false} // CLAVE: Permite interactuar (hover) con el contenido del tooltip
            componentsProps={{
                tooltip: {
                    sx: {
                        bgcolor: 'transparent', // Hacemos transparente el fondo por defecto del tooltip
                        boxShadow: 'none',      // Quitamos la sombra por defecto (ya la tiene el Paper)
                        p: 0,
                        maxWidth: 'none',       // Anulamos el ancho máximo por defecto del tooltip
                        '& .MuiTooltip-arrow': {
                            color: 'background.paper', // La flecha coincide con el color del Paper
                        }
                    }
                },
                popper: {
                    sx: {
                        zIndex: 1300,
                        // Esto evita cualquier salto o recálculo extraño de posición
                        willChange: 'transform',
                    }
                }
            }}
        >
            <IconButton color="primary">
                <Icon>group</Icon>
            </IconButton>
        </Tooltip>
    );
}

const BustiaGrid = () => {
    const { t } = useTranslation();
    const {data, apiRef: formRef, fields} = useFormContext()

    const fieldAssignar = fields?.filter(i=>i.name=='user')[0];
    const fieldComentaris = fields?.filter(i=>i.name=='comentari')[0];

    const columns = useMemo(() => [
        { field: 'nom', flex: 1,
            renderCell: (params:any) => <>
                {params.formattedValue} {params.row.perDefecte &&
                <strong>({t('page.bustia.grid.principal')})</strong>}
            </>,
        },
        { field: 'camp1', flex: 1, headerName: '',
            renderCell: (params:any) => {
                const d = formRef.current?.getData()
                return <Load value={!d.coneixement?.includes(params.id)} noEffect>
                    <FormField
                        key={`assignar#${params.id}`}
                        name={"assignar" + (d?.assignar?.[params.id] ? `#${params.id}` : '')}
                        value={d?.assignar?.[params.id]}
                        field={fieldAssignar}
                        onChange={(value) => {
                            formRef?.current?.setFieldValue('assignar', {
                                ...d?.assignar,
                                [params.id]: value,
                            })
                        }}
                        componentProps={{size: "small"}}
                        requestParams={{ bustiaId: params.id }}
                    />
                </Load>
            },
            hidden: !data.assignarActiva,
        },
        { field: 'camp2', flex: 1, headerName: '',
            renderCell: (params:any) => {
                const d = formRef.current?.getData()
                return <Load value={!d.coneixement?.includes(params.id)} noEffect>
                    <FormField
                        key={`comentaris#${params.id}`}
                        name={"comentaris" + (d?.comentaris?.[params.id] ? `#${params.id}` : '')}
                        value={d?.comentaris?.[params.id]}
                        field={fieldComentaris}
                        onChange={(value) => {
                            formRef?.current?.setFieldValue('comentaris', {
                                ...d?.comentaris,
                                [params.id]: value,
                            })
                        }}
                        componentProps={{size: "small"}}
                        disabled={!d?.assignar?.[params.id]}
                    />
                </Load>
            },
            hidden: !data.assignarActiva,
        },
    ].filter(a => !a.hidden), [t])

    const actions = [
        {
            label: t('common.delete'),
            icon: 'delete',
            showInMenu: false,
            onClick: (id:any) => {
                formRef.current?.setFieldValue("busties",
                    formRef.current?.getData().busties.filter((b:any) => b != id))
            }
        }
    ]

    const springfilter = useMemo(() =>
        builder.inside("id", data.busties, '0'),
        [data.busties])

    return (
        <StyledMuiGrid
            resourceName="bustiaResource"
            title={t('component.RegistreReenviar.busties')}
            columns={columns}
            filter={springfilter}

            rowAdditionalActions={actions}

            toolbarHideRefresh
            paginationActive
            autoHeight
            readOnly
        />
    )
}

const Organig = ({quickFilter, favoriteSearch}:any) => {
    const { t } = useTranslation();
    const {data, apiRef} = useFormContext()

    const namedQueries = useMemo(() => favoriteSearch ?['FAVORITA'] :[], [favoriteSearch])
    const perspectives = useMemo(() => data.permisActiva ?['FAVORITA', 'USUARIS_PERMIS'] :['FAVORITA'], [data.permisActiva])

    const {content, refresh} = useOrganigrama({
        checkboxSelection: true,
        multiSelect: true,
        quickFilter: quickFilter,
        perspectives: perspectives,
        namedQueries: namedQueries,
        selectedItems: data.busties,
        onSelectedItemsChange: (_event:any, id:any) => {
            const d = apiRef.current?.getData()
            apiRef.current?.setFieldValue("busties", id)
            if (d.coneixementActiva)
                apiRef.current?.setFieldValue("coneixement",
                    d.coneixement?.filter((c:any) => id?.includes(c)))
        },
        renderCell: (item:any) => {
            if (item.class == 'bustia') {
                const coneixement = data.coneixement?.includes(item.id)
                return <>
                    <Box display={'flex'} alignItems={'center'} gap={1} onClick={item?.onClick}>
                        <Icon>{item.icon}</Icon>{item.label}

                        {data.coneixementActiva && <IconButton
                            title={coneixement
                                ?t('component.RegistreReenviar.coneixement.desmarcar')
                                :t('component.RegistreReenviar.coneixement.marcar')}
                            onClick={() => {
                                if (coneixement) {
                                    apiRef.current?.setFieldValue("busties",
                                        data.busties.filter((b:any) => b != item.id))
                                    apiRef.current?.setFieldValue("coneixement",
                                        data.coneixement.filter((b:any) => b != item.id))
                                } else {
                                    apiRef.current?.setFieldValue("busties", [...data.busties, item.id])
                                    apiRef.current?.setFieldValue("coneixement", [...data.coneixement, item.id])
                                }
                            } }
                        ><Icon color={coneixement ?'info' :'disabled'} >info</Icon></IconButton>}

                        {data.favoritaActiva && <IconButton
                            title={item.data.favorita
                                ? t('component.RegistreReenviar.favorit.desmarcar')
                                : t('component.RegistreReenviar.favorit.marcar')}
                            onClick={() => favorite(item.id, !item.data.favorita)}
                        ><Icon color={item.data.favorita ? 'warning' : 'disabled'} >star</Icon></IconButton>}

                        {data.permisActiva && <UsuarisPermis rows={item.data.usuarisPermis}/>}
                    </Box>
                </>
            }
        }
    })

    const {favorite} = useActions(refresh)

    return <>
        {content}
        {/*{dialogComponent}*/}
    </>
}

/**
 * `hideAmbCopia`: la Vista de moviments reenvia sense mostrar el camp "Deixar còpia" (sempre activat, forçat pel backend).
 */
export const ReenviarForm = ({ hideAmbCopia }: { hideAmbCopia?: boolean } = {}) => {
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [favoriteSearch, setFavoriteSearch] = React.useState<boolean>(false);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector />

        <Grid container size={6} direction={"row"} columnSpacing={1} rowSpacing={1}>
            <Grid size={6}><QuickFilter label={t('component.RegistreReenviar.quickfilter')} onChange={setQuickFilter}/></Grid>
            <Grid size={6}><FormControlLabel
                control={<Checkbox value={favoriteSearch} onChange={() => setFavoriteSearch(prev => !prev)} />}
                label={<Box display={'flex'} justifyContent={'center'}>
                    <Icon color={'warning'}>star</Icon>{t('component.RegistreReenviar.favoritfilter')}
                </Box>} /></Grid>
            <Grid size={12}><Organig quickFilter={quickFilter} favoriteSearch={favoriteSearch} /></Grid>
        </Grid>

        <Grid container size={6} direction={"column"} columnSpacing={1} rowSpacing={1}>
            <Grid size={12}><BustiaGrid/></Grid>
            {!hideAmbCopia && <GridFormField size={12} name="ambCopia" type={'checkbox'}/>}
            <GridFormField size={12} name="comentari" type={'textarea'}/>
        </Grid>
    </Grid>
}

const Reenviar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => params.massive
            ?t('page.registre.accio.reenviar.titleMassive', { num: params.ids?.length })
            :t('page.registre.accio.reenviar.title')}
        action={'REENVIAR'}
        dialogComponentProps={{ fullWidth: true, maxWidth: 'xl' }}
        initOnChange
        {...props}
    >
        <ReenviarForm/>
    </FormActionDialog>
}

const useReenviar = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.reenviar.ok'), 'success');
    // }

    return {
        handleShow,
        content: <Reenviar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useReenviar;