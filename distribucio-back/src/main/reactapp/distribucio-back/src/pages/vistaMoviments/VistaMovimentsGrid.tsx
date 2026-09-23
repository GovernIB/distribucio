import { useTranslation } from 'react-i18next';
import { GridPage, useMuiDataGridApiRef } from 'reactlib';
import { CardPage } from '../../components/CardData.tsx';
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import { VistaMovimentsFilter } from './VistaMovimentsFilter.tsx';
import React from 'react';
import { Box, Icon, IconButton, Tooltip, Typography } from '@mui/material';
import { RegistreEstat } from '../../components/RegistreEstat.tsx';
import { useCommentsColumn } from '../../components/CommentsColumn.tsx';
import { useProcesEstatLegend } from '../../components/ProcesEstatLegend.tsx';
import { useVistaMovimentsAccions, useVistaMovimentsMassiveAccions } from './VistaMovimentsAccions.tsx';
import { useGridApiRef } from '@mui/x-data-grid-pro';
import { EMPTY_SELECTION_MODEL } from '../../util/selectionModelUtils.ts';

/** Número: icona de llibre (anotació de registre) + número, amb el triangle d'avís si l'anotació té alertes */
const VistaMovimentsNumeroCell = ({ row }: any) => {
    const { t } = useTranslation();
    return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'flex-start' }}>
            <Tooltip title={t('page.contingut.grid.icona.registre')}>
                <Icon fontSize="small" sx={{ verticalAlign: 'text-bottom', mr: 0.5 }}>
                    menu_book
                </Icon>
            </Tooltip>
            <Box
                component="span"
                title={row?.numero}
                sx={{ minWidth: 0, whiteSpace: 'normal', overflowWrap: 'anywhere' }}
            >
                {row?.numero}
            </Box>
            {!!row?.alertesPendents && (
                <Tooltip title={t('page.registre.avisos.alerta')}>
                    <Icon fontSize="small" color="warning" sx={{ verticalAlign: 'text-bottom', ml: 0.5 }}>
                        warning
                    </Icon>
                </Tooltip>
            )}
        </Box>
    );
};

/** Bústia origen/destí: amb l'avís "La bústia està inactiva" quan la bústia ja no està activa. */
const VistaMovimentsBustiaCell = ({ path, activa }: any) => {
    const { t } = useTranslation();
    if (!path?.length) return null;
    return (
        <Box sx={{ whiteSpace: 'normal', lineHeight: 1.4 }}>
            {path.map((nom: string, index: number) => (
                <React.Fragment key={index}>
                    {index > 0 && ' / '}
                    <Tooltip
                        title={
                            index === 0 ? t('page.contingut.grid.icona.unitat') : t('page.contingut.grid.icona.bustia')
                        }
                    >
                        <Icon fontSize="small" sx={{ fontSize: '18px', verticalAlign: 'text-bottom', mr: 0.5 }}>
                            {index === 0 ? 'account_tree' : 'inbox'}
                        </Icon>
                    </Tooltip>
                    {nom}
                </React.Fragment>
            ))}
            {!activa && (
                <Tooltip title={t('page.vistaMoviments.grid.bustiaInactiva')}>
                    <Icon fontSize="small" color="warning" sx={{ verticalAlign: 'text-bottom', ml: 0.5 }}>
                        warning
                    </Icon>
                </Tooltip>
            )}
        </Box>
    );
};

/** Interessats */
const VistaMovimentsInteressatsCell = ({ value }: any) => {
    if (!value) return null;
    return <Box sx={{ whiteSpace: 'pre-line', lineHeight: 1.4 }}>{value}</Box>;
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
            <Icon fontSize="small" color="error">
                warning
            </Icon>
        </Tooltip>
    );
};

