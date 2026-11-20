import {MuiDialog, useBaseAppContext} from "reactlib";
import Load from "../../../components/Load.tsx";
import Iframe, {useToProgramaAntic} from "../../../components/Iframe.tsx";
import {useMemo, useState} from "react";
import {REPORT_DESCARREGAR_IMPRIMIBLE, useDescarregarAnnex} from "../AnnexAccions.tsx";
import { icons } from '../../../util/icons';
import {formatDate} from "../../../util/dateUtils.ts";
import {DetailCard, DetailCardContent} from "../../../components/CardData.tsx";
import {Box, Grid, Icon, Tooltip } from "@mui/material";
import {useTranslation} from "react-i18next";

const AnnexVisualitzar = (props:any) => {
    const {entity} = props;
    const {t} = useTranslation();
    const { getUrl } = useToProgramaAntic();

    const url = useMemo(() => getUrl(`/contingut/annex/${entity.id}/arxiu/content`), [entity])
    const responsables = useMemo(() => entity?.firmes?.flatMap((firma:any) => firma.detalls.map((d:any) => {
        const nom = d.responsableNom != null ? ` - ${d.responsableNom}` : ""
        if (d.responsableNif != null) {
            return `[${d.responsableNif}]` + nom
        }
        if (d.responsableNif == null && firma.autofirma != null) {
            return <Box display={"flex"}>
                [{t("page.annex.detall.gestioDocumental.column.autofirma")}
                <Tooltip title={t("page.annex.detall.gestioDocumental.column.autofirmaTitle")}>
                    <Icon fontSize={"small"} color={"warning"}>error</Icon>
                </Tooltip>]
                {nom}
            </Box>
        }
        if (d.responsableNom != null) {
            return nom
        }
    })).filter((r:any) => r != null), [entity]);

    return <Load value={url}>
        <Grid container>
            <DetailCard cardProps={{ mb: 2 }}>
                <DetailCardContent titleSize={4} textSize={8} title={t("page.annex.detall.camp.dataCaptura")} >{formatDate(entity.dataCaptura)}</DetailCardContent>
                <DetailCardContent titleSize={4} textSize={8} title={t("page.annex.detall.camp.origen")} >{entity.origenCiutadaAdmin}</DetailCardContent>
                <DetailCardContent titleSize={4} textSize={8} title={t("page.annex.detall.camp.observacions")} >{entity.observacions}</DetailCardContent>
                <DetailCardContent titleSize={4} textSize={8} title={t("page.annex.detall.camp.firmants")} >
                    <ul style={{ margin: 0, paddingLeft: '25px' }}>
                        {responsables.map((r:any, index:number) => <li key={index}>{r}</li> )}
                    </ul>
                </DetailCardContent>
            </DetailCard>
            <Grid size={12}>
                <Iframe isPDF src={url}/>
            </Grid>
        </Grid>
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