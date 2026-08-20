import { defineConfig, loadEnv, type Plugin } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';
import { execFileSync } from 'node:child_process';
import { readFileSync } from 'node:fs';
import { resolve } from 'node:path';

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

const pomVersion = (pomPath: string): string | undefined => {
    try {
        const pom = readFileSync(pomPath, 'utf-8');
        // La versió del mòdul viu al bloc <parent>, no com a <version> propi.
        const parent = pom.match(/<parent>[\s\S]*?<\/parent>/)?.[0];
        return (parent ?? pom).match(/<version>([^<]+)<\/version>/)?.[1];
    } catch {
        return undefined;
    }
};

const gitOutput = (args: string[], cwd: string): string | undefined => {
    try {
        return execFileSync('git', args, { cwd, encoding: 'utf-8' }).trim();
    } catch {
        return undefined;
    }
};

// En local no hi ha cap META-INF/MANIFEST.MF accessible des del servlet context: el
// maven-war-plugin escriu aquestes entrades dins del .war, no al directori exploded. Per
// això l'endpoint /manifest del backend retorna `window.__MANIFEST__ = {}` i el peu
// (DrassanaFooter) surt sense versió ni revisió. Aquí el servim nosaltres amb les dades
// reals del pom i del git, amb les mateixes claus que genera el maven-war-plugin.
const devManifest = (): Plugin => ({
    name: 'dev-manifest',
    apply: 'serve',
    configureServer(server) {
        const moduleRoot = resolve(process.cwd(), '../../../..');
        const manifest = {
            'Implementation-Title': 'distribucio-back',
            'Implementation-Version': pomVersion(resolve(moduleRoot, 'pom.xml')) ?? '0.0.0',
            'Implementation-SCM-Branch': gitOutput(['rev-parse', '--abbrev-ref', 'HEAD'], moduleRoot) ?? '',
            'Implementation-SCM-Revision': gitOutput(['rev-parse', 'HEAD'], moduleRoot) ?? '',
            'Build-Timestamp': new Date().toISOString().replace(/\.\d{3}Z$/, 'Z'),
        };
        server.middlewares.use('/distribucioback/manifest', (_req, res) => {
            res.setHeader('Content-Type', 'text/javascript');
            res.end(`window.__MANIFEST__ = ${JSON.stringify(manifest)}`);
        });
    },
});

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
    // Load env file based on `mode` in the current working directory.
    // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
    const env = loadEnv(mode, process.cwd(), '');
    // Backend real (Boot standalone per defecte a 8080; canvia-ho a DEV_BACKEND_URL si el
    // corres a un altre port, p.ex. desplegat a JBoss).
    const backendUrl = env.DEV_BACKEND_URL || 'http://localhost:8080';
    // La versio que mostra el subtitol de Home (i App.tsx) surt de VITE_APP_VERSION, una
    // variable de build que no te res a veure amb __MANIFEST__. Si no s'ha definit a cap
    // fitxer .env, la prenem del pom perque no quedi "(v)" buit en local.
    const appVersion = env.VITE_APP_VERSION || pomVersion(resolve(process.cwd(), '../../../../pom.xml')) || '0.0.0';
    // Vite nomes aplica la substitucio de "define" sobre import.meta.env al build, no en dev.
    // En canvi el seu loadEnv intern si que recull les variables VITE_* de process.env, i
    // s'executa despres d'aquesta funcio: per aixo l'injectem aqui i no via "define".
    process.env.VITE_APP_VERSION = appVersion;

    return {
        preview: {
            port: 5173,
        },
        server: {
            open: env.DISABLE_OPEN_ON_START !== 'true',
            hmr: {
                clientPort: 5173,
            },
            // Permet obrir http://localhost:5173/distribucioback/reactapp directament (sense passar
            // per DevProxyController/backend a :8080): Vite reenvia server-side (sense CORS, ja
            // que el navegador només parla amb :5173) els endpoints de configuració/auth i l'API
            // cap al backend real. La cookie de sessió (domini "localhost", sense "port") s'envia
            // igual si ja t'havies autenticat prèviament contra el backend en el mateix navegador.
            // `/distribucioback/manifest` NO es reenvia: el serveix el plugin devManifest.
            proxy: {
                '/distribucioback/sysenv': { target: backendUrl, changeOrigin: true },
                '/distribucioback/authToken': { target: backendUrl, changeOrigin: true },
                '/distribucioback/authRoles': { target: backendUrl, changeOrigin: true },
                '/distribucioback/api': { target: backendUrl, changeOrigin: true },
            },
        },
        plugins: [react(), tsconfigPaths(), fixBackendEndpointsBase(), devManifest()],
    };
});
