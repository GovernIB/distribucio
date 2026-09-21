import { useTranslation } from 'react-i18next';
import { GridPage } from 'reactlib';
import { CardPage } from '../../components/CardData.tsx';
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import { VistaMovimentsFilter } from './VistaMovimentsFilter.tsx';
import React from 'react';
import { Icon, Tooltip } from '@mui/material';
import { useVistaMovimentsAccions } from './VistaMovimentsAccions.tsx';

/** Número: icona de llibre (anotació de registre) + número, amb el triangle d'avís si l'anotació té alertes */
const VistaMovimentsNumeroCell = ({ row }: any) => {
    const { t } = useTranslation();
    return (
        <>
            <Tooltip title={t('page.contingut.grid.icona.registre')}>
                <Icon fontSize="small" sx={{ verticalAlign: 'text-bottom', mr: 0.5 }}>
                    menu_book
                </Icon>
            </Tooltip>
            <span title={row?.numero}>{row?.numero}</span>
            {!!row?.alertesPendents && (
                <Tooltip title={t('page.registre.avisos.alerta')}>
                    <Icon fontSize="small" color="warning" sx={{ verticalAlign: 'text-bottom', ml: 0.5 }}>
                        warning
                    </Icon>
                </Tooltip>
            )}
        </>
    );
};

/** Bústia origen/destí: amb l'avís "La bústia està inactiva" quan la bústia ja no està activa. */
const VistaMovimentsBustiaCell = ({ description, activa }: any) => {
    const { t } = useTranslation();
    if (!description) return null;
    return (
        <>
            <Tooltip title={t('page.contingut.grid.icona.bustia')}>
                <Icon fontSize="small" sx={{ verticalAlign: 'text-bottom', mr: 0.5 }}>
                    inbox
                </Icon>
            </Tooltip>
            {description}
            {!activa && (
                <Tooltip title={t('page.vistaMoviments.grid.bustiaInactiva')}>
                    <Icon fontSize="small" color="warning" sx={{ verticalAlign: 'text-bottom', ml: 0.5 }}>
                        warning
                    </Icon>
                </Tooltip>
            )}
        </>
    );
};

/** Avís "Error": una icona d'avís amb el missatge corresponent a l'estat en què s'ha produït l'error. */
const VistaMovimentsErrorCell = ({ row }: any) => {
    const { t } = useTranslation();
    if (row?.procesError == null) return null;
    const key =
        row.procesEstat === 'ARXIU_PENDENT'
            ? 'ARXIU_PENDENT'
            : row.procesEstat === 'REGLA_PENDENT'
              ? 'REGLA_PENDENT'
              : row.procesEstat === 'BACK_PENDENT'
                ? 'BACK_PENDENT'
                : row.procesEstat === 'BACK_ERROR'
                  ? 'BACK_ERROR'
                  : 'default';
    return (
        <Tooltip title={t(`page.registre.avisos.procesError.${key}`)}>
            <Icon fontSize="small" color="error">warning</Icon>
        </Tooltip>
    );
};

const columns = [
    {
        field: 'numero',
        flex: 1.5,
        renderCell: (params: any) => <VistaMovimentsNumeroCell row={params.row} />,
    },
    { field: 'titol', flex: 2 },
    { field: 'numeroOrigen', flex: 1 },
    { field: 'remitent', flex: 1.5 },
    { field: 'data', minWidth: 150 },
    {
        field: 'procesError',
        flex: 0.5,
        sortable: false,
        renderCell: (params: any) => <VistaMovimentsErrorCell row={params.row} />,
    },
    {
        field: 'bustiaOrigen',
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => (
            <VistaMovimentsBustiaCell description={params.formattedValue} activa={params.row.bustiaOrigenActiva} />
        ),
    },
    {
        field: 'bustiaDesti',
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => (
            <VistaMovimentsBustiaCell description={params.formattedValue} activa={params.row.bustiaDestiActiva} />
        ),
    },
    { field: 'interessatsString', flex: 2, sortable: false },
    { field: 'procesEstat', flex: 1.5 },
];
const sortModel: any = [{ field: 'data', sort: 'desc' }];

export const VistaMovimentsGrid = () => {
    const { t } = useTranslation();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const { actions, components } = useVistaMovimentsAccions();

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.vistaMoviments.title')}>
                <VistaMovimentsFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />
                <StyledMuiGrid
                    resourceName="vistaMovimentResource"
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    namedQueries={namedQueries}
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    paginationActive
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                />
                {components}
            </CardPage>
        </GridPage>
    );
};

export default VistaMovimentsGrid;
