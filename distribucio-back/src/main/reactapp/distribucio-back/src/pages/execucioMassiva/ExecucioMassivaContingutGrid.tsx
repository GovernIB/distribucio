import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import Load from "../../components/Load.tsx";
import * as builder from '../../util/springFilterUtils';
import {useBaseAppContext, useMuiContentDialog, useMuiDataGridApiRef} from "reactlib";
import {Chip, Tooltip} from "@mui/material";

const columns = [
    { field: 'elementNom', flex: 1 },
    { field: 'estat', flex: 1,
        renderCell: (params:any) => <>
            <Tooltip title={params.row.error || params.row.missatge}>
                <Chip label={params.formattedValue} color={params.row.estat == "ERROR" ?'error' :"default"}/>
            </Tooltip>
        </>
    },
    { field: 'dataCreacio', flex: 1 },
    { field: 'dataInici', flex: 1 },
    { field: 'dataFi', flex: 1 },
]
const sortModel:any = [{ field: 'ordre', sort: 'asc' }]

export const ExecucioMassivaContingutGrid = ({id, apiRef}:any) => {
    return (
        <Load value={id}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName="execucioMassivaContingutResource"
                columns={columns}
                filter={builder.eq('execucioMassiva.id', id)}
                fixedSortModel={sortModel}
                disableColumnSorting
                toolbarHide
                autoHeight
                readOnly
            />
        </Load>
    )
}

export const useEMContent = () => {
    const { t } = useBaseAppContext();
    const apiRef = useMuiDataGridApiRef();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (id:any) => {
        // event.stopPropagation();
        dialogShow(
            t('page.massiva.contingut'),
            <ExecucioMassivaContingutGrid apiRef={apiRef} id={id}/>,
            [],
            { maxWidth: 'md', fullWidth: true }
        );
    };

    return {
        apiRef,
        handleOpen,
        component: dialogComponent
    };
}