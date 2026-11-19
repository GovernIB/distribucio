import React from 'react';
import type { FormFieldCustomProps } from 'reactlib';
import { FormFieldReference } from '../../lib/components/mui/form/FormFieldReference.tsx';

/**
 * Mida de pàgina de les opcions dels camps de referència. La pàgina següent es carrega en fer
 * scroll fins al final de la llista, i la llista de MUI té una alçada màxima de 40vh: amb la mida
 * per defecte de base-react (5) o la del backend (10) no hi arriba i no es podria fer scroll.
 */
export const OPTIONS_PAGE_SIZE = 20;

/**
 * Camp de referència de l'aplicació: el de base-react amb la mida de pàgina de DISTRIBUCIO.
 * Es registra a `BaseApp` per al tipus 'reference'. `GridFormField` fa la seva pròpia consulta
 * de les opcions i hi aplica la mateixa mida.
 *
 * Port de `src/components/AppFormFieldReference.tsx` de RIPEA.
 */
const AppFormFieldReference: React.FC<FormFieldCustomProps> = (props) => {
    const optionsPageSize = (props as any).optionsPageSize ?? OPTIONS_PAGE_SIZE;
    return <FormFieldReference {...(props as any)} optionsPageSize={optionsPageSize} />;
};

export default AppFormFieldReference;
