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
import {UnitatOrganitzativaGrid} from "./pages/unitatOrganitzativa/UnitatOrganitzativaGrid.tsx";
import ServeiGrid from './pages/servei/ServeiGrid';
import LimitCanviEstatGrid from './pages/limitCanviEstat/LimitCanviEstatGrid.tsx';
import ProcedimentGrid from './pages/procediment/ProcedimentGrid.tsx';
import {BustiaGrid} from "./pages/bustia/BustiaGrid.tsx";
import {BustiaOrganigrama} from "./pages/bustia/BustiaOrganigrama.tsx";

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
                // El "handle" de cada ruta duu la clau de traducció del títol de la pestanya del
                // navegador (veure TitolPagina): el mateix títol que la pantalla mostra a la barra
                // d'eines, com a la interfície JSP.
                // Cada pantalla penja d'un ProtectedRoute que en comprova el rol (veure
                // PANTALLA_ROLS a util/pantalles.ts). Una ruta nova ha d'anar dins d'un
                // d'aquests grups: si es penja directament d'aquí queda oberta a tots els rols.
                {
                    element: <ProtectedRoute pantalla="home" />,
                    children: [
                        {
                            path: 'home',
                            element: <Home />,
                            handle: { titol: 'app.menu.home' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="entitat" />,
                    children: [
                        {
                            path: 'entitat',
                            element: <EntitatGrid />,
                            handle: { titol: 'page.entitats.grid.title' },
                        },
                        {
                            path: 'entitat/form',
                            element: <EntitatForm />,
                            handle: { titol: 'page.entitats.form.titleCreate' },
                        },
                        {
                            path: 'entitat/form/:id',
                            element: <EntitatForm />,
                            handle: { titol: 'page.entitats.form.titleUpdate' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="avis" />,
                    children: [
                        {
                            path: 'avis',
                            element: <AvisGrid />,
                            handle: { titol: 'page.avisos.grid.title' },
                        },
                        {
                            path: 'avis/form',
                            element: <AvisForm />,
                            handle: { titol: 'page.avisos.form.titleCreate' },
                        },
                        {
                            path: 'avis/form/:id',
                            element: <AvisForm />,
                            handle: { titol: 'page.avisos.form.titleUpdate' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="procediment" />,
                    children: [
                        {
                            path: 'procediment',
                            element: <ProcedimentGrid />,
                            handle: { titol: 'page.procediment.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="servei" />,
                    children: [
                        {
                            path: 'servei',
                            element: <ServeiGrid />,
                            handle: { titol: 'page.serveis.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="unitatOrganitzativa" />,
                    children: [
                        {
                            path: 'unitatOrganitzativa',
                            element: <UnitatOrganitzativaGrid />,
                            handle: { titol: 'page.unitatOrganitzativa.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="limitCanviEstat" />,
                    children: [
                        {
                            path: 'limitCanviEstat',
                            element: <LimitCanviEstatGrid />,
                            handle: { titol: 'page.limitCanviEstat.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="bustiaAdmin" />,
                    children: [
                        {
                            path: 'bustiaAdmin',
                            element: <BustiaGrid />,
                            handle: { titol: 'page.bustia.grid.title' },
                        },
                    ],
                },
                {
                    element: <ProtectedRoute pantalla="bustiaAdminOrganigrama" />,
                    children: [
                        {
                            path: 'bustiaAdminOrganigrama',
                            element: <BustiaOrganigrama />,
                            handle: { titol: 'page.bustia.grid.title' },
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
                    handle: { titol: 'page.notFound.message' },
                },
            ],
        },
    ],
    {
        basename: import.meta.env.BASE_URL,
    }
);
