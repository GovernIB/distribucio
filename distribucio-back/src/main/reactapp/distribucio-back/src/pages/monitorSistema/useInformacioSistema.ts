import React from 'react';
import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService } from 'reactlib';

const ACCIO_INFORMACIO_SISTEMA = 'INFORMACIO_SISTEMA';

export type MemoriaUs = { usada?: number; total?: number; usadaFormatada?: string; totalFormatada?: string };

/** Resultat de l'acció (MonitorFilResource.InformacioSistema al backend). */
export type InformacioSistema = {
    sistemaOperatiu?: string;
    arquitectura?: string;
    processadors?: number;
    versioJboss?: string;
    servidorAplicacions?: string;
    jvm?: string;
    versioJdk?: string;
    dataArrencada?: string;
    tempsFuncionant?: string;
    memoriaJvm?: MemoriaUs;
    memoriaMaxima?: string;
    memoriaFisica?: MemoriaUs;
    filsActius?: number;
    filsPic?: number;
    filsDaemon?: number;
    filsDeadlock?: number;
    gcExecucions?: number;
    gcTemps?: number;
    nuclis?: number;
    carregaMitjana?: string;
    carregaCpuSistema?: string;
    carregaCpuProces?: string;
    discos?: {
        nom?: string;
        usat?: number;
        total?: number;
        usatFormatat?: string;
        totalFormatat?: string;
    }[];
};

/** Informació del sistema (acció INFORMACIO_SISTEMA del monitor); el refresc el crida la pantalla, des de la barra de títol. */
export const useInformacioSistema = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { isReady: apiIsReady, artifactAction: apiArtifactAction } = useResourceApiService('monitorFilResource');
    const [informacio, setInformacio] = React.useState<InformacioSistema>();
    const [loading, setLoading] = React.useState(false);

    const refresh = () => {
        if (!apiIsReady) {
            return;
        }
        setLoading(true);
        apiArtifactAction(undefined, { code: ACCIO_INFORMACIO_SISTEMA })
            .then((response: InformacioSistema) => setInformacio(response))
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.monitorSistema.accio.error'),
                    error?.description ?? error?.message ?? '',
                    'error'
                )
            )
            .finally(() => setLoading(false));
    };

    React.useEffect(() => {
        refresh();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady]);

    return { informacio, loading, refresh };
};
