import {Box, Checkbox, FormControlLabel, Grid, Icon} from "@mui/material";
import {useFormContext, useMuiFormDialogApiRef} from "reactlib";
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

const columns = (t:any) => [
    { field: 'nom', flex: 0.1,
        renderCell: (params:any) =>  <>{params.formattedValue} {params.row.perDefecte && <strong>({t('page.bustia.grid.principal')})</strong>}</>,
    },
]
const perspectives = ['FAVORITA'];
const BustiaGrid = () => {
    const { t } = useTranslation();
    const {data, apiRef: formRef} = useFormContext()

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
            columns={columns(t)}
            filter={springfilter}
            perspectives={perspectives}

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

    const namedQueries = useMemo(() => favoriteSearch ?['FAVORITA'] :[],[favoriteSearch])

    const {content, refresh} = useOrganigrama({
        checkboxSelection: true,
        multiSelect: true,
        quickFilter: quickFilter,
        perspectives: perspectives,
        namedQueries: namedQueries,
        selectedItems: data.busties,
        onSelectedItemsChange: (_event:any, id:any) => {
            apiRef.current?.setFieldValue("busties", id)
            if (data.coneixementActiva)
                apiRef.current?.setFieldValue("coneixement",
                    data.coneixement?.filter((c:any) => id?.includes(c)))
        },
        renderCell: (item:any) => {
            if (item.class == 'bustia') {
                const coneixement = data.coneixement?.includes(item.id)
                return <>
                    <Box display={'flex'} alignItems={'center'} gap={1} onClick={item?.onClick}>
                        <Icon>{item.icon}</Icon>{item.label}

                        {data.coneixementActiva && <Icon
                            title={coneixement
                                ?t('component.RegistreReenviar.coneixement.desmarcar')
                                :t('component.RegistreReenviar.coneixement.marcar')}
                            color={coneixement ?'info' :'disabled'}
                            onClick={() => {
                                if (coneixement) {
                                    apiRef.current?.setFieldValue("busties",
                                        data.coneixement.filter((b:any) => b != item.id))
                                    apiRef.current?.setFieldValue("coneixement",
                                        data.coneixement.filter((b:any) => b != item.id))
                                } else {
                                    apiRef.current?.setFieldValue("busties", [...data.busties, item.id])
                                    apiRef.current?.setFieldValue("coneixement", [...data.coneixement, item.id])
                                }
                            } }
                        >info</Icon>}
                        <Icon
                            title={item.data.favorita
                                ?t('component.RegistreReenviar.favorit.desmarcar')
                                :t('component.RegistreReenviar.favorit.marcar')}
                            color={item.data.favorita ?'warning' :'disabled'}
                            onClick={() => favorite(item.id, !item.data.favorita) }
                        >star</Icon>
                    </Box>
                </>
            }
        }
    })

    const {favorite} = useActions(refresh)

    return content
}

const ReenviarForm = () => {
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
            <GridFormField size={12} name="ambCopia" type={'checkbox'}/>
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