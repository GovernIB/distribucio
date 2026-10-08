import {SimpleTreeView, TreeItem} from "@mui/x-tree-view";
import { Icon, Box } from "@mui/material";
import {ReactNode} from "react";

const TreeList = ({list, renderCell} :{list: any[], renderCell?: (item:any) => ReactNode}) => {
    return <>
        {list?.map?.(item => <TreeItem
            key={item.id}
            itemId={item.id}
            label={renderCell?.(item) || <Box display={'flex'} alignItems={'center'} gap={1} onClick={item?.onClick}>
                <Icon>{item.icon}</Icon>{item.label}</Box>}
            {...item.componentProps}
        >
            {item.children && <TreeList list={item.children} renderCell={renderCell} />}
        </TreeItem>
        )}
    </>
}
export const TreeView = ({list, renderCell, ...other} :any) => {
    return <SimpleTreeView {...other}>
        <TreeList list={list} renderCell={renderCell} />
    </SimpleTreeView>
}