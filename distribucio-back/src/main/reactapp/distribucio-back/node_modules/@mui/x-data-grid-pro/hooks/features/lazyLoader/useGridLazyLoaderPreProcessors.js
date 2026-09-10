"use strict";

var _interopRequireWildcard = require("@babel/runtime/helpers/interopRequireWildcard").default;
var _interopRequireDefault = require("@babel/runtime/helpers/interopRequireDefault").default;
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.useGridLazyLoaderPreProcessors = exports.GRID_SKELETON_ROW_ROOT_ID = void 0;
var _extends2 = _interopRequireDefault(require("@babel/runtime/helpers/extends"));
var React = _interopRequireWildcard(require("react"));
var _internals = require("@mui/x-data-grid/internals");
var _xDataGrid = require("@mui/x-data-grid");
const GRID_SKELETON_ROW_ROOT_ID = exports.GRID_SKELETON_ROW_ROOT_ID = 'auto-generated-skeleton-row-root';
const getSkeletonRowId = index => `${GRID_SKELETON_ROW_ROOT_ID}-${index}`;
const useGridLazyLoaderPreProcessors = (privateApiRef, props) => {
  const addSkeletonRows = React.useCallback(groupingParams => {
    const rootGroup = groupingParams.tree[_xDataGrid.GRID_ROOT_GROUP_ID];
    if (props.rowsLoadingMode !== 'server' || !props.rowCount || rootGroup.children.length >= props.rowCount) {
      return groupingParams;
    }
    const tree = (0, _extends2.default)({}, groupingParams.tree);
    const rootGroupChildren = [...rootGroup.children];
    for (let i = 0; i < props.rowCount - rootGroup.children.length; i += 1) {
      const skeletonId = getSkeletonRowId(i);
      rootGroupChildren.push(skeletonId);
      const skeletonRowNode = {
        type: 'skeletonRow',
        id: skeletonId,
        parent: _xDataGrid.GRID_ROOT_GROUP_ID,
        depth: 0
      };
      tree[skeletonId] = skeletonRowNode;
    }
    tree[_xDataGrid.GRID_ROOT_GROUP_ID] = (0, _extends2.default)({}, rootGroup, {
      children: rootGroupChildren
    });
    return (0, _extends2.default)({}, groupingParams, {
      tree
    });
  }, [props.rowCount, props.rowsLoadingMode]);
  (0, _internals.useGridRegisterPipeProcessor)(privateApiRef, 'hydrateRows', addSkeletonRows);
};
exports.useGridLazyLoaderPreProcessors = useGridLazyLoaderPreProcessors;