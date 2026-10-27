import React from 'react';
import StyledMuiFilter, { FILTER_ADVANCED_ICON_ONLY_BREAKPOINT } from '../../components/StyledMuiFilter';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import { icons } from '../../util/icons';

const SiaFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="codiSia" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.8 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.8 }} name="unitatOrganitzativa" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="estat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="tipus" />
            <GridButtonField
                size={{ xs: 12, sm: 6, md: 0.5 }}
                name={'nomesComu'}
                icon={icons.problema}
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
        builder.eq('tipus', `'${data?.tipus}'`),
        comu != null && comu ? builder.eq('comu', true) : ''
    );
};

export const SiaFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="siaResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <SiaFilterForm />
        </StyledMuiFilter>
    );
};

export default SiaFilter;
