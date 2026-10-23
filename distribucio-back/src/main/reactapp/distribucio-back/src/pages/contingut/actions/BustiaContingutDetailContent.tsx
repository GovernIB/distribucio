import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import { DetailCardContent } from '../../../components/CardData';
import { AclPermissionGrid } from '../../../components/AclPermissionManager';
import { useBustiaPermisosColumns } from '../../bustia/BustiaPermisosForm';

type BustiaContingutDetailContentProps = {
    bustia: any;
};

/** Amplada (px) de l'etiqueta dels camps del detall, perquè els valors quedin alineats. */
const BUSTIA_LABEL_WIDTH = 220;

type BustiaCampProps = {
    label: string;
    children?: React.ReactNode;
    /** El valor és un element React (no text): no s'embolica en un Typography. */
    isObject?: boolean;
};

const BustiaCamp: React.FC<BustiaCampProps> = ({ label, children, ...other }) => (
    <DetailCardContent title={label} titleSize="auto" textSize="grow" labelWidth={BUSTIA_LABEL_WIDTH} {...other}>
        {children}
    </DetailCardContent>
);

const BooleaValue: React.FC<{ value?: boolean }> = ({ value }) => (
    <Icon fontSize="small">
        {value ? 'check' : 'close'}
    </Icon>
);

export const BustiaContingutDetailContent: React.FC<BustiaContingutDetailContentProps> = ({ bustia }) => {
    const { t } = useTranslation();
    const permisosColumns = useBustiaPermisosColumns();

    return (
        <>
            <BustiaCamp label={t('page.contingut.detall.camp.nom')}>{bustia?.nom}</BustiaCamp>
            <BustiaCamp label={t('page.contingut.detall.camp.entitat')}>{bustia?.entitat?.description}</BustiaCamp>
            <BustiaCamp label={t('page.contingut.detall.camp.unitatOrganitzativa')}>
                {bustia?.unitatOrganitzativa?.description}
            </BustiaCamp>
            <BustiaCamp label={t('page.contingut.detall.camp.perDefecte')} isObject>
                <BooleaValue value={bustia?.perDefecte} />
            </BustiaCamp>
            <BustiaCamp label={t('page.contingut.detall.camp.activa')} isObject>
                <BooleaValue value={bustia?.activa} />
            </BustiaCamp>
            {!!bustia?.permisosCount && (
                <Grid size={12}>
                    <Box sx={{ mt: 2 }}>
                        <AclPermissionGrid
                            title={t('page.contingut.detall.camp.permisos')}
                            resourceId={bustia.id}
                            resourceType="BUSTIA"
                            columns={permisosColumns}
                            readOnly
                            toolbarHideCreate
                            rowHideUpdateButton
                            rowHideDeleteButton
                            autoHeight
                            toolbarHideQuickFilter
                            height="300px"
                            density="compact"
                            sx={{
                                '& .MuiDataGrid-row': { minHeight: '36px !important', maxHeight: '36px !important' },
                                '& .MuiDataGrid-cell': { minHeight: '36px !important', maxHeight: '36px !important' },
                            }}
                        />
                    </Box>
                </Grid>
            )}
        </>
    );
};

export default BustiaContingutDetailContent;
