import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const IntegracioFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="entitat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="tipus" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="descripcio" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataFi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="usuari" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="numeroRegistre" />
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
    return (
        <StyledMuiFilter
            resourceName="monitorIntegracioResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <IntegracioFilterForm />
        </StyledMuiFilter>
    );
};

export default IntegracioFilter;
