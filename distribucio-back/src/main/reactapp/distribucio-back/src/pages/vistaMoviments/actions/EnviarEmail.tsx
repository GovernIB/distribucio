import { Grid } from '@mui/material';
import GridFormField from '../../../components/GridFormField';
import { useTranslation } from 'react-i18next';
import { useMuiFormDialogApiRef } from 'reactlib';
import FormActionDialog from '../../../components/FormActionDialog';

const ACTION_ENVIAR_EMAIL = 'ENVIAR_EMAIL';

// eslint-disable-next-line react-refresh/only-export-components
const VistaMovimentsEnviarEmailForm = () => {
    const { t } = useTranslation();
    return (
        <Grid container direction="row" columnSpacing={1} rowSpacing={1}>
            <GridFormField
                size={12}
                name="destinatari"
                type="textarea"
                componentProps={{ helperText: t('page.registre.accio.email.form.destinatari') }}
            />
            <GridFormField size={12} name="motiu" type="textarea" />
        </Grid>
    );
};

/**
 * "Enviar via email": acció pròpia de VistaMovimentResource. L'id que rep és el de la fila del moviment (compost), no el de l'anotació
 */
const useEnviarViaEmail = (onSuccess?: (result?: any) => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();

    const handleShow = (id: any): void => {
        apiRef.current?.show?.(id);
    };
    /** Obre el diàleg per a l'acció massiva: sense id (l'executor l'entén com a tal per "params.ids"). */
    const handleShowMassive = (ids: any[]): void => {
        apiRef.current?.show?.(undefined, { ids, massive: true });
    };

    return {
        handleShow,
        handleShowMassive,
        content: (
            <FormActionDialog
                resourceName="vistaMovimentResource"
                title={(dadesInicials: any) =>
                    dadesInicials?.massive
                        ? t('page.vistaMoviments.accio.email.titleMassive', { num: dadesInicials?.ids?.length })
                        : t('page.registre.accio.email.title')
                }
                action={ACTION_ENVIAR_EMAIL}
                apiRef={apiRef as any}
                onSuccess={onSuccess}
                dialogComponentProps={{ maxWidth: 'md' }}
            >
                <VistaMovimentsEnviarEmailForm />
            </FormActionDialog>
        ),
    };
};

export default useEnviarViaEmail;
