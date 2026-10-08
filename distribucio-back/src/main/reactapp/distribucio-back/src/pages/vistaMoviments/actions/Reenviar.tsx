import { useTranslation } from 'react-i18next';
import { useMuiFormDialogApiRef } from 'reactlib';
import FormActionDialog from '../../../components/FormActionDialog';
import { ReenviarForm } from '../../registre/actions/Reenviar.tsx';

const ACTION_REENVIAR = 'REENVIAR';

/**
 * "Reenviar": acció pròpia de VistaMovimentResource. Reutilitza el mateix formulari
 * (arbre de bústies, "per coneixement", favorits...) que l'acció de l'Anotació, sense el camp "Deixar còpia".
 */
const useReenviar = (onSuccess?: (result?: any) => void) => {
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
                        ? t('page.vistaMoviments.accio.reenviar.titleMassive', { num: dadesInicials?.ids?.length })
                        : t('page.vistaMoviments.accio.reenviar.title')
                }
                action={ACTION_REENVIAR}
                dialogComponentProps={{ fullWidth: true, maxWidth: 'xl' }}
                initOnChange
                apiRef={apiRef as any}
                onSuccess={onSuccess}
            >
                <ReenviarForm hideAmbCopia />
            </FormActionDialog>
        ),
    };
};

export default useReenviar;
