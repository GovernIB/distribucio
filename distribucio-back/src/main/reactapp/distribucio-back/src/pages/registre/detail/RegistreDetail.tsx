import {
    useBaseAppContext,
    useDetailContext,
} from "reactlib";
import {Badge, Box, Breadcrumbs, Icon, IconButton, Typography} from "@mui/material";
import TabComponent from "../../../components/TabComponent.tsx";
import {useEffect, useMemo, useState} from "react";
import {useCommentDialog} from "../../CommentDialog.tsx";
import {useConfig} from "../../../components/ConfigProvider.tsx";
import {
    ROLE_USER,
    useDistribucioContext
} from "../../../components/DistribucioContext.ts";
import Button from "@mui/material/Button";
import {MenuActionButton} from "../../../components/MenuButton.tsx";
import {useRegistreActions} from "./RegistreActions.tsx";
import {useSession} from "../../../components/SessionStorageContext.tsx";
import { icons } from '../../../util/icons';
import {Resum} from "./tabs/ResumTab.tsx";
import {InformacioRegistre} from "./tabs/InformacioTab.tsx";
import {InteressatsGrid} from "./tabs/InteressatsTab.tsx";
import {AnnexTab} from "./tabs/AnnexosTab.tsx";
import {ArxiuDetall} from "./tabs/ArxiuTab.tsx";
import {ProcessAuto} from "./tabs/ProcesAutoTab.tsx";
import {ProcessBack} from "./tabs/ProcesBackTab.tsx";
import {MetaDadesTab} from "./tabs/MetaDadesTab.tsx";
import {Copies} from "./tabs/CopiesTab.tsx";

const RegistreDetailHeader = ({data}:any) => (
    <Breadcrumbs
        separator={'/'}
        sx={{
            px: 1, py: 0.75, mb: 1,
            borderBottom: 1, borderColor: 'divider',
            color: 'text.secondary',
            fontSize: '0.875rem',
        }}
    >
        <Box display={'flex'} alignItems={'center'} gap={0.5}>
            <Icon fontSize={'small'}>{icons.unitatOrganitzativa}</Icon>
            {data.unitatAdministrativaDescripcio}
        </Box>
        <Box display={'flex'} alignItems={'center'} gap={0.5}>
            <Icon fontSize={'small'}>{icons.bustia}</Icon>
            {data.pare?.description}
        </Box>
        <Typography variant={'inherit'} fontWeight={500} color={'text.primary'}>
            {data.nom}
        </Typography>
    </Breadcrumbs>
);

const headerButtonSx = { height: 32, minHeight: 32, px: 1.25, fontSize: '0.8125rem', '& .MuiIcon-root': { fontSize: 18 } }

