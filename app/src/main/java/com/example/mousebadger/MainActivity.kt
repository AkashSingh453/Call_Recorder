package com.example.mousebadger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

           requestPermissions()

//        setContent {
//            LaunchedEffect(Unit) {
//            if (!hasRequiredPermissions()) {
//                requestLocationPermissions()
//            } else {
//                startForegroundService()
//            }
//        }
//            Button(onClick = { startForegroundService() }) {
//                Text("Start Foreground Service")
//            }
//        }
    }


    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE
        )

        permissionLauncher.launch(permissions)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }
}


