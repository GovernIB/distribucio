import React from 'react';
import { useTranslation } from 'react-i18next';
import { MuiDialog, useCloseDialogButtons, useResourceApiService } from 'reactlib';
import Load from '../../../components/Load';
import AnnexDetailContent from './AnnexDetailContent';

export const useAnnexDetailDialog = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService('registreAnnexResource');
    const [open, setOpen] = React.useState(false);
    const [annexId, setAnnexId] = React.useState<any>();
    const [row, setRow] = React.useState<any>();
    const [annex, setAnnex] = React.useState<any>();

    const handleOpen = (id: any, r: any) => {
        setAnnexId(id);
        setRow(r);
        setAnnex(undefined);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const refreshAnnex = () => {
        if (annexId != null) {
            apiGetOne(annexId).then(setAnnex);
        }
    };

    React.useEffect(() => {
        if (open && apiIsReady) {
            refreshAnnex();
        }
    }, [open, apiIsReady, annexId]);

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={row?.titol ? `${t('page.annex.detall.title')}: ${row.titol}` : t('page.annex.detall.title')}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            <Load value={annex}>
                <AnnexDetailContent annex={annex} onRefresh={refreshAnnex} />
            </Load>
        </MuiDialog>
    );

    return { show: handleOpen, close: handleClose, component: dialog };
};

export default useAnnexDetailDialog;
