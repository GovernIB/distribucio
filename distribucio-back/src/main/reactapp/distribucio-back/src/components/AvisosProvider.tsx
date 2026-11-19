import React from 'react';
import { useAvisosSse } from './SseClient';
import { AvisosContext, getAvisNivell, type AvisNivell, type AvisosState } from './AvisosContext';

/**
 * Proveïdor de l'estat dels avisos minimitzats. Ha d'envoltar el banner i els chips, que són a llocs diferents de l'arbre.
 */
export const AvisosProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const avisos = useAvisosSse();
    const [idsMinimitzats, setIdsMinimitzats] = React.useState<ReadonlySet<number>>(new Set());

    // Oblida els avisos que han deixat de ser actius.
    React.useEffect(() => {
        setIdsMinimitzats((previous) => {
            const actius = new Set(avisos.map((avis) => avis.id));
            const filtrats = [...previous].filter((id) => actius.has(id));
            return filtrats.length === previous.size ? previous : new Set(filtrats);
        });
    }, [avisos]);

    const value = React.useMemo<AvisosState>(() => {
        const delNivell = (nivell: AvisNivell) => avisos.filter((avis) => getAvisNivell(avis) === nivell);

        return {
            visibles: avisos.filter((avis) => !idsMinimitzats.has(avis.id)),
            total: (nivell) => delNivell(nivell).length,
            minimitzats: (nivell) => delNivell(nivell).filter((avis) => idsMinimitzats.has(avis.id)).length,
            minimitza: (id) => setIdsMinimitzats((previous) => new Set(previous).add(id)),
            restaura: (nivell) => {
                const ids = delNivell(nivell).map((avis) => avis.id);
                setIdsMinimitzats((previous) => {
                    if (!ids.some((id) => previous.has(id))) {
                        return previous;
                    }
                    const next = new Set(previous);
                    ids.forEach((id) => next.delete(id));

                    return next;
                });
            },
        };
    }, [avisos, idsMinimitzats]);

    return <AvisosContext.Provider value={value}>{children}</AvisosContext.Provider>;
};

export default AvisosProvider;
