package by.overpass.android.solicitor.impl

import androidx.fragment.app.Fragment
import by.overpass.android.solicitor.core.PermissionFramework

/**
 * Basic configurations
 */
object Solicitor {

    /**
     * An alternative implementation of [PermissionFramework] can be used for some purposes,
     * for instance, testing. [permissionFrameworkFactory] should be set accordingly in such a case
     */
    var permissionFrameworkFactory: (Fragment) -> PermissionFramework =
        { FragmentPermissionFramework(it) }
}
