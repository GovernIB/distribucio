import React from 'react';
import { useTranslation } from 'react-i18next';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import CampDetall from '../../../components/CampDetall';
import { AclPermissionGrid } from '../../../components/AclPermissionManager';
import { useBustiaPermisosColumns } from '../../bustia/BustiaPermisosForm';

type BustiaContingutDetailContentProps = {
    bustia: any;
};

const BooleaValue: React.FC<{ value?: boolean }> = ({ value }) => (
    <Icon fontSize="small">
        {value ? 'check' : 'close'}
    </Icon>
);

export const BustiaContingutDetailContent: React.FC<BustiaContingutDetailContentProps> = ({ bustia }) => {
    const { t } = useTranslation();
    const permisosColumns = useBustiaPermisosColumns();

    return (
        <Table size="small">
            <TableBody>
                <CampDetall label={t('page.contingut.detall.camp.nom')} value={bustia?.nom} />
                <CampDetall label={t('page.contingut.detall.camp.entitat')} value={bustia?.entitat?.description} />
                <CampDetall
                    label={t('page.contingut.detall.camp.unitatOrganitzativa')}
                    value={bustia?.unitatOrganitzativa?.description}
                />
                <CampDetall
                    label={t('page.contingut.detall.camp.perDefecte')}
                    value={<BooleaValue value={bustia?.perDefecte} />}
                />
                <CampDetall
                    label={t('page.contingut.detall.camp.activa')}
                    value={<BooleaValue value={bustia?.activa} />}
                />
                {!!bustia?.permisosCount && (
                    <TableRow>
                        <TableCell colSpan={2} sx={{ p: 0, borderBottom: 'none' }}>
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
                                    toolbarHideQuickFilter
                                    height="300px"
                                    density="compact"
                                    sx={{
                                        '& .MuiDataGrid-row': { minHeight: '36px !important', maxHeight: '36px !important' },
                                        '& .MuiDataGrid-cell': { minHeight: '36px !important', maxHeight: '36px !important' },
                                    }}
                                />
                            </Box>
                        </TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>
    );
};

export default BustiaContingutDetailContent;
