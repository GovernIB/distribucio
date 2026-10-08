import React from 'react';
import StyledMuiFilter, { FILTER_ADVANCED_ICON_ONLY_BREAKPOINT } from '../../components/StyledMuiFilter';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const ServeiFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codiSia" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="unitatOrganitzativa" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="estat" />
            <GridButtonField
                size={{ xs: 12, sm: 6, md: 0.5 }}
                name={'nomesComu'}
                icon={'report_problem'}
                iconOnlyBreakpoint={FILTER_ADVANCED_ICON_ONLY_BREAKPOINT}
            />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    const comu = data?.nomesComu;
    return builder.and(
        builder.like('codiSia', data?.codiSia),
        builder.like('nom', data?.nom),
        builder.eq('unitatOrganitzativa.id', data?.unitatOrganitzativa?.id),
        builder.eq('estat', `'${data?.estat}'`),
        comu != null && comu ? builder.eq('comu', true) : ''
    );
};

export const ServeiFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="serveiResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <ServeiFilterForm />
        </StyledMuiFilter>
    );
};

export default ServeiFilter;
