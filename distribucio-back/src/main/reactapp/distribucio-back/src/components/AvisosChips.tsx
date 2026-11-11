import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import ButtonBase from '@mui/material/ButtonBase';
import Icon from '@mui/material/Icon';
import Tooltip from '@mui/material/Tooltip';
import { darken, getContrastRatio, lighten } from '@mui/material/styles';
import { icons } from '../util/icons';
import { AVIS_NIVELLS, useAvisos, type AvisNivell } from './AvisosContext';

/**
 * Resum dels avisos a la capçalera: un chip per nivell (informació, advertència, error) amb la
 * seva icona i el nombre d'avisos actius. Els avisos que l'usuari ha minimitzat des del banner
 * ({@link ./AvisosBanner.tsx}) es recuperen des d'aquí: un clic restaura els del nivell a la seva
 * posició. El chip d'un nivell amb avisos minimitzats es mostra ple perquè es vegi de quin tipus n'hi ha d'amagats.
 */

const NIVELL_PALETTE: Record<AvisNivell, 'info' | 'warning' | 'error'> = {
    INFO: 'info',
    WARNING: 'warning',
    ERROR: 'error',
};

const NIVELL_ICON: Record<AvisNivell, string> = {
    INFO: icons.informacio,
    WARNING: icons.advertencia,
    ERROR: icons.error,
};

const AvisChip: React.FC<{ nivell: AvisNivell }> = ({ nivell }) => {
    const { t } = useTranslation();
    const { total, minimitzats, restaura } = useAvisos();
    const count = total(nivell);
    const amagats = minimitzats(nivell);
    const nom = t(`app.avisos.nivell.${nivell}`);
    const tooltip =
        count === 0
            ? t('app.avisos.chipBuit', { nivell: nom })
            : amagats > 0
              ? t('app.avisos.chipRestaura', { nivell: nom })
              : t('app.avisos.chip', { nivell: nom, count });

    return (
        <Tooltip title={tooltip}>
            <span>
                <ButtonBase
                    disabled={count === 0}
                    aria-label={t('app.avisos.chip', { nivell: nom, count })}
                    onClick={() => restaura(nivell)}
                    sx={(theme) => {
                        const color = theme.palette[NIVELL_PALETTE[nivell]];
                        const dark = theme.palette.mode === 'dark';
                        const minTint = dark ? 4.5 : 3;
                        let tint = dark ? color.light : color.main;
                        for (let i = 0; i < 10 && getContrastRatio(tint, theme.palette.background.paper) < minTint; i++) {
                            tint = dark ? lighten(tint, 0.1) : darken(tint, 0.1);
                        }
                        // Fons de l'estat ple: el color del nivell, enfosquit només si cal perquè el
                        // text blanc hi tingui contrast.
                        const minFilledText = dark ? 4.5 : 3.5;
                        let fill = color.main;
                        for (let i = 0; i < 10 && getContrastRatio('#ffffff', fill) < minFilledText; i++) {
                            fill = darken(fill, 0.05);
                        }
                        const base = {
                            height: 26,
                            gap: '4px',
                            pl: '6px',
                            pr: '8px',
                            borderRadius: '13px',
                            fontSize: 12,
                            fontWeight: 600,
                        };
                        if (count === 0) {
                            // Sense avisos: neutre i de vora discontínua
                            return {
                                ...base,
                                border: `1px dashed ${theme.palette.text.secondary}`,
                                color: theme.palette.text.secondary,
                                cursor: 'default',
                                '&.Mui-disabled': { color: theme.palette.text.secondary },
                            };
                        }
                        if (amagats > 0) {
                            // Amb avisos minimitzats: ple, amb número i icona en blanc, i una vora clara
                            // que el separa de la capçalera als temes foscos.
                            return {
                                ...base,
                                border: `1px solid ${tint}`,
                                backgroundColor: fill,
                                color: '#ffffff',
                                cursor: 'pointer',
                            };
                        }
                        // Amb avisos tots visibles: el número en el color del text de la pantalla
                        // i el nivell a la vora i la icona, sense fons.
                        return {
                            ...base,
                            border: `1px solid ${tint}`,
                            color: theme.palette.text.primary,
                            cursor: 'default',
                            '& .MuiIcon-root': { color: tint },
                        };
                    }}
                >
                    <Icon sx={{ fontSize: 16, m: 0 }}>{NIVELL_ICON[nivell]}</Icon>
                    <span>{count}</span>
                </ButtonBase>
            </span>
        </Tooltip>
    );
};

export const AvisosChips: React.FC = () => (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: '6px', mr: 2 }}>
        {AVIS_NIVELLS.map((nivell) => (
            <AvisChip key={nivell} nivell={nivell} />
        ))}
    </Box>
);

export default AvisosChips;
