import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FormPage, MuiForm } from 'reactlib';
import AvisFormContent from './AvisFormContent';

export const AvisForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="avisResource"
                id={id}
                title={id != null ? t('page.avisos.form.titleUpdate') : t('page.avisos.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <AvisFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default AvisForm;
