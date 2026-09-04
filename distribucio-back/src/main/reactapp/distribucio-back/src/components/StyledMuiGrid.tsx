import { Box, Button, Icon, Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { FilterCountChip } from './FilterCountChip'; // TODO: corregir ruta d'import segons projecte
import { MuiDataGrid, MuiDataGridProps, useMuiDataGridApiRef } from 'reactlib';
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";
import { useUserPreferences } from './UserProfile';
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import { useState } from "react";
import {fromSelectionModel, toSelectionModel} from "../util/selectionModelUtils.ts";

/**
 * Wrapper propi sobre MuiDataGrid (base-react).
 *
 * Centralitza aquí les props/comportaments per defecte que volem a totes les
 * pantalles del projecte, per no haver de repetir-los a cada graella.
 *
 * Funcionalitats afegides (port parcial de StyledMuiGrid de RIPEA):
 *  - Xip amb el nombre de filtres aplicats a la barra d'eines (FilterCountChip existent).
 *  - Botó estàndard de barra d'eines (ToolbarButton) reutilitzat per "Refrescar".
 *  - Defaults pel formulari popup: botons Guardar/Cancel·lar, mida del diàleg,
 *    no tancar en fer clic fora, i no permetre guardar si hi ha errors de validació.
 *  - Mida de pàgina per defecte treta del perfil de l'usuari (numElementsPagina).
 *
 * Deliberadament NO s'inclou (pendent de decidir més endavant):
 *  - Accions massives (RIPEA #3).
 *  - Refresc lligat a la sessió d'usuari (RIPEA #8).
 *  - Estils per fila via <style> injectat (RIPEA #5).
 *  - Menú contextual amb click dret (RIPEA #14).
 */

/** Botó estàndard per a la barra d'eines: icona + tooltip, amaga el text en pantalles petites. */
export const ToolbarButton = (props: any) => {
    const { title, icon, hidden, children, ...other } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Tooltip title={title}>
            <span>
                <Button
                    variant="outlined"
                    size="small"
                    startIcon={<Icon sx={{ m: 0 }}>{icon}</Icon>}
                    {...other}
                    sx={{ borderRadius: '4px', minWidth: '20px', minHeight: '32px' }}
                >
                    {children && (
                        <Typography
                            variant="body2"
                            sx={{ display: { xs: 'none', sm: 'none', md: 'block' } }}
                            ml={1}
                        >
                            {children}
                        </Typography>
                    )}
                </Button>
            </span>
        </Tooltip>
    );
};

/**
 * Mides de pàgina que es poden triar al peu de les graelles: les mateixes que ofereix el perfil de
 * l'usuari (OpcionsPaginacio, veure UsuariPreferenciesController.opcionsPaginacio) i que la
 * interfície JSP posa al desplegable de les seves taules.
 */
const OPCIONS_PAGINACIO = [10, 20, 50, 100, 250];

/**
 * Mida de pàgina per als usuaris que no en tenen cap de configurada al perfil. És la mateixa que
 * la interfície JSP aplica per defecte a les seves taules (iDisplayLength de dataTable.tag).
 */
const NUM_ELEMENTS_PAGINA_DEFECTE = 10;

type FilterCount = number | ((num: number) => number);

export type StyledMuiGridProps = Omit<MuiDataGridProps, 'toolbarElementsWithPositions' | 'onRowSelectionModelChange'> & {
    /** Nombre de filtres aplicats a mostrar al xip (per defecte es calcula a partir de `filter`). */
    filterCount?: FilterCount;
    /** Mostra el xip amb el nombre de filtres aplicats a la barra d'eines. */
    toolbarShowFilterCount?: boolean;
    /** Text per a la barra d'eines de la pantalla de creació. */
    toolbarCreateTitle?: string;
    /** Elements addicionals de la barra d'eines (es combinen amb el xip de filtres i el botó de refresc). */
    toolbarElementsWithPositions?: MuiDataGridProps['toolbarElementsWithPositions'];
    /** Oculta el camp de quickFilter de la barra d'eines. Per defecte es false. */
    toolbarShowQuickFilter?: boolean;
    /** Event que es llença quan hi ha canvis en les files seleccionades de la graella */
    onRowSelectionModelChange?: (ids:any[], detail:any) => void,
    /** Accions addicionals per les files seleccionades */
    toolbarMassiveActions?: MassiveActionProps[],
    /** Acció a executar en clicar el botó de refresc (a més del refresc intern de la graella). */
    onRefresh?: () => void;
};

// Props que es fixaran per defecte a tot el projecte.
const defaultProps: Partial<StyledMuiGridProps> = {
    // Exemple (comentat, no actiu):
    // paginationActive: true,
    // striped: true,
    title: '',
};

