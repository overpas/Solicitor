package by.overpass.android.solicitor.core

interface PermissionFramework {

    fun requestPermissions(permissions: Array<out String>, requestCode: Int)

    fun status(permissions: Array<out String>): PermissionStatus

    fun status(permissions: Array<out String>, grantResults: IntArray): PermissionStatus

    fun allGranted(permissions: Array<out String>): Boolean
}
