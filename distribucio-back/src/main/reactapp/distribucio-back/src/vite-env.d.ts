/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_API_URL: string;
    readonly VITE_API_PUBLIC_URL: string;
    readonly VITE_API_BASE_URL: string;
    readonly VITE_API_SUFFIX: string;
    readonly VITE_APP_VERSION: string;
    readonly VITE_MUI_LICENSE_KEY: string;
    readonly VITE_AUTH_URL: string;
    readonly VITE_AUTH_REALM: string;
    readonly VITE_AUTH_CLIENTID: string;
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}
