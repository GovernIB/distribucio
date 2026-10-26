import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import { MuiDialog, useCloseDialogButtons, useResourceApiService } from 'reactlib';
import { DetailCard, DetailCardContent } from '../../../components/CardData';
import Load from '../../../components/Load';
import { formatDate } from '../../../util/dateUtils.ts';
import { IntegracioEstat } from '../IntegracioEstat.tsx';
import { IntegracioStacktrace } from './IntegracioStacktrace.tsx';

const RESOURCE_NAME = 'monitorIntegracioResource';
const PERSPECTIVES = ['PARAMS'];
/** Amplada (px) de l'etiqueta dels camps del detall, perquè els valors quedin alineats. */
const LABEL_WIDTH = 140;

/**
 * Detall d'una entrada del monitor d'integracions. Es consulta per id (amb la perspectiva PARAMS
 * per als paràmetres) i les etiquetes dels camps vénen del backend (_prompt de
 * MonitorIntegracioResource).
 */
export const useIntegracioDetail = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields: fields,
    } = useResourceApiService(RESOURCE_NAME);
    const [open, setOpen] = React.useState(false);
    const [id, setId] = React.useState<any>();
    const [data, setData] = React.useState<any>();

    const handleOpen = (rowId: any) => {
        setId(rowId);
        setData(undefined);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    React.useEffect(() => {
        if (open && apiIsReady && id != null) {
            apiGetOne(id, { perspectives: PERSPECTIVES }).then(setData);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [open, apiIsReady, id]);

    const field = (name: string) => fields?.find?.((f: any) => f?.name === name);
    const label = (name: string) => field(name)?.label ?? name;
    const isError = data?.estat === 'ERROR';
    const params: { key: string; value: string }[] = data?.params ?? [];

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.integracio.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            <Load value={data}>
                <Grid container>
                    <DetailCard cardProps={{ mb: 2 }}>
                        <DetailCardContent
                            title={label('data')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.data ? formatDate(data.data, 'DD/MM/YYYY HH:mm:ss') : undefined}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('tipus')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {field('tipus')?.options?.[data?.tipus] ?? data?.tipus}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('descripcio')}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.descripcio}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('codiUsuari')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.codiUsuari}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('entitat')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.entitat?.description}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('numeroRegistre')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.numeroRegistre}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('tempsResposta')}
                            size={6}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                        >
                            {data?.tempsResposta != null ? `${data.tempsResposta} ms` : undefined}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('estat')}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                            isObject
                        >
                            {data?.estat && (
                                <Box sx={{ display: 'flex' }}>
                                    <IntegracioEstat value={data.estat} />
                                </Box>
                            )}
                        </DetailCardContent>
                        <DetailCardContent
                            title={label('params')}
                            titleSize="auto"
                            textSize="grow"
                            labelWidth={LABEL_WIDTH}
                            isObject
                            hiddenIfEmpty
                        >
                            {params.length > 0 && (
                                <Box component="ul" sx={{ m: 0, pl: 2 }}>
                                    {params.map((param, index) => (
                                        <li key={index}>
                                            <strong>{param.key}:</strong> {param.value}
                                        </li>
                                    ))}
                                </Box>
                            )}
                        </DetailCardContent>
                        {isError && (
                            <>
                                <DetailCardContent
                                    title={label('errorDescripcio')}
                                    titleSize="auto"
                                    textSize="grow"
                                    labelWidth={LABEL_WIDTH}
                                    hiddenIfEmpty
                                >
                                    {data?.errorDescripcio}
                                </DetailCardContent>
                                <DetailCardContent
                                    title={label('excepcioMessage')}
                                    titleSize="auto"
                                    textSize="grow"
                                    labelWidth={LABEL_WIDTH}
                                    hiddenIfEmpty
                                >
                                    {data?.excepcioMessage}
                                </DetailCardContent>
                            </>
                        )}
                    </DetailCard>
                    {isError && data?.excepcioStacktrace && (
                        <Grid size={12}>
                            <IntegracioStacktrace
                                label={label('excepcioStacktrace')}
                                stacktrace={data.excepcioStacktrace}
                            />
                        </Grid>
                    )}
                </Grid>
            </Load>
        </MuiDialog>
    );

    return { show: handleOpen, close: handleClose, component: dialog };
};

export default useIntegracioDetail;
