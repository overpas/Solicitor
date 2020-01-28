package by.overpass.android.solicitor.impl

import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus

class UninitializedPermissionFramework : PermissionFramework {

    override fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        throw IllegalStateException("PermissionFramework hasn't been initialized")
    }

    override fun status(permissions: Array<out String>): PermissionStatus {
        throw IllegalStateException("PermissionFramework hasn't been initialized")
    }

    override fun status(permissions: Array<out String>, grantResults: IntArray): PermissionStatus {
        throw IllegalStateException("PermissionFramework hasn't been initialized")
    }

    override fun allGranted(permissions: Array<out String>): Boolean {
        throw IllegalStateException("PermissionFramework hasn't been initialized")
    }
}
