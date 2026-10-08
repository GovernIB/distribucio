import {useBaseAppContext, useMuiContentDialog} from "reactlib";
import Load from "../../../components/Load.tsx";
import Iframe, {useToProgramaAntic} from "../../../components/Iframe.tsx";

const AnnexVisualitzar = (props:any) => {
    const {entity} = props;
    const { getUrl } = useToProgramaAntic();

    /// TODO: revisar visualización de documento

    return <Load value={entity}>
        <Iframe isPDF src={getUrl(`/contingut/registre/${entity.registre.id}/annex/${entity.id}/arxiu/content/DOCUMENT`)}/>
    </Load>
}

const useVisualitzar = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (entity:any) => {
        // event.stopPropagation();
        dialogShow(
            t("Previsualitzar"),
            <AnnexVisualitzar entity={entity}/>,
            [],
            { maxWidth: 'md', fullWidth: true }
        );
    };

    return {
        handleOpen,
        component: dialogComponent
    };
}
export default useVisualitzar;