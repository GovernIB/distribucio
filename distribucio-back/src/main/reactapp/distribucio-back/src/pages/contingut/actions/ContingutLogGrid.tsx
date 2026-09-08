import React from 'react';
import { MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import StyledMuiGrid from '../../../components/StyledMuiGrid';
import { formatDate } from '../../../util/dateUtils';
import * as builder from '../../../util/springFilterUtils';
import ContingutLogDetallContent from './ContingutLogDetallDialog';

type ContingutLogGridProps = {
    contingutId: any;
};

const isSecundari = (row: any): boolean => row?.tipus === 'MODIFICACIO' && row?.objecteId != null;

const columns: MuiDataGridColDef[] = [
    {
        field: 'createdDate',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
    },
    { field: 'createdByFullName', flex: 1.5 },
    {
        field: 'tipus',
        flex: 2,
        renderCell: (params: any) => {
            if (!isSecundari(params.row)) {
                return params.formattedValue;
            }
            const objecte =
                params.row.objecteNom ?? `${params.row.objecteTipus}#${params.row.objecteId}`;
            return `${params.formattedValue} — "${objecte}"`;
        },
    },
];

/** Pestanya "Accions" de l'historial d'un contingut. */
export const ContingutLogGrid: React.FC<ContingutLogGridProps> = ({ contingutId }) => {
    const apiRef = useMuiDataGridApiRef();

    return (
        <StyledMuiGrid
            toolbarHideCreate
            resourceName="contingutLogResource"
            apiRef={apiRef}
            columns={columns}
            filter={builder.eq('contingut.id', contingutId)}
            popupEditActive
            popupEditFormContent={<ContingutLogDetallContent row />}
            paginationActive
            rowHideDeleteButton
        />
    );
};

export default ContingutLogGrid;
