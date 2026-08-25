import React from 'react';
import Grid from '@mui/material/Grid';
import { useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

/**
 * Camps del formulari d'entitat. Es comparteix entre el diàleg de creació/modificació de la
 * graella (EntitatGrid, popupEditFormContent) i la pàgina .../entitat/form, de manera que els
 * dos camins editen exactament els mateixos camps.
 *
 * Els camps són els mateixos que la interfície JSP (entitatForm.jsp):
 * - `codi` no es pot canviar un cop creada l'entitat: identifica les seves propietats de
 *   configuració (dis_config.entitat_codi), que quedarien orfes si es modifiqués.
 * - `activa` no hi és: les entitats es creen actives i l'estat es canvia amb les accions
 *   Activa/Desactiva del menú de la fila (veure EntitatAccions).
 */
export const EntitatFormContent: React.FC = () => {
    const { data } = useFormContext();
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="codi" disabled={data?.id != null} />
            <GridFormField size={8} name="nom" />
            <GridFormField size={12} name="descripcio" />
            <GridFormField size={6} name="cif" />
            <GridFormField size={6} name="codiDir3" />
            <GridFormField size={6} name="logoImgFile" />
            <GridFormField size={6} name="logoImgFileDark" />
        </Grid>
    );
};

export default EntitatFormContent;
