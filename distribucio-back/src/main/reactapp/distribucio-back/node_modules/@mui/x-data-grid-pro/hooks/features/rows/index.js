"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
var _useGridRowAriaAttributes = require("./useGridRowAriaAttributes");
Object.keys(_useGridRowAriaAttributes).forEach(function (key) {
  if (key === "default" || key === "__esModule") return;
  if (key in exports && exports[key] === _useGridRowAriaAttributes[key]) return;
  Object.defineProperty(exports, key, {
    enumerable: true,
    get: function () {
      return _useGridRowAriaAttributes[key];
    }
  });
});