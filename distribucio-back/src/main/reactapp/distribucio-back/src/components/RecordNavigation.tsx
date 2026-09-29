import {useCallback, useEffect, useState} from "react";
import {useResourceApiService} from "reactlib";

export const useRecordNavigation = (props:any = {}) => {
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

    const move = useCallback((i:number) => {
        /// Se recomienda ordenación secundaria
        if (apiIsReady) {
            apiFind({ page: i - 1, size: 1, filter, namedQueries, sorts: sortModel })
                .then((response:any) => {
                    setId(response?.rows[0]?.id)
                    setIndex(i)
                })
                .catch(() => setId(undefined) )
        }
    }, [apiIsReady, filter, namedQueries, sortModel])

    const prev = useCallback(() => move(index - 1), [apiIsReady, index])
    const next = useCallback(() => move(index + 1), [apiIsReady, index])

    return {
        apiIsReady,
        index,
        totalElem,
        prev,
        next,
        move,
    }
}