package by.overpass.android.solicitor.impl

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import by.overpass.android.solicitor.core.PermissionCallbacks
import by.overpass.android.solicitor.core.PermissionRequest

/**
 * Provides a [lazy] implementation of [PermissionRequest] within an [AppCompatActivity] instance
 */
fun AppCompatActivity.request(func: PermissionCallbacks.() -> Unit): Lazy<PermissionRequest> =
    requestWithFragmentManager(supportFragmentManager, func)

/**
 * Provides a [lazy] implementation of [PermissionRequest] within an [Fragment] instance
 */
fun Fragment.request(func: PermissionCallbacks.() -> Unit): Lazy<PermissionRequest> =
    requestWithFragmentManager(requireFragmentManager(), func)

private fun requestWithFragmentManager(
    fragmentManager: FragmentManager,
    func: PermissionCallbacks.() -> Unit
) = lazy {
    PermissionRequestFragment.create(fragmentManager, func)
}
