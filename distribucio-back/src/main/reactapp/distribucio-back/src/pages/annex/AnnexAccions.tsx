import { useTranslation } from 'react-i18next';
import { useBaseAppContext, useResourceApiService, type MuiDataGridProps } from 'reactlib';
import { iniciaDescargaBlob } from '../../util/downloadUtils';

export const REPORT_DESCARREGAR_ORIGINAL = 'DESCARREGAR_ORIGINAL';
export const REPORT_DESCARREGAR_IMPRIMIBLE = 'DESCARREGAR_IMPRIMIBLE';

export const fitxerExtensio = (row: any): string | undefined => {
    const nom: string | undefined = row?.fitxerNom;
    return nom?.includes('.') ? nom.substring(nom.lastIndexOf('.') + 1) : undefined;
};

/** Descàrrega d'un annex (original/imprimible); compartit entre el menú de la graella i el diàleg de detalls. */
export const useDescarregarAnnex = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const { artifactReport: apiArtifactReport } = useResourceApiService('registreAnnexResource');

    return (id: any, code: string) => {
        apiArtifactReport(id, { code, fileType: 'PDF' })
            .then((result: any) => iniciaDescargaBlob(result))
            .catch((error: any) =>
                temporalMessageShow(
                    t('page.annex.accio.error'),
                    error?.description ?? error?.message,
                    'error'
                )
            );
    };
};

type AccionsFila = NonNullable<MuiDataGridProps['rowAdditionalActions']>;

export const useAnnexAccions = (mostrarDetall: (id: any, row: any) => void): AccionsFila => {
    const { t } = useTranslation();
    const descarregar = useDescarregarAnnex();

    return [
        {
            label: t('page.annex.accio.detalls'),
            icon: 'info',
            showInMenu: true,
            onClick: (id: any, row: any) => mostrarDetall(id, row),
        },
        {
            label: t('page.annex.accio.concsv'),
            icon: 'open_in_new',
            showInMenu: true,
            linkTo: (row: any) => row?.concsvUrl,
            linkTarget: '_blank',
            hidden: (row: any) => !row?.concsvUrl,
        },
        {
            label: t('page.annex.accio.descarregarOriginal'),
            icon: 'download',
            showInMenu: true,
            onClick: (id: any) => descarregar(id, REPORT_DESCARREGAR_ORIGINAL),
        },
        {
            label: t('page.annex.accio.descarregarImprimible'),
            icon: 'print',
            showInMenu: true,
            onClick: (id: any) => descarregar(id, REPORT_DESCARREGAR_IMPRIMIBLE),
            hidden: (row: any) => fitxerExtensio(row) !== 'csv',
        },
    ];
};

export default useAnnexAccions;
