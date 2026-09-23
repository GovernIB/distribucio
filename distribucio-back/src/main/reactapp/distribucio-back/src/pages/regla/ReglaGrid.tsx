import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import { useGridApiRef } from '@mui/x-data-grid-pro';
import { Box, Chip, Icon, Tooltip } from '@mui/material';
import { CardPage } from '../../components/CardData';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { ReglaFilter } from './ReglaFilter';
import ReglaFormContent from './ReglaFormContent';
import { useReglaAccions, useReglaMassiveAccions } from './ReglaAccions';

/** Nom: mostra un avís si la unitat organitzativa de filtre ha quedat obsoleta (reglaList.jsp: nomTemplate). */
const ReglaNomCell = ({ row }: any) => {
    const { t } = useTranslation();
    const obsoleta = row?.unitatOrganitzativaFiltre != null && row?.unitatOrganitzativaFiltreEstat !== 'V';
    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <span>{row?.nom}</span>
            {obsoleta && (
                <Tooltip title={t('page.regla.grid.unitatObsoleta')}>
                    <Icon fontSize="small" color="warning">
                        warning
                    </Icon>
                </Tooltip>
            )}
        </Box>
    );
};

/** Unitat organitzativa de filtre: mateix avís d'obsolescència que la columna Nom. */
const ReglaUnitatFiltreCell = ({ row }: any) => {
    const { t } = useTranslation();
    if (!row?.unitatOrganitzativaFiltre) return null;
    const obsoleta = row.unitatOrganitzativaFiltreEstat !== 'V';
    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
            <span>{row.unitatOrganitzativaFiltre?.description}</span>
            {obsoleta && (
                <Tooltip title={t('page.regla.grid.unitatObsoleta')}>
                    <Icon fontSize="small" color="warning">
                        warning
                    </Icon>
                </Tooltip>
            )}
        </Box>
    );
};

/** Destinació: nom de la destinació + una etiqueta de color diferent segons el tipus de regla */
const ReglaDestinacioCell = ({ row }: any) => {
    const { t } = useTranslation();
    let color: 'info' | 'error' | 'success' | undefined;
    let nom: string | undefined;
    let label: string | undefined;
    if (row?.tipus === 'BUSTIA') {
        color = 'info';
        nom = row?.bustiaDesti?.description;
        label = t('page.regla.grid.columna.icona.bustia');
    } else if (row?.tipus === 'BACKOFFICE') {
        color = 'error';
        nom = row?.backofficeDesti?.description;
        label = t('page.regla.grid.columna.icona.backoffice');
    } else if (row?.tipus === 'UNITAT') {
        color = 'success';
        nom = row?.unitatDesti?.description;
        label = t('page.regla.grid.columna.icona.unitat');
    } else {
        return null;
    }
    return (
        <Box sx={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center', width: '100%', rowGap: 0.25 }}>
            <span>{nom}</span>
            <Chip size="small" color={color} label={label} sx={{ ml: 'auto' }} />
        </Box>
    );
};

/**
 * Definició de columnes. Es declara com a funció (i no com a const de mòdul) perquè algunes
 * capçaleres necessiten traducció (`t`), que només està disponible dins del component.
 */
const getColumns = (t: (key: string) => string): MuiDataGridColDef[] => [
    { field: 'nom', flex: 1.5, sortable: false, renderCell: (params: any) => <ReglaNomCell row={params.row} /> },
    { field: 'assumpteCodiFiltre', flex: 1, sortable: false },
    { field: 'procedimentCodiFiltre', flex: 1, sortable: false },
    { field: 'serveiCodiFiltre', flex: 1, sortable: false },
    { field: 'tramitCodiFiltre', flex: 1, sortable: false },
    {
        field: 'unitatOrganitzativaFiltre',
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => <ReglaUnitatFiltreCell row={params.row} />,
    },
    { field: 'bustiaFiltre', flex: 1, sortable: false },
    { field: 'presencial', flex: 0.6, sortable: false },
    {
        field: 'tipus',
        headerName: t('page.regla.grid.columna.destinacio'),
        flex: 1.5,
        sortable: false,
        renderCell: (params: any) => <ReglaDestinacioCell row={params.row} />,
    },
    { field: 'activa', flex: 0.5, type: 'boolean', sortable: false },
    {
        field: 'aturarAvaluacio',
        headerName: t('page.regla.grid.columna.aturar'),
        flex: 0.5,
        type: 'boolean',
        sortable: false,
    },
];

const sortModel: any = [{ field: 'ordre', sort: 'asc' }];

export const ReglaGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const datagridApiRef = useGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();

    const refresh = () => apiRef.current?.refresh?.();
    const accions = useReglaAccions(refresh);
    const { actions: massiveActions, components: massiveComponents } = useReglaMassiveAccions();

    const columns = getColumns(t);

    return (
        <GridPage autoHeight>
            <CardPage title={t('page.regla.grid.title')}>
                <ReglaFilter onSpringFilterChange={setSpringFilter} />
                <StyledMuiGrid
                    toolbarCreateTitle={t('page.regla.accio.new')}
                    resourceName="reglaResource"
                    apiRef={apiRef}
                    datagridApiRef={datagridApiRef}
                    columns={columns}
                    filter={springFilter}
                    toolbarShowFilterCount
                    paginationActive
                    sortModel={sortModel}
                    popupEditActive
                    popupEditFormContent={<ReglaFormContent />}
                    popupEditFormDialogResourceTitle={t('page.regla.form.resourceTitle')}
                    popupEditFormDialogComponentProps={{ maxWidth: 'lg' }}
                    popupEditFormI18nKeys={{
                        createSuccess: 'page.regla.accio.crearOk',
                        updateSuccess: 'page.regla.accio.modificarOk',
                        deleteSuccess: 'page.regla.accio.esborrarOk',
                    }}
                    rowHideUpdateButton
                    rowHideDeleteButton
                    rowAdditionalActions={accions}
                    toolbarMassiveActions={massiveActions}
                    selectionActive
                />
                {massiveComponents}
            </CardPage>
        </GridPage>
    );
};

export default ReglaGrid;
