import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useMuiContentDialog, useMuiDataGridApiRef} from "reactlib";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../util/icons';
import StyledMuiGrid, {ToolbarButton} from "../../components/StyledMuiGrid.tsx";
import {Box, Chip, LinearProgress} from "@mui/material";
import {useEMContent} from "./ExecucioMassivaContingutGrid.tsx";
import ExecucioMassivaFilter from "./ExecucioMassivaFiltre.tsx";
import React, {useEffect, useMemo, useRef, useState} from "react";
import {useExecucioMassivaActions} from "./detail/ExecucioMassivaActions.tsx";

const StyledLinearProgress = (props: any) => {
    const {sx, textColor = "white", children, ...other} = props

    return <Box position="relative" width="100%" sx={{ml: 1}}>
        <LinearProgress variant="determinate" {...other} sx={{width: '100%', borderRadius: '4px', ...sx}}/>
        <Box
            color={textColor}
            position="absolute"
            top={0}
            left={0}
            width="100%"
            height="100%"
            display="flex"
            alignItems="center"
            justifyContent="center"
        >
            {children}
        </Box>
    </Box>
}

const useToogleInterval = (time:number, fn: () => void) => {
    const [isRunning, setIsRunning] = useState(false);
    const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

    useEffect(() => {
        if (isRunning) {
            intervalRef.current = setInterval(() => {
                fn()
            }, time);
        } else {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
                intervalRef.current = null;
            }
        }

        return () => {
            if (intervalRef.current) {
                clearInterval(intervalRef.current);
            }
        };
    }, [isRunning]);

    const handleToggle = () => {
        setIsRunning((prev) => !prev);
    };

    return {
        isRunning,
        handleToggle
    }
}

const columns = [
    { field: 'tipus', flex: 2 },
    { field: 'exec', flex: 1.5,
        renderCell: (params:any) => (
            <>
                <Chip label={params.formattedValue} color={params.row.estat == "CANCELADA" ?"warning" :"default"} />
                <StyledLinearProgress
                    color={'success'}
                    value={params.row.percExec}
                    sx={{height: '20px'}}
                >
                    {params.row.percExec}%
                </StyledLinearProgress>
            </>
        )
    },
    { field: 'errors', flex: 0.5,
        renderCell: (params:any) => (
            <Chip label={params.formattedValue} color={params.formattedValue > 0 ?"error" :"default"} />
        ),
    },
    { field: 'dataInici', flex: 1 },
    { field: 'dataFi', flex: 1 },
    { field: 'createdByFullName', flex: 1 },
]
const sortModel:any = [{ field: 'createdDate', sort: 'desc' }]

// `autoHeight`: la graella creix amb el contingut (diàleg d'execucions massives). A la pantalla pròpia
// s'hi passa `false` perquè ocupi l'alçada de ScreenFrame i la seva barra d'eines vagi a la barra de títol.
const EMGrid = ({ autoHeight = true }: { autoHeight?: boolean }) => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();
    const [springFilter, setSpringFilter] = React.useState<string>();
    const {apiRef: apiRefEMC, handleOpen, component} = useEMContent()

    const refresh = () => {
        apiRef.current?.refresh()
        apiRefEMC.current?.refresh()
    }

    const {actions, components} = useExecucioMassivaActions(refresh)

    const additionalActions = useMemo(() => [
        {
            label: t('page.massiva.accio.detail.label'),
            icon: icons.detall,
            showInMenu: true,
            onClick: handleOpen,
        },
        ...actions,
    ], [t, actions])

    const intervalTime = 5;
    const {isRunning, handleToggle} = useToogleInterval(intervalTime * 1000, refresh)

    return (
        <>
            <ExecucioMassivaFilter onSpringFilterChange={setSpringFilter} />
            {isRunning}
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName="execucioMassivaResource"
                columns={columns}
                filter={springFilter}
                fixedSortModel={sortModel}
                disableColumnSorting
                toolbarShowFilterCount
                rowAdditionalActions={additionalActions}
                toolbarHideRefresh
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: (
                            <ToolbarButton
                                // title={t('page.massiva.refresh', {segons: intervalTime})}
                                icon={isRunning ? icons.opcioMarcada : icons.opcioDesmarcada}
                                aria-pressed={isRunning}
                                onClick={handleToggle}
                            >
                                {t('page.massiva.refresh', { segons: intervalTime })}
                            </ToolbarButton>
                        ),
                    },
                ]}
                paginationActive
                autoHeight={autoHeight}
                readOnly
            />
            {component}
            {components}
        </>
    );
}

export const ExecucioMassivaGrid = () => {
    const { t } = useTranslation();

    return (<>
         <GridPage>
             <ScreenFrame icon={icons.massiva} title={t('page.massiva.title')}>
                <EMGrid autoHeight={false}/>
             </ScreenFrame>
         </GridPage>
    </>)
}

export const useExecucioMassivaGrid = () => {
    const { t } = useBaseAppContext();
    const apiRef = useMuiDataGridApiRef();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = () => {
        dialogShow(
            t('page.massiva.title'),
            <EMGrid/>,
            [],
            { maxWidth: 'xl', fullWidth: true }
        );
    };

    return {
        apiRef,
        handleOpen,
        component: dialogComponent
    };
}