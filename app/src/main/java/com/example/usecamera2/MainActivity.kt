package com.example.usecamera2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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

        var file: File = createFile()
        uri = FileProvider.getUriForFile(this, "com.example.usecamera2.myprovider", file)

        val cameraContract = ActivityResultContracts.TakePicture()
        val cameraCallback = CameraResults()
        cameraLauncher = registerForActivityResult(cameraContract, cameraCallback)

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
                    Log.e(MA, "no results or permission denied")
                }
            }
            permissionLauncher.launch(cameraPermission)
        }
    }

    private fun useCamera() {
        Log.d(MA, "inside useCamera")
        cameraLauncher.launch(uri)
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            val recipients = arrayOf("chostar@umd.edu")
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Hi from Android")
            putExtra(Intent.EXTRA_EMAIL, recipients)
            putExtra(Intent.EXTRA_TEXT, "Hello")
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(emailIntent, "Pick one"))
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

    fun createFile(): File {
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyMMdd_HHmmss")
        var timeStamp: String = sdf.format(Date())
        var dir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("Image$timeStamp", ".png", dir)
    }

    inner class CameraResults : ActivityResultCallback<Boolean> {
        override fun onActivityResult(result: Boolean?) {
            if (result != null && result == true) {
                Log.w(MA, "success")
                // picture has been saved into uri, use uri
                Log.d(MA, "uri is $uri")
                // convert uri to bitmap
                val source: ImageDecoder.Source = ImageDecoder.createSource(contentResolver, uri)
                val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
                // place bitmap into imageview
                imageView.setImageBitmap(bitmap)
                // send email
                sendEmail()
            } else {
                Log.e("MA", "failure to take picture")
            }
        }
    }
}