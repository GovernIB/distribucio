import {Grid} from "@mui/material";
import {useDetailContext, useFormContext, useMuiFormDialogApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import MuiDetail from "../../../../lib/components/mui/detail/MuiDetail.tsx";
import {DetailField, DetailExpandCard} from "../../../components/CardData.tsx";
import Alert from "@mui/material/Alert";
import {formatDate} from "../../../util/dateUtils.ts";

const Detail = ({expanded}:any) => {
    const {data} = useDetailContext()

    return <>
        <DetailExpandCard title={data.numero}
                          cardProps={{backgroundColor: 'greyBackground'}}
                          sx={{backgroundColor: 'customBackground', p: 2}}
                          expanded={expanded}
        >
            <DetailField size={6} name={"registreTipus"} inline/>
            <DetailField size={6} name={"idiomaDescripcio"} inline>{data.idiomaDescripcio}({data.idiomaCodi})</DetailField>
            <DetailField size={6} name={"numero"} inline/>
            <DetailField size={6} name={"numeroOrigen"} inline/>
            <DetailField size={6} name={"data"} inline>{formatDate(data.data)}</DetailField>
            <DetailField size={6} name={"unitatAdministrativaDescripcio"} inline/>
            <DetailField size={6} name={"oficinaDescripcio"} inline>{data.oficinaDescripcio}({data.oficinaCodi})</DetailField>
            <DetailField size={6} name={"assumpteTipusDescripcio"} inline>{data.assumpteTipusDescripcio}({data.assumpteTipusCodi})</DetailField>
            <DetailField size={6} name={"llibreDescripcio"} inline>{data.llibreDescripcio}({data.llibreCodi})</DetailField>
            <DetailField size={6} name={"assumpteDescripcio"} inline>{data.assumpteDescripcio}({data.assumpteCodi})</DetailField>
            <DetailField size={12} name={"extracte"} inline/>
        </DetailExpandCard>
    </>
}

const ClassificarForm = () => {
    const { t } = useTranslation();
    const { data } = useFormContext()

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>

        <Grid size={12}>
            <Alert severity={'warning'}>{t('page.registre.accio.classifica.warning')}</Alert>
        </Grid>

        {data.ids?.map((id:any) => <Grid size={12} key={id}>
            <MuiDetail
                id={id}
                resourceName={'registreResource'}
                hiddenToolbar
                componentProps={{ sx: { mt: 0 } }}
            >
                <Detail expanded={data.ids?.length == 1} />
            </MuiDetail>
        </Grid>)}

        <GridFormField size={12} name="tipus" required/>
        <GridFormField size={12} name="procediment" namedQueries={[`BUSTIA#${data.bustiaId}`]} hidden={data.tipus != "PROCEDIMENT"} required/>
        <GridFormField size={12} name="servei" namedQueries={[`BUSTIA#${data.bustiaId}`]} hidden={data.tipus != "SERVEI"} required/>
    </Grid>
}

const Classificar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"registreResource"}
        title={t('page.registre.accio.classifica.title')}
        action={'CLASSIFICAR'}
        dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
        initOnChange
        {...props}
    >
        <ClassificarForm/>
    </FormActionDialog>
}

const useClassificar = (onSuccess?: (result?: any) => void) => {
    // const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    // const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[], massive?:boolean) :void => {
        apiRef.current?.show?.(undefined, {ids, massive})
    }
    // const onSuccess = () :void => {
    //     refresh?.()
    //     temporalMessageShow(null, t('page.registre.accio.classifica.ok'), 'success');
    // }

    return {
        handleShow,
        content: <Classificar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useClassificar;