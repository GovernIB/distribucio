import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import ProtectedRoute from './components/ProtectedRoute';
import RutaInicial from './components/RutaInicial';
import Home from './pages/Home';
import NotFound from './pages/NotFound';
import EntitatGrid from './pages/entitat/EntitatGrid';
import EntitatForm from './pages/entitat/EntitatForm';
import AvisGrid from './pages/avis/AvisGrid';
import AvisForm from './pages/avis/AvisForm';

export const router = createBrowserRouter(
    [
        {
            path: '/',
            element: <App />,
            children: [
                {
                    index: true,
                    element: <RutaInicial />,
                },
                // Cada pantalla penja d'un ProtectedRoute que en comprova el rol (veure
                // PANTALLA_ROLS a util/pantalles.ts). Una ruta nova ha d'anar dins d'un
                // d'aquests grups: si es penja directament d'aquí queda oberta a tots els rols.
                {
                    element: <ProtectedRoute pantalla="home" />,
                    children: [
                        {
                            path: 'home',
                            element: <Home />,
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="entitat" />,
                    children: [
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
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="avis" />,
                    children: [
                        {
                            path: 'avis',
                            element: <AvisGrid />,
                        },
                        {
                            path: 'avis/form',
                            element: <AvisForm />,
                        },
                        {
                            path: 'avis/form/:id',
                            element: <AvisForm />,
                        },
                    ],
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
