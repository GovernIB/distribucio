import {useBaseAppContext, useMuiContentDialog, useResourceApiService} from "reactlib";
import {DataGridPro} from "@mui/x-data-grid-pro";

const columns = (t:any):any[] => [
    { headerName: t('page.bustia.accio.usersPermis.grid.codi'), field: 'codi', flex: 1 },
    { headerName: t('page.bustia.accio.usersPermis.grid.nom'), field: 'nom', flex: 1 },
    { headerName: t('page.bustia.accio.usersPermis.grid.hasUsuariPermission'), field: 'hasUsuariPermission', flex: 0.5, type: 'boolean' },
    { headerName: t('page.bustia.accio.usersPermis.grid.rols'), field: 'rols', flex: 1, sortable: false },
]

const perspectives = ['USUARIS_PERMIS']
export const useUsuarisAmbPermis = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const {
        getOne: apiGet,
    } = useResourceApiService('bustiaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const handleOpen = async (id:any, row:any) => {
        try {
            const response = await apiGet(id, { perspectives });
            const rows=response?.usuarisPermis || []

            dialogShow(
                t('page.bustia.accio.usersPermis.title', {nom: row.nom}),
                <DataGridPro
                    getRowId={(row) => row.codi}
                    columns={columns(t)}
                    rows={rows}

                    disableColumnMenu
                    autoHeight
                />,
                [],
                { maxWidth: 'xl', fullWidth: true }
            );
        } catch (error: any) {
            temporalMessageShow(null, error?.message, 'error');
        }
    };

    return {
        handleOpen,
        component: dialogComponent
    };
}