/** Estat: icona de sobre amb els enviaments per email (si n'hi ha) i el detall de l'estat (regla o backoffice). */
const VistaMovimentsEstatCell = ({ row, formattedValue }: any) => {
    const { t } = useTranslation();
    const enviaments: string[] = row?.enviamentsPerEmail ?? [];
    const text =
        row?.procesEstat === 'BACK_PROCESSADA' && row?.backCodi
            ? t('component.RegistreEstat.processadaPer')
            : formattedValue;
    return (
        <>
            {row?.enviatPerEmail && (
                <Tooltip
                    title={
                        <>
                            <div>
                                {t('page.vistaMoviments.grid.enviatPerEmail')}
                                {enviaments.length > 0 && ':'}
                            </div>
                            {enviaments.map((enviament, index) => (
                                <div key={index}>{enviament}</div>
                            ))}
                        </>
                    }
                >
                    <Icon fontSize="small" sx={{ verticalAlign: 'text-bottom', mr: 0.5 }}>
                        mail
                    </Icon>
                </Tooltip>
            )}
            <RegistreEstat entity={row} showReintents={false}>
                {text}
            </RegistreEstat>
        </>
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
    { field: 'data', minWidth: 100 },
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
            <VistaMovimentsBustiaCell path={params.row.bustiaOrigenPath} activa={params.row.bustiaOrigenActiva} />
        ),
    },
    {
        field: 'bustiaDesti',
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => (
            <VistaMovimentsBustiaCell path={params.row.bustiaDestiPath} activa={params.row.bustiaDestiActiva} />
        ),
    },
    {
        field: 'interessatsString',
        flex: 2,
        sortable: false,
        renderCell: (params: any) => <VistaMovimentsInteressatsCell value={params.value} />,
    },
];
const sortModel: any = [{ field: 'data', sort: 'desc' }];

export const VistaMovimentsGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const datagridApiRef = useGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);

    const { actions, components } = useVistaMovimentsAccions(() => apiRef.current?.refresh());
    // Refresca la graella i neteja la selecció (després de qualsevol acció massiva)
    const refreshAfterMassiveAction = () => {
        apiRef.current?.refresh();
        datagridApiRef.current?.setRowSelectionModel?.(EMPTY_SELECTION_MODEL);
    };
    const { actions: massiveActions, components: massiveComponents } =
        useVistaMovimentsMassiveAccions(refreshAfterMassiveAction);
    const { handleOpen: handleLlegenda, component: llegendaComponent } = useProcesEstatLegend();
    const { column: commentsColumn, component: commentsComponent } = useCommentsColumn({
        getId: (row) => row.idRegistre,
        getName: (row) => row.numero,
        onClose: () => apiRef.current?.refresh(),
    });

    // Estat amb la icona de la llegenda a la capçalera; s'ha de definir aquí perquè necessita handleLlegenda
    const estatColumn = {
        field: 'procesEstat',
        minWidth: 140,
        renderHeader: (params: any) => (
            <>
                <Typography variant="body2" sx={{ fontWeight: '500' }}>
                    {params.colDef.headerName}
                </Typography>
                <IconButton
                    size="small"
                    title={t('component.ProcesEstatLegend.title')}
                    sx={{ ml: 0.5 }}
                    onClick={(e) => {
                        e.stopPropagation();
                        handleLlegenda();
                    }}
                >
                    <Icon fontSize="small">list</Icon>
                </IconButton>
            </>
        ),
        renderCell: (params: any) => (
            <VistaMovimentsEstatCell row={params.row} formattedValue={params.formattedValue} />
        ),
    };
    const allColumns = [...columns, estatColumn, commentsColumn];

    return (
        <GridPage>
            <CardPage title={t('page.vistaMoviments.title')}>
                <VistaMovimentsFilter onSpringFilterChange={setSpringFilter} onNamedQueriesChange={setNamedQueries} />
                <StyledMuiGrid
                    apiRef={apiRef}
                    datagridApiRef={datagridApiRef}
                    resourceName="vistaMovimentResource"
                    columns={allColumns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    namedQueries={namedQueries}
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    toolbarMassiveActions={massiveActions}
                    selectionActive
                    paginationActive
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                />
                {components}
                {massiveComponents}
                {llegendaComponent}
                {commentsComponent}
            </CardPage>
        </GridPage>
    );
};

export default VistaMovimentsGrid;
