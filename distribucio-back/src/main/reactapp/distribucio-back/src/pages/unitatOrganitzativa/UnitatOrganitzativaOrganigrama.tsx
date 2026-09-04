import Load from "../../components/Load.tsx";
import {useMemo, useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {useDistribucioContext} from "../../components/DistribucioContext.ts";
import * as builder from '../../util/springFilterUtils';
import {TreeView} from "../../components/TreeView.tsx";

const structureUnitats = (pareId:string | null, unitats:any) => {
    return unitats
        ?.filter((u:any) => u?.unitatSuperior?.id == pareId)
        ?.map((u:any) => {
            const children = structureUnitats(u.id, unitats)
            return {
                id: u.codi,
                label: u.codi + " - " + u.denominacio,
                icon: pareId == null ?'home' :'folder',
                children: (children != null && children.length > 0) ?children :undefined
            }
        })
}

export const useUnitatOrganitzativaOrganigrama = () => {
    const { t } = useTranslation();
    const { currentEntitat } = useDistribucioContext();

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('unitatOrganitzativaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [unitats, setUnitats] = useState<any>();

    const organigrama:any = useMemo(() => {
        if (unitats) {
            return structureUnitats(null, unitats)
        }
        return undefined;
    }, [unitats]);

    const handleOpen = () => {
        if(apiIsReady){
            apiFind({filter: builder.eq('estat', `'V'`), unpaged: true, sorts: ['codi,asc']})
                .then((app) => setUnitats(app?.rows))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setUnitats(undefined);
            setOpen(false);
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.unitatOrganitzativa.accio.organigrama.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            // dialogContentProps={{ sx: { height: '100%', px: 2, py: 0 } }}
        >
            <Load value={organigrama}>
                <TreeView
                    defaultExpandedItems={[currentEntitat?.codiDir3]}
                    list={organigrama}/>
            </Load>
        </MuiDialog>
    );

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}