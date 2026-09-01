import { Button, Icon, Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { FilterCountChip } from './FilterCountChip'; // TODO: corregir ruta d'import segons projecte
import { MuiDataGrid, MuiDataGridProps } from 'reactlib';

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

type FilterCount = number | ((num: number) => number);

export type StyledMuiGridProps = Omit<MuiDataGridProps, 'toolbarElementsWithPositions'> & {
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

    const {
        filter,
        filterCount,
        toolbarShowFilterCount = false,
        toolbarCreateTitle,
        toolbarElementsWithPositions,
        toolbarHideRefresh,
        toolbarHideCreate,
        toolbarShowQuickFilter = false,
        onRefresh,
        apiRef,
        popupEditFormDialogComponentProps,
        popupEditFormComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormDialogButtons,
        ...others
    } = { ...defaultProps, ...props };

    const refresh = () => {
        onRefresh?.();
        apiRef?.current?.refresh?.();
    };

    const create = () => {
        apiRef?.current?.triggerCreate?.();
    };

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
            hidden: toolbarHideCreate, // || !toolbarShowCreate || readOnly,
        },
        ...(toolbarElementsWithPositions ?? []),
    ].filter((e: any) => !e?.hidden);

    return (
        <MuiDataGrid
            {...others}
            filter={filter}
            apiRef={apiRef}
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
        />
    );
};

export default StyledMuiGrid;
