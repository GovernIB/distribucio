import React from 'react';
import { useTranslation } from 'react-i18next';
import { alpha } from '@mui/material/styles';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Box from '@mui/material/Box';
import { useBaseAppContext } from 'reactlib';

type IntegracioStacktraceProps = {
    stacktrace: string;
    height?: number;
};

/** Traça d'una excepció d'integració, amb el botó per copiar-la al portapapers. */
export const IntegracioStacktrace: React.FC<IntegracioStacktraceProps> = ({
    stacktrace,
    height = 300,
}) => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const copyStacktrace = () => {
        navigator.clipboard?.writeText(stacktrace);
        temporalMessageShow(null, t('common.copiat'), 'success');
    };

    return (
        <Stack>
            <Stack direction="row" alignItems="center" spacing={1} justifyContent="space-between">
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
                    height,
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
                    {stacktrace}
                </Typography>
            </Box>
        </Stack>
    );
};

export default IntegracioStacktrace;
