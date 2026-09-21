import React from 'react';
import { useTranslation } from 'react-i18next';
import { Badge, Icon, IconButton } from '@mui/material';
import { useCommentDialog } from '../pages/CommentDialog.tsx';

type CommentsColumnOptions = {
    /** Camp de la fila amb el nombre de comentaris. Per defecte `numComentaris`. */
    field?: string;
    /** Id del contingut al qual pertanyen els comentaris. Per defecte l'id de la fila. */
    getId?: (row: any) => any;
    /** Nom que es mostra al títol del diàleg. Per defecte `row.nom`. */
    getName?: (row: any) => string;
    readOnly?: boolean;
    /** S'invoca en tancar el diàleg (p. ex. per refrescar el grid i actualitzar el comptador). */
    onClose?: () => void;
};

/**
 * Columna de comentaris per a un grid: botó amb el comptador que obre el diàleg de comentaris del
 * contingut. Retorna la definició de la columna i el component del diàleg, que s'ha de renderitzar
 * dins la mateixa pàgina que el grid.
 */
export const useCommentsColumn = (options: CommentsColumnOptions = {}) => {
    const { field = 'numComentaris', getId, getName, readOnly, onClose } = options;
    const { t } = useTranslation();
    const { handleOpen, component } = useCommentDialog({ readOnly, onClose });

    // Amplada fixa (només hi cap el botó amb el comptador) i sense títol a la capçalera. Es fa amb
    // renderHeader i no buidant headerName perquè el nom continuï sortint a la gestió de columnes.
    const column = {
        field,
        width: 60,
        minWidth: 60,
        sortable: false,
        renderHeader: () => null,
        renderCell: (params: any) => (
            <IconButton
                title={t('component.CommentDialog.label')}
                onClick={() =>
                    handleOpen(getId ? getId(params.row) : params.id, getName ? getName(params.row) : params.row.nom)
                }
            >
                <Badge badgeContent={params.formattedValue} color="primary" showZero>
                    <Icon>forum</Icon>
                </Badge>
            </IconButton>
        ),
    };

    return { column, component: component as React.ReactElement };
};

export default useCommentsColumn;
