import { useBaseAppContext } from 'reactlib';
import { Grid } from '@mui/material';
import { DetailCard, DetailField } from '../../../../components/CardData.tsx';
import { ToolbarButton } from '../../../../components/StyledMuiGrid.tsx';
import { ErrorArea } from '../../../../components/ErrorArea.tsx';
import { useActions } from '../RegistreActions.tsx';
import { icons } from '../../../../util/icons';

export const ProcessBack = ({ entity, refresh }: any) => {
    const { t } = useBaseAppContext();

    const { reenviarBackoffice } = useActions(refresh);

    return (
        <Grid container columnSpacing={1} rowSpacing={1} >
            <DetailCard>
                <DetailField name={'procesEstat'} size={9} inline />
                {entity.potModificar &&
                    (entity.procesEstat == 'BACK_COMUNICADA' ||
                        entity.procesEstat == 'BACK_REBUTJADA' ||
                        entity.procesEstat == 'BACK_ERROR') && (
                        <Grid size={3} sx={{ textAlign: 'end', pr: 1 }}>
                            <ToolbarButton icon={icons.refresca} onClick={() => reenviarBackoffice(entity.id)}>
                                {t('page.registre.accio.reenviarBackoffice.label')}
                            </ToolbarButton>
                        </Grid>
                    )}
                <DetailField name={'backCodi'} inline />
                <DetailField name={'backPendentData'} inline />
                <DetailField name={'backRebudaData'} inline />
                {entity.procesEstat == 'BACK_PROCESSADA' && (
                    <DetailField
                        name={'backProcesRebutjErrorData'}
                        title={t('page.registre.grid.backProcesData')}
                        inline
                    />
                )}
                {entity.procesEstat == 'BACK_REBUTJADA' && (
                    <DetailField
                        name={'backProcesRebutjErrorData'}
                        title={t('page.registre.grid.backRebutjData')}
                        inline
                    />
                )}
                {entity.procesEstat == 'BACK_ERROR' && (
                    <DetailField
                        name={'backProcesRebutjErrorData'}
                        title={t('page.registre.grid.backErrorData')}
                        inline
                    />
                )}
            </DetailCard>

            <Grid size={12}>
                {entity.procesError && (
                    <ErrorArea sx={{ backgroundColor: 'customBackground', fontSize: 12 }}>
                        {entity.procesError}
                    </ErrorArea>
                )}
                {entity.backObservacions && (
                    <ErrorArea sx={{ backgroundColor: 'customBackground', fontSize: 12 }}>
                        {entity.backObservacions}
                    </ErrorArea>
                )}
            </Grid>
        </Grid>
    );
};
