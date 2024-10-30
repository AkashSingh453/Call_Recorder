package com.example.mousebadger

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

class OverlayPermissionActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
        setContent {
            PermissionPopup(
                onAllow = {
                    startCallRecordingService()
                    finish() // Close the popup
                },
                onDeny = {
                    finish() // Close the popup if denied
                }
            )
        }
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, 123)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startCallRecordingService() {
        val serviceIntent = Intent(this, CallRecordingService::class.java)
        startForegroundService(serviceIntent)
    }
}

@Composable
fun PermissionPopup(onAllow: () -> Unit, onDeny: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text("Allow Call Recording?") },
        text = { Text("This app needs permission to record your calls.") },
        confirmButton = {
            Button(onClick = onAllow) {
                Text("Allow")
            }
        },
        dismissButton = {
            Button(onClick = onDeny) {
                Text("Deny")
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}

