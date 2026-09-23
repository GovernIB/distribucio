import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import { useFormContext } from 'reactlib';

const ReglaFilterForm: React.FC = () => {
    const { data } = useFormContext();

    return (
        <>
            {!data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codiAssumpte" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="codiSIA" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codiServei" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="codiTramit" />
                </>
            )}
            {data?.advanced && (
                <>
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nom" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="codiAssumpte" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="codiSIA" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="codiServei" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="codiTramit" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="unitat" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="bustia" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="activa" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 1.5 }} name="presencial" />
                    <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="tipus" />

                    {data?.tipus === 'UNITAT' && <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="unitatDesti" />}
                    {data?.tipus === 'BUSTIA' && <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="bustiaDesti" />}
                    {data?.tipus === 'BACKOFFICE' && (
                        <GridFormField size={{ xs: 12, sm: 6, md: 3.5 }} name="backoffice" />
                    )}
                </>
            )}
        </>
    );
};

const springFilterBuilder = (data: any) =>
    builder.and(
        builder.like('nom', data?.nom),
        builder.like('assumpteCodiFiltre', data?.codiAssumpte),
        builder.like('procedimentCodiFiltre', data?.codiSIA),
        builder.like('serveiCodiFiltre', data?.codiServei),
        builder.like('tramitCodiFiltre', data?.codiTramit),
        builder.eq('unitatOrganitzativaFiltre.id', data?.unitat?.id),
        builder.eq('bustiaFiltre.id', data?.bustia?.id),
        data?.activa && builder.equals('activa', true, data.activa === 'ACTIVES'),
        builder.eq('presencial', data?.presencial ? `'${data.presencial}'` : undefined),
        builder.eq('tipus', data?.tipus ? `'${data.tipus}'` : undefined),
        builder.eq('unitatDesti.id', data?.unitatDesti?.id),
        builder.eq('bustiaDesti.id', data?.bustiaDesti?.id),
        builder.eq('backofficeDesti.id', data?.backoffice?.id)
    );

export const ReglaFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="reglaResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            advancedSearch
            {...props}
        >
            <ReglaFilterForm />
        </StyledMuiFilter>
    );
};

export default ReglaFilter;
