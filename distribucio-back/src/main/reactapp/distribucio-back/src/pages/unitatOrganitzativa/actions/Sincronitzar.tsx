import html2canvas from "html2canvas";
import jsPDF from "jspdf";
import {useTranslation} from "react-i18next";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useMemo, useRef, useState} from "react";
import Load from "../../../components/Load.tsx";
import {CardData} from "../../../components/CardData.tsx";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";

const descargarPDF = async (element:HTMLElement) => {
    const canvas = await html2canvas(element, {
        scale: 2,
        useCORS: true,
    });

    const imgData = canvas.toDataURL("image/png");

    const pdf = new jsPDF("p", "mm", "a4");

    const pageWidth = pdf.internal.pageSize.getWidth();
    const pageHeight = pdf.internal.pageSize.getHeight();

    const imgWidth = pageWidth;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    let heightLeft = imgHeight;
    let position = 0;

    // Primera página
    pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
    heightLeft -= pageHeight;

    // Páginas siguientes
    while (heightLeft > 0) {
        position = heightLeft - imgHeight;
        pdf.addPage();
        pdf.addImage(imgData, "PNG", 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
    }

    pdf.save("Predicció_de_sincronització.pdf");
};

const Node = (props: any) => {
    const { children, backgroundColor, color, sx: sxProp } = props;
    return (
        <Grid
            sx={{
                backgroundColor: backgroundColor ?? "white",
                color: color ?? "black",
                textAlign: "center",
                borderRadius: 2,
                fontWeight: "bold",
                boxShadow: 2,
                // width: 350,
                flexShrink: 0,
                flexGrow: 0,
                padding: '5px',
                ...(sxProp || {}),
            }}
        >
            {children}
        </Grid>
    );
};

const NodeGrup = (props: any) => {
    const { nodeKey, values, divider } = props;
    return (
        <Grid
            container
            direction="row"
            wrap="nowrap"
            justifyContent={'space-between'}
            alignItems="center"
            columnSpacing={1}
            rowSpacing={1}
            sx={{ width: '100%' }}
        >
            {nodeKey && (
                <Node
                    backgroundColor={'lightgreen'}
                    color="text.primary"
                >
                    <Typography
                        sx={{ fontSize: '1rem', padding: '5px' }}
                    >{`${nodeKey?.codi} - ${nodeKey.denominacio}`}</Typography>
                </Node>
            )}
            {(divider || (nodeKey && values)) && (
                <Grid sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Divider orientation="horizontal" flexItem sx={{ borderColor: 'text.primary', borderWidth: 2, width: 50 }} />
                </Grid>
            )}
            <Grid container direction="column" spacing={1}>
                {values?.map((value: any) => (
                    <Node
                        backgroundColor={'orange'}
                        color="text.primary"
                    >
                        <Typography
                            sx={{fontSize: '1rem', padding: '5px'}}
                        >{`${value?.codi} - ${value.denominacio}`}</Typography>
                    </Node>
                ))}
            </Grid>
        </Grid>
    );
};

export const useSincronitzar = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('unitatOrganitzativaResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [info, setInfo] = useState<any>();
    const ref = useRef<HTMLDivElement | null>(null);

    const handleOpen = () => {
        if(apiIsReady){
            apiAction(undefined, {code: "SYNCHRONIZE_INFO"})
                .then((app) => setInfo(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
        }
        setOpen(true);
    }

    const sync = (value:boolean) => {
        apiAction(undefined, {code: "SYNCHRONIZE", data: value})
            .then(() => temporalMessageShow(null, t('page.unitatOrganitzativa.accio.sincronitzar.ok'), 'success'))
            .catch((error) => {
                handleClose()
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setInfo(undefined);
            setOpen(false);
        }
    };

    const buttons = useMemo(() => [
        {
            value: 'download',
            text: t('page.unitatOrganitzativa.accio.descarregarPdf.label'),
            icon: 'download',
            componentProps: {
                variant: 'outlined',
                disabled: !info,
            }
        },
        {
            value: 'sinc',
            text: t('page.unitatOrganitzativa.accio.sincronitzar.sincronitzar'),
            icon: 'save',
            componentProps: {
                variant: 'contained',
                color: 'success',
                disabled: !info || info?.isAllEmpty && !info?.isFirstSincronization,
            }
        },
        {
            value: 'forzar',
            text: t('page.unitatOrganitzativa.accio.sincronitzar.forzar'),
            icon: 'save',
            hidden: !(info?.isAllEmpty && !info?.isFirstSincronization),
            componentProps: {
                variant: 'contained',
                color: 'error',
                disabled: !info,
            }
        },
        {
            value: 'close',
            text: t('common.cancel'),
            icon: 'close',
            componentProps: {
                variant: 'outlined',
            }
        },
    ].filter((b) => !b.hidden), [t, info?.isAllEmpty])

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.unitatOrganitzativa.accio.sincronitzar.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={(value) => {
                switch (value) {
                    case 'download':
                        if (ref?.current)
                            descargarPDF(ref?.current);
                        break;
                    case 'sinc': sync(false); break;
                    case 'forzar': sync(true);  break;
                }
                handleClose();
            }}
        >
            <Load value={info}>
            <Grid component={"div"} ref={ref} container direction="row" columnSpacing={1} rowSpacing={1}>
                {info?.isFirstSincronization && <>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.first')}>
                        {!info?.unitatsVigentsFirstSincro && <>{t('page.unitatOrganitzativa.accio.sincronitzar.info.empty.unitat')}</>}
                        {info?.unitatsVigentsFirstSincro && <>{info?.unitatsVigentsFirstSincro?.map((unitat:any) => <NodeGrup nodeKey={unitat}/>)}</>}
                    </CardData>
                </>}
                {!info?.isFirstSincronization && <>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.empty.title')} hidden={!info?.isAllEmpty}>
                        {t('page.unitatOrganitzativa.accio.sincronitzar.info.empty.label')}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.divisions')} hidden={info?.splitMap?.length <= 0}>
                        {info?.splitMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.fusions')} hidden={info?.mergeMap?.length <= 0}>
                        {info?.mergeMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.substitucio')} hidden={info?.substMap?.length <= 0}>
                        {info?.substMap?.map?.((map:any) => <NodeGrup nodeKey={map?.key} values={map?.values}/>)}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.canvi')} rowSpacing={2} hidden={info?.unitatsVigents?.length <= 0}>
                        {info?.unitatsVigents?.map?.((unitat:any) => <>
                            <Grid container direction="row" wrap="nowrap" justifyContent={"space-between"} alignItems="center" columnSpacing={1} rowSpacing={1} sx={{width: "100%"}}>
                                <Node backgroundColor={'orange'} color="text.primary" >
                                    <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${unitat?.codi} - ${unitat.denominacio}`}</Typography>
                                </Node>
                            </Grid>
                        </>)}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.reglesAfectades')} hidden={info?.rulesFiltre?.length <= 0 && info?.rulesDesti?.length <= 0}>
                        {info?.rulesFiltre?.map?.((regla:any) => <>
                            <Node backgroundColor={'orange'} color="text.primary">
                                <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${regla.nom}  (${regla.unitatOrganitzativaFiltre.codi} - ${regla.unitatOrganitzativaFiltre.denominacio})`}</Typography>
                            </Node>
                        </>)}
                        {info?.rulesDesti?.map?.((regla:any) => <>
                            <Node backgroundColor={'orange'} color="text.primary">
                                <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${regla.nom}  (${regla.unitatDesti.codi} - ${regla.unitatDesti.denominacio})`}</Typography>
                            </Node>
                        </>)}
                    </CardData>
                    <CardData title={t('page.unitatOrganitzativa.accio.sincronitzar.info.noves')} hidden={info?.unitatsNew?.length <= 0}>
                        <Grid container direction="row"  justifyContent={"space-between"} alignItems="center" columnSpacing={1} rowSpacing={1} sx={{width: "100%"}}>
                            {info?.unitatsNew?.map?.((unitat:any) =>
                                <Node backgroundColor={'lightgreen'} color="text.primary">
                                    <Typography sx={{fontSize: '1rem', padding: '5px'}}>{`${unitat?.codi} - ${unitat.denominacio}`}</Typography>
                                </Node>
                            )}
                        </Grid>
                    </CardData>
                </>}
            </Grid>
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