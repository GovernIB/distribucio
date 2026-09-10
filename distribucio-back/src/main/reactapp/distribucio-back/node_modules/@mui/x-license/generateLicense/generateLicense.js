"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.generateLicense = generateLicense;
var _md = require("../encoding/md5");
var _base = require("../encoding/base64");
var _plan = require("../utils/plan");
var _licenseModel = require("../utils/licenseModel");
const licenseVersion = '2';
function getClearLicenseString(details) {
  if (details.planScope && !_plan.PLAN_SCOPES.includes(details.planScope)) {
    throw new Error('MUI X: Invalid scope');
  }
  if (details.licenseModel && !_licenseModel.LICENSE_MODELS.includes(details.licenseModel)) {
    throw new Error('MUI X: Invalid licensing model');
  }
  const keyParts = [`O=${details.orderNumber}`, `E=${details.expiryDate.getTime()}`, `S=${details.planScope}`, `LM=${details.licenseModel}`, `PV=${details.planVersion}`, `KV=${licenseVersion}`];
  return keyParts.join(',');
}
function generateLicense(details) {
  const licenseStr = getClearLicenseString(details);
  return `${(0, _md.md5)((0, _base.base64Encode)(licenseStr))}${(0, _base.base64Encode)(licenseStr)}`;
}