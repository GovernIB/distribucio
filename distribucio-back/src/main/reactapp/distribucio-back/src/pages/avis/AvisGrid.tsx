import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import AvisFormContent from './AvisFormContent';
import { formatDate } from '../../util/dateUtils';
import useAvisAccions from './AvisAccions';

// Les capçaleres no es declaren aquí: el MuiDataGrid les omple amb l'etiqueta que el backend
// publica per a cada camp (el `_prompt` del HAL-FORMS, veure distribucio-back-rest-messages).
// És la mateixa font que fan servir el formulari i les capçaleres del fitxer d'exportació, que
// es genera al servidor (BaseReadonlyResourceController.toExportFields).
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
    // El camp del recurs es diu `actiu` (AvisResource), no `activa`.
    { field: 'actiu', flex: 0.6, type: 'boolean' },
];

export const AvisGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const refresh = () => apiRef.current?.refresh?.();
    const accions = useAvisAccions(refresh);

    return (
        <GridPage>
            <CardPage title={t('page.avisos.grid.title')}>
                <StyledMuiGrid
                    toolbarCreateTitle={t('page.avisos.accio.new')}
                    resourceName="avisResource"
                    apiRef={apiRef}
                    toolbarShowQuickFilter
                    columns={columns}
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
