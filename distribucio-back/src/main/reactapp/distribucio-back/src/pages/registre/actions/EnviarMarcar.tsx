import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
import GridFormField from "../../../components/GridFormField.tsx";

const EnviarMarcarForm = () => {
    const { t } = useTranslation();
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector />

        <GridFormField size={12} name="destinatari" componentProps={{ helperText: t('page.registre.accio.enviaMarca.destinatari') }} type={'textarea'}/>
        <GridFormField size={12} name="motiu" type={'textarea'}/>
    </Grid>
}

const EnviarMarcar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => t('page.registre.accio.enviaMarca.titleMassive', { num: params.ids?.length })}
        action={'ENVIAR_MARCAR'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        initOnChange
        {...props}
    >
        <EnviarMarcarForm/>
    </FormActionDialog>
}

const useEnviarMarcar = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.enviaMarca.ok'), 'success');
    // }

    return {
        handleShow,
        content: <EnviarMarcar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useEnviarMarcar;