import {useBaseAppContext, useMuiContentDialog} from "reactlib";
import Load from "../../../components/Load.tsx";
import Iframe, {useToProgramaAntic} from "../../../components/Iframe.tsx";

const AnnexVisualitzar = (props:any) => {
    const {entity} = props;
    const { getUrl } = useToProgramaAntic();

    const url = getUrl(`/contingut/annex/${entity.id}/arxiu/content`)

    return <Load value={entity && url}>
        <Iframe isPDF src={url}/>
    </Load>
}

const useVisualitzar = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (entity:any) => {
        // event.stopPropagation();
        dialogShow(
            t("page.annex.accio.previsualitzar"),
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