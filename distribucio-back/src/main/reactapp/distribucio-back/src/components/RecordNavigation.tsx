import {useCallback, useEffect, useState} from "react";
import {useResourceApiService} from "reactlib";

export const useRecordNavigation = (props:any = {}) => {
    // "id" és l'id de la fila del grid; 
    // "setId(id, row)" rep la fila sencera per poder derivar-ne l'id del detall quan difereix de l'id de la fila.
    const { id, setId, gridApiRef, resourceName, filter, namedQueries } = props

    const [index, setIndex] = useState<any>();
    const [totalElem, setTotalElem] = useState<any>();
    const [sortModel, setSortModel] = useState<any>();

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService(resourceName);

    useEffect(() => {
        if (id && gridApiRef.current && !index) {
            const api = gridApiRef.current
            const sortModel = api?.getSortModel()
            setSortModel(sortModel?.map((m: any) => `${m.field},${m.sort}`))

            const pagination = api?.state?.pagination?.paginationModel;

            const visibleRowCount = api?.getRowsCount();
            setTotalElem(visibleRowCount)

            const sortedRowIds = api.getSortedRowIds();
            const localIndex = sortedRowIds.indexOf(id);

            if (localIndex !== -1) {
                const indice = localIndex + 1 + (pagination.page * pagination.pageSize);
                setIndex(indice)
            } else {
                setIndex(undefined)
            }
        }
    }, [id, gridApiRef]);

    /** Fila `i` (1-based) si ja és a la pàgina carregada del grid: evita una petició. */
    const getLoadedRow = (i:number) => {
        const api = gridApiRef.current
        const pagination = api?.state?.pagination?.paginationModel
        if (!api || !pagination) return undefined
        const rowId = api.getSortedRowIds()[i - 1 - pagination.page * pagination.pageSize]
        return rowId === undefined ? undefined : (api.getRow(rowId) ?? undefined)
    }

    const fetchRow = async (i:number) => {
        /// Se recomienda ordenación secundaria
        return getLoadedRow(i) ?? (await apiFind({ page: i - 1, size: 1, filter, namedQueries, sorts: sortModel }))?.rows?.[0]
    }

    /**
     * Es mou a la fila `i` (1-based).
     * Retorna `false` si no hi ha cap fila on anar; es rebutja si la petició falla.
     */
    const move = useCallback(async (i:number): Promise<boolean> => {
        if (!apiIsReady) return false
        const row = await fetchRow(i)
        if (!row) return false
        setId(row.id, row)
        setIndex(i)
        return true
    }, [apiIsReady, filter, namedQueries, sortModel])

    const reset = useCallback(() => {
        setIndex(undefined)
        setTotalElem(undefined)
    }, [])

    const prev = useCallback(() => move(index - 1), [move, index])
    const next = useCallback(() => move(index + 1), [move, index])

    return {
        apiIsReady,
        index,
        totalElem,
        prev,
        next,
        move,
        reset,
    }
}
