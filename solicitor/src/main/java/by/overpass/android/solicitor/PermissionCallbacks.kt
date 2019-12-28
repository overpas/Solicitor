package by.overpass.android.solicitor

interface PermissionCallbacks {
    fun onGranted(permissions: Permissions)
    fun onDenied(permissions: Permissions)
    fun onDeniedPermanently(permissions: Permissions)
    fun onShouldShowRationale(permissions: Permissions, repeat: () -> Unit)
}