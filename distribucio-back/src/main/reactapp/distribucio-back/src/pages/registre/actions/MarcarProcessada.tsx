import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";

const MarcarProcessadaForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector disabled />

        <GridFormField size={12} name="motiu" type={'textarea'}/>
    </Grid>
}

const MarcarProcessada = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => params.massive
            ?t('page.registre.accio.marcarProcessada.titleMassive', { num: params.ids?.length })
            :t('page.registre.accio.marcarProcessada.title')}
        action={'MARCAR_PROCESSADA'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        // initOnChange
        {...props}
    >
        <MarcarProcessadaForm/>
    </FormActionDialog>
}

const useMarcarProcessada = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.marcarProcessada.ok'), 'success');
    // }

    return {
        handleShow,
        content: <MarcarProcessada apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useMarcarProcessada;