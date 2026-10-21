import { useTranslation } from 'react-i18next';
import { GridPage, useMuiDataGridApiRef } from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import RegistreFilter from './RegistreFilter.tsx';
import BustiaPathCell from '../../components/BustiaPathCell.tsx';
import React, { useEffect, useMemo, useRef } from 'react';
import { Box, Icon, Tooltip } from '@mui/material';
import { useCommentsColumn } from '../../components/CommentsColumn.tsx';
import { RegistreEstat } from '../../components/RegistreEstat.tsx';
import { useRegistreActions, useRegistreMassiveActions } from './detail/RegistreActions.tsx';
import { useGridApiRef } from '@mui/x-data-grid-pro';
import { useRegistreDetail, useBasicDetail } from './detail/useRegistreDetail.tsx';
import { useSearchParams } from 'react-router-dom';
import Load from '../../components/Load.tsx';
import IconButton from '@mui/material/IconButton';
import { useConfig } from '../../components/ConfigProvider.tsx';
import useProcesEstatLegend from '../../components/ProcesEstatLegend.tsx';

const RegistreAvisos = ({ entity }: any) => {
    const { t } = useTranslation();
    return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', gap: 0.25, lineHeight: 1.4 }}>
            {entity.sobreescriure && (
                <Icon fontSize="small" title={t('page.registre.avisos.sobreescriure')}>
                    {icons.historial}
                </Icon>
            )}
            {entity.enviatPerEmail && (
                <Tooltip
                    title={
                        <>
                            {t('page.registre.avisos.enviatPerEmail')}
                            {entity.enviamentsPerEmail.map?.((e: string) => (
                                <>
                                    <br /> {'>'} {e}
                                </>
                            ))}
                        </>
                    }
                >
                    <Icon fontSize="small" title={t('page.registre.avisos.enviatPerEmail')}>
                        {icons.correu}
                    </Icon>
                </Tooltip>
            )}
            {entity.documentacioFisicaCodi == '1' && (
                <Icon fontSize="small" title={t('page.registre.avisos.documentacio._1')} color="error">
                    {icons.documentacioFisica}
                </Icon>
            )}
            {entity.documentacioFisicaCodi == '2' && (
                <>
                    <Icon fontSize="small" title={t('page.registre.avisos.documentacio._2')} color="warning">
                        {icons.documentacioElectronica}
                    </Icon>
                    <Icon fontSize="small" title={t('page.registre.avisos.documentacio._2')} color="warning">
                        {icons.documentacioFisica}
                    </Icon>
                </>
            )}
            {entity.documentacioFisicaCodi == '3' && (
                <Icon fontSize="small" title={t('page.registre.avisos.documentacio._3')} color="success">
                    {icons.documentacioElectronica}
                </Icon>
            )}
            {entity.annexosEstatEsborrany > 0 && (
                <Icon fontSize="small" title={t('page.registre.avisos.estatEsborrany')} color={'warning'}>
                    {icons.error}
                </Icon>
            )}
            {entity.alerta && (
                <Icon fontSize="small" title={t('page.registre.avisos.alerta')} color={'warning'}>
                    {icons.alertes}
                </Icon>
            )}
            {entity.procesError != null &&
                (entity.procesError == 'ARXIU_PENDENT' ? (
                    <Icon fontSize="small" title={t('page.registre.avisos.procesError.ARXIU_PENDENT')} color={'error'}>
                        {icons.advertencia}
                    </Icon>
                ) : entity.procesError == 'REGLA_PENDENT' ? (
                    <Icon fontSize="small" title={t('page.registre.avisos.procesError.REGLA_PENDENT')} color={'error'}>
                        {icons.advertencia}
                    </Icon>
                ) : entity.procesError == 'BACK_PENDENT' ? (
                    <Icon fontSize="small" title={t('page.registre.avisos.procesError.BACK_PENDENT')} color={'error'}>
                        {icons.advertencia}
                    </Icon>
                ) : entity.procesError == 'BACK_ERROR' ? (
                    <Icon fontSize="small" title={t('page.registre.avisos.procesError.BACK_ERROR')} color={'error'}>
                        {icons.advertencia}
                    </Icon>
                ) : (
                    <Icon fontSize="small" title={t('page.registre.avisos.procesError.default')} color={'error'}>
                        {icons.advertencia}
                    </Icon>
                ))}
            {entity.pendentExecucioMassiva && (
                <Icon fontSize="small" title={t('page.registre.avisos.pendentExecucioMassiva')} color={'warning'}>
                    {icons.programat}
                </Icon>
            )}
        </Box>
    );
};

