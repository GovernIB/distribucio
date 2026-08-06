import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const EntitatFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="codi" />
            <GridFormField size={8} name="nom" />
            <GridFormField size={12} name="descripcio" />
            <GridFormField size={4} name="cif" />
            <GridFormField size={4} name="codiDir3" />
            <GridFormField size={3} name="activa" />
        </Grid>
    );
};

export const EntitatForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="entitatResource"
                id={id}
                title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <EntitatFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default EntitatForm;
