import Grid from "@mui/material/Grid";
import GridFormField from "../../components/GridFormField.tsx";
import React from "react";
import {useTranslation} from "react-i18next";
import {MuiDataFormDialogApi, MuiForm, MuiFormDialog} from "reactlib";
import {BustiaPermisosForm, useBustiaPermisosColumns} from "./BustiaPermisosForm.tsx";
import {AclPermissionGrid} from "../../components/AclPermissionManager.tsx";

export const BustiaForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField size={12} name="unitatOrganitzativa" componentProps={{ size: "small" }} />
        <GridFormField size={12} name="nom" componentProps={{ size: "small" }} />
    </Grid>
}

export const BustiaFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = ({formDialogApiRef}) => {
    const { t } = useTranslation();

    return <MuiFormDialog
        resourceName="bustiaResource"
        resourceTitle={t('page.bustia.title')}
        apiRef={formDialogApiRef}
        dialogComponentProps={{ fullWidth: true, maxWidth: 'md' }}
        formComponentProps={{ commonFieldComponentProps: { size: 'small' } }}
    >
        <BustiaForm />
    </MuiFormDialog>
}

export const BustiaOrganigramaForm = ({apiRef, entity, toolbarElementsWithPositions}:any) => {
    const { t } = useTranslation();

    return (
        <MuiForm
            key={entity.id}
            id={entity.id}
            title={t('page.bustia.accio.update.title')}
            resourceName={'bustiaResource'}
            apiRef={apiRef}
            goBackLink={'/bustiaAdminOrganigrama'}

            hiddenBackButton
            hiddenRevertButton
            hiddenSaveButton
            hiddenDeleteButton
            toolbarElementsWithPositions={toolbarElementsWithPositions}
        >
            <BustiaForm />

            <AclPermissionGrid
                resourceId={entity.id}
                resourceType={'BUSTIA'}
                columns={useBustiaPermisosColumns()}
                formContent={<BustiaPermisosForm/>}
            />
        </MuiForm>
    );
}