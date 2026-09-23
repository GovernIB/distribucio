import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import LimitCanviEstatFormContent from './LimitCanviEstatFormContent';
import useLimitCanviEstatAccions from './LimitCanviEstatAccions';

const columns: MuiDataGridColDef[] = [
    { field: 'usuariCodi', flex: 1 },
    { field: 'descripcio', flex: 2 },
    { field: 'limitMinutLaboral', flex: 1 },
    { field: 'limitMinutNoLaboral', flex: 1 },
    { field: 'limitDiaLaboral', flex: 1 },
    { field: 'limitDiaNoLaboral', flex: 1 },
];

export const LimitCanviEstatGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const accions = useLimitCanviEstatAccions();

    return (
        <GridPage>
            <CardPage title={t('page.limitCanviEstat.grid.title')}>
                <StyledMuiGrid
                    toolbarCreateTitle={t('page.limitCanviEstat.accio.new')}
                    resourceName="limitCanviEstatResource"
                    apiRef={apiRef}
                    toolbarShowQuickFilter
                    columns={columns}
                    paginationActive
                    popupEditActive
                    popupEditFormContent={<LimitCanviEstatFormContent />}
                    popupEditFormDialogResourceTitle={t('page.limitCanviEstat.form.resourceTitle')}
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.limitCanviEstat.accio.crearOk',
                        updateSuccess: 'page.limitCanviEstat.accio.modificarOk',
                        deleteSuccess: 'page.limitCanviEstat.accio.esborrarOk',
                    }}
                    rowHideUpdateButton
                    rowHideDeleteButton
                    rowAdditionalActions={accions}
                />
            </CardPage>
        </GridPage>
    );
};

export default LimitCanviEstatGrid;
