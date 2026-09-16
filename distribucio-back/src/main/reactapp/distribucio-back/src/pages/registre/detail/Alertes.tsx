import {
    useBaseAppContext,
    useConfirmDialogButtons,
    useMuiContentDialog,
    useMuiDataGridApiRef,
    useResourceApiService
} from "reactlib";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from '../../../util/springFilterUtils';
import {useTranslation} from "react-i18next";
import Icon from "@mui/material/Icon";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        patch: apiPatch,
    } = useResourceApiService('alertaResource');
    const {temporalMessageShow, messageDialogShow, t: tLib} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };

    const llegida = (id:any) => {
        if (apiIsReady) {
            messageDialogShow(
                tLib('actionreport.action.confirm.title'),
                t('page.alerta.accio.llegida.confirm.message'),
                confirmDialogButtons,
                confirmDialogComponentProps
            ).then((value: any) => {
                if (value) {
                    apiPatch(id, {data: { llegida: true }})
                        .then(() => {
                            refresh?.()
                            temporalMessageShow(null, t('page.alerta.accio.llegida.ok'), 'success');
                        })
                        .catch((error) => {
                            temporalMessageShow(null, error?.message, 'error');
                        });
                }
            });
        }
    }

    return {
        apiIsReady,
        llegida
    }
}

const columns = [
    { field: 'text', flex: 2,
        renderCell: (params:any) => <>
            {params.formattedValue}
            {params.row.error && <Icon title={params.row.error} color={'error'} >error</Icon>}
        </>
    },
];
const sortModel:any = [{ field: 'createdDate', sort: 'desc' }]
const Alertes = ({id}:any) => {
    const { t } = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef.current?.refresh()
    }

    const { llegida } = useActions(refresh)

    const actions = [
        {
            label: t('page.alerta.accio.llegida.label'),
            icon: 'description',
            showInMenu: false,
            onClick: llegida,
        },
    ]

    return <>
        <StyledMuiGrid
            apiRef={apiRef}
            resourceName={'alertaResource'}
            filter={builder.eq('contingut.id', id)}
            columns={columns}

            rowAdditionalActions={actions}
            fixedSortModel={sortModel}

            toolbarHide
            readOnly
            autoHeight
            paginationActive
        />
    </>
}

export const useAlertes = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (id:any) => {
        // event.stopPropagation();
        dialogShow(
            t('page.alerta.title'),
            <Alertes id={id} />,
            [],
            { maxWidth: 'md', fullWidth: true }
        );
    };

    return {
        handleOpen,
        component: dialogComponent
    };
}