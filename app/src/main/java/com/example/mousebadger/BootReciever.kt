package com.example.mousebadger

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters

//class CallReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
//        requestBatteryOptimizationExemption(context)
//        if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
//            // Call is answered
//            Log.d("CallReceiver", "Call answered")
//            val workRequest = OneTimeWorkRequestBuilder<CallRecordingWorker>()
//                .build()
//            WorkManager.getInstance(context).enqueue(workRequest)
//        } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
//            // Call ended
//            Log.d("CallReceiver", "Call ended")
//            // Stop the recording service if needed
//            val serviceIntent = Intent(context, CallRecordingService::class.java)
//            context.stopService(serviceIntent)
//        }
//    }
//}

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

        if (state == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            // Call answered: Show the popup to ask permission
            Log.d("CallReceiver", "Call answered, showing permission popup...")

            val popupIntent = Intent(context, OverlayPermissionActivity::class.java)
            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(popupIntent)
        }
    }
}

fun requestBatteryOptimizationExemption(context: Context) {
    val powerManager = context.getSystemService(PowerManager::class.java)
    if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
    }
}



class CallRecordingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            // Start the foreground service
            val serviceIntent = Intent(applicationContext, CallRecordingService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(serviceIntent)
            }
            return Result.success()
        } catch (e: Exception) {
            // Handle exceptions
            return Result.failure()
        }
    }
}