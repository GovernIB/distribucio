"use strict";

var _interopRequireDefault = require("@babel/runtime/helpers/interopRequireDefault").default;
var _interopRequireWildcard = require("@babel/runtime/helpers/interopRequireWildcard").default;
Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.LicenseInfoProvider = LicenseInfoProvider;
var React = _interopRequireWildcard(require("react"));
var _MuiLicenseInfoContext = _interopRequireDefault(require("./MuiLicenseInfoContext"));
var _jsxRuntime = require("react/jsx-runtime");
/**
 * @ignore - do not document.
 */

/**
 * @ignore - do not document.
 */
function LicenseInfoProvider({
  info,
  children
}) {
  return /*#__PURE__*/(0, _jsxRuntime.jsx)(_MuiLicenseInfoContext.default.Provider, {
    value: info,
    children: children
  });
}