import {useFormContext} from "reactlib";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from '../../../util/springFilterUtils';
import {toSelectionModel} from "../../../util/selectionModelUtils.ts";
import Load from "../../../components/Load.tsx";

const columns = [
    { field: 'numero', flex: 1 },
    { field: 'extracte', flex: 2 },
]

export const RegistreSelector = ({disabled = false}:any) => {
    const { data, apiRef } = useFormContext()

    const setIds = (ids:string[]) => {
        if(!disabled) {
            apiRef.current?.setFieldValue("ids", ids)
        }
    }

    return <Load value={data.tempIds && data.massive} noEffect>
        <StyledMuiGrid
            resourceName="registreResource"
            columns={columns}
            filter={builder.inside("id", data.tempIds)}

            rowSelectionModel={ toSelectionModel(data.ids) }
            onRowSelectionModelChange={setIds}

            toolbarHide
            selectionActive={!disabled ?true :undefined}
            paginationActive
            autoHeight
            readOnly
        />
    </Load>
}