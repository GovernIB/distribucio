import {Grid} from "@mui/material";
import {useFormContext, useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import Alert from "@mui/material/Alert";
import {useEffect, useMemo, useState} from "react";

const DescargaMassivaForm = ({setDisabled}:any) => {
    const { data } = useFormContext()

    useEffect(() => {
        setDisabled(data.disabled)
    }, [data.disabled]);

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector />

        {data.info?.map((info:string, i:number) => <Grid size={12} key={`info-${i}`}><Alert severity={'info'}>{info}</Alert></Grid>)}
        {data.errors?.map((error:string, i:number) => <Grid size={12} key={`error-${i}`}><Alert severity={'error'}>{error}</Alert></Grid>)}

        <GridFormField size={12} name="estructuraCarpetes" disabled={data.disabled}/>
        <GridFormField size={12} name="versioImprimible" disabled={data.disabled}/>
        <GridFormField size={12} name="nomDocument" disabled={data.disabled}/>
    </Grid>
}

const DescargaMassiva = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => t('page.registre.accio.descargaMassiva.titleMassive', { num: params.ids?.length })}
        action={'DESCARREGAR_MASSIU'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        initOnChange
        {...props}
    >
        <DescargaMassivaForm setDisabled={props.setDisabled}/>
    </FormActionDialog>
}

const useDescargaMassiva = (onSuccess?: (result?: any) => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const [disabled, setDisabled] = useState<boolean>(true)

    const buttons = useMemo(() => [
        {
            value: true,
            text: t('common.download'),
            icon: 'bolt',
            componentProps: { variant: 'contained', disabled: disabled, },
        },
        {
            value: false,
            text: t('common.cancel'),
            componentProps: { variant: 'outlined' },
        },
    ], [t, disabled])

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.descargaMassiva.ok'), 'success');
    // }

    return {
        handleShow,
        content: <DescargaMassiva apiRef={apiRef} buttons={buttons} setDisabled={setDisabled} onSuccess={onSuccess}/>
    }
}
export default useDescargaMassiva;