import React from 'react';
import { useTranslation } from 'react-i18next';
import { alpha, lighten } from '@mui/material/styles';
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

/**
 * Línies que encapçalen una excepció: la primera de la traça i les causes o excepcions suprimides
 * encadenades. Es destaquen al tema fosc perquè són les que expliquen l'error; la resta són frames.
 */
const esLiniaExcepcio = (linia: string, index: number) =>
    index === 0 || /^\s*(Caused by|Suppressed):/.test(linia);

/**
 * Traça d'una excepció d'integració, amb el botó per copiar-la al portapapers.
 *
 * Al tema clar es pinta tota en vermell fosc sobre un fons vermellós. Al tema fosc aquell vermell
 * no té prou contrast (~2:1), així que el fons és neutre, el vermell queda a la vora esquerra i a
 * les línies d'excepció, i els frames van amb el color de text secundari.
 */
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
                    ...(theme.palette.mode === 'dark'
                        ? {
                              borderColor: theme.palette.divider,
                              borderLeft: `4px solid ${theme.palette.error.light}`,
                              backgroundColor: alpha(theme.palette.common.white, 0.05),
                          }
                        : {
                              borderColor: alpha(theme.palette.error.main, 0.3),
                              backgroundColor: alpha(theme.palette.error.main, 0.06),
                          }),
                })}
            >
                <Typography
                    variant="body2"
                    component="pre"
                    sx={(theme) => ({
                        m: 0,
                        p: 1,
                        width: 'max-content',
                        minWidth: '100%',
                        whiteSpace: 'pre',
                        color: theme.palette.mode === 'dark' ? 'text.secondary' : 'error.dark',
                        fontFamily: 'monospace',
                        '& .linia-excepcio': theme.palette.mode === 'dark'
                            ? { color: lighten(theme.palette.error.main, 0.5), fontWeight: 600 }
                            : {},
                    })}
                >
                    {stacktrace.split(/\r?\n/).map((linia, index, linies) => (
                        <span key={index} className={esLiniaExcepcio(linia, index) ? 'linia-excepcio' : undefined}>
                            {index < linies.length - 1 ? linia + '\n' : linia}
                        </span>
                    ))}
                </Typography>
            </Box>
        </Stack>
    );
};

export default IntegracioStacktrace;
