import React from 'react';
import StyledMuiFilter from '../../components/StyledMuiFilter';
import GridFormField from '../../components/GridFormField';
import * as builder from '../../util/springFilterUtils';

/**
 * Codis curts (TFxx) que fa servir la BBDD per a cada tipus de firma -- veure ArxiuConversions al
 * backend (es.caib.distribucio.logic.intf.helper.ArxiuConversions). El backend exposa el tipus de
 * firma en cru (aquest codi) perquè és l'únic que es pot filtrar directament; aquí es tradueix
 * l'opció triada (l'enum, amb etiqueta traduïda) al codi que el filtre necessita.
 */
const TIPUS_FIRMA_CODIS: Record<string, string> = {
    CSV: 'TF01',
    XADES_DET: 'TF02',
    XADES_ENV: 'TF03',
    CADES_DET: 'TF04',
    CADES_ATT: 'TF05',
    PADES: 'TF06',
    SMIME: 'TF07',
    ODT: 'TF08',
    OOXML: 'TF09',
};

/**
 * "Número de còpia" és un enumerat fix (COPIA_0..COPIA_10, veure RegistreNumeroCopiaEnumDto al
 * backend), no un desplegable dependent del camp "numero": no hi ha cap relació entre tots dos
 * camps. Tots els noms segueixen el mateix patró "COPIA_<n>" (0 inclòs, encara que l'etiqueta que
 * es mostra per aquest sigui "Original") perquè no calgui mantenir aquí cap taula de traducció: el
 * número amb què es filtra registreNumeroCopia surt directament del nom triat.
 */
const numeroCopiaValor = (value: string): number => Number(value.split('_')[1]);

const AnnexFilterForm: React.FC = () => {
    return (
        <>
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="numero" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="numeroCopia" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="arxiuEstat" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="tipusFirma" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="fitxerTipusMime" />
            <GridFormField size={{ xs: 12, sm: 6, md: 3 }} name="titol" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataAnotacioInici" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2 }} name="dataAnotacioFi" />
            <GridFormField size={{ xs: 12, sm: 6, md: 2.5 }} name="fitxerNom" />
        </>
    );
};

const springFilterBuilder = (data: any) => {
    return builder.and(
        builder.like('registreNumero', data?.numero),
        data?.numeroCopia && builder.eq('registreNumeroCopia', numeroCopiaValor(data.numeroCopia)),
        builder.eq('arxiuEstat', data?.arxiuEstat ? `'${data.arxiuEstat}'` : undefined),
        data?.tipusFirma && builder.eq('tipusFirma', `'${TIPUS_FIRMA_CODIS[data.tipusFirma]}'`),
        builder.like('fitxerTipusMime', data?.fitxerTipusMime),
        builder.like('titol', data?.titol),
        builder.betweenDates('dataAnotacio', data?.dataAnotacioInici, data?.dataAnotacioFi),
        builder.like('fitxerNom', data?.fitxerNom)
    );
};

export const AnnexFilter: React.FC<any> = (props) => {
    return (
        <StyledMuiFilter
            resourceName="registreAnnexResource"
            code="FILTER"
            springFilterBuilder={springFilterBuilder}
            {...props}
        >
            <AnnexFilterForm />
        </StyledMuiFilter>
    );
};

export default AnnexFilter;
