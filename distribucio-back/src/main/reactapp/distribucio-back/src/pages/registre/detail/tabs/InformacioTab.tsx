import {useBaseAppContext, MuiDetail} from "reactlib";
import {Box, Grid, Icon, IconButton} from "@mui/material";
import {DetailCard, DetailExpandCard, DetailField} from "../../../../components/CardData.tsx";
import Load from "../../../../components/Load.tsx";
import {ROLE_ADMIN, ROLE_ADMIN_LECTURA, useDistribucioContext} from "../../../../components/DistribucioContext.ts";
import {useActions} from "../RegistreActions.tsx";
import { icons } from '../../../../util/icons';

export const InformacioRegistre = ({entity}:any) => {
    const { t } = useBaseAppContext();
    const {currentRole} = useDistribucioContext()
    const isAdmin = currentRole == ROLE_ADMIN || currentRole == ROLE_ADMIN_LECTURA

    const {justificant} = useActions()

    return (<Grid container columnSpacing={1} rowSpacing={1}>
        <DetailCard>
            <DetailField name={"registreTipus"} inline/>
            <DetailField name={"nom"} inline hidden={!isAdmin}/>
            <DetailField name={isAdmin ?"identificador" :"numero"} inline/>
            <DetailField name={"data"} inline/>
            <DetailField name={"procesEstat"} formatterValue={(v:any) => <Box display={'flex'} alignItems={'center'}>
                {v}
                {(entity.procesEstat?.includes('BACK_')) && entity.backCodi && <> - {entity.backCodi}</>}
                {entity.procesEstat == 'REGLA_PENDENT' && entity.regla && <> - {entity.regla?.description}</>}
                {entity.procesError && <Icon title={entity.procesError} color={'error'} fontSize={'small'}>{icons.advertencia}</Icon>}
            </Box>} inline></DetailField>
            <DetailField name={"presencial"} inline>{t(`common.boolean.${entity.presencial}`)}</DetailField>
        </DetailCard>


        <DetailCard title={t('component.RegistreDetail.titles.obligatori')} headerProps={{backgroundColor: 'greyBackground'}} size={6}>
            <DetailField name={"oficinaDescripcio"} inline>{entity.oficinaDescripcio} ({entity.oficinaCodi})</DetailField>
            <DetailField name={"llibreDescripcio"} inline>{entity.llibreDescripcio} ({entity.llibreCodi})</DetailField>
            <DetailField name={"extracte"} inline/>
            <DetailField name={"documentacioFisicaDescripcio"} inline>{entity.documentacioFisicaDescripcio} ({entity.documentacioFisicaCodi})</DetailField>
            <DetailField name={"unitatAdministrativaDescripcio"} label={t(`page.registre.detail.unitatAdmin.${entity.registreTipus}`)} inline
            >{entity.unitatAdministrativaDescripcio} ({entity.unitatAdministrativaCodi})</DetailField>
            <DetailField name={"assumpteTipusDescripcio"} inline>{entity.assumpteTipusDescripcio} ({entity.assumpteTipusCodi})</DetailField>
            <DetailField name={"idiomaDescripcio"} inline>{entity.idiomaDescripcio} ({entity.idiomaCodi})</DetailField>
        </DetailCard>

        <DetailCard title={t('component.RegistreDetail.titles.opcional')} headerProps={{backgroundColor: 'greyBackground'}} size={6}>
            <DetailField name={"procediment"} inline hidden={entity.serveiCodi}/>
            <DetailField name={"servei"} inline hidden={!entity.serveiCodi}/>
            <DetailField name={"tramitNom"} inline>{entity.tramitCodi} - {entity.tramitNom}</DetailField>
            <DetailField size={6} name={"referencia"} inline/>
            <DetailField size={6} name={"expedientNumero"} inline/>
            <DetailField size={6} name={"transportTipusDescripcio"} inline>{entity.transportTipusDescripcio} ({entity.transportTipusCodi})</DetailField>
            <DetailField size={6} name={"transportNumero"} inline/>
            <DetailField size={6} name={"oficinaOrigenDescripcio"} inline>{entity.oficinaOrigenDescripcio} ({entity.oficinaOrigenCodi})</DetailField>
            <DetailField size={6} name={"assumpteDescripcio"} inline>({entity.assumpteCodi})</DetailField>
            <DetailField size={6} name={"numeroOrigen"} inline/>
            <DetailField size={6} name={"dataOrigen"} inline/>
            <DetailField name={"observacions"} inline/>
        </DetailCard>

        <DetailExpandCard title={t('component.RegistreDetail.titles.seguiment')} headerProps={{backgroundColor: 'greyBackground'}}>
            <DetailField name={"entitat"} inline/>
            <DetailField name={"aplicacioCodi"} inline>{entity.aplicacioCodi} {entity.aplicacioVersio}</DetailField>
            <DetailField name={"usuariNom"} inline hidden={!entity.usuariCodi}>{entity.usuariNom} ({entity.usuariCodi})</DetailField>
            <DetailField name={"createdDate"} inline/>
        </DetailExpandCard>

        <Load value={entity.justificant?.id} noEffect>
            <DetailExpandCard title={t('component.RegistreDetail.titles.justificant')} headerProps={{backgroundColor: 'greyBackground'}}>
                <Grid size={12}>
                <MuiDetail
                    id={entity.justificant?.id}
                    resourceName={'registreAnnexResource'}
                    hiddenToolbar
                    componentProps={{ sx: { mt: 0 } }}
                >
                    <DetailField name={"dataCaptura"} inline/>
                    <DetailField name={"origenCiutadaAdmin"} inline/>
                    <DetailField name={"ntiElaboracioEstat"} inline/>
                    <DetailField name={"ntiTipusDocument"} inline/>
                    <DetailField name={"fitxerNom"} formatterValue={(v:any, data:any) =>
                        <Box display={'flex'} alignItems={'center'} justifyContent={'space-between'}>
                            <>{v} ({data.fitxerTamany} bytes)</>
                            <IconButton
                                title={t('page.registre.accio.justificant.label')}
                                onClick={() => justificant(entity.id)}
                                sx={{ m:0, p:0 }}>
                                <Icon fontSize={'small'}>{icons.descarrega}</Icon>
                            </IconButton>
                        </Box>} inline/>
                </MuiDetail>
                </Grid>
            </DetailExpandCard>
        </Load>
    </Grid>)
}
