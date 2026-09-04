import React from "react";
import {useTranslation} from "react-i18next";
import {useFormContext} from "reactlib";
import {FormControl, FormControlLabel, FormLabel, Grid, Radio, RadioGroup} from "@mui/material";
import GridFormField from "../../components/GridFormField.tsx";

export const useBustiaPermisosColumns = () => {
    const { t } = useTranslation();

    return [
        {
            field: 'subjectType',
            sortable: false,
            flex: 2
        }, {
            field: 'subjectValue',
            sortable: false,
            flex: 4
        }, {
            field: 'writeAllowed',
            headerName: t("page.bustia.grid.writeAllowed"),
            renderCell: (params: any) => (params.row.writeAllowed
                ?t("page.bustia.permisos.writeAllowed")
                :t("page.bustia.permisos.readAllowed")),
            sortable: false,
            flex: 1
        }
    ];
}

export const BustiaPermisosForm: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();

    const value = data.writeAllowed || false;
    const handleChange = (_event:any, value:any) => {
        apiRef.current?.setFieldValue("writeAllowed", value)
    };

    return <Grid container spacing={2}>
        <GridFormField size={12} name={"subjectType"} disabled={data.id} />
        <GridFormField size={12} name={"subjectValue"} disabled={data.id} />

        <FormControl>
            <FormLabel>{t("page.bustia.grid.writeAllowed")}</FormLabel>
            <RadioGroup row value={value} onChange={handleChange}>
                <FormControlLabel value={false} control={<Radio />} label={t("page.bustia.permisos.readAllowed")} />
                <FormControlLabel value={true} control={<Radio />} label={t("page.bustia.permisos.writeAllowed")} />
            </RadioGroup>
        </FormControl>
    </Grid>;
}