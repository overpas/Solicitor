package by.overpass.android.solicitor.impl

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionStatus

internal class FragmentPermissionFramework(
    private val fragment: Fragment
) : PermissionFramework {

    override fun requestPermissions(permissions: Array<out String>, requestCode: Int) {
        fragment.requestPermissions(permissions, requestCode)
    }

    override fun allGranted(permissions: Array<out String>): Boolean = permissions.all {
        isGranted(it)
    }

    override fun checkStatus(permissions: Array<out String>): PermissionStatus = getStatus(permissions) {
        isGranted(it)
    }

    override fun parseStatus(
        permissions: Array<out String>,
        grantResults: IntArray
    ): PermissionStatus = getStatus(permissions) {
        grantResults[permissions.indexOf(it)] == GRANTED
    }

    private fun isGranted(permission: String): Boolean = ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        permission
    ) == GRANTED

    private fun getStatus(
        permissions: Array<out String>,
        isGranted: (String) -> Boolean
    ): PermissionStatus {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val needRationale = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            if (isGranted(permission)) {
                granted += permission
            } else {
                denied += permission
            }
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                needRationale += permission
            }
        }
        return PermissionStatus(granted, denied, needRationale)
    }

    companion object {
        private const val GRANTED = PackageManager.PERMISSION_GRANTED
    }
}
