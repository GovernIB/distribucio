import React from 'react';
import { useTranslation } from 'react-i18next';
import Badge from '@mui/material/Badge';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { GridPage, MuiDataGrid, MuiDataGridColDef, useMuiDataGridApiRef } from 'reactlib';
import EntitatFilter from './EntitatFilter';
import EntitatFormContent from './EntitatFormContent';
import { useEntitatAccions } from './EntitatAccions';
import { useEntitatPermisosDialog } from './EntitatPermisos';
import FilterCountChip from '../../components/FilterCountChip';

/** Perspectiva d'EntitatResource que omple el comptador de permisos de cada fila. */
const PERSPECTIVA_PERMISOS_COUNT = ['PERMISOS_COUNT'];

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'descripcio', flex: 3 },
    { field: 'cif', flex: 1 },
    { field: 'codiDir3', flex: 1 },
    { field: 'activa', flex: 0.6, type: 'boolean' },
];

export const EntitatGrid: React.FC = () => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    // El MuiFilter ja empeny el filtre al DataGridContext pare, però es manté l'estat explícit
    // (com fa RIPEA) perquè la graella el rebi per la prop `filter` i el xip pugui comptar-ne
    // els criteris aplicats.
    const [springFilter, setSpringFilter] = React.useState<string>();
    // Refresca el llistat sense recarregar la pàgina. La creació, la modificació i l'esborrat el
    // fan pel seu compte (MuiDataCommon), per això només cal passar-lo a les accions pròpies.
    const refresh = () => apiRef.current?.refresh?.();
    const accions = useEntitatAccions(refresh);
    // En tancar el diàleg es refresca el llistat perquè el comptador de permisos de la fila
    // reculli les altes i les baixes que s'hi hagin fet.
    const { handleShow: mostrarPermisos, dialog: permisosDialog } = useEntitatPermisosDialog(refresh);
    const columnsWithLabels = React.useMemo(
        () => [
            ...columns.map((column) => ({
                ...column,
                headerName: t(`page.entitats.grid.column.${column.field}`),
            })),
            // Accés als permisos de l'entitat: el mateix botó de clau amb el nombre de permisos
            // que la interfície JSP posa a cada fila del llistat (entitatList.jsp), però obrint
            // una modal en comptes de canviar de pantalla.
            {
                // El camp és el comptador que omple la perspectiva; el botó, en canvi, obre el
                // llistat complet de permisos.
                field: 'permisosCount',
                headerName: t('page.entitats.grid.column.permisos'),
                sortable: false,
                filterable: false,
                width: 100,
                align: 'center' as const,
                headerAlign: 'center' as const,
                renderCell: (params: any) => (
                    <IconButton
                        title={t('page.entitats.permis.accio.gestionar')}
                        onClick={(event) => {
                            event.stopPropagation();
                            mostrarPermisos(params.row?.id, params.row?.nom);
                        }}
                    >
                        <Badge badgeContent={params.row?.permisosCount ?? 0} color="primary" showZero>
                            <Icon>key</Icon>
                        </Badge>
                    </IconButton>
                ),
            },
        ],
        // eslint-disable-next-line react-hooks/exhaustive-deps
        [t]
    );
    // Posició 1: just després del títol de la barra d'eines (la posició 0 el precediria).
    const toolbarElements = React.useMemo(
        () => [
            {
                position: 1,
                element: <FilterCountChip filter={springFilter} sx={{ ml: 1.5 }} />,
            },
        ],
        [springFilter]
    );
    return (
        <GridPage>
            <EntitatFilter onSpringFilterChange={setSpringFilter} />
            <MuiDataGrid
                title={t('page.entitats.grid.title')}
                resourceName="entitatResource"
                apiRef={apiRef}
                columns={columnsWithLabels}
                filter={springFilter}
                perspectives={PERSPECTIVA_PERMISOS_COUNT}
                toolbarElementsWithPositions={toolbarElements}
                paginationActive
                toolbarType="upper"
                // Creació i modificació en finestra emergent, com a RIPEA: el botó de crear de la
                // barra d'eines i l'acció "Modifica" obren el mateix formulari dins un diàleg.
                popupEditActive
                popupEditFormContent={<EntitatFormContent />}
                popupEditFormDialogResourceTitle={t('page.entitats.form.resourceTitle')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.entitats.accio.crearOk',
                    updateSuccess: 'page.entitats.accio.modificarOk',
                    deleteSuccess: 'page.entitats.accio.esborrarOk',
                }}
                // Les accions de la fila són només les del menú (veure useEntitatAccions): s'amaguen
                // les que la graella hi posa pel seu compte per no duplicar modificar i esborrar.
                rowHideUpdateButton
                rowHideDeleteButton
                rowAdditionalActions={accions}
            />
            {permisosDialog}
        </GridPage>
    );
};

export default EntitatGrid;
