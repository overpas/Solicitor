package by.overpass.android.solicitor.impl

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import by.overpass.android.solicitor.core.PermissionCallbacks
import by.overpass.android.solicitor.core.PermissionRequest

fun AppCompatActivity.request(func: PermissionCallbacks.() -> Unit): Lazy<PermissionRequest> =
    lazy {
        PermissionRequestImpl.create(
            supportFragmentManager,
            func
        )
    }

fun Fragment.request(func: PermissionCallbacks.() -> Unit): Lazy<PermissionRequest> = lazy {
    PermissionRequestImpl.create(
        requireFragmentManager(),
        func
    )
}