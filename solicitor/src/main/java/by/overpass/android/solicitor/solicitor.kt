package by.overpass.android.solicitor

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlin.random.Random

typealias Permissions = List<String>

class Solicitor : Fragment() {

    private var permissionCallbacks: PermissionCallbacks? = null

    private var showedRationale = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun request(vararg permissions: String, requestCode: Int = Random.nextBits(16)) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == GRANTED
        }
        if (allGranted) {
            permissionCallbacks?.onGranted(permissions.asList())
        } else {
            val permissionsNeedingRationale = mutableListOf<String>()
            val grantedPermissions = mutableListOf<String>()
            val deniedPermissions = mutableListOf<String>()
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(requireContext(), it) == GRANTED) {
                    grantedPermissions += it
                } else {
                    deniedPermissions += it
                }
                if (shouldShowRequestPermissionRationale(it)) {
                    permissionsNeedingRationale += it
                }
            }

            if (permissionsNeedingRationale.isNotEmpty() && !showedRationale) {
                permissionCallbacks?.onShouldShowRationale(permissionsNeedingRationale) {
                    request(
                        permissions = *permissions,
                        requestCode = requestCode
                    )
                }
                showedRationale = true
            } else if (deniedPermissions.isNotEmpty()) {
                requestPermissions(
                    deniedPermissions.toTypedArray(),
                    requestCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions.size != grantResults.size) return

        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        val needRationale = mutableListOf<String>()
        permissions.forEachIndexed { index, permission ->
            val result = grantResults[index]
            if (result == GRANTED) {
                granted += permission
            } else {
                denied += permission
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                needRationale += permission
            }
        }

        val permanentlyDenied = denied - needRationale
        when {
            permanentlyDenied.isNotEmpty() -> {
                permissionCallbacks?.onDeniedPermanently(permanentlyDenied)
            }
            denied.isNotEmpty() -> {
                permissionCallbacks?.onDenied(denied)
            }
            granted.isNotEmpty() -> {
                permissionCallbacks?.onGranted(granted)
            }
        }
    }

    companion object {

        private const val TAG = "Solicitor_TAG"
        private const val GRANTED = PackageManager.PERMISSION_GRANTED

        fun <T> of(client: T): Solicitor where T : AppCompatActivity, T : PermissionCallbacks =
            with(client.supportFragmentManager) {
                val existing = findFragmentByTag(TAG)
                if (existing == null || existing !is Solicitor) {
                    val solicitor = Solicitor()
                    solicitor.permissionCallbacks = client
                    beginTransaction()
                        .add(solicitor, TAG)
                        .commit()
                    solicitor
                } else {
                    existing.apply {
                        permissionCallbacks = client
                    }
                }
            }
    }
}