import { useEffect, useRef, useState, useCallback } from 'react';
import { Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import FormActionDialog, { FormActionDialogApi } from '../../components/FormActionDialog';
import BackdropLoading from '../../components/BackdropLoading';
import { usePollingArtifactAction } from '../../components/ActionPollingOptions';
import { useBaseAppContext } from 'reactlib';
import { buildEstatProcedimentsMessage, UpdateProgressDto } from './ProcedimentEstat';

const TEMPS_REFRES_PROGRES = 1000;
/**
 * Hook personalitzat per gestionar el polling de l'acció PROGRES.
 */
const usePolling = () => {
    const [progres, setProgres] = useState<UpdateProgressDto | undefined>(undefined);
    const [finished, setFinished] = useState(true);

    const { startPolling } = usePollingArtifactAction('serveiResource', {
        intervalMs: TEMPS_REFRES_PROGRES,
        stopCondition: (data: UpdateProgressDto) =>
            data?.estat === 'FINALITZAT' || data?.estat === 'ERROR',
        onProgress: (data: UpdateProgressDto) => {
            setProgres(data);
            setFinished(data?.estat === 'FINALITZAT' || data?.estat === 'ERROR');
        },
    });

    return { startPolling, progres, setProgres, finished, setFinished };
};

/**
 * Hook principal per controlar l'acció "Actualitzar tots els procediments".
 */
const useProcedimentActualitzarTots = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<FormActionDialogApi | undefined>(undefined);
    const polling = usePolling();
    const [showBackdrop, setShowBackdrop] = useState(false);
    const { temporalMessageShow } = useBaseAppContext();
    const [isProcessing, setIsProcessing] = useState(false);
    const [disabled, setDisabled] = useState(false);

    // Sincronitza la visibilitat del Backdrop amb l'estat del polling
    useEffect(() => {
        setShowBackdrop(!polling.finished);
    }, [polling.finished]);

    // Obre el diàleg de confirmació en fer clic
    const handleShow = useCallback(() => {
        setDisabled(false);
        polling.setFinished(true);
        polling.setProgres(undefined);

        // Utilitzem un timeout zero o verificació directa per assegurar que apiRef.current està assignat
        if (apiRef.current) {
            apiRef.current.show('');
        } else {
            // Si és el primer renderitzat, esperam un tick d'execució
            setTimeout(() => {
                apiRef.current?.show('');
            }, 0);
        }
    }, [polling]);

    // Es crida quan l'acció síncrona ACTUALITZAR ha finalitzat amb èxit
    const processResult = async () => {
        if (!isProcessing) {
            setIsProcessing(true);
            polling.setFinished(false);

            const finalResult = await polling.startPolling(null, 'PROGRES');

            if (finalResult?.estat === 'FINALITZAT') {
                refresh?.();
                temporalMessageShow(null, t('page.procediments.accio.actualitzarTotsOk'), 'success');
                setIsProcessing(false);
                return (
                    <Typography
                        variant="body2"
                        sx={{ mt: 2 }}
                        dangerouslySetInnerHTML={{
                            __html: buildEstatProcedimentsMessage(t, finalResult),
                        }}
                    />
                );
            }
            if (finalResult?.estat === 'ERROR') {
                refresh?.();
                temporalMessageShow(null, t('page.procediments.accio.error'), 'error');
                setIsProcessing(false);
                return <Typography>{t('page.procediments.accio.error')}</Typography>;
            }

            setIsProcessing(false);
        }

        return null;
    };

    const handleCloseBackdrop = () => {
        setShowBackdrop(false);
    };

    const content = (
        <>
            <FormActionDialog
                apiRef={apiRef}
                resourceName="serveiResource"
                action="ACTUALITZAR"
                title={t('page.procediments.accio.actualitzarTots.title')}
                buttons={[
                    {
                        icon: 'sync',
                        text: t('common.actualitza'),
                        componentProps: { variant: 'contained', disabled },
                        value: true,
                    },
                    {
                        text: t('common.close'),
                        componentProps: { variant: 'outlined' },
                        value: false,
                    },
                ]}
                onSuccess={() => setDisabled(true)}
                formDialogResultProcessor={processResult}
            >
                <>
                    <Typography variant="body2">
                        {t('page.procediments.accio.actualitzarTots.confirmacio')}
                    </Typography>
                    <Typography
                        variant="body2"
                        sx={{ mt: 2 }}
                        dangerouslySetInnerHTML={{ __html: buildEstatProcedimentsMessage(t) }}
                    />
                </>
            </FormActionDialog>

            <BackdropLoading
                open={showBackdrop}
                progress={polling.progres?.progres ?? 0}
                progressMessage={buildEstatProcedimentsMessage(t, polling.progres)}
                onCancel={handleCloseBackdrop}
                onClose={handleCloseBackdrop}
            />
        </>
    );

    return { handleShow, content };
};

export default useProcedimentActualitzarTots;
