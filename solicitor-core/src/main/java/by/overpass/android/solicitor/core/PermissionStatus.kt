package by.overpass.android.solicitor.core

typealias Permissions = List<String>

data class PermissionStatus(
    val granted: Permissions,
    val denied: Permissions,
    val needRationale: Permissions
)
