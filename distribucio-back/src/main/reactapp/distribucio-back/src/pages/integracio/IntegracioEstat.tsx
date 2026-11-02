import React from 'react';
import EtiquetaEstat from '../../components/EtiquetaEstat';
import type { TonalColor } from '../../util/tonal';

type IntegracioEstatProps = {
    /** Codi de l'estat (OK, ERROR...): decideix el color i la icona. */
    value?: string;
    /** Text que es mostra; per defecte, el codi. A la graella, el valor formatat (traduït). */
    children?: React.ReactNode;
    excepcioMessage?: string;
};

/** Icona i color tonal de cada estat. La resta d'estats es mostren com a text pla. */
const ESTATS: Record<string, { color: TonalColor; icon: string }> = {
    OK: { color: 'success', icon: 'check' },
    ERROR: { color: 'error', icon: 'warning' },
};

export const IntegracioEstat: React.FC<IntegracioEstatProps> = ({ value, children, excepcioMessage }) => {
    const text = children ?? value;
    const estat = value != null ? ESTATS[value] : undefined;
    if (estat == null) {
        return <>{text}</>;
    }
    return (
        <EtiquetaEstat
            color={estat.color}
            icon={estat.icon}
            title={value === 'ERROR' && excepcioMessage ? excepcioMessage : undefined}
        >
            {text}
        </EtiquetaEstat>
    );
};

export default IntegracioEstat;
