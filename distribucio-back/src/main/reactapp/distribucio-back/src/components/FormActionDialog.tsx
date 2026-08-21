import React from 'react';
import { useBaseAppContext, useMuiActionReportLogic, type DialogButton } from 'reactlib';

export type FormActionDialogApi = {
    /**
     * Obre el diàleg amb el formulari de l'acció.
     *
     * @param id identificador del recurs sobre el qual s'executa l'acció.
     * @param dadesInicials valors amb què s'inicialitza el formulari (i que també s'envien amb
     *        l'acció encara que no hi hagi cap camp que els mostri).
     */
    show: (id: any, dadesInicials?: any) => void;
    close: () => void;
};

export type FormActionDialogProps = {
    /** Nom del recurs que publica l'acció. */
    resourceName: string;
    /** Codi de l'artefacte de tipus ACTION a executar. */
    action: string;
    /** Títol del diàleg; com a funció, es compon amb els valors inicials del formulari. */
    title?: string | ((dadesInicials?: any) => string);
    /** Botons del diàleg; per defecte els del formulari ("Guarda" i "Cancel·la"). */
    buttons?: DialogButton[];
    /** Propietats pel component Dialog de MUI. */
    dialogComponentProps?: any;
    /** Referència per a poder obrir i tancar el diàleg des de fora. */
    apiRef?: React.RefObject<FormActionDialogApi | undefined>;
    onSuccess?: (result?: any) => void;
    onError?: (error?: any) => void;
    /** Camps del formulari (GridFormField...). */
    children: React.ReactElement;
};

/**
 * Diàleg amb el formulari d'una acció d'un recurs.
 *
 * Els camps del formulari (etiquetes, tipus, opcions dels desplegables i validacions) els
 * descriu el backend a la formClass de l'artefacte; aquí només s'indica quins es mostren i en
 * quin ordre, amb els mateixos GridFormField que la resta de formularis.
 *
 * És un embolcall de `useMuiActionReportLogic` de base-react, que té una vintena de paràmetres
 * posicionals: aquest component en fixa els que no ens interessen i dona nom a la resta. Està
 * portat de RIPEA (jsapp/ripea-back/src/components/FormActionDialog.tsx), retallat a la part
 * d'accions -- RIPEA hi té també la variant per a informes.
 */
export const FormActionDialog: React.FC<FormActionDialogProps> = (props) => {
    const {
        resourceName,
        action,
        title,
        buttons,
        dialogComponentProps,
        apiRef,
        onSuccess,
        onError,
        children,
    } = props;
    const { temporalMessageShow } = useBaseAppContext();
    const {
        available,
        formDialogComponent,
        exec,
        close,
    } = useMuiActionReportLogic(
        resourceName,
        action,
        undefined, // report
        undefined, // reportFileType
        false, // confirm
        undefined, // confirmMessage
        undefined, // formAdditionalData
        undefined, // formI18nKeys
        false, // formInitOnChangeRequest
        children, // formDialogContent
        undefined, // formDialogLoading
        buttons,
        dialogComponentProps,
        undefined, // formDialogResultProcessor
        onSuccess,
        onError ??
            ((error: any) => error?.message && temporalMessageShow(null, error.message, 'error')),
        undefined, // onClose
        true, // dialogCloseIcon
        // Un clic fora del diàleg no el tanca: s'hi estan editant dades.
        (reason?: string) => reason !== 'backdropClick'
    );
    if (apiRef != null) {
        apiRef.current = {
            show: (id: any, dadesInicials?: any) => {
                if (available) {
                    exec(
                        id,
                        typeof title === 'function' ? title(dadesInicials) : title,
                        dadesInicials
                    );
                }
            },
            close,
        };
    }
    return formDialogComponent;
};

export default FormActionDialog;
