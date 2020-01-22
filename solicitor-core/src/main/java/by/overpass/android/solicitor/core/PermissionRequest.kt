package by.overpass.android.solicitor.core

import kotlin.random.Random

interface PermissionRequest {

    fun request(requestCode: Int = Random.nextBits(MAX_BITS), permissions: Array<out String>)

    companion object {
        const val MAX_BITS = 16
    }
}
