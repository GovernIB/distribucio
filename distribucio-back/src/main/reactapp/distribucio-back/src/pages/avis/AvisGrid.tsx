import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import AvisFormContent from './AvisFormContent';
import { formatDate } from '../../util/dateUtils';
import useAvisAccions from './AvisAccions';

const columns: MuiDataGridColDef[] = [
    { field: 'assumpte', flex: 4 },
    {
        field: 'dataInici',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY') : ''),
    },
    {
        field: 'dataFinal',
        flex: 1,
        valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY') : ''),
    },
    { field: 'entitat', flex: 2 },
    { field: 'avisNivell', flex: 1 },
    { field: 'activa', flex: 0.6, type: 'boolean' },
];

export const AvisGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const refresh = () => apiRef.current?.refresh?.();
    const accions = useAvisAccions(refresh);

    const columnsWithLabels = React.useMemo(
        () => [
            ...columns.map((column) => ({
                ...column,
                headerName: t(`page.avisos.grid.column.${column.field}`),
            })),
        ],
        [t]
    );

    return (
        <GridPage>
            <CardPage title={t('page.avisos.grid.title')}>
                <StyledMuiGrid
                    toolbarCreateTitle={t('page.avisos.accio.new')}
                    resourceName="avisResource"
                    apiRef={apiRef}
                    toolbarShowQuickFilter
                    columns={columnsWithLabels}
                    paginationActive
                    popupEditActive
                    popupEditFormContent={<AvisFormContent />}
                    popupEditFormDialogResourceTitle={t('page.avisos.form.resourceTitle')}
                    // popupEditFormDialogComponentProps={{maxWidth: 'sm'}}
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.avisos.accio.crearOk',
                        updateSuccess: 'page.avisos.accio.modificarOk',
                        deleteSuccess: 'page.avisos.accio.esborrarOk',
                    }}
                    // Les accions de la fila són només les del menú (veure useEntitatAccions): s'amaguen
                    // les que la graella hi posa pel seu compte per no duplicar modificar i esborrar.
                    rowHideUpdateButton
                    rowHideDeleteButton
                    rowAdditionalActions={accions}
                />
            </CardPage>
        </GridPage>
    );
};

export default AvisGrid;
