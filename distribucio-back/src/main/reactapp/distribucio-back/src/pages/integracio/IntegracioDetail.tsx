import React from 'react';
import { useTranslation } from 'react-i18next';
import { alpha } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Divider from '@mui/material/Divider';
import Box from '@mui/material/Box';
import { MuiDialog, useBaseAppContext, useCloseDialogButtons } from 'reactlib';
import { formatDate } from '../../util/dateUtils';
import { IntegracioEstat } from './IntegracioEstat';

type DetailFieldSpec = {
    label: string;
    value?: React.ReactNode;
};

const DetailRow: React.FC<{ fields: DetailFieldSpec[] }> = ({ fields }) => (
    <Stack direction="row" spacing={2} divider={<Divider orientation="vertical" flexItem />}>
        {fields.map((field, index) => (
            <Stack key={index} sx={{ flex: 1, minWidth: 0 }}>
                <Typography variant="subtitle2">{field.label}</Typography>
                <Typography variant="body2">{field.value}</Typography>
            </Stack>
        ))}
    </Stack>
);

export const useIntegracioDetail = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const [open, setOpen] = React.useState(false);
    const [row, setRow] = React.useState<any>();
    const { temporalMessageShow } = useBaseAppContext();

    const handleOpen = (_id: any, r: any) => {
        setRow(r);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const copyStacktrace = () => {
        if (row?.excepcioStacktrace) {
            navigator.clipboard?.writeText(row.excepcioStacktrace);
            temporalMessageShow(null, t('common.copiat'), 'success');
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.integracio.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            <Stack spacing={2} divider={<Divider />} sx={{ pt: 1 }}>
                <DetailRow
                    fields={[
                        {
                            label: t('page.integracio.grid.column.data'),
                            value: row?.data
                                ? formatDate(row.data, 'DD/MM/YYYY HH:mm:ss')
                                : undefined,
                        },
                        {
                            label: t('page.integracio.grid.column.tipus'),
                            value: t('page.integracio.detail.tipus.' + row?.tipus),
                        },
                    ]}
                />
                <DetailRow
                    fields={[
                        {
                            label: t('page.integracio.grid.column.descripcio'),
                            value: row?.descripcio,
                        },
                    ]}
                />
                <DetailRow
                    fields={[
                        {
                            label: t('page.integracio.grid.column.codiUsuari'),
                            value: row?.codiUsuari,
                        },
                    ]}
                />
                <DetailRow
                    fields={[
                        {
                            label: t('page.integracio.grid.column.entitat'),
                            value: row?.entitat?.description,
                        },
                    ]}
                />
                <DetailRow
                    fields={[
                        {
                            label: t('page.integracio.grid.column.numeroRegistre'),
                            value: row?.numeroRegistre,
                        },
                        {
                            label: t('page.integracio.grid.column.tempsResposta'),
                            value:
                                row?.tempsResposta != null ? `${row.tempsResposta} ms` : undefined,
                        },
                        {
                            label: t('page.integracio.grid.column.estat'),
                            value: row?.estat ? (
                                <Box sx={{ display: 'flex' }}>
                                    <IntegracioEstat value={row.estat} />
                                </Box>
                            ) : undefined,
                        },
                    ]}
                />
                {row?.estat === 'ERROR' && (
                    <>
                        <DetailRow
                            fields={[
                                {
                                    label: t('page.integracio.detail.errorDescripcio'),
                                    value: row?.errorDescripcio,
                                },
                            ]}
                        />
                        <DetailRow
                            fields={[
                                {
                                    label: t('page.integracio.detail.excepcioMessage'),
                                    value: row?.excepcioMessage,
                                },
                            ]}
                        />
                        {row?.excepcioStacktrace && (
                            <Stack>
                                <Stack
                                    direction="row"
                                    alignItems="center"
                                    spacing={1}
                                    justifyContent="space-between"
                                >
                                    <Typography variant="subtitle2">
                                        {t('page.integracio.detail.excepcioStacktrace')}
                                    </Typography>
                                    <IconButton
                                        size="small"
                                        onClick={copyStacktrace}
                                        title={t('page.integracio.detail.copyTooltip')}
                                    >
                                        <Icon fontSize="small">content_copy</Icon>
                                    </IconButton>
                                </Stack>
                                <Box
                                    sx={(theme) => ({
                                        width: '100%',
                                        height: 300,
                                        overflow: 'auto',
                                        borderRadius: 1,
                                        border: '1px solid',
                                        borderColor: alpha(theme.palette.error.main, 0.3),
                                        backgroundColor: alpha(theme.palette.error.main, 0.06),
                                    })}
                                >
                                    <Typography
                                        variant="body2"
                                        component="pre"
                                        sx={{
                                            m: 0,
                                            p: 1,
                                            width: 'max-content',
                                            minWidth: '100%',
                                            whiteSpace: 'pre',
                                            color: 'error.dark',
                                            fontFamily: 'monospace',
                                        }}
                                    >
                                        {row.excepcioStacktrace}
                                    </Typography>
                                </Box>
                            </Stack>
                        )}
                    </>
                )}
            </Stack>
        </MuiDialog>
    );

    return { show: handleOpen, close: handleClose, component: dialog };
};

export default useIntegracioDetail;
