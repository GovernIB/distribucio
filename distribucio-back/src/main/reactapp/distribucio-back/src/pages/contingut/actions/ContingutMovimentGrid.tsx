import React from 'react';
import { MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import StyledMuiGrid from '../../../components/StyledMuiGrid';
import { formatDate } from '../../../util/dateUtils';
import * as builder from '../../../util/springFilterUtils';

type ContingutMovimentGridProps = {
    contingutId: any;
};

const columns: MuiDataGridColDef[] = [
    {
        field: 'createdDate',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
    },
    {
        field: 'remitent',
        flex: 1.5,
        renderCell: (params: any) => params.row.remitent?.description,
    },
    { field: 'origenNom', flex: 1.5 },
    { field: 'destiNom', flex: 1.5 },
    { field: 'comentari', flex: 2 },
];

/** Pestanya "Moviments" de l'historial d'un contingut. */
export const ContingutMovimentGrid: React.FC<ContingutMovimentGridProps> = ({ contingutId }) => {
    const apiRef = useMuiDataGridApiRef();

    return (
        <StyledMuiGrid
            toolbarHideCreate
            resourceName="contingutMovimentResource"
            apiRef={apiRef}
            columns={columns}
            readOnly
            filter={builder.eq('contingut.id', contingutId)}
            paginationActive
            rowHideUpdateButton
            rowHideDeleteButton
        />
    );
};

export default ContingutMovimentGrid;
