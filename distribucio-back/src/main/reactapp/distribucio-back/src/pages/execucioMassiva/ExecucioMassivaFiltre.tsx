import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import {ROLE_USER, useDistribucioContext} from "../../components/DistribucioContext.ts";
import {useFormContext} from "reactlib";

const ExecucioMassivaFilterForm: React.FC = () => {
    const {data} = useFormContext()
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 4.8 }} name="usuari" disabled={data.isUser} />
            <GridFormField size={{ xs: 12, sm: 6, md: 4.8 }} name="tipus" />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('usuari.id', data?.usuari),
        builder.eq('tipus', `'${data?.tipus}'`),
    );
};

export const ExecucioMassivaFilter: React.FC<any> = (props) => {
    const { currentUser, currentRole } = useDistribucioContext();
    const isUser = currentRole == ROLE_USER
    return (
        <StyledMuiFilter
            resourceName="execucioMassivaResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            defaultData={{
                usuari: isUser ?currentUser.id :undefined,
                isUser
            }}
            sessionKey={null}
            {...props}
        >
            <ExecucioMassivaFilterForm />
        </StyledMuiFilter>
    );
};

export default ExecucioMassivaFilter;
