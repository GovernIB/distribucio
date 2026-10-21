import {
    useBaseAppContext,
    MuiDetail,
    MuiDialog,
    useMuiContentDialog, useDetailApiRef
} from "reactlib";
import {alpha, Box, CircularProgress, Icon} from "@mui/material";
import {useMemo, useRef, useState} from "react";
import Load from "../../../components/Load.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import {useRecordNavigation} from "../../../components/RecordNavigation.tsx";
import { icons } from '../../../util/icons';
import RegistreDetail from "./RegistreDetail.tsx";

const perspectives = ['ARXIU_DETALL', 'DARRER_MOVIMENT', 'COMMENT_NUM', 'DETAIL_INFO']
export const useBasicDetail = (props:any = {}) => {
    const { t } = useBaseAppContext();
    const { perspectives: customPersp = [] } = props
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const additionalPersp = useMemo(() => [
        ...perspectives,
        ...customPersp,
    ], [customPersp.join(',')])

    const handleOpen = (id:any, _row:any) => {
        dialogShow(
            t('page.contingut.accio.detalls.title'),
            <MuiDetail
                id={id}
                resourceName={'registreResource'}
                perspectives={additionalPersp}
                hiddenToolbar
                componentProps={{ sx: { mt: 0 } }}
            >
                <RegistreDetail/>
            </MuiDetail>,
            [{
                value: 'close',
                text: t('common.close'),
                icon: icons.cancela,
                componentProps: { variant: 'outlined' },
            }],
            { maxWidth: 'xl', fullWidth: true }
        );
    };

    return {
        handleOpen,
        dialog: dialogComponent
    };
}

type RegistreDetailOptions = {
    /** Api del grid des del qual es navega. */
    gridApiRef: any
    /** Filtre (Spring filter) aplicat al grid. */
    filter?: any
    /** Named queries aplicades al grid. */
    namedQueries?: any[]
    /** Recurs del grid des del qual es navega (per defecte, el propi registre). */
    resourceName?: string
    /** Id de l'anotació a mostrar a partir de la fila del grid (per defecte, l'id de la fila). */
    getDetailId?: (row: any) => any
    /** S'invoca en tancar el diàleg (p. ex. per refrescar el grid). */
    onClose?: () => void
    /** Perspectives addicionals a les per defecte en carregar l'anotació. */
    perspectives?: string[]
}

export const useRegistreDetail = (options:RegistreDetailOptions) => {
    const { t, temporalMessageShow } = useBaseAppContext();
    const { gridApiRef, filter, namedQueries, resourceName = 'registreResource', getDetailId, onClose, perspectives: customPersp } = options
    const detailPerspectives = useMemo(
        () => [...perspectives, ...(customPersp ?? [])],
        [customPersp?.join(',')]
    )

    const apiDetialRef = useDetailApiRef()

    const [open, setOpen] = useState(false);
    const [entityId, setEntityId] = useState<any>(); // id de la fila del grid (navegació); 
    const [detailId, setDetailId] = useState<any>(); // id de l'anotació mostrada
    // Càrrega d'una nova anotació en navegar (anterior/següent): es mostra un indicador sobre el detall
    const [navigating, setNavigating] = useState(false);
    // setRow el captura el useCallback de useRecordNavigation: cal un ref per llegir el valor actual
    const detailIdRef = useRef<any>(undefined);

    const setRow = (rowId:any, row?:any) => {
        const newDetailId = rowId === undefined ? undefined : (getDetailId && row ? getDetailId(row) : rowId)
        // Si l'anotació no canvia (p. ex. dos annexos seguits del mateix registre) MuiDetail no la
        // tornarà a carregar i mai avisaria que ha acabat
        if (newDetailId === undefined || newDetailId === detailIdRef.current) {
            setNavigating(false)
        }
        detailIdRef.current = newDetailId
        setEntityId(rowId)
        setDetailId(newDetailId)
    }

    const { apiIsReady, index, totalElem, prev, next, reset } = useRecordNavigation({
        id: entityId, setId: setRow, gridApiRef, filter, namedQueries, resourceName
    })

    const navigate = (move: () => Promise<boolean>) => {
        if (!apiIsReady || navigating) return
        setNavigating(true)
        move()
            .then((moved) => { if (!moved) setNavigating(false) })
            .catch(() => {
                setNavigating(false)
                temporalMessageShow(null, t('component.RecordNavigation.error'), 'error')
            })
    }

    const { value: avanzarPagina } = useSession('avanzarPagina');
    const refresh = (code?:string) => {
        if (code == 'REENVIAR' && avanzarPagina && index < totalElem) {
            navigate(next)
        } else {
            apiDetialRef.current?.refresh()
        }
    }

    const handleOpen = (id:any, row?:any) => {
        setRow(id, row)
        setOpen(true)
    };

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntityId(undefined);
            setDetailId(undefined);
            detailIdRef.current = undefined;
            setNavigating(false);
            reset();
            setOpen(false);
            onClose?.();
        }
    };

    const buttons:any = useMemo(() => [
        {
            value: 'prev',
            text: t('component.RecordNavigation.prev'),
            icon: icons.anterior,
            componentProps: { variant: 'outlined', disabled: index == 1 || navigating },
        },
        {
            text: `${index} / ${totalElem}`,
            componentProps: {
                disabled: true,
                sx: {
                    '&.Mui-disabled': {
                        color: 'primary.main',
                        opacity: 1,
                        cursor: 'not-allowed',
                    }
                }
            },
        },
        {
            value: 'next',
            text: <>{t('component.RecordNavigation.next')}<Icon sx={{ ml: 1 }}>{icons.seguent}</Icon></>,
            componentProps: { variant: 'outlined', sx: { mr: 'auto' }, disabled: index == totalElem || navigating },
        },
        {
            value: 'close',
            text: t('common.close'),
            icon: icons.cancela,
            componentProps: { variant: 'outlined' },
        },
    ], [t, apiIsReady, index, totalElem, navigating])

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.accio.detalls.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={buttons}
            buttonCallback={(value:string) => {
                switch (value) {
                    case 'prev':navigate(prev);break;
                    case 'next':navigate(next);break;
                    case 'close':handleClose();break;
                }
            }}
        >
            <Load value={detailId}>
                <Box sx={{ position: 'relative' }}>
                    <MuiDetail
                        apiRef={apiDetialRef}
                        id={detailId}
                        resourceName={'registreResource'}
                        perspectives={detailPerspectives}
                        hiddenToolbar
                        componentProps={{ sx: { mt: 0 } }}
                    >
                        <RegistreDetail refresh={refresh} onLoaded={() => setNavigating(false)}/>
                    </MuiDetail>
                    {navigating &&
                        <Box sx={{
                            position: 'absolute', inset: 0, zIndex: 10,
                            display: 'flex', justifyContent: 'center', alignItems: 'flex-start',
                            bgcolor: (theme) => alpha(theme.palette.background.paper, 0.7),
                        }}>
                            <CircularProgress sx={{ position: 'sticky', top: '30vh' }}/>
                        </Box>}
                </Box>
            </Load>
        </MuiDialog>
    )

    return {
        handleOpen,
        handleClose,
        dialog
    }
}