import React from 'react';
import { useTranslation } from 'react-i18next';
import Alert, { type AlertColor } from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useAvisosSse, type AvisSse } from './SseClient';

/**
 * Avisos actius de l'aplicació, damunt del contingut de la pàgina.
 *
 * Equival a l'acordió d'avisos de la interfície JSP (`decorators/default.jsp`): l'assumpte sempre
 * és visible, amb el color i l'icona del nivell de l'avís, i el missatge es desplega en fer-hi
 * clic. Com allà, els avisos no es poden descartar: hi són mentre siguin actius.
 *
 * La llista arriba per SSE (veure {@link ./SseClient.tsx}) i s'actualitza tota sola quan un
 * administrador crea, modifica, activa, desactiva o esborra un avís.
 */

const NIVELL_SEVERITY: Record<string, AlertColor> = {
    INFO: 'info',
    WARNING: 'warning',
    ERROR: 'error',
};

const getSeverity = (avisNivell: string): AlertColor => NIVELL_SEVERITY[avisNivell] ?? 'info';

const AvisBanner: React.FC<{ avis: AvisSse }> = ({ avis }) => {
    const { t } = useTranslation();
    const [desplegat, setDesplegat] = React.useState(false);
    return (
        <Alert
            severity={getSeverity(avis.avisNivell)}
            sx={{ py: 0.75, alignItems: 'center' }}
            action={
                <IconButton
                    color="inherit"
                    size="small"
                    aria-label={desplegat ? t('app.avisos.amaga') : t('app.avisos.mostra')}
                    onClick={() => setDesplegat((previous) => !previous)}
                >
                    <Icon sx={{ m: 0 }}>{desplegat ? 'expand_less' : 'expand_more'}</Icon>
                </IconButton>
            }
        >
            <AlertTitle sx={{ m: 0 }}>{avis.assumpte}</AlertTitle>
            <Collapse in={desplegat}>
                {/* El missatge s'edita en un camp de text pla: es respecten els salts de línia
                    però no s'hi interpreta cap marcatge. */}
                <Box sx={{ mt: 1, whiteSpace: 'pre-line' }}>{avis.missatge}</Box>
            </Collapse>
        </Alert>
    );
};

export const AvisosBanner: React.FC = () => {
    const avisos = useAvisosSse();
    if (avisos.length === 0) {
        return null;
    }
    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mb: 1 }}>
            {avisos.map((avis) => (
                <AvisBanner key={avis.id} avis={avis} />
            ))}
        </Box>
    );
};

export default AvisosBanner;
