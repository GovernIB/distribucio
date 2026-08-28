import React from 'react';
import Grid from '@mui/material/Grid';
import GridFormField from '../../components/GridFormField';

export const AvisFormContent: React.FC = () => {
    // const { data } = useFormContext();

    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="assumpte" />
            <GridFormField size={12} name="missatge" type={'textarea'}/>
            <GridFormField size={6} name="dataInici" />
            <GridFormField size={6} name="dataFinal" />
            <GridFormField size={12} name="avisNivell" />
            <GridFormField size={12} name="entitat" />
        </Grid>
    );
};

export default AvisFormContent;
