import '@react-pdf-viewer/core/lib/styles/index.css';
import '@react-pdf-viewer/default-layout/lib/styles/index.css';
import { Viewer, Worker } from "@react-pdf-viewer/core";
import { defaultLayoutPlugin } from "@react-pdf-viewer/default-layout";
import { Alert } from "@mui/material";
import {useResourceApiContext} from "reactlib";
import {useCallback} from "react";
import AlertTitle from "@mui/material/AlertTitle";

const commonProps = { width: '100%', height: '500px', border: '1px solid lightgray', borderRadius: '4px' }

export const useToProgramaAntic = () => {
    const { apiUrl } = useResourceApiContext();
    const cleanApiUrl = apiUrl.replace(/\/api\/?$/, '/');

    const getUrl = useCallback(
        (ref: string) => {
            // console.log("apiUrl", apiUrl, cleanApiUrl)
            if (cleanApiUrl.endsWith('/') && ref.startsWith('/')) {
                ref = ref.substring(1);
            }
            if (!cleanApiUrl.endsWith('/') && !ref.startsWith('/')) {
                ref = '/' + ref;
            }
            return `${cleanApiUrl}${ref}`;
        },
        [cleanApiUrl]
    );

    return {
        getUrl,
        toProgramaAntic: (ref: string) => (window.location.href = getUrl(ref)),
    };
};

const Iframe = (props:any) => {
    const { src, hidden, style, isPDF = false, ...other } = props

    if(!src || hidden) {
        return <></>
    }

    if (!isPDF) {
        return <iframe src={src} {...other} style={{...commonProps, ...style}}/>
    }

    const defaultLayoutPluginInstance = defaultLayoutPlugin();
    return (
        <div style={{ ...style }}>
            <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
                <Viewer
                    fileUrl={src}   // tu PDF (en public/ o desde una URL)
                    plugins={[defaultLayoutPluginInstance]}
                    renderError={(error) => (
                        <Alert severity="warning" sx={{m: 2}}><AlertTitle>{error.name}</AlertTitle>{error.message}</Alert>
                    )}
                    {...other}
                />
            </Worker>
        </div>
    );
}

export default Iframe;