const RegistreDetail = (props:any) => {
    const { t } = useBaseAppContext();
    const {data} = useDetailContext()
    const {currentRole, currentEntitat} = useDistribucioContext()
    const { refresh, onLoaded } = props

    // MuiDetail manté les dades velles mentre carrega un altre id: quan arriben les noves, ho notifiquem
    useEffect(() => {
        if (data) onLoaded?.()
    }, [data]);

    const { getByName, getByNameAndEntity } = useConfig()
    const metadadesActives = getByName("es.caib.distribucio.permetre.metadades.registre")
    const isConeixementActiu = getByNameAndEntity("es.caib.distribucio.contingut.enviar.coneixement", currentEntitat.codi)

    const isUser = currentRole == ROLE_USER

    const [avanzarPagina, setAvanzarPagina] = useState<boolean>(true)
    const {save} = useSession('avanzarPagina');
    useEffect(() => {
        save(avanzarPagina)
    }, [avanzarPagina]);

    const { handleOpen, component } = useCommentDialog();
    const {actions, components: actionComponents} = useRegistreActions(refresh)

    const tabs = useMemo(() => [
        {
            value: 'resum',
            label: t('component.RegistreDetail.tabs.resum'),
            content: <Resum entity={data}/>
        },
        {
            value: 'info',
            label: t('component.RegistreDetail.tabs.info'),
            content: <InformacioRegistre entity={data}/>
        },
        {
            value: 'interessats',
            label: t('component.RegistreDetail.tabs.interessats'),
            content: <InteressatsGrid id={data.id}/>,
            badge: data.numInteressats,
            showZero: true,
        },
        {
            value: 'annexos',
            label: t('component.RegistreDetail.tabs.annexos'),
            content: <AnnexTab entity={data}/>,
            badge: data.numAnnexos,
            showZero: true,
        },
        {
            value: 'arxiu',
            label: t('component.RegistreDetail.tabs.arxiu'),
            content: <ArxiuDetall entity={data.arxiuDetall}/>
        },
        {
            value: 'procesAuto',
            label: t('component.RegistreDetail.tabs.procesAuto'),
            icon: icons.advertencia,
            content: <ProcessAuto entity={data} refresh={refresh}/>,
            error: true,
            hidden: !(data.procesEstat == 'ARXIU_PENDENT' || data.procesEstat == 'REGLA_PENDENT' || data.procesEstat == 'BACK_PENDENT' || (data.procesEstat == 'BUSTIA_PROCESSADA' && data.procesError != null))
        },
        {
            value: 'procesBack',
            label: t('component.RegistreDetail.tabs.procesBack'),
            content: <ProcessBack entity={data} refresh={refresh}/>,
            hidden: data.procesEstat == 'BACK_PENDENT' || !(data.procesEstat?.includes('BACK_'))
        },
        {
            value: "dades",
            label: t('component.RegistreDetail.tabs.dades'),
            content: <MetaDadesTab entity={data}/>,
            badge: data.numDada,
            showZero: true,
            hidden: !metadadesActives,
        },
        {
            value: 'copia',
            label: t('component.RegistreDetail.tabs.copia'),
            content: <Copies entity={data}/>,
            badge: data.numCopies,
        },
    ], [data, metadadesActives])

    return (
        <>
            <RegistreDetailHeader data={data}/>
            <TabComponent
                tabs={tabs}
                variant="scrollable"
                headerAdditionalData={
                    <Box display={'flex'} justifyContent={'end'} gap={1}>
                        {isUser && isConeixementActiu && (
                            <Button
                                variant={'contained'}
                                color={data.darrerMovimentResource?.perConeixement ? 'info' : 'warning'}
                                sx={headerButtonSx}
                            >
                                <Typography variant={'caption'}>
                                    {data.darrerMovimentResource?.perConeixement
                                        ? t('page.registre.detail.coneixement')
                                        : t('page.registre.detail.tramit')}
                                </Typography>
                            </Button>
                        )}
                        {data.pendentExecucioMassiva && (
                            <Icon title={t('page.registre.avisos.pendentExecucioMassiva')} color={'warning'} sx={{ alignSelf: 'center'}}>
                                {icons.programat}
                            </Icon>
                        )}
                        <Button
                            title={t('component.RegistreDetail.avancar')}
                            variant={avanzarPagina ? 'contained' : 'outlined'}
                            onClick={() => setAvanzarPagina((prev) => !prev)}
                            sx={headerButtonSx}
                        >
                            <Icon>{icons.avanca}</Icon>
                        </Button>
                        <IconButton
                            title={t('component.CommentDialog.label')}
                            onClick={() => handleOpen(data.id, data.nom)}
                            sx={{ ...headerButtonSx, width: 32, px: 0, borderRadius: 1 }}
                        >
                            <Badge badgeContent={data.numComentaris} color="primary" showZero>
                                <Icon>{icons.comentaris}</Icon>
                            </Badge>
                        </IconButton>

                        <MenuActionButton
                            resourceName={'registreResource'}
                            id={data.id}
                            entity={data}
                            buttonLabel={
                                <>
                                    <Icon sx={{ mr: 1 }}>{icons.configuracio}</Icon>
                                    {t('common.actions')}
                                </>
                            }
                            actions={actions}
                            buttonProps={{ variant: 'contained', sx: headerButtonSx }}
                        />
                    </Box>
                }
            />
            {component}
            {actionComponents}
        </>
    );
}

export default RegistreDetail;
