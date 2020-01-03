package by.overpass.android.solicitor.sample

import by.overpass.android.solicitor.core.Permissions

fun Permissions.print(separator: String = ", "): String = reduce { acc, current ->
    "$acc$separator$current"
}