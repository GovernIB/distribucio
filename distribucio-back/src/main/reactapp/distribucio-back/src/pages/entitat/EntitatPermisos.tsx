import {AclPermissionGrid, useAclCustomPermissionManager} from "../../components/AclPermissionManager.tsx";
import React from "react";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import {CardPage} from "../../components/CardData.tsx";
import {GridPage, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import {Grid} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";

const EntitatPermisForm: React.FC = () => {
    const { t } = useTranslation();
    const {data} = useFormContext();

    return <Grid container spacing={2}>
        <GridFormField size={12} name={"subjectType"} disabled={data.id} />
        <GridFormField size={12} name={"subjectValue"} disabled={data.id} />

        <GridFormField size={4} name={"adminAllowed"} label={t("page.entitats.grid.adminAllowed")} />
        <GridFormField size={4} name={"perm0Allowed"} label={t("page.entitats.grid.perm0Allowed")} />
        <GridFormField size={4} name={"readAllowed"} label={t("page.entitats.grid.readAllowed")} />
    </Grid>;
}


const useEntitatPermisosColumns = () => {
    const { t } = useTranslation();

    return [
        {
            field: 'subjectType',
            sortable: false,
            flex: 2
        }, {
            field: 'subjectValue',
            sortable: false,
            flex: 3
        }, {
            field: 'adminAllowed',
            headerName: t("page.entitats.grid.adminAllowed"),
            sortable: false,
            flex: 1,
            type: 'boolean'
        }, {
            field: 'perm0Allowed',
            headerName: t("page.entitats.grid.perm0Allowed"),
            sortable: false,
            flex: 1,
            type: 'boolean'
        }, {
            field: 'readAllowed',
            headerName: t("page.entitats.grid.readAllowed"),
            sortable: false,
            flex: 1,
            type: 'boolean'
        }
    ] as any[];
}

export const EntitatPermisos = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = useDistribucioContext();

    return (
        <GridPage>
            <CardPage title={t('page.entitats.permis.title')}>
                <AclPermissionGrid
                    resourceId={currentEntitatId}
                    resourceType={'ENTITAT'}
                    columns={useEntitatPermisosColumns()}
                    formContent={<EntitatPermisForm/>}
                />
            </CardPage>
        </GridPage>
    );
}

export const useEntitatPermisosDialog = (refresh?: () => void) => {
    return useAclCustomPermissionManager({
        resourceType: 'ENTITAT',
        columns: useEntitatPermisosColumns(),
        formContent: <EntitatPermisForm/>,
        onEntryChanged: refresh
    });
}