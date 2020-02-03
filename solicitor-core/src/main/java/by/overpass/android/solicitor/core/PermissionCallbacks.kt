package by.overpass.android.solicitor.core

/**
 * Provides optional callbacks for requesting permissions
 */
interface PermissionCallbacks {

    /**
     * Is called when the requested permissions are granted
     */
    var onGranted: (Permissions) -> Unit

    /**
     * Is called when the requested permissions are denied
     */
    var onDenied: (Permissions) -> Unit

    /**
     * Is called when the requested permissions are denied permanently
     */
    var onDeniedPermanently: (Permissions) -> Unit

    /**
     * Is called when the requested permissions need rationale
     * To repeat the same request, repeat() can be invoked
     */
    var onShouldShowRationale: (permissions: Permissions, repeat: () -> Unit) -> Unit
}
