import {useMuiFormDialogApiRef, useBaseAppContext, useFormContext} from "reactlib";
import {Grid} from "@mui/material";
import {useTranslation} from "react-i18next";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import * as builder from '../../../util/springFilterUtils';

const DesactivarForm = () => {
    const { data } = useFormContext();

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="bustia" filter={
            builder.and(
                builder.eq('unitatOrganitzativa.id', data.unitatOrganitzativa?.id),
                builder.neq('id', data.id),
            )
        }/>
    </Grid>
}

export const Desactivar = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"bustiaResource"}
        title={t('page.bustia.accio.desactivar.title')}
        action={"DESACTIVAR"}
        buttons={[
            {icon: 'save', text: t('common.save'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        dialogComponentProps={{ fullWidth: true, maxWidth: 'sm' }}
        {...props}
    >
        <DesactivarForm/>
    </FormActionDialog>
}

export const useDesactivar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        apiRef.current?.show?.(id, {id, unitatOrganitzativa: row.unitatOrganitzativa})
    }
    const onSuccess = (response:any) :void => {
        refresh?.()
        temporalMessageShow(null, t('page.bustia.accio.desactivar.ok', {expedient: response?.nom}), 'success');
    }

    return {
        handleShow,
        content: <Desactivar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}