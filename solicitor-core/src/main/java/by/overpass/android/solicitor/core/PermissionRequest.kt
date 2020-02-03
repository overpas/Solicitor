package by.overpass.android.solicitor.core

import kotlin.random.Random

/**
 * Requesting permissions from a client's perspective
 */
interface PermissionRequest {

    /**
     * Requests [permissions] with [requestCode]
     */
    fun request(requestCode: Int = Random.nextBits(MAX_BITS), permissions: Array<out String>)

    companion object {
        /**
         * Max number of bits for random request code generation
         */
        const val MAX_BITS = 16
    }
}
