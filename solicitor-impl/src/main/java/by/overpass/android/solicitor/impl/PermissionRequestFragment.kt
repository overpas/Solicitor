package by.overpass.android.solicitor.impl

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.overpass.android.solicitor.core.PermissionCallbacks
import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionRequest
import by.overpass.android.solicitor.core.Permissions

internal class PermissionRequestFragment : Fragment(), PermissionRequest, PermissionCallbacks {

    override var onGranted: (Permissions) -> Unit = {}
    override var onDenied: (Permissions) -> Unit = {}
    override var onDeniedPermanently: (Permissions) -> Unit = {}
    override var onShouldShowRationale: (Permissions, () -> Unit) -> Unit =
        { permissions: Permissions, repeat: () -> Unit -> }

    @VisibleForTesting
    internal lateinit var permissionFramework: PermissionFramework

    @VisibleForTesting
    internal var showedRationale = false
    @VisibleForTesting
    internal var requestCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun request(vararg permissions: String, requestCode: Int) {
        this.requestCode = requestCode
        if (permissionFramework.allGranted(permissions)) {
            onGranted(permissions.asList())
        } else {
            val (_, denied, needRationale) = permissionFramework.status(permissions)

            if (needRationale.isNotEmpty() && !showedRationale) {
                onShouldShowRationale(needRationale) {
                    request(
                        permissions = *permissions,
                        requestCode = requestCode
                    )
                }
                showedRationale = true
            } else if (denied.isNotEmpty()) {
                permissionFramework.requestPermissions(
                    denied.toTypedArray(),
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
        if (requestCode != this.requestCode || requestCode == -1) return

        val (granted, denied, needRationale) = permissionFramework.status(permissions, grantResults)

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

        internal fun create(
            fragmentManager: FragmentManager,
            func: PermissionCallbacks.() -> Unit
        ): PermissionRequestFragment = create({
            with(fragmentManager) {
                val existing = findFragmentByTag(TAG)
                if (existing == null || existing !is PermissionRequestFragment) {
                    val solicitor = PermissionRequestFragment()
                    beginTransaction()
                        .add(solicitor, TAG)
                        .commitNow()
                    solicitor
                } else {
                    existing
                }
            }
        }, func)

        @VisibleForTesting
        internal fun create(
            createFragment: () -> PermissionRequestFragment,
            func: PermissionCallbacks.() -> Unit
        ): PermissionRequestFragment = createFragment()
            .apply {
                permissionFramework = FragmentPermissionFramework(this)
            }
            .apply(func)
    }
}