package by.overpass.android.solicitor.core

/**
 * Encapsulates actions related to Android permissions
 */
interface PermissionFramework {

    /**
     * Requests the specified [permissions] with the specified [requestCode]
     * @param permissions permissions to be requested
     * @param requestCode request code
     */
    fun requestPermissions(permissions: Array<out String>, requestCode: Int)

    /**
     * Checks the status of the specified [permissions] e.g. which are granted, denied or which
     * need rationale
     * @param permissions permissions to be checked
     * @return instance of [PermissionStatus] describing the current [permissions] state
     */
    fun checkStatus(permissions: Array<out String>): PermissionStatus

    /**
     * Parses the status of the specified [permissions] by checking [grantResults] e.g. which
     * are granted, denied or which need rationale
     * @param permissions permissions to be checked
     * @param grantResults array representing the permission request results
     * @return an instance of [PermissionStatus] describing the current [permissions] state
     */
    fun parseStatus(permissions: Array<out String>, grantResults: IntArray): PermissionStatus

    /**
     * Checks if the [permissions] are all already granted
     * @param permissions permissions to be checked
     * @return <b>true</b> if all [permissions] have already been granted, <b>false</b> otherwise
     */
    fun allGranted(permissions: Array<out String>): Boolean
}
