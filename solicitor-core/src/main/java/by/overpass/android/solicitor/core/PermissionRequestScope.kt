package by.overpass.android.solicitor.core

interface PermissionRequestScope : PermissionRequest {
    var permissionFramework: PermissionFramework
    var showedRationale: Boolean
    var requestCode: Int
}
