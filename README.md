# Solicitor
[![](https://jitpack.io/v/overpas/Solicitor.svg)](https://jitpack.io/#overpas/Solicitor) <br>
A small library helping to request android permissions as described [here](https://developer.android.com/training/permissions/requesting). It spares you the trouble of supplying a request code, handling the request results, etc. You are only required to implement the callbacks you need.
### Add
Add the JitPack repository in your root build.gradle at the end of repositories:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add the dependency
```
dependencies {
    implementation 'com.github.overpas.Solicitor:solicitor-impl:{version}'
}
```
### Use
```
class MainActivity : AppCompatActivity() {

    private val request by request {
        onGranted = {
            // The permissions have been granted
        }
        onDenied = {
            // The permissions have been denied
        }
        onDeniedPermanently = {
            // The permissions have been denied permanently. 
            // It's recommended to notify the user about it and explain
            // how to enable the permissions in settings
        }
        onShouldShowRationale = { permissions: Permissions, repeat: () -> Unit ->
            // The permissions need rationale. The user should be notified about it. 
            // Use repeat() to repeat the same request
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myButton.setOnClickListener {
            // Perform the permission request
            request.request(
                permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.SEND_SMS
                )
            )
        }
    }
}
```