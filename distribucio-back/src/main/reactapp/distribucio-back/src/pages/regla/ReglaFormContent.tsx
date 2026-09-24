import React from 'react';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import { useTranslation } from 'react-i18next';
import GridFormField, { GridRadioButtonField } from '../../components/GridFormField';
import { useFormContext } from 'reactlib';
import { formatDate } from '../../util/dateUtils';

/** Capçalera de secció ("Filtre"/"Acció") */
const SectionDivider: React.FC<{ label: string }> = ({ label }) => (
    <Grid size={12} sx={{ mt: 1 }}>
        <Typography variant="h6" color="text.secondary">
            {label}
        </Typography>
        <Divider textAlign="left"></Divider>
    </Grid>
);

export const ReglaFormContent: React.FC = () => {
    const { t } = useTranslation();
    const { data } = useFormContext();

    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="nom" />
            <GridFormField size={12} name="descripcio" type="textarea" />

            <SectionDivider label={t('page.regla.form.legend.filtre')} />

            <GridFormField size={6} name="unitatOrganitzativaFiltre" />
            {data?.tipus !== 'BACKOFFICE' && <GridFormField size={6} name="bustiaFiltre" />}

            <Grid container spacing={2} size={12}>
                <GridRadioButtonField size={2.5} name="tipusSia" />
                {data?.tipusSia === 'PROCEDIMENT' && (
                    <GridFormField
                        size={9.5}
                        name="procedimentCodiFiltre"
                        type="textarea"
                        componentProps={{ helperText: t('page.regla.form.camp.procedimentCodiFiltre.info') }}
                    />
                )}
                {data?.tipusSia === 'SERVEI' && (
                    <GridFormField
                        size={8}
                        name="serveiCodiFiltre"
                        type="textarea"
                        componentProps={{ helperText: t('page.regla.form.camp.serveiCodiFiltre.info') }}
                    />
                )}
            </Grid>
            <GridFormField size={6} name="tramitCodiFiltre" type="textarea" />
            <Grid container spacing={2} size={6}>
                {data?.tipus !== 'BACKOFFICE' && <GridFormField size={12} name="assumpteCodiFiltre" />}
                <GridFormField size={12} name="presencial" />
            </Grid>

            <SectionDivider label={t('page.regla.form.legend.accio')} />

            <GridFormField size={4} name="tipus" />
            {data?.tipus === 'BUSTIA' && <GridFormField size={8} name="bustiaDesti" required />}
            {data?.tipus === 'BACKOFFICE' && <GridFormField size={8} name="backofficeDesti" required />}
            {data?.tipus === 'UNITAT' && <GridFormField size={8} name="unitatDesti" required />}

            <GridFormField size={12} name="aturarAvaluacio" />

            {data?.id && (
                <Grid size={12}>
                    <Alert severity="info" sx={{ alignItems: 'center' }}>
                        {t('page.regla.form.auditoria.creat', {
                            data: formatDate(data.createdDate),
                            usuari: data.createdByFullName,
                        })}
                        {data?.lastModifiedBy && (
                            <span>
                                &nbsp;&nbsp;
                                {t('page.regla.form.auditoria.modificat', {
                                    data: formatDate(data.lastModifiedDate),
                                    usuari: data.lastModifiedByFullName,
                                })}
                            </span>
                        )}
                    </Alert>
                </Grid>
            )}
        </Grid>
    );
};

export default ReglaFormContent;
