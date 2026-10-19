import { useTranslation } from 'react-i18next';
import {useBaseAppContext, useResourceApiService} from 'reactlib';
import useContingutHistorialDialog from '../contingut/actions/ContingutHistorialDialog.tsx';
import { useAlertes } from '../registre/detail/Alertes.tsx';
import {useRegistreDetail} from '../registre/detail/RegistreDetail.tsx';
import useEnviarViaEmail from "../registre/actions/EnviarViaEmail.tsx";
import {useActions} from "../registre/detail/RegistreActions.tsx";
import {Divider} from "@mui/material";
import useReenviar from "../registre/actions/Reenviar.tsx";
import {useExecucioMassivaGrid} from "../execucioMassiva/ExecucioMassivaGrid.tsx";
import * as builder from '../../util/springFilterUtils';

/** Menú d'accions de la "Vista de moviments" */
export const useVistaMovimentsAccions = (options: {
    gridApiRef: any;
    filter?: string;
    namedQueries?: any[];
    refresh?: () => void;
}) => {
    const { gridApiRef, filter, namedQueries, refresh } = options;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const { descarregarDoc } = useActions(refresh)
    const { show: handleHistoric, component: componentHistoric } = useContingutHistorialDialog();
    const { handleOpen: handleDetail, dialog: dialogDetail } = useRegistreDetail({
        gridApiRef,
        filter,
        namedQueries,
        resourceName: 'vistaMovimentResource',
        getDetailId: (row: any) => row?.idRegistre,
        perspectives: ['VISTA_MOVIMENTS'],
        onClose: refresh,
    });
    const { handleOpen: handleAlertes, component: componentAlertes } = useAlertes(refresh);
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.email.ok`, {numero: result.numero}), 'success');
    })
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar((result:any) => {
        refresh?.()
        temporalMessageShow(null, t(`page.registre.accio.reenviar.ok`, {numero: result.numero}), 'success');
    })

    const actions: any[] = [
        {
            label: t('page.contingut.accio.detalls.label'),
            icon: 'info',
            showInMenu: true,
            onClick: (id: any, row: any) => handleDetail(id, row),
        },
        {
            label: t('page.contingut.accio.historial.label'),
            icon: 'list',
            showInMenu: true,
            onClick: (_id: any, row: any) => handleHistoric(row.idRegistre, { id: row.idRegistre, nom: row.numero }),
        },
        {
            label: t('page.alerta.label'),
            icon: 'note_stack',
            showInMenu: true,
            // Les alertes pengen de l'anotació (idRegistre)
            onClick: (_id: any, row: any) => handleAlertes(row.idRegistre),
            hidden: (row: any) => !row.alertesPendents,
        },
        {
            label: t('page.registre.accio.reenviar.label'),
            icon: 'send',
            showInMenu: true,
            onClick: (_id: any, row: any) => handleReenviar([row.idRegistre], false, { destiLogic: row.destiLogic }),
        },
        {
            label: <Divider sx={{width: '100%'}} color={"none"}/>,
            showInMenu: true,
            disabled: true,
        },
        {
            label: t('page.registre.accio.email.label'),
            icon: 'mail',
            showInMenu: true,
            disabled: (row: any) => row.procesEstat === 'ARXIU_PENDENT',
            onClick: (_id:any, row:any) => handleEnviarEmail([row.idRegistre], false, { vistaMoviments: true }),
        },
        {
            label: t('page.registre.accio.descargaOriginal.label'),
            icon: 'download',
            showInMenu: true,
            onClick: (_id:any, row:any) => descarregarDoc(row.idRegistre, false),
        },
        {
            label: t('page.registre.accio.descargaAutentica.label'),
            icon: 'download',
            showInMenu: true,
            onClick: (_id:any, row:any) => descarregarDoc(row.idRegistre, true),
        },
    ];

    const components = (
        <>
            {dialogDetail}
            {componentHistoric}
            {componentAlertes}
            {contentEnviarEmail}
            {contentReenviar}
        </>
    );

    return {
        actions,
        components,
    };
};

/** Accions múltiples de la "Vista de moviments" (només Reenviar i Enviar per email, com a la JSP antiga) */
export const useVistaMovimentsMassiveAccions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        find: apiFindAll,
    } = useResourceApiService('vistaMovimentResource');

    const toRegistreId = (ids:any[]) => new Promise<any[]>((resolve, reject) => {
        if (!apiIsReady) reject()
        apiFindAll({ filter: builder.inside("id", ids.map(id => `'${id}'`)), unpaged: true })
            .then((response) => {
                const idsUnicos = [...new Set(response.rows.map(mov => mov.idRegistre))];
                console.log("idsUnicos", idsUnicos)
                resolve(idsUnicos)
            })
            .catch(reject)
    })

    const { handleOpen: handleEM, component: componentEM } = useExecucioMassivaGrid();
    const { handleShow: handleEnviarEmail, content: contentEnviarEmail } = useEnviarViaEmail(() => {
        refresh?.()
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.email.ok`), 'success');
    })
    const { handleShow: handleReenviar, content: contentReenviar } = useReenviar(() => {
        refresh?.()
        handleEM()
        // temporalMessageShow(null, t(`page.registre.accio.reenviar.ok`), 'success');
    })

    const actions: any[] = [
        {
            label: t('page.registre.accio.reenviar.label'),
            icon: 'send',
            showInMenu: true,
            onClick: (ids: any[]) => {
                toRegistreId(ids)
                    .then((registreIds:any[]) => handleReenviar(registreIds, true, { vistaMoviments: true }))
            },
        },
        {
            label: t('page.registre.accio.email.label'),
            icon: 'mail',
            showInMenu: true,
            onClick: (ids: any[]) => {
                toRegistreId(ids)
                    .then((registreIds:any[]) => handleEnviarEmail(registreIds, true, { vistaMoviments: true }))
            },
        },
    ];

    const components = (
        <>
            {componentEM}
            {contentEnviarEmail}
            {contentReenviar}
        </>
    );

    return {
        actions,
        components,
    };
};

export default useVistaMovimentsAccions;
