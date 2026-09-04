import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField, {GridButtonField} from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

const BustiaFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="unitatSuperior" />
            <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="unitatOrganitzativa" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} type={'checkbox'} name="pendent" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} type={'checkbox'} name="principal" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} type={'checkbox'} name="activa" />
            <GridButtonField size={{ xs: 12, sm: 6, md: 0.5 }} name="permisPerUsuari" icon={'warning'}/>
        </>
    );
};

const springFilterBuilder = (data: any, setNamedQuery?: (value:string[]) => void) => {
    const namedQueries = []

    if (data?.unitatSuperior?.id != null)
        namedQueries.push(`UNITAT_SUPERIOR#${data?.unitatSuperior?.id}`)

    if (data?.permisPerUsuari)
        namedQueries.push('PERMIS_PER_USUARI')

    setNamedQuery?.(namedQueries)
    return builder.and(
        builder.like('nom', data?.nom),
        builder.eq('unitatOrganitzativa.id', data?.unitatOrganitzativa?.id),
        data?.pendent && builder.inside('unitatOrganitzativa.estat', [`'E'`, `'A'`, `'T'`]),
        data?.principal && builder.eq('perDefecte', `'${data?.principal}'`),
        data?.activa && builder.eq('activa', `'${data?.activa}'`),
    );
};

export const BustiaFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="bustiaResource"
            code="FILTER"
            springFilterBuilder={(data:any) => springFilterBuilder(data, props.onNamedQueriesChange)}
            {...props}
        >
            <BustiaFilterForm />
        </StyledMuiFilter>
    );
};

export default BustiaFilter;
