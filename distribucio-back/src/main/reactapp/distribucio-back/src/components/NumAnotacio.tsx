import {useSession} from "./SessionStorageContext.tsx";
import {useResourceApiService} from "reactlib";
import {ROLE_USER, useDistribucioContext} from "./DistribucioContext.ts";
import {useEffect} from "react";

export const useNumAnotacio = () => {
    const { value: numAnotacions, save: setNumAnotacions } = useSession("numAnotacions")

    const {
        isReady: apiIsReady,
        artifactAction: apiAction,
    } = useResourceApiService('registreResource');

    const {currentRole, currentEntitatId} = useDistribucioContext();
    const isUser = currentRole == ROLE_USER

    const refresh = () => {
        if (apiIsReady && isUser) {
            apiAction(undefined, {code: "ANOTACIO_NUM"})
                .then((response) => {
                    setNumAnotacions(response.num)
                })
                .catch((_error) => {
                    setNumAnotacions(null)
                });
        }
    }

    const clear = () => {
        setNumAnotacions(null)
    }

    useEffect(() => {
        if (!numAnotacions && apiIsReady) {
            refresh()
        }
        // return () => clear()
    }, [apiIsReady])

    useEffect(() => {
        clear()
    }, [currentRole, currentEntitatId])

    return {
        value: (isUser && numAnotacions) ?numAnotacions :null,
        refresh,
        clear,
    }
}