import {useBaseAppContext, useMuiContentDialog} from "reactlib";
import Alert from "@mui/material/Alert";
import {DetailExpandCard} from "../../../components/CardData.tsx";
import {useActions} from "./BustiaActions.tsx";
import {useEffect} from "react";
import Grid from "@mui/material/Grid";
import Typography from "@mui/material/Typography";
import {useSession} from "../../../components/SessionStorageContext.tsx";

export const BustiaObsoleta = (props:any) => {
    const { t } = useBaseAppContext();
    const {id, title = t('component.BustiaObsoleta.title')} = props

    const { value: map, save: setMap } =useSession(`transicioInfo_${id}`)

    const { apiIsReady, transicioInfo } = useActions();

    useEffect(() => {
        if (apiIsReady && !map) {
            transicioInfo(id)
                .then((response:any) => setMap(response.blob))
        }
    }, [apiIsReady]);

    return (<>
        <DetailExpandCard
            header={
                <Alert severity={'error'} sx={{ width: '100%' }}>{title}</Alert>
            }
            headerProps={{ p: 0 }}
            sx={{ p: 1 }}
            expanded
        >
            <Grid container width={'100%'}>
                {map?.newUnitats.length > 0 && <>
                    <Grid size={4} textAlign={'center'}>
                        <Typography variant={'h6'} mt={2}>{t('component.BustiaObsoleta.newUnitats')}</Typography>
                    </Grid>
                    <Grid size={8}>
                        <ul>
                            {map?.newUnitats?.map((u:any) => <li>{u}</li>)}
                        </ul>
                    </Grid>
                </>}


                {map?.afectedBusties.length > 0 && <>
                    <Grid size={4} textAlign={'center'}>
                        <Typography variant={'h6'} mt={2}>{t('component.BustiaObsoleta.afectedBusties')}</Typography>
                    </Grid>
                    <Grid size={8}>
                        <ul>
                            {map?.afectedBusties?.map((u:any) => <li>{u}</li>)}
                        </ul>
                    </Grid>
                </>}
            </Grid>
        </DetailExpandCard>
    </>)
}

export const useBustiaObsoleta = () => {
    const { t } = useBaseAppContext();
    const [dialogShow, dialogComponent] = useMuiContentDialog();

    const handleOpen = (id:any, row:any) => {
        dialogShow(
            t('component.BustiaObsoleta.title'),
            <BustiaObsoleta id={id} title={t('component.BustiaObsoleta.alert',
                { unitat: row.unitatOrganitzativa?.description, bustia: row.nom })}/>,
            [],
            { maxWidth: 'md', fullWidth: true }
        );
    };

    return {
        handleOpen,
        component: dialogComponent
    };
}