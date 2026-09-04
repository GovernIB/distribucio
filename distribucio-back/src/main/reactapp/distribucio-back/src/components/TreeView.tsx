import {SimpleTreeView, TreeItem} from "@mui/x-tree-view";
import { Icon, Box } from "@mui/material";

const TreeList = ({list} :{list: any[]}) => {
    return <>
        {list?.map?.(item => <TreeItem
            key={item.id}
            itemId={item.id}
            label={<Box display={'flex'} alignItems={'center'} gap={1} onClick={item?.onClick}>
                <Icon>{item.icon}</Icon>{item.label}</Box>}
            {...item.componentProps}
        >
            {item.children && <TreeList list={item.children}/>}
        </TreeItem>
        )}
    </>
}
export const TreeView = ({list, ...other} :any) => {
    return <SimpleTreeView {...other}>
        <TreeList list={list} />
    </SimpleTreeView>
}