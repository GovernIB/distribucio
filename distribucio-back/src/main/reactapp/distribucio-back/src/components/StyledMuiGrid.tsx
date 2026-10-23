import { Box, Button, Icon, IconButton, Theme, Tooltip, Typography } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { FilterCountChip } from './FilterCountChip';
import {MuiDataGrid, MuiDataGridProps, useMuiDataGridApiRef, useResourceApiService} from 'reactlib';
import {useGridApiRef as useMuiDatagridApiRef} from "@mui/x-data-grid-pro";
import { useUserPreferences } from './UserProfile';
import MassiveActionSelector, {MassiveActionProps} from "./MassiveActionSelector.tsx";
import { useEffect, useRef, useState } from "react";
import { createPortal } from 'react-dom';
import { useScreenFrame } from './ScreenFrameContext';
import { titleBarControlsSx } from './titleBarControlsSx';
import {fromSelectionModel, toSelectionModel} from "../util/selectionModelUtils.ts";
import { SCREEN_GRID_MIN_HEIGHT, SCREEN_TITLE_MIN_HEIGHT, SCREEN_TITLE_PADDING_X } from '../util/layout';
import { icons } from '../util/icons';

/**
 * Wrapper propi sobre MuiDataGrid (base-react).
 *
 * Centralitza aquí les props/comportaments per defecte que volem a totes les
 * pantalles del projecte, per no haver de repetir-los a cada graella.
 *
 * Funcionalitats afegides (port parcial de StyledMuiGrid de RIPEA):
 *  - Recompte de filtres aplicats (FilterCountChip): a la barra de títol de la pantalla (ScreenFrame),
 *    al costat del títol, o a la barra d'eines si la graella no és dins un marc.
 *  - Dins un ScreenFrame, la barra d'eines de la llibreria es superposa a la barra de títol (veure l'sx).
 *  - Botó estàndard de barra d'eines (ToolbarButton) reutilitzat per "Refresca".
 *  - Defaults pel formulari popup: botons Guardar/Cancel·lar, mida del diàleg,
 *    no tancar en fer clic fora, i no permetre guardar si hi ha errors de validació.
 *  - Mida de pàgina per defecte treta del perfil de l'usuari (numElementsPagina).
 *  - Files d'alçada variable: el text llarg passa de línia en lloc de quedar tallat.
 *  - Scroll dins la graella (no a la pàgina); les graelles incrustades passen autoHeight.
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
                            sx={{ display: { xs: 'none', sm: 'none', md: 'block' }, whiteSpace: 'nowrap' }}
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

/** Posició que col·loca un element al final de la barra d'eines de la llibreria (veure reactNodePosition). */
const TOOLBAR_END = 100;

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

/**
 * Selector del contenidor de la barra d'eines que la llibreria pinta sobre la graella (DataToolbar),
 * fill de l'embolcall de la llibreria. Es descriu pel que conté (el `.MuiToolbar-root` de MUI) i no
 * per la posició, perquè no depengui de l'ordre dels germans. Únic punt a revisar si la llibreria
 * canvia aquesta estructura: l'usen l'sx i l'efecte de mesura d'aquest component.
 *
 * Només s'aplica a les graelles que no són `autoHeight` (el DOM de la llibreria no hi té l'embolcall).
 */
