import * as React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import {
    FormField, MuiDataGridColDef,
    MuiDataGridDialog,
    MuiDataGridDialogApi,
} from 'reactlib';
import {ROLE_ADMIN, ROLE_SUPER, useDistribucioContext} from "./DistribucioContext.ts";
import StyledMuiGrid from "./StyledMuiGrid.tsx";

const AclEntryForm: React.FC = () => {
    return <Grid container spacing={2}>
        <Grid size={4}>
            <FormField name="subjectType" />
        </Grid>
        <Grid size={8}>
            <FormField name="subjectValue" />
        </Grid>
        <Grid size={12}>
            <FormField name="readAllowed" />
        </Grid>
    </Grid>;
}

export const AclPermissionGrid = (
    {
        resourceId,
        resourceType,
        columns = [{
            field: 'subjectType',
            sortable: false,
            flex: 2
        }, {
            field: 'subjectValue',
            sortable: false,
            flex: 5
        }, {
            field: 'readAllowed',
            sortable: false,
            flex: 1
        }],
        formContent = <AclEntryForm />,
        additionalData,
        ...other
    }: {resourceId: any, resourceType: string, columns?:MuiDataGridColDef[], formContent?: any, additionalData?: any }
) => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext()
    const gestorReadOnly = !(currentRole == ROLE_SUPER ||  currentRole == ROLE_ADMIN);

    return <StyledMuiGrid
        title={t('component.AclPermissionManager.title')}
        popupEditFormDialogResourceTitle={t('component.AclPermissionManager.resourceTitle')}
        resourceName={"aclEntryResource"}
        filter={"resourceType:'" + resourceType + "' and resourceId:" + resourceId}
        columns={columns}
        popupEditUpdateActive
        popupEditFormContent={formContent}
        formAdditionalData={{
            resourceId,
            resourceType,
            readAllowed: true,
            ...additionalData
        }}
        paginationActive
        readOnly={gestorReadOnly || undefined}
        {...other}
    />
}

export const useAclPermissionManager = (resourceType: string) => useAclCustomPermissionManager({resourceType, additionalData: { readAllowed: true }})
export const useAclCustomPermissionManager = (
    {
        resourceType,
        columns = [{
            field: 'subjectType',
            sortable: false,
            flex: 2
        }, {
            field: 'subjectValue',
            sortable: false,
            flex: 5
        }, {
            field: 'readAllowed',
            sortable: false,
            flex: 1
        }],
        formContent = <AclEntryForm />,
        additionalData,
        onEntryChanged
    }: {resourceType: string, columns?:MuiDataGridColDef[], formContent?: any, additionalData?: any, onEntryChanged?: (resourceId: any) => void }
) => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext()
    const gestorReadOnly = !(currentRole == ROLE_SUPER ||  currentRole == ROLE_ADMIN);

    const dataGridDialogApiRef = React.useRef<MuiDataGridDialogApi | any>({});
    const currentResourceIdRef = React.useRef<any>(undefined);
    const show = (id: any, description: string) => {
        currentResourceIdRef.current = id;
        dataGridDialogApiRef.current.show({
            title: description,
            dataGridComponentProps: {
                title: t('component.AclPermissionManager.title'),
                toolbarHideQuickFilter: true,
                fixedFilter: "resourceType:'" + resourceType + "' and resourceId:" + id,
                staticSortModel: [{ field: 'subjectType', sort: 'asc' }, { field: 'subjectValue', sort: 'asc' }],
                formAdditionalData: (_row: any, action: string) => ({
                    resourceType,
                    resourceId: id,
                    ...(action === 'create'
                        ? {
                            subjectType: 'ROLE',
                            ...additionalData
                        }
                        : {}),
                }),
                readOnly: gestorReadOnly,
                popupEditActive: true,
                popupEditFormContent: formContent,
                popupEditFormDialogResourceTitle: t('component.AclPermissionManager.resourceTitle'),
                // rowHideDeleteButton: gestorReadOnly,
                // Permet als consumidors del hook saber que s'ha creat/editat/eliminat un permís (p.ex. per
                // refrescar un comptador de permisos mostrat en una altra pantalla, com l'organigrama d'entitats).
                onRowCreate: () => onEntryChanged?.(currentResourceIdRef.current),
                onRowUpdate: () => onEntryChanged?.(currentResourceIdRef.current),
                onRowDelete: () => onEntryChanged?.(currentResourceIdRef.current),
            }
        });
    }
    const close = () => dataGridDialogApiRef.current.close();
    const component = <MuiDataGridDialog
        resourceName="aclEntryResource"
        columns={columns}
        apiRef={dataGridDialogApiRef}
        dialogComponentProps={{
            fullWidth: true,
            maxWidth: 'lg'
        }}/>;
    return {
        show,
        close,
        component
    };
}
