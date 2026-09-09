import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const BackofficeFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="url" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="tipus" />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.like('url', data?.url),
        data?.tipus && builder.eq('tipus', `'${data?.tipus}'`),
    );
};

export const BackofficeFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="backofficeResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <BackofficeFilterForm />
        </StyledMuiFilter>
    );
};

export default BackofficeFilter;
