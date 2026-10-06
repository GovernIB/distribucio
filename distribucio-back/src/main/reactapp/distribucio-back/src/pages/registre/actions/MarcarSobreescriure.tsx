import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
// import GridFormField from "../../../components/GridFormField.tsx";

const MarcarSobreescriureForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector expanded disabled />

        {/*<GridFormField size={12} name="motiu" type={'textarea'}/>*/}
    </Grid>
}

const MarcarSobreescriure = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => params.massive
            ?t('page.registre.accio.sobreescriure.titleMassive', { num: params.ids?.length })
            :t('page.registre.accio.sobreescriure.title')}
        action={'MARCAR_SOBREESCRIURE'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        // initOnChange
        {...props}
    >
        <MarcarSobreescriureForm/>
    </FormActionDialog>
}

const useMarcarSobreescriure = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.sobreescriure.ok'), 'success');
    // }

    return {
        handleShow,
        content: <MarcarSobreescriure apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useMarcarSobreescriure;