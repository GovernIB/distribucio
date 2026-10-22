import {useBaseAppContext} from "reactlib";
import {Box, Grid, Icon, IconButton, Typography} from "@mui/material";
import {DetailCard, DetailExpandCard, DetailField} from "../../../../components/CardData.tsx";
import {ROLE_ADMIN, ROLE_ADMIN_LECTURA, useDistribucioContext} from "../../../../components/DistribucioContext.ts";
import {useActions} from "../RegistreActions.tsx";
import {InteressatsGrid} from "./InteressatsTab.tsx";
import {AnnexGrid} from "./AnnexosTab.tsx";
import { icons } from '../../../../util/icons';

export const Resum = ({entity}:any) => {
    const { t } = useBaseAppContext();
    const {currentRole} = useDistribucioContext()
    const isAdmin = currentRole == ROLE_ADMIN || currentRole == ROLE_ADMIN_LECTURA
    const senseCodi = ['ARXIU_PENDENT', 'REGLA_PENDENT', 'BUSTIA_PENDENT', 'BUSTIA_PROCESSADA'].includes(entity.procesEstat);

    const {justificant} = useActions()

    return (<Grid container columnSpacing={1} rowSpacing={1}>
        <DetailCard>
            <DetailField size={6} name={isAdmin ?"identificador" :"numero"}
                         formatterValue={(v:any) =>
                             <Box display={'flex'} alignItems={'center'} justifyContent={'space-between'}>
                                 <Typography variant="inherit" color="text.primary">
                                     {v}
                                 </Typography>
                                 <IconButton
                                     title={t('page.registre.accio.justificant.label')}
                                     onClick={() => justificant(entity.id)}
                                     sx={{ m:0, p:0 }}>
                                     <Icon fontSize={'small'}>{icons.descarrega}</Icon>
                                 </IconButton>
                             </Box>
                         } isObject inline/>
            <DetailField size={6} name={"data"} inline/>
            <DetailField size={6} name={"oficinaDescripcio"} inline>{entity.oficinaDescripcio} ({entity.oficinaCodi})</DetailField>
            <DetailField size={6} name={"presencial"} inline>{t(`common.boolean.${entity.presencial}`)}</DetailField>
            <DetailField size={12} name={"extracte"} inline/>
            <DetailField size={6} name={"procediment"} isObject inline hidden={entity.serveiCodi}>
                <Box display={'flex'}>
                    <Typography variant="inherit" color="text.primary">
                        {entity.procediment?.description}
                    </Typography>
                    {entity.siaExtingit && <Icon title={t('page.procediments.extingit')} fontSize={'small'} color={'warning'}>{icons.advertencia}</Icon>}
                </Box>
            </DetailField>
            <DetailField size={6} name={"servei"} isObject inline hidden={!entity.serveiCodi}>
                <Box display={'flex'} >
                    <Typography variant="inherit" color="text.primary">
                        {entity.servei?.description}
                    </Typography>
                    {entity.siaExtingit && <Icon title={t('page.serveis.extingit')} fontSize={'small'} color={'warning'}>{icons.advertencia}</Icon>}
                </Box>
            </DetailField>
            <DetailField size={6} name={"expedientNumero"} inline/>
            <DetailField size={12} name={"tramitNom"} inline>{entity.tramitCodi} - {entity.tramitNom}</DetailField>
            <DetailField size={12} name={"observacions"} inline/>
            <DetailField size={12} name={"regla"} inline hidden={entity.procesEstat != 'REGLA_PENDENT' || !entity.regla}/>
            <DetailField size={12} name={"backCodi"} inline hidden={senseCodi}/>
            <DetailField size={4} name={"numeroOrigen"} inline/>
            <DetailField size={4} name={"dataOrigen"} inline/>
            <DetailField size={4} name={"oficinaOrigenDescripcio"} inline>{entity.oficinaOrigenDescripcio} ({entity.oficinaOrigenCodi})</DetailField>
        </DetailCard>

        <DetailExpandCard title={t('component.RegistreDetail.tabs.interessats')} variant={'body1'} headerProps={{backgroundColor: 'greyBackground'}}>
            <InteressatsGrid id={entity.id}/>
        </DetailExpandCard>

        <DetailExpandCard title={t('component.RegistreDetail.tabs.annexos')} variant={'body1'} headerProps={{backgroundColor: 'greyBackground'}}>
            <AnnexGrid entity={entity}/>
        </DetailExpandCard>

    </Grid>)
}
