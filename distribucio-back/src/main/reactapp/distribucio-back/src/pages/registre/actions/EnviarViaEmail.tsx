import {Grid} from "@mui/material";
import {useFormContext, useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
import Alert from "@mui/material/Alert";

const EnviarViaEmailForm = () => {
    const { t } = useTranslation();
    const {data} = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector disabled />

        {data.massive && data.vistaMoviments && <Grid size={12}>
            <Alert severity={'warning'}>L'enviament via email es farà dels registres diferents a partir de la selecció de moviments</Alert>
        </Grid>}

        <GridFormField size={12} name="destinatari" type={'textarea'} componentProps={{ helperText: t('page.registre.accio.email.form.destinatari') }}/>
        <GridFormField size={12} name="motiu" type={'textarea'}/>
    </Grid>
}

const EnviarViaEmail = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => params.massive
            ?t('page.registre.accio.email.titleMassive', { num: params.ids?.length })
            :t('page.registre.accio.email.title')}
        action={'ENVIAR_EMAIL'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        // initOnChange
        {...props}
    >
        <EnviarViaEmailForm/>
    </FormActionDialog>
}

const useEnviarViaEmail = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean, additionalProps:any = {}) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids, ...additionalProps})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.email.ok'), 'success');
    // }

    return {
        handleShow,
        content: <EnviarViaEmail apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useEnviarViaEmail;