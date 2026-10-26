import {SimpleTreeView, TreeItem} from "@mui/x-tree-view";
import {Icon, Box, Button, Tooltip, ButtonGroup} from "@mui/material";
import {ReactNode, useMemo, useState} from "react";
import {useTranslation} from "react-i18next";

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

function getAllParentIds(nodes:any): string[] {
    const ids: string[] = [];

    const traverse = (items:any) => {
        items.forEach((item:any) => {
            if (item.children && item.children.length > 0) {
                ids.push(item.id);
                traverse(item.children);
            }
        });
    };

    traverse(nodes);
    return ids;
}
export const TreeView = ({list, renderCell, defaultExpandedItems, ...other} :any) => {
    const {t} = useTranslation();
    const [expanded, setExpanded] = useState<any[]>(defaultExpandedItems || []);
    const allParentIds = useMemo(() => getAllParentIds(list), [list]);

    const expandAll = () => {
        setExpanded(allParentIds)
    }
    const collapseAll = () => {
        setExpanded([])
    }

    return (
        <SimpleTreeView expandedItems={expanded} onExpandedItemsChange={(_e, ids) => setExpanded(ids)} {...other}>
            <ButtonGroup sx={{ mb: 1 }}>
                <Tooltip title={t('component.TreeView.expandAll')}>
                    <Button onClick={expandAll} startIcon={<Icon>arrow_drop_down</Icon>}>
                        {t('component.TreeView.expandAll')}
                    </Button>
                </Tooltip>
                <Tooltip title={t('component.TreeView.collapseAll')}>
                    <Button onClick={collapseAll} startIcon={<Icon>arrow_drop_up</Icon>}>
                        {t('component.TreeView.collapseAll')}
                    </Button>
                </Tooltip>
            </ButtonGroup>
            <TreeList list={list} renderCell={renderCell} />
        </SimpleTreeView>
    );
}