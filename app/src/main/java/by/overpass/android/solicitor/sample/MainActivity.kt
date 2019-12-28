package by.overpass.android.solicitor.sample

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import by.overpass.android.solicitor.PermissionCallbacks
import by.overpass.android.solicitor.Permissions
import by.overpass.android.solicitor.Solicitor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), PermissionCallbacks {

    private lateinit var solicitor: Solicitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        solicitor = Solicitor.of(this)
        textView.setOnClickListener {
            solicitor.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onGranted(permissions: Permissions) {
        Toast.makeText(
            this,
            "GRANTED: ${permissions.print()}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDenied(permissions: Permissions) {
        Toast.makeText(
            this,
            "DENIED: ${permissions.print()}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDeniedPermanently(permissions: Permissions) {
        AlertDialog.Builder(this)
            .setTitle("Permissions required")
            .setMessage("Permanently denied permissions: ${permissions.print()}")
            .setPositiveButton("GO TO SETTINGS") { a, b ->
                Intent()
                    .apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", packageName, null)
                    }
                    .let { startActivity(it) }
            }
            .create()
            .show()
    }

    override fun onShouldShowRationale(permissions: Permissions, repeat: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Rationale")
            .setMessage(
                "We need following permissions: ${permissions.print()}"
            )
            .setPositiveButton("OK") { a, b ->
                repeat()
            }
            .setNegativeButton("NO") { a, b ->
                Toast.makeText(
                    this,
                    "THE APP WILL NOT WORK WITHOUT PERMISSIONS",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .create()
            .show()
    }
}

fun Permissions.print(separator: String = ", "): String = reduce { acc, current ->
    "$acc$separator$current"
}
