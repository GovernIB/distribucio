import React from 'react';
import { Box, Breakpoint, Button, Grid, GridSize, Icon, useMediaQuery, useTheme } from '@mui/material';
import { FormField, FormFieldProps, useFormContext } from 'reactlib';
import Load from './Load';

type ResponsiveStyleValue<T> = T | Array<T | null> | { [key in Breakpoint]?: T | null };

type GridFormFieldProps = FormFieldProps & {
    size: ResponsiveStyleValue<GridSize>;
};

/**
 * Icona base amb una segona icona més petita enganxada al seu angle inferior dret
 * (p. ex. zoom_in + expand_more), sense superposar-se al glif base. Totes dues hereten
 * el color del botó que les conté.
 *
 * Port de `src/components/GridFormField.tsx` de RIPEA.
 */
export const CombinedIcon = (props: any) => {
    const { base, badge, sx, badgeSx } = props;
    return (
        <Box sx={{ display: 'inline-flex', alignItems: 'flex-end', lineHeight: 1, ...sx }}>
            <Icon>{base}</Icon>
            <Icon sx={{ fontSize: '0.85rem', lineHeight: 1, ml: '1px', ...badgeSx }}>{badge}</Icon>
        </Box>
    );
};

/** Botó que ocupa una cel·la del Grid; per sota de `iconOnlyBreakpoint` amaga el text. */
export const GridButton = (props: any) => {
    const { title, icon, size, children, hidden, sx, iconOnlyBreakpoint = 'md', iconSx, ...other } = props;

    const theme = useTheme();
    const iconOnly = useMediaQuery(theme.breakpoints.down(iconOnlyBreakpoint));

    const iconNode =
        typeof icon === 'string' ? <Icon sx={{ mr: !iconOnly && children ? 0.5 : 0, ...iconSx }}>{icon}</Icon> : icon;

    return (
        <Grid title={title} size={size} hidden={hidden}>
            <Button
                variant="outlined"
                sx={{ borderRadius: '4px', width: '100%', height: '100%', ...sx }}
                style={{ margin: 0 }}
                aria-label={title && (iconOnly || !children) ? title : undefined}
                {...other}
            >
                {iconNode}
                {!iconOnly && children}
            </Button>
        </Grid>
    );
};

/**
 * Botó associat a un camp booleà del formulari: en fer clic commuta el valor del camp i
 * el botó queda "contained" mentre està actiu.
 */
export const GridButtonField = (props: any) => {
    const { name, whitLabel, icon, title, ...other } = props;
    const { data, apiRef, fields } = useFormContext();

    const active = !!data?.[name];
    const label = fields?.find?.((item: any) => item?.name === name)?.label || '';
    return (
        <Load value={apiRef} noEffect>
            <GridButton
                onClick={() => {
                    apiRef?.current?.setFieldValue?.(name, !active);
                }}
                variant={active ? 'contained' : 'outlined'}
                title={typeof title === 'function' ? title?.(active) : (title ?? label)}
                icon={typeof icon === 'function' ? icon?.(active) : icon}
                {...other}
            >
                {whitLabel && label}
            </GridButton>
        </Load>
    );
};

const GridFormField: React.FC<GridFormFieldProps> = (props) => {
    const { size } = props;
    return (
        <Grid size={size}>
            <FormField {...props} />
        </Grid>
    );
};

export default GridFormField;
