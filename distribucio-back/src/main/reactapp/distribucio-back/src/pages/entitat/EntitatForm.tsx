import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FormPage, MuiForm } from 'reactlib';
import EntitatFormContent from './EntitatFormContent';

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
