import { createBrowserRouter, Navigate } from 'react-router-dom';
import App from './App';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <Navigate to="/home" replace />,
                },
                {
                    path: 'home',
                    element: <Home />,
                },
                {
                    path: 'entitat',
                    element: <EntitatGrid />,
                },
                {
                    path: 'entitat/form',
                    element: <EntitatForm />,
                },
                {
                    path: 'entitat/form/:id',
                    element: <EntitatForm />,
                },
                // Necessari perquè OidcAuthProvider (App -> AuthProvider) es munti també quan
                // l'iframe de renovació silenciosa de sessió (oidc-client-ts) navega a
                // "oidcSilentRenew": sense cap ruta que hi encaixi, React Router no arriba mai a
                // muntar <App>, signinSilentCallback() no s'executa mai, i el fallback que hauria
                // de redirigir a la pantalla real de login de Keycloak tampoc s'arriba a disparar.
                {
                    path: '*',
                    element: <NotFound />,
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);
