import {useBaseAppContext} from "reactlib";
import {Box, Grid} from "@mui/material";
import {DetailCard, DetailField} from "../../../../components/CardData.tsx";
import {ToolbarButton} from "../../../../components/StyledMuiGrid.tsx";
import {ErrorArea} from "../../../../components/ErrorArea.tsx";
import {ROLE_ADMIN, useDistribucioContext} from "../../../../components/DistribucioContext.ts";
import {useActions} from "../RegistreActions.tsx";
import Alert from "@mui/material/Alert";
import { icons } from '../../../../util/icons';

export const ProcessAuto = ({entity, refresh}:any) => {
    const { t } = useBaseAppContext();
    const {currentRole} = useDistribucioContext()
    const { tornarProcessar, reenviarBackoffice } = useActions(refresh)

    const isAdmin = currentRole == ROLE_ADMIN;

    const reenviar = <>
        {entity.potModificar && (entity.procesEstat == 'BACK_PENDENT') && <>
            <Grid size={12} sx={{ textAlign: 'end' }}>
                <ToolbarButton icon={icons.refresca} onClick={() => reenviarBackoffice(entity.id)} >
                    {t('page.registre.accio.reenviarBackoffice.label')}</ToolbarButton>
            </Grid>
        </>}
    </>

    return <Grid container columnSpacing={1} rowSpacing={1}>

        {entity.procesError != null && <Grid size={12}>
            <Alert severity={'error'}
                   action={<>
                       {isAdmin && (entity.procesEstat == 'ARXIU_PENDENT' || entity.procesEstat == 'REGLA_PENDENT' || entity.procesEstat == 'BUSTIA_PROCESSADA') && <>
                           <Box sx={{ textAlign: 'end' }}>
                               <ToolbarButton icon={icons.refresca} onClick={() => tornarProcessar(entity.id)} >
                                   {t('page.registre.accio.tornarProcessar.label')}</ToolbarButton>
                           </Box>
                       </>}
                       {reenviar}
                   </>}
            >{t('component.RegistreDetail.error')}</Alert>
        </Grid>}

        {entity.procesError == null && reenviar}

        <DetailCard>
            <DetailField name={"procesEstat"} inline>{entity.procesEstat}</DetailField>
            {entity.procesEstat == 'REGLA_PENDENT' && <DetailField name={"regla"} inline hidden={!entity.regla?.id}/>}
            <DetailField name={"backCodi"} inline hidden={!entity.backCodi}/>
            <DetailField name={"procesData"} inline hidden={!entity.procesData}/>
            {entity.procesEstat == 'BACK_PENDENT' && <DetailField name={"backRetryEnviarData"} inline hidden={!entity.backRetryEnviarData}/>}
            <DetailField name={"procesIntents"} inline/>
        </DetailCard>

        {entity.procesError && <Grid size={12}>
            <ErrorArea sx={{backgroundColor: 'customBackground', fontSize: 12}}>{entity.procesError}</ErrorArea>
        </Grid>}
    </Grid>
}
