import React from 'react';
import { MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import StyledMuiGrid, {ToolbarButton} from '../../../components/StyledMuiGrid';
import { formatDate } from '../../../util/dateUtils';
import * as builder from '../../../util/springFilterUtils';
import ContingutLogDetallContent from './ContingutLogDetallDialog';
import {useActions} from "../../registre/detail/RegistreActions.tsx";
import {useTranslation} from "react-i18next";

type ContingutLogGridProps = {
    contingutRow: any;
};

const isSecundari = (row: any): boolean => row?.tipus === 'MODIFICACIO' && row?.objecteId != null;

const perspectives:any = ['RESUM']
const sortModel:any = [{ field: 'createdDate', sort: 'asc' }]
/** Pestanya "Accions" de l'historial d'un contingut. */
export const ContingutLogGrid: React.FC<ContingutLogGridProps> = ({ contingutRow: contingut }) => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const columns: MuiDataGridColDef[] = [
        {
            field: 'createdDate',
            flex: 1,
            valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
        },
        { field: 'resum', flex: 3, hidden: contingut.tipus != 'REGISTRE' },
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
    ].filter(c=>!c.hidden);

    const {informeLogs} = useActions()

    return (
        <StyledMuiGrid
            toolbarHideCreate
            resourceName="contingutLogResource"
            apiRef={apiRef}
            columns={columns}
            filter={builder.eq('contingut.id', contingut.id)}
            sortModel={sortModel}
            perspectives={perspectives}
            popupEditActive
            popupEditFormContent={<ContingutLogDetallContent row />}
            paginationActive
            rowHideDeleteButton

            toolbarElementsWithPositions={contingut.tipus == 'REGISTRE' ?[
                {
                    position: 0,
                    element: <ToolbarButton
                        icon={'description'}
                        variant={'contained'}
                        title={t('page.contingut.historial.informe.label')}
                        onClick={() => informeLogs(contingut.id)}
                    >Informe</ToolbarButton>
                }
            ] :[]}
        />
    );
};

export default ContingutLogGrid;
