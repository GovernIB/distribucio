import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField, {GridButtonField} from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';
import {useFormContext} from "reactlib";
import {ROLE_ADMIN, useDistribucioContext} from "../../components/DistribucioContext.ts";

const RegistreFilterForm: React.FC = () => {
    const {data} = useFormContext()
    return (
        <>
            {(!data?.advanced) && <>
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="numero" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="titol" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="interessat" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="dataRecepcioInici" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="dataRecepcioFi" />
                <GridFormField size={{ xs: 12, sm: 6, md: 4 }} name="estat" />
            </>}
            {(data?.advanced) && <>
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="numero" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="titol" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="numeroOrigen" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="remitent" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="interessat" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataRecepcioInici" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="dataRecepcioFi" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="unitatOrganitzativa" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="bustia" />
                <GridButtonField icon={'inbox'} size={{ xs: 12, sm: 1, md: 1 }} name="inactives" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="enviatPerEmail" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="documentacio" />
                <GridButtonField icon={'warning'} size={{ xs: 12, sm: 1, md: 1 }} name="ambEsborranys" />
                <GridButtonField icon={'visibility'} size={{ xs: 12, sm: 1, md: 1 }} name="annexosInterns" hidden={!data?.isAdmin} />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="backoffice" valueField={"codi"} />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="estat" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="procesEstat" />
                <GridButtonField icon={'error'} size={{ xs: 12, sm: 1, md: 1 }} name="ambErrors" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="sobreescriure" hidden={!data?.isAdmin} />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="nombreAnnexes" />
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="procediment" valueField={"codiSia"}
                               additionalOpctions={(q) => q ?[{ id: q, description: `${q} - (No trobat)` }] :[]}/>
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="servei" valueField={"codiSia"}
                               additionalOpctions={(q) => q ?[{ id: q, description: `${q} - (No trobat)` }] :[]}/>
                <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="reintents" />
            </>}
        </>
    );
};

const springFilterBuilder = (data: any, setNamedQuery?: (value:string[]) => void) => {
    const namedQueries:any[] = []

    // console.log("data", data)

    // UNITAT_ORGANITZATIVA -> builder.eq('pare.unitatOrganitzativa.id', data?.unitatOrganitzativa?.id),
    if (data?.unitatOrganitzativa)
        namedQueries.push(`UNITAT_ORGANITZATIVA#${data?.unitatOrganitzativa?.id}`)

    // INACTIVES -> builder.eq('pare.activa', false),
    if (data?.inactives)
        namedQueries.push(`INACTIVES`)

    if (data?.nombreAnnexes)
        namedQueries.push(`NOMBRE_ANNEXOS#${data?.nombreAnnexes}`)

    if (data?.reintents != null) {
        if (data?.reintents == 'true') {
            namedQueries.push(`AMB_REINTENTS`)
        } else {
            namedQueries.push(`SENSE_REINTENTS`)
        }
    }

    setNamedQuery?.(namedQueries)
    return builder.and(
        builder.like('numero', data?.numero),
        builder.like('extracte', data?.titol),
        builder.like('numeroOrigen', data?.numeroOrigen),
        builder.eq('darrerMoviment.remitent.id', `'${data?.remitent?.id}'`),
        builder.exists(
            builder.or(
                builder.like(builder.concat(
                    "lower(interessats.documentNum)",
                    "lower(interessats.nom)",
                    "lower(interessats.llinatge1)",
                    "lower(interessats.llinatge2)",
                ), data.interessat?.toLowerCase()),
                builder.like('lower(interessats.raoSocial)', data.interessat?.toLowerCase())
            )
        ),
        builder.betweenDates('data', data?.dataRecepcioInici, data?.dataRecepcioFi),
        builder.eq('pare.id', data?.bustia?.id),
        data?.enviatPerEmail && builder.eq('enviatPerEmail', data?.enviatPerEmail),

        data?.ambEsborranys && builder.exists( // annexos
            builder.and(
                builder.or(
                    builder.eq('justificant.id', null),
                    builder.neq('annexos.id', 'justificant.id'),
                ),
                builder.or(
                    builder.eq('justificantArxiuUuid', null),
                    builder.eq('annexos.fitxerArxiuUuid', null),
                    builder.neq('annexos.fitxerArxiuUuid', 'justificantArxiuUuid'),
                ),
                !data?.isAdmin && builder.neq('annexos.sicresTipusDocument', `'03'`),
                builder.eq('annexos.arxiuEstat', `'ESBORRANY'`),
            ),
        ),

        data?.isAdmin && data.annexosInterns && builder.exists( // annexos
            builder.eq('annexos.sicresTipusDocument', `'03'`)
        ),

        data?.documentacio && builder.eq('documentacioFisicaCodi', data?.documentacio == 'PAPER' ?1 : data?.documentacio == 'DIGIT_PAPER' ?2 :3),
        builder.eq('backCodi', `'${data?.backoffice?.id}'`),
        data?.estat && builder.equals('pendent', true, data?.estat == 'PENDENT'),
        builder.eq('procesEstat', `'${data.procesEstat}'`),
        data?.ambErrors && builder.neq('procesError', null),
        data?.isAdmin && data?.sobreescriure && builder.eq('sobreescriure', data?.sobreescriure),
        builder.eq('procedimentCodi', `'${data?.procediment?.id}'`),
        builder.eq('serveiCodi', `'${data?.servei?.id}'`),
    );
};

export const RegistreFilter: React.FC<any> = (props) => {
    const { currentRole } = useDistribucioContext()
    return (
        <StyledMuiFilter
            resourceName="registreResource"
            code="FILTER"
            springFilterBuilder={(data:any) => springFilterBuilder(data, props.onNamedQueriesChange)}
            advancedSearch
            defaultData={{
                isAdmin: currentRole == ROLE_ADMIN
            }}
            {...props}
        >
            <RegistreFilterForm />
        </StyledMuiFilter>
    );
};

export default RegistreFilter;
