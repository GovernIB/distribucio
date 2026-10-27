import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import { useFormContext } from 'reactlib';

const IntegracioFilterForm: React.FC = () => {
        const { data } = useFormContext();
    
    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="entitat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.25 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.75 }} name="descripcio" />
                </>
            )}

            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="entitat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="tipus" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="descripcio" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.25 }} name="dataFi" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="usuari" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="numeroRegistre" />
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('descripcio', data?.descripcio),
        builder.like('codiUsuari', data?.usuari),
        builder.eq('entitat.id', data?.entitat?.id),
        builder.eq('estat', data?.estat ? `'${data.estat}'` : undefined),
        builder.eq('tipus', data?.tipus ? `'${data.tipus}'` : undefined),
        builder.like('numeroRegistre', data?.numeroRegistre),
        builder.betweenDates('data', data?.dataInici, data?.dataFi)
    );
};

export const IntegracioFilter: React.FC<any> = (props) => {
    const { onDataChange, ...other } = props
    return (
        <StyledMuiFilter
            resourceName="monitorIntegracioResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            advancedSearch
            {...other}
        >
            <IntegracioFilterForm />
        </StyledMuiFilter>
    );
};

export default IntegracioFilter;
