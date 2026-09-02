import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const UnitatOrganitzativaFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 2.4 }} name="codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.4 }} name="denominacio" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.4 }} name="unitatSuperior" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.4 }} name="estat" />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.eq('unitatSuperior.id', data?.unitatSuperior?.id),
        builder.eq('estat', `'${data?.estat}'`),
    );
};

export const UnitatOrganitzativaFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="unitatOrganitzativaResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <UnitatOrganitzativaFilterForm />
        </StyledMuiFilter>
    );
};

export default UnitatOrganitzativaFilter;