const RegistreRemitent = ({ entity }: any) => {
    const { t } = useTranslation();
    return entity.darrerMoviment != null ? (
        entity.darrerMovimentResource.bustiaOrigen != null &&
        entity.darrerMovimentResource.unitatOrganitzativaOrigen != null ? (
            <>
                /
                <Icon fontSize="small" title={entity.darrerMovimentResource.unitatOrganitzativaOrigen.description}>
                    {icons.unitatOrganitzativa}
                </Icon>
                /
                <Icon fontSize="small" title={entity.darrerMovimentResource.bustiaOrigen.description}>
                    {icons.bustia}
                </Icon>
                {entity.darrerMovimentResource.remitent.description}
            </>
        ) : (
            <>
                <Icon fontSize="small">{icons.inici}</Icon>
                {entity.oficinaDescripcio} ({entity.darrerMovimentResource.remitent.description})
            </>
        )
    ) : (
        <Icon title={t('page.registre.remitentEmpty')}>{icons.senseDades}</Icon>
    );
};

const columns = (t: any, handleEstat: () => void) => [
    { field: 'numero', flex: 2 },
    { field: 'extracte', flex: 2 },
    { field: 'numeroOrigen', flex: 1 },
    {
        field: 'remitent',
        headerName: t('page.registre.grid.remitent'),
        flex: 1.5,
        renderCell: (params: any) => <RegistreRemitent entity={params.row} />,
    },
    // { field: 'createdDate', flex: 1 },
    // L'amplada mínima és la que necessita la part "dd/mm/aaaa" (76px) més el padding de la cel·la
    // (2 x 10px): per sota, la data quedava tallada. L'hora passa a la línia de sota.
    { field: 'data', flex: 1, minWidth: 100 },
    {
        field: 'procesEstat',
        flex: 2,
        minWidth: 140,
        headerName: (
            <>
                {t('page.registre.grid.procesEstat')}
                <IconButton
                    size="small"
                    sx={{ ml: 0.5 }}
                    onClick={(e) => {
                        e.stopPropagation();
                        handleEstat();
                    }}
                >
                    <Icon fontSize="small">{icons.llista}</Icon>
                </IconButton>
            </>
        ),
        renderCell: (params: any) => <RegistreEstat entity={params.row}>{params.formattedValue}</RegistreEstat>,
    },
    {
        field: 'avisos',
        headerName: t('page.registre.grid.avisos'),
        flex: 1,
        sortable: false,
        renderCell: (params: any) => <RegistreAvisos entity={params.row} />,
    },
    {
        field: 'pare',
        flex: 3,
        sortable: false,
        renderCell: (params: any) => (
            <BustiaPathCell
                path={params.row.pare?.id ? [params.row.unitatAdministrativaDescripcio, params.formattedValue] : []}
            >
                {(!params.row.pare?.id || !params.row.bustiaActiva) && (
                    <Icon
                        title={t('page.registre.grid.bustiaActiva')}
                        color={'warning'}
                        sx={{ fontSize: '18px', verticalAlign: 'text-bottom', ml: 0.5 }}
                    >
                        {icons.advertencia}
                    </Icon>
                )}
            </BustiaPathCell>
        ),
    },
    { field: 'interessatsString', flex: 4, sortable: false },
];
const perspectives = ['DARRER_MOVIMENT', 'COMMENT_NUM'];
const sortModel: any = [{ field: 'data', sort: 'desc' }];

