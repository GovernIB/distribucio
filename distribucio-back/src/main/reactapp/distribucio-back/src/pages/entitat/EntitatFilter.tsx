import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

/**
 * Camps del filtre del llistat d'entitats. Els labels i els tipus els aporta el backend
 * (artefacte FILTER d'EntitatResource, formClass EntitatResource.FormFilter), per això
 * només cal indicar-hi el `name`.
 *
 * Les mides `md` sumen 9.6 perquè el bloc de botons de StyledMuiFilter (md 2.4) càpiga
 * a la mateixa fila; si se'n canvia alguna cal mantenir la suma.
 */
const EntitatFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="codi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.8 }} name="nom" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="cif" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.8 }} name="codiDir3" />
            <GridFormField size={{ xs: 12, sm: 6, md: 1.4 }} name="activa" />
        </>
    );
};

// "activa" és un Boolean, que el motor de recursos representa amb un desplegable de tres
// valors (buit / Sí / No). Cal comparar-lo explícitament amb null i amb la cadena buida:
// amb una comprovació de veritat, triar "No" (false) es confondria amb "sense filtrar".
const springFilterBuilder = (data: any) => {
    const activa = data?.activa;
    return builder.and(
        builder.like('codi', data?.codi),
        builder.like('nom', data?.nom),
        builder.like('cif', data?.cif),
        builder.like('codiDir3', data?.codiDir3),
        activa != null && activa !== '' ? builder.eq('activa', activa) : ''
    );
};

export const EntitatFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="entitatResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <EntitatFilterForm />
        </StyledMuiFilter>
    );
};

export default EntitatFilter;
