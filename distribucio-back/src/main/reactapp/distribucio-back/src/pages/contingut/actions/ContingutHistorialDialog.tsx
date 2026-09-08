import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { MuiDialog, useCloseDialogButtons } from 'reactlib';
import ContingutHistorialContent from './ContingutHistorialContent';

export const useContingutHistorialDialog = () => {
    const { t } = useTranslation();
    const closeButtons = useCloseDialogButtons();
    const [open, setOpen] = React.useState(false);
    const [contingut, setContingut] = React.useState<any>();

    const handleOpen = (_id: any, row: any) => {
        setContingut(row);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.historial.title', { nom: contingut?.nom })}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={closeButtons}
            buttonCallback={() => handleClose()}
        >
            <Box sx={{ height: '70vh', minHeight: 0 }}>
                <ContingutHistorialContent contingutId={contingut?.id} contingutRow={contingut} />
            </Box>
        </MuiDialog>
    );

    return { show: handleOpen, close: handleClose, component: dialog };
};

export default useContingutHistorialDialog;
