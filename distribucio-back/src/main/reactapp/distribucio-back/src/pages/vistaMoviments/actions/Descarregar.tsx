import { useBaseAppContext, useResourceApiService } from 'reactlib';
import { iniciaDescargaBlob } from '../../../util/downloadUtils';

/**
 * Descàrrega del ZIP de documentació (justificant + annexos) d'una anotació,
 * en versió original o en còpia autèntica imprimible.
 */
const useDescarregarZip = () => {
    const { temporalMessageShow } = useBaseAppContext();
    const { artifactReport: apiArtifactReport } = useResourceApiService('vistaMovimentResource');

    return (id: any, code: string) => {
        apiArtifactReport(id, { code, fileType: 'CUSTOM' as any })
            .then((result: any) => iniciaDescargaBlob(result))
            .catch((error: any) => temporalMessageShow(null, error?.description ?? error?.message, 'error'));
    };
};

export default useDescarregarZip;
