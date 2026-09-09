import {useTranslation} from "react-i18next";
import {FormApi, MuiDialog, useBaseAppContext, useFormContext, useResourceApiService} from "reactlib";
import React, {useMemo, useState} from "react";
import {Grid} from "@mui/material";
import Load from "../../components/Load.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import GridFormField from "../../components/GridFormField.tsx";
import {configTypeValueLabel, getFieldFromItem} from "./PropietatsForm.tsx";

const PropietatsForm = ({item, itemField}:any) => {
    const {data, fields, apiRef} = useFormContext()

    const valueField = useMemo(() => ({
        ...itemField,
        label: fields?.filter(i=>i.name=='value')[0]?.label,
        key: data?.key,
    }), [itemField, data?.key]);

    if (data.id && valueField.type == 'checkbox' && typeof data.value == 'string') {
        apiRef.current?.setFieldValue('value', data.value == 'true')
    }
    if (!data?.id && !data?.description) {
        apiRef.current?.setFieldValue('key', item?.key)
        apiRef.current?.setFieldValue('description', item?.description)
        apiRef.current?.setFieldValue('value', itemField?.value)
    }

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name={'entitat'} disabled required/>
        <GridFormField size={12} name={'key'} disabled required/>
        <GridFormField size={12} name={'value'} field={valueField} required/>
    </Grid>
}

const sortModel:any[] = [{field: 'entitatCodi', sort: 'asc'}];

export const usePropietatsDialog = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [item, setItem] = useState<any>();
    const [itemField, setItemField] = useState<any>();

    const filter = useMemo(() =>
        builder.and(
            builder.neq('entitat.codi', null),
            builder.like('key', item?.key?.replace('es.caib.distribucio', '') )
        ), [item])

    // El valor es mostra amb l'etiqueta traduida del tipus de propietat, igual que al
    // formulari d'edicio. El valor desat no canvia: si no hi ha traduccio es mostra tal qual.
    const columns:any[] = useMemo(() => [
        {
            field: 'entitatCodi',
            flex: 0.25,
        },
        {
            field: 'key',
            flex: 1,
        },
        {
            field: 'value',
            flex: 0.5,
            valueFormatter: (value: any) =>
                value != null && item?.type?.id != null
                    ? configTypeValueLabel(t, item.type.id, value)
                    : value,
        },
    ], [item, t]);

    const handleOpen = (_id:any, row:any) => {
        // console.log("row", row)
        setItem(row)
        setItemField(getFieldFromItem(row, t))
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setItem(undefined)
            setItemField(undefined)
            setOpen(false);
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: 'close',
            componentProps: { variant: 'outlined' }
        },
    ]


    const {
        isReady: apiIsReady,
        patch: apiPatch,
    } = useResourceApiService('configResource');
    const { temporalMessageShow, t: tLib } = useBaseAppContext();
    const formApiRef = React.useRef<FormApi | any>({});

    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: false,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: false,
            onClick: (id:any) => {
                if (apiIsReady) {
                    apiPatch(id, {data: {value: null}})
                        .then(() => {
                            formApiRef.current?.refresh()
                            temporalMessageShow(null, tLib('form.update.success'), 'success');
                        })
                        .catch((error) =>
                            temporalMessageShow(null, error.message, 'error')
                        );
                }
            },
            hidden: (row:any) => row.value == null
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={item?.description}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={buttons}
            buttonCallback={() :void => {
                handleClose();
            }}
        >
            <Load value={item}>
                <StyledMuiGrid
                    apiRef={formApiRef}
                    resourceName={'configResource'}
                    columns={columns}
                    filter={filter}
                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.propietats.title')}
                    toolbarCreateTitle={t('page.propietats.action.new.label')}
                    popupEditFormContent={<PropietatsForm item={item} itemField={itemField}/>}
                    sortModel={sortModel}
                    autoHeight
                    paginationActive
                    rowAdditionalActions={actions}

                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                />
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}