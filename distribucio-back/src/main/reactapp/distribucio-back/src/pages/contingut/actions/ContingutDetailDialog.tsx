import React from 'react';
import { useTranslation } from 'react-i18next';
import { MuiDialog, useCloseDialogButtons, useResourceApiService } from 'reactlib';
import Load from '../../../components/Load';
import BustiaContingutDetailContent from './BustiaContingutDetailContent';
import {useBasicDetail} from '../../registre/detail/RegistreDetail';

/** "Detalls" del llistat de continguts: (Bústia -> bustiaAdminDetall.jsp, Registre -> registreDetall.jsp). */
export const useContingutDetailDialog = () => {
    const { t } = useTranslation();
    const { handleOpen: handleOpenRegistre, dialog: registreDialog } = useBasicDetail();
    const closeButtons = useCloseDialogButtons();
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService('bustiaResource');
    const [open, setOpen] = React.useState(false);
    const [contingutId, setContingutId] = React.useState<any>();
    const [row, setRow] = React.useState<any>();
    const [bustia, setBustia] = React.useState<any>();

    const isBustia = row?.tipus !== 'REGISTRE';

    const handleOpen = (id: any, r: any) => {
        if (r?.tipus === 'REGISTRE') {
            handleOpenRegistre(id, r);
            return;
        }
        setContingutId(id);
        setRow(r);
        setBustia(undefined);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const refreshBustia = () => {
        if (contingutId != null) {
            apiGetOne(contingutId, { perspectives: ['PERMISOS_COUNT'] }).then(setBustia);
        }
    };

    React.useEffect(() => {
        if (open && apiIsReady && isBustia) {
            refreshBustia();
        }
    }, [open, apiIsReady, contingutId, isBustia]);

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={row?.nom ? `${t('page.contingut.detall.title')}: ${row.nom}` : t('page.contingut.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            <Load value={bustia}>
                <BustiaContingutDetailContent bustia={bustia} />
            </Load>
        </MuiDialog>
    );

    const component = (
        <>
            {dialog}
            {registreDialog}
        </>
    );

    return { show: handleOpen, close: handleClose, component };
};

export default useContingutDetailDialog;
