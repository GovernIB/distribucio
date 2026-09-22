import {Grid} from "@mui/material";
import {useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {RegistreSelector} from "./RegistreSelector.tsx";
// import GridFormField from "../../../components/GridFormField.tsx";

const TornarProcessarForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <RegistreSelector expanded />

        {/*<GridFormField size={12} name="motiu" type={'textarea'}/>*/}
    </Grid>
}

const TornarProcessar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={(params) => params.massive
            ?t('page.registre.accio.tornarProcessar.titleMassive', { num: params.ids?.length })
            :t('page.registre.accio.tornarProcessar.title')}
        action={'TORNAR_PROCESSAR'}
        // dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        initOnChange
        {...props}
    >
        <TornarProcessarForm/>
    </FormActionDialog>
}

const useTornarProcessar = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive, tempIds: ids})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.tornarProcessar.ok'), 'success');
    // }

    return {
        handleShow,
        content: <TornarProcessar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useTornarProcessar;