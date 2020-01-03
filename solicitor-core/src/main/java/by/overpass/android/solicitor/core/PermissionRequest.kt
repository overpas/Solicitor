package by.overpass.android.solicitor.core

import kotlin.random.Random

interface PermissionRequest {
    fun request(vararg permissions: String, requestCode: Int = Random.nextBits(16))
}