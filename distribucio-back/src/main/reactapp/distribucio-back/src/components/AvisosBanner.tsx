import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert, { type AlertColor } from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { icons } from '../util/icons';
import { useAvisos, getAvisNivell } from './AvisosContext';
import type { AvisSse } from './SseClient';

const AVISOS_GAP = 2;

/**
 * Avisos actius de l'aplicació, damunt del contingut de la pàgina.
 *
 * Equival a l'acordió d'avisos de la interfície JSP (`decorators/default.jsp`): l'assumpte sempre
 * és visible, amb el color i l'icona del nivell de l'avís, i el missatge es desplega en fer-hi
 * clic. A diferència d'allà, cada avís es pot minimitzar amb la X: deixa de sortir aquí però no
 * desapareix, es recupera des dels chips de la capçalera ({@link ./AvisosChips.tsx}). En recarregar
 * la pàgina tornen a sortir tots (veure {@link ./AvisosContext.tsx}).
 *
 * La llista arriba per SSE (veure {@link ./SseClient.tsx}) i s'actualitza tota sola quan un
 * administrador crea, modifica, activa, desactiva o esborra un avís.
 */

const NIVELL_SEVERITY: Record<string, AlertColor> = {
    INFO: 'info',
    WARNING: 'warning',
    ERROR: 'error',
};

const AvisBanner: React.FC<{ avis: AvisSse }> = ({ avis }) => {
    const { t } = useTranslation();
    const { minimitza } = useAvisos();
    const [desplegat, setDesplegat] = React.useState(false);

    return (
        <Alert
            severity={NIVELL_SEVERITY[getAvisNivell(avis)]}
            sx={{ py: 0, px: 1.5, alignItems: 'center' }}
            action={
                <>
                    <IconButton
                        color="inherit"
                        size="small"
                        aria-label={desplegat ? t('app.avisos.amaga') : t('app.avisos.mostra')}
                        onClick={() => setDesplegat((previous) => !previous)}
                    >
                        <Icon sx={{ m: 0 }}>{desplegat ? icons.plega : icons.desplega}</Icon>
                    </IconButton>
                    <IconButton
                        color="inherit"
                        size="small"
                        aria-label={t('app.avisos.tanca')}
                        onClick={() => minimitza(avis.id)}
                    >
                        <Icon sx={{ m: 0 }}>{icons.cancela}</Icon>
                    </IconButton>
                </>
            }
        >
            <AlertTitle sx={{ m: 0, lineHeight: 1.4 }}>{avis.assumpte}</AlertTitle>
            <Collapse in={desplegat}>
                {/* El missatge s'edita en un camp de text pla: es respecten els salts de línia
                    però no s'hi interpreta cap marcatge. */}
                <Box sx={{ mt: 1, whiteSpace: 'pre-line' }}>{avis.missatge}</Box>
            </Collapse>
        </Alert>
    );
};

export const AvisosBanner: React.FC = () => {
    const { visibles: avisos } = useAvisos();

    if (avisos.length === 0) {
        return null;
    }

    return (
        // La separació amb la pantalla de sota la posa el `gap` de <main> (veure frameSx a BaseApp.tsx);
        // entre els avisos, la mateixa separació.
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: AVISOS_GAP + 'px', flex: '0 0 auto' }}>
            {avisos.map((avis) => (
                <AvisBanner key={avis.id} avis={avis} />
            ))}
        </Box>
    );
};

export default AvisosBanner;
