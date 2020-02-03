package by.overpass.android.solicitor.core

typealias Permissions = List<String>

/**
 * Contains permissions that are [granted], [denied] or [needRationale]
 */
data class PermissionStatus(
    val granted: Permissions,
    val denied: Permissions,
    val needRationale: Permissions
)
