package com.example.usecamera2

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

const val MA = "MainActivity"

class MainActivity : AppCompatActivity() {
    private var cameraPermission: String = Manifest.permission.CAMERA
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageView: ImageView
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)

        val permissionGranted: Int = ContextCompat.checkSelfPermission(this, cameraPermission)
        if (permissionGranted == PackageManager.PERMISSION_GRANTED) {
            useCamera()
        } else {
            // ask permission
            val permissionContract = ActivityResultContracts.RequestPermission()
            // val  permissionCallback = PermissionResults()
            permissionLauncher = registerForActivityResult(permissionContract) {
                if (it) {
                    useCamera()
                } else {
                    Log.d(MA, "no results or permission denied")
                }
            }
            permissionLauncher.launch(cameraPermission)
        }
    }

    fun useCamera() {
        Log.d(MA, "inside useCamera")
    }

    // inner class PermissionResults : ActivityResultCallback<Boolean> {
    //     override fun onActivityResult(result: Boolean?) {
    //         if (result != null && result == true) {
    //             useCamera()
    //         } else {
    //             Log.d(MA, "no results or permission denied")
    //         }
    //     }
    // }
}