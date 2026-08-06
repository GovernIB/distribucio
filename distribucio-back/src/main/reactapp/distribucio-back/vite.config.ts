import { defineConfig, loadEnv, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// Vite prepon `base` a qualsevol src/href arrel-relatiu que trobi a l'index.html. Els
// endpoints del backend que hi referenciem (sysenv, manifest, authToken, authRoles) viuen
// sota el context path de l'aplicació, no sota el base path del SPA -- desfem aquest prefix
// només per a ells, un cop Vite ja ha aplicat la seva pròpia transformació de l'HTML.
const fixBackendEndpointsBase = (): Plugin => {
    let base = '/';
    return {
        name: 'fix-backend-endpoints-base',
        configResolved(config) {
            base = config.base;
        },
        transformIndexHtml: {
            order: 'post',
            handler(html) {
                if (base === '/' || base === '') return html;
                return html.replaceAll(`${base}distribucioback/`, '/distribucioback/');
            },
        },
    };
};

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    // Load env file based on `mode` in the current working directory.
    // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
    const env = loadEnv(mode, process.cwd(), '');
    // Backend real (Boot standalone per defecte a 8080; canvia-ho a DEV_BACKEND_URL si el
    // corres a un altre port, p.ex. desplegat a JBoss). Només cal si es reactiva el bloc
    // "proxy" de sota.
    // const backendUrl = env.DEV_BACKEND_URL || 'http://localhost:8080';

    return {
        preview: {
            port: 5173,
        },
        server: {
            open: env.DISABLE_OPEN_ON_START !== 'true',
            hmr: {
                clientPort: 5173,
            },
            // // Permet obrir http://localhost:5173/distribucioback/reactapp directament (sense passar
            // // per DevProxyController/backend a :8080): Vite reenvia server-side (sense CORS, ja
            // // que el navegador només parla amb :5173) els endpoints de configuració/auth i l'API
            // // cap al backend real. La cookie de sessió (domini "localhost", sense "port") s'envia
            // // igual si ja t'havies autenticat prèviament contra el backend en el mateix navegador.
            // proxy: {
            //     '/distribucioback/sysenv': { target: backendUrl, changeOrigin: true },
            //     '/distribucioback/manifest': { target: backendUrl, changeOrigin: true },
            //     '/distribucioback/authToken': { target: backendUrl, changeOrigin: true },
            //     '/distribucioback/authRoles': { target: backendUrl, changeOrigin: true },
            //     '/distribucioback/api': { target: backendUrl, changeOrigin: true },
            // },
        },
        plugins: [react(), tsconfigPaths(), fixBackendEndpointsBase()],
    };
});