const LIB_TOOLBAR = 'div:has(> .MuiToolbar-root):not(.MuiDataGrid-root)';

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
        toolbarShowFilterCount: toolbarShowFilterCountProp,
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
        autoHeight = false,
        sx,
        ...others
    } = { ...defaultProps, ...props };

    const {
        currentActions: apiCurrentActions,
    } = useResourceApiService(props.resourceName);
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;

    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    // Dins un ScreenFrame, la barra d'eines de la llibreria es superposa a la barra de títol (CSS de
    // més avall) i el recompte de filtres es pinta al costat del títol. Fora d'un marc tot es manté
    // a la barra d'eines de la llibreria. Les graelles `autoHeight` (diàlegs, pestanyes, detalls) no
    // s'hi enganxen: el context de React travessa els portals dels diàlegs i es mourien les eines de
    // la graella principal de la pantalla.
    const frame = useScreenFrame();
    const infoSlot = !autoHeight ? (frame?.infoSlot ?? null) : null;
    const setToolbarWidth = !autoHeight ? frame?.setToolbarWidth : undefined;
    // El recompte és visible per defecte quan la graella és al marc d'una pantalla.
    const toolbarShowFilterCount = toolbarShowFilterCountProp ?? infoSlot != null;
    const wrapperRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        if (!setToolbarWidth || infoSlot == null) {
            return;
        }
        const wrapper = wrapperRef.current;
        if (!wrapper) {
            return;
        }
        // Separació entre el títol (o el recompte) i la primera eina de la barra.
        const GAP = 12;
        let toolbar: HTMLElement | null = null;
        let sizeObserver: ResizeObserver | undefined;
        // La barra de la llibreria (LIB_TOOLBAR, veure l'sx) no es pinta fins que la graella és a
        // punt: se la cerca a cada canvi del DOM de dins.
        const attach = () => {
            const found = wrapper.querySelector<HTMLElement>(`:scope > div > ${LIB_TOOLBAR}`);
            if (found === toolbar) {
                return;
            }
            sizeObserver?.disconnect();
            toolbar = found;
            if (toolbar) {
                const observed = toolbar;
                sizeObserver = new ResizeObserver(() => setToolbarWidth(Math.ceil(observed.offsetWidth) + GAP));
                sizeObserver.observe(observed);
            } else {
                setToolbarWidth(0);
            }
        };
        const domObserver = new MutationObserver(attach);
        domObserver.observe(wrapper, { childList: true, subtree: true });
        attach();
        return () => {
            domObserver.disconnect();
            sizeObserver?.disconnect();
            setToolbarWidth(0);
        };
    }, [setToolbarWidth, infoSlot]);

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

    // Ordre de la barra d'eines (d'esquerra a dreta): xip de filtres, filtre ràpid (el posa la llibreria),
    // accions massives, accions pròpies de la pantalla (toolbarElementsWithPositions), «Nou/Nova X» i,
    // sempre al final de tot, refresca. Tot va a «TOOLBAR_END» (el final de la barra) en aquest ordre: la
    // llibreria insereix per posició relativa i les pantalles en passaven de diferents: aquestes posicions
    // s'ignoren i es manté l'ordre de l'array de cada pantalla.
    const toolbarElements = [
        {
            position: 0,
            element: <FilterCountChip filter={filter} filterCount={filterCount} sx={{ ml: 1.5 }} />,
            hidden: !toolbarShowFilterCount || infoSlot != null,
        },
        {
            position: TOOLBAR_END,
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
        ...(toolbarElementsWithPositions ?? []).map((e) => ({ ...e, position: TOOLBAR_END })),
        {
            position: TOOLBAR_END,
            element: (
                <ToolbarButton
                    title={t('common.create')}
                    icon={icons.crea}
                    onClick={create}
                    color={'primary'}
                >
                    {toolbarCreateTitle}
                </ToolbarButton>
            ),
            hidden: !isCreateLinkPresent || toolbarHideCreate || others?.readOnly,
        },
        {
            position: TOOLBAR_END,
            element: (
                <IconButton
                    title={t('common.refresh')}
                    onClick={refresh}
                    sx={{ color: (theme: Theme) => theme.palette.primary.contrastText }}
                >
                    <Icon>{icons.refresca}</Icon>
                </IconButton>
            ),
            hidden: toolbarHideRefresh,
        },
    ].filter((e: any) => !e?.hidden);

    return (
        <Box
            ref={wrapperRef}
            sx={{
                display: 'flex',
                flexDirection: 'column',
                width: '100%',
                // Sense autoHeight, dins ScreenFrame (columna flex amb alçada acotada) la graella ocupa
                // l'alçada que sobra, amb un mínim: si no hi cap, és el contingut de la pantalla qui fa
                // scroll i el títol i el peu es mantenen visibles. Cal que la pàgina usi <GridPage> sense
                // autoHeight, que és qui fixa l'alçada disponible (100vh).
                // Amb autoHeight l'embolcall no té alçada pròpia: la graella és un element flex i, amb
                // `height: 100%`, es comprimia fins a l'alçada del pare (p. ex. el panell d'una pestanya)
                // i retallava les files i els panells de detall expandits, sense deixar fer scroll.
                ...(!autoHeight && { flex: '1 1 0', minHeight: SCREEN_GRID_MIN_HEIGHT + 'px' }),
                // El filtre ràpid de la barra d'eines és un TextField "small" de 37px i quedava
                // més alt que la resta de controls de la barra (32px). S'iguala des d'aquí perquè
                // el crea reactlib i no admet ni props ni estils des de fora.
                '& .MuiToolbar-root .MuiOutlinedInput-root': { height: '32px' },
                '& .MuiToolbar-root .MuiOutlinedInput-input': { pt: 0, pb: 0 },
                // Barra d'eines de la llibreria sobre la barra de títol del ScreenFrame, a la dreta
                // i centrada verticalment (DataToolbar, que no es pot moure per props): amb
                // `position: absolute` s'ancora al `.screen-frame` i conserva l'ordre dels seus
                // elements, el filtre ràpid i l'indicador d'error. Es localitza amb LIB_TOOLBAR,
                // el mateix selector que fa servir l'efecte de més amunt.
                ...(infoSlot != null && {
                    [`& > div > ${LIB_TOOLBAR}`]: {
                        position: 'absolute',
                        // `--screen-toolbar-top` el posa el ScreenFrame: 0 si la barra hi cap
                        // al costat del títol, o l'alçada d'una fila si ha de passar a sota.
                        top: 'var(--screen-toolbar-top, 0px)',
                        right: SCREEN_TITLE_PADDING_X + 'px',
                        height: SCREEN_TITLE_MIN_HEIGHT + 'px',
                        display: 'flex',
                        alignItems: 'center',
                        zIndex: 1,
                        '& .MuiToolbar-root': { width: 'auto', minHeight: 0, mt: 0 },
                        // Botons, xips i filtre ràpid sobre la barra de títol
                        ...titleBarControlsSx,
                    },
                }),
            }}
        >
            {toolbarShowFilterCount && infoSlot != null
                ? createPortal(<FilterCountChip filter={filter} filterCount={filterCount} />, infoSlot)
                : null}
            <MuiDataGrid
                // Files multilínia (com a RIPEA): amb l'alçada 'auto' MUI marca la fila com a
                // row--dynamicHeight i el seu CSS deixa passar de línia el text de les cel·les, en
                // lloc de tallar-lo amb el·lipsi. Va abans d'`others` perquè una pantalla concreta
                // el pugui sobreescriure.
                getRowHeight={() => 'auto'}
                {...others}
                // Per defecte la graella fa scroll ella mateixa (capçalera, filtre i paginació
                // queden fixos). Les graelles que no són el contingut principal de la pàgina
                // (diàlegs, pestanyes, detalls) passen autoHeight i creixen amb el contingut.
                autoHeight={autoHeight}
                // Amb l'alçada 'auto' la cel·la queda enganxada al text; l'alçada mínima de la fila
                // (45px) ve del tema. reactlib fa l'spread de l'sx com a objecte: no admet arrays.
                sx={{
                    '& .MuiDataGrid-cell': { paddingTop: '5px', paddingBottom: '5px' },
                    ...sx,
                }}
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
                            icon: icons.desa,
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
