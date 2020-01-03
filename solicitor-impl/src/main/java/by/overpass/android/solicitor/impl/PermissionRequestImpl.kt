package by.overpass.android.solicitor.impl

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.overpass.android.solicitor.core.PermissionCallbacks
import by.overpass.android.solicitor.core.PermissionRequest
import by.overpass.android.solicitor.core.Permissions

internal class PermissionRequestImpl : Fragment(), PermissionRequest, PermissionCallbacks {

    override var onGranted: (Permissions) -> Unit = {}
    override var onDenied: (Permissions) -> Unit = {}
    override var onDeniedPermanently: (Permissions) -> Unit = {}
    override var onShouldShowRationale: (Permissions, () -> Unit) -> Unit =
        { permissions: Permissions, repeat: () -> Unit -> }

    private var showedRationale = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun request(vararg permissions: String, requestCode: Int) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == GRANTED
        }
        if (allGranted) {
            onGranted(permissions.asList())
        } else {
            val permissionsNeedingRationale = mutableListOf<String>()
            val grantedPermissions = mutableListOf<String>()
            val deniedPermissions = mutableListOf<String>()
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(requireContext(), it) == GRANTED) {
                    grantedPermissions += it
                } else {
                    deniedPermissions += it
                }
                if (shouldShowRequestPermissionRationale(it)) {
                    permissionsNeedingRationale += it
                }
            }

            if (permissionsNeedingRationale.isNotEmpty() && !showedRationale) {
                onShouldShowRationale(permissionsNeedingRationale) {
                    request(
                        permissions = *permissions,
                        requestCode = requestCode
                    )
                }
                showedRationale = true
            } else if (deniedPermissions.isNotEmpty()) {
                requestPermissions(
                    deniedPermissions.toTypedArray(),
                    requestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size != grantResults.size) return

        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val needRationale = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            val result = grantResults[index]
            if (result == GRANTED) {
                granted += permission
            } else {
                denied += permission
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                needRationale += permission
            }
        }

        val permanentlyDenied = denied - needRationale
        when {
            permanentlyDenied.isNotEmpty() -> {
                onDeniedPermanently(permanentlyDenied)
            }
            denied.isNotEmpty() -> {
                onDenied(denied)
            }
            granted.isNotEmpty() -> {
                onGranted(granted)
            }
        }
    }

    companion object {

        private const val TAG = "Solicitor_TAG"
        private const val GRANTED = PackageManager.PERMISSION_GRANTED

        internal fun create(
            fragmentManager: FragmentManager,
            func: PermissionCallbacks.() -> Unit
        ): PermissionRequestImpl = with(fragmentManager) {
            val existing = findFragmentByTag(TAG)
            if (existing == null || existing !is PermissionRequestImpl) {
                val solicitor = PermissionRequestImpl()
                beginTransaction()
                    .add(solicitor, TAG)
                    .commitNow()
                solicitor
            } else {
                existing
            }
        }.apply(func)
    }
}