const StyledMuiGrid = (props: StyledMuiGridProps) => {
    const { t } = useTranslation();
    // Mida de pàgina desada al perfil (dis_usuari.num_elements_pagina). Hi és sempre al primer
    // render: DistribucioProvider no pinta cap pantalla fins a tenir el perfil carregat.
    const { numElementsPagina } = useUserPreferences();
    const defApiRef = useMuiDataGridApiRef();
    const dataApiRef = useMuiDatagridApiRef();

    const {
        filter,
        filterCount,
        namedQueries,
        toolbarShowFilterCount = false,
        toolbarCreateTitle,
        toolbarElementsWithPositions,
        toolbarHideRefresh,
        toolbarHideCreate,
        toolbarShowQuickFilter = false,
        toolbarMassiveActions,
        onRefresh,
        apiRef = defApiRef,
        datagridApiRef = dataApiRef,
        defaultPaginationModel,
        pageSizeOptions,
        popupEditFormDialogComponentProps,
        popupEditFormComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormDialogButtons,
        onRowSelectionModelChange,
        ...others
    } = { ...defaultProps, ...props };

    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const refresh = () => {
        onRefresh?.();
        apiRef?.current?.refresh?.();
    };

    const create = () => {
        apiRef?.current?.triggerCreate?.();
    };

    const setGridSelectedRows = (value:any) => {
        datagridApiRef?.current?.setRowSelectionModel?.(toSelectionModel(value))
    }

    // Custom row styling with colored bar
    const getRowClassName = (params: any): string =>
        `row-with-color-${params.row.id} ${params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd'}`;

    // Posició 0: xip de filtres. Posició 3: botó de refresc.
    // Si la pantalla passa els seus propis toolbarElementsWithPositions, es combinen (no se sobreescriuen).
    const toolbarElements = [
        {
            position: 0,
            element: <FilterCountChip filter={filter} filterCount={filterCount} sx={{ ml: 1.5 }} />,
            hidden: !toolbarShowFilterCount,
        },
        {
            position: 3,
            element: <MassiveActionSelector
                resourceName={others?.resourceName}
                selectedRows={selectedRows}
                setSelectedRows={setGridSelectedRows}
                filter={filter}
                namedQueries={namedQueries}
                actions={toolbarMassiveActions ?? []}
                // disabledDefSelector={disabledMassiveDefSelector}
                // hiddenDefSelector={hiddenMassiveDefSelector}
                isRowSelectable={props?.isRowSelectable}
            />,
            hidden: !toolbarMassiveActions || others?.readOnly,
        },
        {
            position: 3,
            element: (
                <ToolbarButton
                    title={t('common.refresh')}
                    icon="refresh"
                    onClick={refresh}
                    color="primary"
                />
            ),
            hidden: toolbarHideRefresh,
        },
        {
            position: 3,
            element: (
                <ToolbarButton
                    title={t('common.create')}
                    icon={'add'}
                    onClick={create}
                    color={'primary'}
                >
                    {toolbarCreateTitle}
                </ToolbarButton>
            ),
            hidden: toolbarHideCreate || others?.readOnly,
        },
        ...(toolbarElementsWithPositions ?? []),
    ].filter((e: any) => !e?.hidden);

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                width: '100%',
                height: '100%',
                // El filtre ràpid de la barra d'eines és un TextField "small" de 37px i quedava
                // més alt que la resta de controls de la barra (32px). S'iguala des d'aquí perquè
                // el crea reactlib i no admet ni props ni estils des de fora.
                '& .MuiToolbar-root .MuiOutlinedInput-root': { height: '32px' },
                '& .MuiToolbar-root .MuiOutlinedInput-input': { pt: 0, pb: 0 },
            }}
        >
            <MuiDataGrid
                {...others}
                filter={filter}
                namedQueries={namedQueries}
                apiRef={apiRef}
                datagridApiRef={datagridApiRef}
                // Fixar la mida de pàgina desactiva l'autoPageSize de la llibreria (que ajusta el
                // nombre de files a l'alçada disponible), que és el que s'aplicaria si no se'n
                // passés cap. Una pantalla concreta encara pot imposar-ne una de pròpia.
                defaultPaginationModel={
                    defaultPaginationModel ?? {
                        page: 0,
                        pageSize: numElementsPagina ?? NUM_ELEMENTS_PAGINA_DEFECTE,
                    }
                }
                pageSizeOptions={pageSizeOptions ?? OPCIONS_PAGINACIO}
                toolbarHideRefresh
                toolbarHideCreate
                toolbarHideQuickFilter={!toolbarShowQuickFilter ? true : undefined}
                getRowClassName={getRowClassName}
                toolbarElementsWithPositions={toolbarElements}
                // Defaults pel formulari popup: cada pantalla pot sobreescriure'ls parcialment,
                // ja que el spread de `others` no inclou aquestes props (es gestionen aquí).
                popupEditFormDialogComponentProps={{
                    fullWidth: true,
                    maxWidth: 'md',
                    ...popupEditFormDialogComponentProps,
                }}
                popupEditFormComponentProps={{
                    avoidSubmitIfAnyValidatorErrors: true,
                    ...popupEditFormComponentProps,
                }}
                popupEditFormDialogOnClose={
                    popupEditFormDialogOnClose ?? ((reason?: string) => reason !== 'backdropClick')
                }
                popupEditFormDialogButtons={
                    popupEditFormDialogButtons ?? [
                        {
                            icon: 'save',
                            text: t('common.save'),
                            componentProps: { variant: 'contained' },
                            value: true,
                        },
                        {
                            text: t('common.cancel'),
                            componentProps: { variant: 'outlined' },
                            value: false,
                        },
                    ]
                }
                onRowSelectionModelChange={(newSelection, details) => {
                    const ids = fromSelectionModel(newSelection);
                    setSelectedRows(ids);
                    onRowSelectionModelChange?.(ids, details);
                }}
            />
        </Box>
    );
};

export default StyledMuiGrid;
