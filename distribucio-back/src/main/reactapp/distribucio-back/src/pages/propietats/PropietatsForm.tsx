import { useTranslation } from "react-i18next";
import React from "react";
import Typography from "@mui/material/Typography";
import { usePropietatsDialog } from "./PropietatsDialog";
import {Box, Grid, Icon, IconButton, ListItemButton} from "@mui/material";
import {FormField, MuiForm, useFormContext} from "reactlib";
import ListItem from "@mui/material/ListItem";
import { icons } from '../../util/icons';

// Etiqueta traduida d'un dels valors valids d'un tipus de propietat (IPA_CONFIG_TYPE). El valor
// que es desa a la base de dades no canvia: si no hi ha traduccio definida es mostra tal qual.
export type TranslateFn = (key: string, options?: any) => string;
export const configTypeValueLabel = (t: TranslateFn, typeCode: string, value: string) =>
    t(`enum.configType.${typeCode}.${value}`, { defaultValue: value, nsSeparator: false });

const fieldPropType = (typeCode: string, typeValue?: string) => {
    if (typeValue != null) {
        return 'search';
    } else {
        switch (typeCode) {
            case 'INT':
            case 'FLOAT':
                return 'number';
            case 'BOOL':
                return 'checkbox';
            default:
                return 'text';
        }
    }
};

const TextHighlight: React.FC<{ text: string; variant?:any, component?: React.ElementType, match?: string; ignoreCase?: boolean }> = (
    props
) => {
    const { text, match, ignoreCase, variant = 'inherit', component } = props;
    const componentProps = component ? { component } : {};
    if (!match) {
        return <Typography variant={variant} {...componentProps}>{text}</Typography>;
    }
    const escapedMatch = match.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const flags = ignoreCase ? 'gi' : 'g';
    const pattern = new RegExp(`(${escapedMatch})`, flags);
    const parts = text.split(pattern);
    return (
        <Typography variant={variant} {...componentProps}>
            {parts.map((part, index) =>
                pattern.test(part) ? (
                    <mark key={index}>{part}</mark>
                ) : (
                    <span key={index}>{part}</span>
                )
            )}
        </Typography>
    );
};

export const getFieldFromItem = (item:any, t: TranslateFn) => {
    const type = fieldPropType(item.type.id, item.type.description);
    const options = item.type.description
        ? Object.fromEntries(
            item.type.description
                .split(',')
                .map((v: string) => [v, configTypeValueLabel(t, item.type.id, v)])
        )
        : undefined;
    const value = item.value != null ?item.value :item.alternativeValue

    return {
        label: item.description,
        name: item.key,
        type,
        value,
        options,
    };
}

const PropsListItem: React.FC<any> = ({ highlight }:any) => {
    const { t } = useTranslation();
    const { data: item, apiRef } = useFormContext()

    const disabled = item.jbossProperty;
    const password = item.type.id === "CREDENTIALS" ? true : undefined;
    const decimalScale = item.type.id === 'INT' ? 0 : undefined;

    const field = getFieldFromItem(item, t)
    // La descripció de la propietat és l'etiqueta (<label for>) del camp, que es pinta `inline` (sense
    // etiqueta pròpia); sense, l'OAW (1.9) el dona per camp sense etiqueta. Les propietats amb valors
    // possibles es pinten amb un Select de MUI, que posa l'id a un <div>: un <label for> que hi apuntés
    // seria una associació incorrecta, i per això en aquest cas la descripció queda com a text.
    const inputId = React.useId();
    const ambEtiqueta = field.type !== 'search';

    const save = () => {
        apiRef.current?.save()
    };
    const {handleOpen, dialog} = usePropietatsDialog();
    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={4.5}>
                {ambEtiqueta ? (
                    <label htmlFor={inputId}>
                        <TextHighlight text={item.description} component="span" match={highlight} ignoreCase />
                    </label>
                ) : (
                    <TextHighlight text={item.description} match={highlight} ignoreCase />
                )}
            </Grid>
            <Grid size={6}>
                <FormField
                    field={field}
                    name={'value'}
                    value={(item.type.id == 'BOOL') ?field.value == "true" :field.value}
                    inline
                    decimalScale={decimalScale}
                    disabled={disabled}
                    componentProps={{
                        ...(ambEtiqueta && { id: inputId }),
                        type: password ?'password' :field.type,
                        placeholder: item.alternativeValue || item.key,
                        helperText: <TextHighlight text={item.key} component="span" match={highlight} ignoreCase />
                    }}
                />
            </Grid>
            <Grid size={1.5}>
                <Box sx={{ display: 'flex', justifyContent: 'end' }}>
                     {(!disabled) && (<>
                        <IconButton
                            title={t('common.save')}
                            size="small"
                            onClick={save}
                            color={'success'}>
                            <Icon fontSize="small">{icons.desa}</Icon>
                        </IconButton>
                         {(item?.entitatCodi && item.value != null) &&
                             <IconButton size="small" onClick={async() => {
                                 await apiRef.current?.setFieldValue("value", null)
                                 save()
                             }} >
                                 <Icon sx={{m:0}} fontSize="small">{icons.esborra}</Icon>
                             </IconButton>}
                         {(item?.configurable && !item?.entitatCodi) &&
                             <IconButton size="small" onClick={() => handleOpen(item.id, item)} >
                                 <Icon sx={{m:0}} fontSize="small">{icons.configuracio}</Icon>
                             </IconButton>}
                    </>)}
                </Box>
                {dialog}
            </Grid>
        </Grid>
    );
};

const perspectives = ["ALTERNATIVE_VALUE"]
export const PropietatsForm = ({id, highlight}:any) => {
    return (
        <MuiForm
            key={id}
            id={id}
            resourceName="configResource"
            hiddenToolbar
            formBlockerDisabled
            perspectives={perspectives}
            // componentProps={{ sx: {m: 0} }}
            commonFieldComponentProps={{ size: 'small' }}>
            {/* <div> i no <li>: el MuiForm s'interposa entre la llista i l'element, i un <li> fora
                d'un <ul> és una llista mal construïda per a l'OAW (1.3) */}
            <ListItem component="div" disablePadding>
                <ListItemButton disableRipple>
                    <PropsListItem highlight={highlight}/>
                </ListItemButton>
            </ListItem>
        </MuiForm>
    )
}