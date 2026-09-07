import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {useOrganigrama} from "../BustiaOrganigrama.tsx";

const MoureAnotacionsForm = () => {
    const { apiRef } = useFormContext();

    const {content} = useOrganigrama({
        checkboxSelection: true,
        onSelectedItemsChange: (_event:any, id:any) => apiRef.current?.setFieldValue("bustia", {id})
    })

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <Grid size={12}>{content}</Grid>

        <GridFormField size={12} name="comment" type={"textarea"}/>
    </Grid>
}

export const MoureAnotacions = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"bustiaResource"}
        title={(dades) => t('page.bustia.accio.moureAnotacions.title', {nom: dades?.nom})}
        action={"MOURE_ANOTACIO"}
        buttons={[
            {icon: 'turn_right', text: t('page.bustia.accio.moureAnotacions.label'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <MoureAnotacionsForm/>
    </FormActionDialog>
}

export const useMoureAnotacions = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {nom: row.nom})
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.bustia.accio.moureAnotacions.ok', {expedient: response?.nom}), 'success');
    }

    return {
        handleShow,
        content: <MoureAnotacions apiRef={apiRef} onSuccess={onSuccess}/>
    }
}