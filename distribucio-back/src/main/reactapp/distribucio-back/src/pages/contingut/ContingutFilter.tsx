import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const ContingutFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 3.25 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.9 }} name="tipus" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.9 }} name="dataCreacioInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.9 }} name="dataCreacioFi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.3 }} name="opcionsEsborrat" />
        </>
    );
};

const esborratFilter = (opcionsEsborrat: string | undefined): string => {
    switch (opcionsEsborrat) {
        case 'NOMES_ESBORRATS':
            return builder.eq('esborrat', 1);
        case 'ESBORRATS_I_NO_ESBORRATS':
            return '';
        case 'NOMES_NO_ESBORRATS':
        default:
            return builder.eq('esborrat', 0);
    }
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('nom', data?.nom),
        builder.eq('tipus', data?.tipus ? `'${data.tipus}'` : undefined),
        builder.betweenDates('createdDate', data?.dataCreacioInici, data?.dataCreacioFi),
        esborratFilter(data?.opcionsEsborrat)
    );
};

export const ContingutFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="contingutResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <ContingutFilterForm />
        </StyledMuiFilter>
    );
};

export default ContingutFilter;
