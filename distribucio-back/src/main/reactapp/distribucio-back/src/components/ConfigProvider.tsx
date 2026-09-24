import {useResourceApiService} from "reactlib";
import {useState} from "react";

interface ConfigContextType {
    isConfigReady: boolean;
    getByName: (name: string) => any | undefined;
    getByNameAndEntity: (key: string, entitat: string) => any | undefined;
}

const construirKeyEspecifica = (keyBase: string, entitat: string): string => {
    const prefijo = "es.caib.distribucio.";

    if (keyBase.startsWith(prefijo)) {
        const resto = keyBase.substring(prefijo.length);
        return `${prefijo}${entitat}.${resto}`;
    }

    return keyBase;
};

const getValue = (item:any) => {
    return (item.value != null)
        ?(item.type?.id == 'BOOL' && typeof item.value == 'string')
            ? item.value === "true"
            : item.value
        :(item.type?.id == 'BOOL' && typeof item.alternativeValue == 'string')
            ? item.alternativeValue === "true"
            : item.alternativeValue
}

const perspectives = ['ALTERNATIVE_VALUE']
export const useConfig = () :ConfigContextType => {
    const {
        isReady: isConfigReady,
        getOne: apiGetOne,
    } = useResourceApiService('configResource');

    const [configCache, setConfigCache] = useState<Map<string, any>>(new Map());

    const getByName = (name: string): any | undefined => {
        if (configCache.has(name)) {
            return configCache.get(name);
        }

        apiGetOne(name, { perspectives })
            .then(response => {
                const valor = getValue(response);
                setConfigCache(prev => new Map(prev).set(name, valor));
            });

        return configCache.get(name);
    };

    const getByNameAndEntity = (name:string, entitat:string): any | undefined => {
        return getByName( construirKeyEspecifica(name, entitat) );
    }

    return {
        isConfigReady,
        getByName,
        getByNameAndEntity
    }
}