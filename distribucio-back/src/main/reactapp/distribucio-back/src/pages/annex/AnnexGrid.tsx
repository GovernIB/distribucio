import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Icon, Tooltip } from '@mui/material';
import { useGridApiRef } from '@mui/x-data-grid-pro';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { formatDate } from '../../util/dateUtils';
import AnnexFilter from './AnnexFilter';
import useAnnexAccions, { useAnnexMassiveActions } from './AnnexAccions';
import useAnnexDetailDialog from './actions/AnnexDetailDialog';

const ESTATS_FIRMA_AMB_ERROR = ['FIRMA_INVALIDA', 'ERROR_VALIDANT'];

const sortModel: any = [{ field: 'dataAnotacio', sort: 'desc' }];

export const AnnexArxiuEstatCell: React.FC<{ params: any }> = ({ params }) => {
    const { t } = useTranslation();
    const row = params?.row;
    const estat = row?.arxiuEstat;

    if (estat === 'ESBORRANY') {
        const ambErrorFirma = ESTATS_FIRMA_AMB_ERROR.includes(row?.validacioFirmaEstat);
        return (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                {params.formattedValue}
                {ambErrorFirma && (
                    <Tooltip
                        arrow
                        title={row?.validacioFirmaError ?? ''}
                        slotProps={{
                            tooltip: {
                                sx: { maxWidth: 700 },
                            },
                        }}
                    >
                        <Icon fontSize="small" color="error" sx={{ fontSize: '18px' }}>{icons.error}</Icon>
                    </Tooltip>
                )}
            </Box>
        );
    }

    if (estat === 'DEFINITIU') {
        return params.formattedValue;
    }

    return (
        <Tooltip title={t('page.annex.grid.arxiuEstat.buitAvis')}>
            <Icon fontSize="small" color="warning">{icons.advertencia}</Icon>
        </Tooltip>
    );
};

export const bytesToMb = (bytes: number, decimals: number = 2): number => {
    if (bytes <= 0) return 0;
    const value = (bytes * 8) / 1_000_000;
    const multiplier = Math.pow(10, decimals);
    return Math.floor(value * multiplier) / multiplier;
};

const AnnexGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const gridApiRef = useGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const { show: mostrarDetall, component: detailDialog } = useAnnexDetailDialog();
    const refresh = () => {
        apiRef.current?.refresh();
    };
    const { actions: accions, components: accionsComponents } = useAnnexAccions(
        mostrarDetall,
        gridApiRef,
        springFilter,
        refresh
    );
    const { actions: massiveActions, components: massiveComponents } = useAnnexMassiveActions();

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'registreNumero',
                flex: 1.5,
                renderCell: (params: any) => {
                    const numero = params?.row?.registreNumero;
                    if (!numero) {
                        return '';
                    }
                    const copia = params?.row?.registreNumeroCopia;
                    const sufix = copia
                        ? t('page.annex.grid.registre.copia', { num: copia })
                        : t('page.annex.grid.registre.original');
                    return <span title={`${numero} (${sufix})`}>{`${numero} (${sufix})`}</span>;
                },
            },
            { field: 'titol', flex: 1.5 },
            {
                field: 'dataAnotacio',
                flex: 0.7,
                valueFormatter: (value: string) => (value ? formatDate(value, 'DD/MM/YYYY HH:mm:ss') : ''),
            },
            { field: 'fitxerNom', flex: 1 },
            {
                field: 'arxiuEstat',
                flex: 0.7,
                renderCell: (params: any) => <AnnexArxiuEstatCell params={params} />,
            },
            { field: 'fitxerTipusMime', flex: 0.7 },
            { field: 'fitxerTamany', flex: 1,
                valueFormatter: (value:number) => bytesToMb(value),
                renderCell: (params:any) => <>{params.formattedValue} (Mb)</>,
            },
            { field: 'signaturaInfo', flex: 0.7, sortable: false },
        ],
        [t]
    );

    return (
        <GridPage>
            <ScreenFrame icon={icons.annex} title={t('page.annex.grid.title')}>
                <AnnexFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                    selectionActive
                    resourceName="registreAnnexResource"
                    apiRef={apiRef}
                    datagridApiRef={gridApiRef}
                    columns={columns}
                    filter={springFilter}
                    paginationActive
                    sortModel={sortModel}
                    onRowClick={(params: any) => mostrarDetall(params?.row?.id, params?.row)}
                    rowAdditionalActions={accions}
                    toolbarMassiveActions={massiveActions}
                />
                {detailDialog}
                {accionsComponents}
                {massiveComponents}
            </ScreenFrame>
        </GridPage>
    );
};

export default AnnexGrid;
