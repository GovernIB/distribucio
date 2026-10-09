import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField, { GridButtonField } from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import { useFormContext } from 'reactlib';

const VistaMovimentsFilterForm: React.FC = () => {
    const { data } = useFormContext();

    // Quan el botó corresponent no està activat, el desplegable només mostra bústies actives
    const filtreBustiaOrigen = data?.mostrarInactivesOrigen ? undefined : builder.eq('activa', true);
    const filtreBustiaDesti = data?.mostrarInactives ? undefined : builder.eq('activa', true);

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="numero" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="titol" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.1 }} name="estat" />
                    <GridFormField size={{ xs: 10, sm: 5, md: 2 }} name="bustiaOrigen" filter={filtreBustiaOrigen} />
                    <GridButtonField
                        icon={'inbox'}
                        size={{ xs: 2, sm: 1, md: 0.5 }}
                        sx={{ minWidth: 0 }}
                        name="mostrarInactivesOrigen"
                    />
                    <GridFormField size={{ xs: 10, sm: 5, md: 2 }} name="bustiaDesti" filter={filtreBustiaDesti} />
                    <GridButtonField
                        icon={'inbox'}
                        size={{ xs: 2, sm: 1, md: 0.5 }}
                        sx={{ minWidth: 0 }}
                        name="mostrarInactives"
                    />
                </>
            )}

            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="numero" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="titol" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="numeroOrigen" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="remitent" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="estat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="dataRecepcioInici" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="dataRecepcioFi" />
                    <GridFormField size={{ xs: 10, sm: 5, md: 3 }} name="bustiaOrigen" filter={filtreBustiaOrigen} />
                    <GridButtonField
                        icon={'inbox'}
                        size={{ xs: 2, sm: 1, md: 0.5 }}
                        sx={{ minWidth: 0 }}
                        name="mostrarInactivesOrigen"
                    />
                    <GridFormField size={{ xs: 10, sm: 5, md: 3 }} name="bustiaDesti" filter={filtreBustiaDesti} />
                    <GridButtonField
                        icon={'inbox'}
                        size={{ xs: 2, sm: 1, md: 0.5 }}
                        sx={{ minWidth: 0 }}
                        name="mostrarInactives"
                    />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="enviatPerEmail" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="interessat" />
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any, setNamedQuery?: (value: string[]) => void) => {
    const namedQueries: any[] = [];

    if (data?.interessat) {
        namedQueries.push(`INTERESSAT#${data.interessat}`);
    }

    setNamedQuery?.(namedQueries);
    return builder.and(
        builder.like('numero', data?.numero),
        builder.like('titol', data?.titol),
        builder.like('numeroOrigen', data?.numeroOrigen),
        builder.eq('remitent.id', data?.remitent?.id ? `'${data.remitent.id}'` : undefined),
        builder.betweenDates('data', data?.dataRecepcioInici, data?.dataRecepcioFi),
        builder.eq('bustiaOrigen.id', data?.bustiaOrigen?.id),
        builder.eq('bustiaDesti.id', data?.bustiaDesti?.id),
        data?.estat && builder.equals('pendent', true, data.estat == 'PENDENT'),
        data?.enviatPerEmail && builder.eq('enviatPerEmail', data.enviatPerEmail === 'ENVIAT')
    );
};

export const VistaMovimentsFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="vistaMovimentResource"
            code="FILTER"
            springFilterBuilder={(data: any) => springFilterBuilder(data, props.onNamedQueriesChange)}
            advancedSearch
            {...props}
        >
            <VistaMovimentsFilterForm />
        </StyledMuiFilter>
    );
};

export default VistaMovimentsFilter;
