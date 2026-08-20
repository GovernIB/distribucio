import React from 'react';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { useLocation } from 'react-router-dom';
import { useResourceApiContext } from 'reactlib';

// Rutes del SPA que no tenen pantalla equivalent al JSP. Per a aquestes anem a l'arrel de
// l'aplicació antiga i deixem que DistribucioController.get() decideixi on aterrar segons el
// rol actual (integracio / registreAdmin / registreUser).
const RUTES_SENSE_EQUIVALENT_JSP = ['/', '/home'];

/**
 * Enllaç cap a la interfície JSP. La URL del back antic surt de l'apiUrl del context de
 * recursos: n'hi ha prou de treure-li el sufix "/api". Així funciona igual desplegat (mateix
 * origen) que en desenvolupament, on el SPA es serveix des de Vite (:5173) i el JSP viu al
 * backend (:8080).
 */
export const useInterficieClassica = () => {
    const { apiUrl } = useResourceApiContext();
    // L'apiUrl pot acabar amb "/api" o amb "/api/": contemplem els dos casos.
    const baseUrl = apiUrl.replace(/\/api\/?$/, '/');
    const { pathname } = useLocation();
    const getUrl = React.useCallback(() => {
        const ref = RUTES_SENSE_EQUIVALENT_JSP.includes(pathname)
            ? ''
            : pathname.replace(/^\//, '');
        return baseUrl + ref;
    }, [baseUrl, pathname]);
    return {
        getUrl,
        // Sortida "dura" del SPA: el JSP és una altra aplicació, no una ruta de React Router.
        anarAInterficieClassica: () => {
            window.location.href = getUrl();
        },
    };
};

export const InterficieClassicaButton: React.FC = () => {
    const { t } = useTranslation();
    const { anarAInterficieClassica } = useInterficieClassica();
    return (
        <Button
            color="inherit"
            startIcon={<Icon fontSize="small">fast_rewind</Icon>}
            onClick={anarAInterficieClassica}>
            {t('app.interficie.classica')}
        </Button>
    );
};

export default InterficieClassicaButton;
