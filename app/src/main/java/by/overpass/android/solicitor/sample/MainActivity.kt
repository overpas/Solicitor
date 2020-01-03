package by.overpass.android.solicitor.sample

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import by.overpass.android.solicitor.core.Permissions
import by.overpass.android.solicitor.impl.request
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val request by request {
        onGranted = {
            Toast.makeText(
                this@MainActivity,
                "GRANTED: ${it.print()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        onDenied = {
            Toast.makeText(
                this@MainActivity,
                "DENIED: ${it.print()}",
                Toast.LENGTH_SHORT
            ).show()
        }
        onDeniedPermanently = {
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Permissions required")
                .setMessage("Permanently denied permissions: ${it.print()}")
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
        onShouldShowRationale = { permissions: Permissions, repeat: () -> Unit ->
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Rationale")
                .setMessage(
                    "We need following permissions: ${permissions.print()}"
                )
                .setPositiveButton("OK") { a, b ->
                    repeat()
                }
                .setNegativeButton("NO") { a, b ->
                    Toast.makeText(
                        this@MainActivity,
                        "THE APP WILL NOT WORK WITHOUT PERMISSIONS",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .create()
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView.setOnClickListener {
            request.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
}
