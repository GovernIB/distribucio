import React from 'react';
import { useTranslation } from 'react-i18next';
import { MuiDialog, useCloseDialogButtons, useResourceApiService } from 'reactlib';
import Load from '../../../components/Load';
import BustiaContingutDetailContent from './BustiaContingutDetailContent';
import RegistreContingutDetailContent from './RegistreContingutDetailContent';

/** "Detalls" del llistat de continguts: (Bústia -> bustiaAdminDetall.jsp, Registre -> registreDetall.jsp). */
export const useContingutDetailDialog = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService('bustiaResource');
    const [open, setOpen] = React.useState(false);
    const [contingutId, setContingutId] = React.useState<any>();
    const [row, setRow] = React.useState<any>();
    const [bustia, setBustia] = React.useState<any>();

    const isBustia = row?.tipus !== 'REGISTRE';

    const handleOpen = (id: any, r: any) => {
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
            {isBustia ? (
                <Load value={bustia}>
                    <BustiaContingutDetailContent bustia={bustia} />
                </Load>
            ) : (
                <RegistreContingutDetailContent /> // TODO: Aquest component està pendent de la implementació del registre
            )}
        </MuiDialog>
    );

    return { show: handleOpen, close: handleClose, component: dialog };
};

export default useContingutDetailDialog;
