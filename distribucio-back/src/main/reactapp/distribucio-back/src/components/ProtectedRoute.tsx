import React from 'react';
import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import NotFound from '../pages/NotFound';
import { isPantallaPermesa, type Pantalla } from '../util/pantalles';
import { useDistribucioContext } from './DistribucioContext';
import Load from './Load';

export type ProtectedRouteProps = {
    pantalla: Pantalla;
};

/**
 * Guarda de ruta per rol: només deixa passar cap a la pantalla si el rol actual hi està autoritzat
 * (veure PANTALLA_ROLS) i, si no, mostra el missatge de no autoritzat en lloc de la pàgina. Port
 * del ProtectedRoute de RIPEA (ripea-back/src/main/jsapp/ripea-back/src/AppRoutes.tsx:51), adaptat
 * per llegir el rol del DistribucioContext en comptes de la sessió d'usuari de RIPEA.
 *
 * Cobreix tant qui escriu la URL a mà com qui es queda en una pantalla que el rol nou ja no pot
 * obrir (el canvi de rol pot arribar d'una altra pestanya, pel BroadcastChannel de
 * DistribucioProvider). No és la barrera de seguretat: aquesta és el backend, amb els
 * @ResourceAccessConstraint de cada recurs.
 */
export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ pantalla }) => {
    const { t } = useTranslation();
    const { currentRole } = useDistribucioContext();
    // DistribucioProvider no munta els fills fins que no ha resolt el rol actual, però en mode
    // offline sí que ho fa: sense esperar-lo es mostraria un "no autoritzat" fals (el mateix motiu
    // pel qual el ProtectedRoute de RIPEA espera isLoaded).
    if (currentRole == null) {
        return <Load value={false} />;
    }
    if (!isPantallaPermesa(pantalla, currentRole)) {
        return <NotFound message={t('page.forbidden.message')} />;
    }
    return <Outlet />;
};

export default ProtectedRoute;
