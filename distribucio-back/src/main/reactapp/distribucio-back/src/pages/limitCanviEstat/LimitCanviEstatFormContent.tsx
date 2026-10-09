import React from 'react';
import Grid from '@mui/material/Grid';
import GridFormField from '../../components/GridFormField';

export const LimitCanviEstatFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="usuariCodi" />
            <GridFormField size={12} name="descripcio" />
            <GridFormField size={6} name="limitMinutLaboral" />
            <GridFormField size={6} name="limitMinutNoLaboral" />
            <GridFormField size={6} name="limitDiaLaboral" />
            <GridFormField size={6} name="limitDiaNoLaboral" />
        </Grid>
    );
};

export default LimitCanviEstatFormContent;
