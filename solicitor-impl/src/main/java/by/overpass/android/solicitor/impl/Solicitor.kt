package by.overpass.android.solicitor.impl

import androidx.fragment.app.Fragment
import by.overpass.android.solicitor.core.PermissionFramework

object Solicitor {

    var permissionFrameworkFactory: (Fragment) -> PermissionFramework =
        { FragmentPermissionFramework(it) }
}
