package by.overpass.android.solicitor.core

interface PermissionCallbacks {
    var onGranted: (Permissions) -> Unit
    var onDenied: (Permissions) -> Unit
    var onDeniedPermanently: (Permissions) -> Unit
    var onShouldShowRationale: (permissions: Permissions, repeat: () -> Unit) -> Unit
}
