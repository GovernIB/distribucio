import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
// import GridFormField from "../../../components/GridFormField.tsx";

const ReenviarBackofficeForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector expanded disabled />

        {/*<GridFormField size={12} name="motiu" type={'textarea'}/>*/}
    </Grid>
}

const ReenviarBackoffice = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => t('page.registre.accio.reenviarBackoffice.titleMassive', { num: params.ids?.length })}
        action={'REENVIAR_BACKOFFICE'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        // initOnChange
        {...props}
    >
        <ReenviarBackofficeForm/>
    </FormActionDialog>
}

const useReenviarBackoffice = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.reenviarBackoffice.ok'), 'success');
    // }

    return {
        handleShow,
        content: <ReenviarBackoffice apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useReenviarBackoffice;