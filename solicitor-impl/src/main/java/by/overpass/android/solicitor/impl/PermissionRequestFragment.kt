package by.overpass.android.solicitor.impl

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.overpass.android.solicitor.core.PermissionCallbacks
import by.overpass.android.solicitor.core.PermissionFramework
import by.overpass.android.solicitor.core.PermissionRequestScope
import by.overpass.android.solicitor.core.Permissions

/**
 * A [PermissionRequestScope] implementation based on [Fragment]. Extending [Fragment] is convenient
 * because a [Fragment] has a lifecycle and can retain its instance across configuration changes
 */
internal class PermissionRequestFragment : Fragment(), PermissionCallbacks, PermissionRequestScope {

    override var onGranted: (Permissions) -> Unit = {}
    override var onDenied: (Permissions) -> Unit = {}
    override var onDeniedPermanently: (Permissions) -> Unit = {}
    override var onShouldShowRationale: (Permissions, () -> Unit) -> Unit =
        { permissions: Permissions, repeat: () -> Unit -> }

    override var permissionFramework: PermissionFramework = UninitializedPermissionFramework()
    override var showedRationale = false
    override var requestCode = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.apply {
            showedRationale = getBoolean(EXTRA_SHOWED_RATIONALE, false)
            requestCode = getInt(EXTRA_REQUEST_CODE, -1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.apply {
            putBoolean(EXTRA_SHOWED_RATIONALE, showedRationale)
            putInt(EXTRA_REQUEST_CODE, requestCode)
        }
        super.onSaveInstanceState(outState)
    }

    override fun request(requestCode: Int, permissions: Array<out String>) {
        this.requestCode = requestCode
        if (permissionFramework.allGranted(permissions)) {
            onGranted(permissions.asList())
        } else {
            val (_, denied, needRationale) = permissionFramework.checkStatus(permissions)

            if (needRationale.isNotEmpty() && !showedRationale) {
                onShouldShowRationale(needRationale) {
                    request(requestCode, permissions)
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

        val (granted, denied, needRationale) = permissionFramework.parseStatus(permissions, grantResults)

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

        private const val EXTRA_SHOWED_RATIONALE = "extra_showed_rationale"
        private const val EXTRA_REQUEST_CODE = "extra_request_code"
        private const val TAG = "Solicitor_TAG"

        internal fun create(
            fragmentManager: FragmentManager,
            func: PermissionCallbacks.() -> Unit
        ): PermissionRequestFragment = create({
            with(fragmentManager) {
                with(findFragmentByTag(TAG)) {
                    if (this == null || this !is PermissionRequestFragment) {
                        PermissionRequestFragment().also {
                            beginTransaction()
                                .add(it, TAG)
                                .commitNow()
                        }
                    } else this
                }
            }
        }, func)

        @VisibleForTesting
        internal fun create(
            createFragment: () -> PermissionRequestFragment,
            func: PermissionCallbacks.() -> Unit
        ): PermissionRequestFragment = createFragment()
            .apply {
                permissionFramework = Solicitor.permissionFrameworkFactory.invoke(this)
            }
            .apply(func)
    }
}
