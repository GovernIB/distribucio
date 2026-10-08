import React from 'react';
import { MuiDataGridColDef } from 'reactlib';
import Box from '@mui/material/Box';
import StyledMuiGrid from '../../components/StyledMuiGrid';

// Les columnes no són ordenables: el servei retorna els fils en memòria, sense ordenació al servidor, i el temps
// de CPU/espera/bloqueig són cadenes amb unitats ("12 %", "0 ns").
const columns: MuiDataGridColDef[] = [
    { field: 'nom', flex: 5, sortable: false },
    { field: 'tempsCpu', flex: 1, sortable: false },
    { field: 'estat', flex: 1, sortable: false },
    { field: 'tempsEspera', flex: 1, sortable: false },
    { field: 'tempsBloqueig', flex: 1, sortable: false },
];

/** Pestanya "Fils d'execució" */
export const MonitorSistemaFils: React.FC = () => (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
        <StyledMuiGrid
            resourceName="monitorFilResource"
            columns={columns}
            toolbarShowQuickFilter
            readOnly
            rowHideUpdateButton
            rowHideDeleteButton
            rowHideDetailsButton
        />
    </Box>
);

export default MonitorSistemaFils;