export const RegistreGrid = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const gridApiRef = useGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const [namedQueries, setNamedQueries] = React.useState<string[]>([]);
    // La primera vegada que s'obre la pantalla, el filtre obté els valors per defecte del backend
    // (estat pendent, bústies inactives...) de manera asíncrona. Fins que no els té, la graella
    // mostra la cortina de càrrega i no consulta res. Quan ja els té, se'n munta una instància
    // nova (key) que arrenca amb el filtre definitiu i fa una sola consulta. No n'hi ha prou amb
    // activar la consulta a la mateixa instància: la graella de base-react copia el filtre a un
    // estat intern amb un efecte, i consultaria primer sense filtre i després amb cada pas; a més
    // apaga la cortina amb la primera resposta, encara que la descarti, i mostraria "no hi ha
    // dades" fins que arribés la bona.
    const [filtreLlest, setFiltreLlest] = React.useState(false);

    const { handleOpen: handleOpen, component: component } = useProcesEstatLegend();
    const { column: commentsColumn, component: dialogComponent } = useCommentsColumn();

    const { getByName } = useConfig();

    const isPermesAssignarAnotacions = getByName('es.caib.distribucio.assignar.anotacions');

    const additionalColumns: any[] = [
        ...columns(t, handleOpen),
        ...(isPermesAssignarAnotacions ? [{ field: 'agafatPer', flex: 2 }] : []),
        commentsColumn,
    ];

    const refresh = () => {
        apiRef.current?.refresh();
    };

    const { handleOpen: handleDetail, dialog: dialogDetail } = useRegistreDetail({
        gridApiRef,
        filter: springFilter,
        namedQueries,
    });

    // Enllaç directe a una anotació: /registre?registreID=XXX
    const { handleOpen: handleOpenBasic, dialog: dialogBasic } = useBasicDetail();
    const [searchParams, setSearchParams] = useSearchParams();
    const registreIdParam = searchParams.get('registreID');
    const registreIdObert = useRef<string | null>(null);
    useEffect(() => {
        if (filtreLlest && registreIdParam && registreIdParam !== registreIdObert.current) {
            registreIdObert.current = registreIdParam;
            handleOpenBasic(registreIdParam, undefined);
            // es neteja el paràmetre perquè un refresc de la pàgina no torni a obrir el detall
            setSearchParams(
                (prev) => {
                    prev.delete('registreID');
                    return prev;
                },
                { replace: true }
            );
        }
    }, [registreIdParam, filtreLlest]);

    const { actions, components } = useRegistreActions(refresh);
    const { actions: massiveActions, components: massiveComponents } = useRegistreMassiveActions();

    const additionalActions = useMemo(
        () => [
            {
                label: t('page.contingut.accio.detalls.label'),
                icon: icons.detall,
                showInMenu: true,
                onClick: handleDetail,
            },
            ...actions,
        ],
        [actions]
    );

    return (
        <GridPage>
            <ScreenFrame icon={icons.anotacio} title={t('page.registre.title')}>
                <RegistreFilter
                    onSpringFilterChange={setSpringFilter}
                    onNamedQueriesChange={setNamedQueries}
                    onReady={() => setFiltreLlest(true)}
                />

                <Load value={filtreLlest} noEffect>
                    <StyledMuiGrid
                        apiRef={apiRef}
                        datagridApiRef={gridApiRef}
                        resourceName="registreResource"
                        columns={additionalColumns}
                        filter={springFilter}
                        perspectives={perspectives}
                        namedQueries={namedQueries}
                        autoFindDisabled={!filtreLlest}
                        loading={!filtreLlest ? true : undefined}
                        sortModel={sortModel}
                        // filterCount={(num) => num + (namedQueries.length || 0)}
                        // toolbarShowFilterCount

                        onRowClick={(params: any, event: any) => {
                            // el checkbox de selecció i el menú d'accions no han d'obrir el detall
                            if (event?.target?.closest?.('.MuiCheckbox-root, [data-field="actions"], [data-field="__check__"]')) return;
                            handleDetail(params?.row?.id, params?.row);
                        }}
                        rowAdditionalActions={additionalActions}
                        // reactlib crea la columna del menú d'accions amb 100px fixos i només hi va el
                        // botó "⋮": s'estreny com a RIPEA (ExpedientGrid) per deixar més espai al text.
                        rowActionsColumnProps={{ width: 55, minWidth: 55 }}
                        toolbarMassiveActions={massiveActions}
                        selectionActive
                        isRowSelectable={(params: any) => !params.row.pendentExecucioMassiva}
                        paginationActive
                        toolbarHideCreate
                        rowHideUpdateButton
                        rowHideDeleteButton
                    />
                </Load>
                {dialogComponent}
                {components}
                {massiveComponents}
                {dialogDetail}
                {dialogBasic}
                {component}
            </ScreenFrame>
        </GridPage>
    );
};
