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

const springFilterBuilder = (data: any, setNamedQuery?: (value:string[]) => void) => {
    const namedQueries = []

    if (data?.unitatSuperior?.id != null)
        namedQueries.push(`UNITAT_SUPERIOR#${data?.unitatSuperior?.id}`)

    setNamedQuery?.(namedQueries)
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('denominacio', data?.denominacio),
        builder.eq('estat', `'${data?.estat}'`),
    );
};

export const UnitatOrganitzativaFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="unitatOrganitzativaResource"
            code="FILTER"
            springFilterBuilder={(data:any) => springFilterBuilder(data, props.onNamedQueriesChange)}
            {...props}
        >
            <UnitatOrganitzativaFilterForm />
        </StyledMuiFilter>
    );
};

export default UnitatOrganitzativaFilter;
