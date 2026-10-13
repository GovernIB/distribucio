import {Grid} from "@mui/material";
import {useFormContext, useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const AssignarForm = () => {
    const {data} = useFormContext()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="usuari" requestParams={{ registreId: data.id }} />
        <GridFormField size={12} name="motiu" type={'textarea'}/>
    </Grid>
}

const Assignar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={t('page.registre.accio.assignar.title')}
        action={'ASSIGNAR'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        // initOnChange
        {...props}
    >
        <AssignarForm/>
    </FormActionDialog>
}

const useAssignar = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any) :void => {
        apiRef.current?.show?.(id, {id})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.assignar.ok'), 'success');
    // }

    return {
        handleShow,
        content: <Assignar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useAssignar;