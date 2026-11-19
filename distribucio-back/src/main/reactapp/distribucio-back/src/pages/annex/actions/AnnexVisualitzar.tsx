import {MuiDialog, useBaseAppContext} from "reactlib";
import Load from "../../../components/Load.tsx";
import Iframe, {useToProgramaAntic} from "../../../components/Iframe.tsx";
import {useMemo, useState} from "react";
import {REPORT_DESCARREGAR_IMPRIMIBLE, useDescarregarAnnex} from "../AnnexAccions.tsx";
import { icons } from '../../../util/icons';

const AnnexVisualitzar = (props:any) => {
    const {entity} = props;
    const { getUrl } = useToProgramaAntic();

    const url = getUrl(`/contingut/annex/${entity.id}/arxiu/content`)

    return <Load value={url}>
        <Iframe isPDF src={url}/>
    </Load>
}

const useVisualitzar = () => {
    const { t } = useBaseAppContext();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const descarregar = useDescarregarAnnex();

    const handleOpen = (entity:any) => {
        setEntity(entity);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons = useMemo(() => [
        {
            value: 'download',
            text: t('page.annex.accio.descarregarImprimible'),
            icon: icons.descarrega,
            componentProps: {
                variant: 'outlined',
            }
        },
    ], [t])

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t("page.annex.accio.previsualitzar")}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={(value) => {
                switch (value) {
                    case 'download':
                        descarregar(entity?.id, REPORT_DESCARREGAR_IMPRIMIBLE);
                        break;
                    case 'close': handleClose(); break;
                }
            }}
        >
            <Load value={entity}>
                <AnnexVisualitzar entity={entity}/>
            </Load>
        </MuiDialog>
    );

    return {
        handleOpen,
        handleClose,
        component: dialog
    };
}
export default useVisualitzar;