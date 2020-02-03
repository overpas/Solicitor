package by.overpass.android.solicitor.core

/**
 * A [PermissionRequest] with the dependencies required to perform permission requests
 */
interface PermissionRequestScope : PermissionRequest {
    var permissionFramework: PermissionFramework
    var showedRationale: Boolean
    var requestCode: Int
}
