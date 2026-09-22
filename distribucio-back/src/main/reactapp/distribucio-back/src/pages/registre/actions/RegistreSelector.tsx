import { useFormContext } from 'reactlib';
import StyledMuiGrid from '../../../components/StyledMuiGrid.tsx';
import * as builder from '../../../util/springFilterUtils';
import { toSelectionModel } from '../../../util/selectionModelUtils.ts';
import Load from '../../../components/Load.tsx';
import { useTranslation } from 'react-i18next';
import { DetailExpandCard } from '../../../components/CardData.tsx';
import { Chip, Typography, Box, Icon } from '@mui/material';

export const RegistreSelector = ({ disabled = false }: any) => {
    const { t } = useTranslation();
    const { data, apiRef } = useFormContext();

    const columns = [
        { field: 'numero', flex: 1 },
        { field: 'extracte', flex: 2,
            renderCell: (params:any) => <Box display={'flex'} alignItems={'center'}>
                {params.formattedValue}
                {data.warning?.[params.id] && <Icon title={data.warning?.[params.id]} color={'warning'}>warning</Icon>}
            </Box>
        },
    ]

    const setIds = (ids: string[]) => {
        if (!disabled) {
            apiRef.current?.setFieldValue('ids', ids);
        }
    };

    return (
        <Load value={data.tempIds && data.massive} noEffect>
            <DetailExpandCard
                header={
                    <Box display={'flex'} alignItems={'center'} py={1}>
                        <Chip label={data?.ids?.length} size={'small'} sx={{ mr: 1 }} />
                        <Typography mt={0.5} variant={'body2'}>
                            {t('component.RegistreSelector.title')}
                        </Typography>
                        {data.warning && <Icon title={data.warning?.[0]} color={'warning'}>warning</Icon>}
                    </Box>
                }
                cardProps={{ backgroundColor: 'greyBackground' }}
                sx={{ backgroundColor: 'customBackground', p: 2 }}
            >
                <StyledMuiGrid
                    resourceName="registreResource"
                    title={t('component.RegistreSelector.title')}
                    columns={columns}
                    filter={builder.inside('id', data.tempIds)}
                    rowSelectionModel={toSelectionModel(data.ids)}
                    onRowSelectionModelChange={setIds}
                    toolbarHide
                    selectionActive={!disabled ? true : undefined}
                    paginationActive
                    autoHeight
                    readOnly
                />
            </DetailExpandCard>
        </Load>
    );
};
