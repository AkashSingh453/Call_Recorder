package com.example.mousebadger

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File

class CallRecordingService : Service() {

    private var recorder: MediaRecorder? = null
    private val CHANNEL_ID = "CallRecorderService"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_STICKY
    }

    private fun startRecording() {
        val fileName = "call_recording_${System.currentTimeMillis()}.3gp"
        val fileUri = getRecordingFileUri(fileName)

        try {
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                // Set the file destination using the Uri path
                fileUri?.let { setOutputFile(it.path) }

                prepare()
                start()
            }
            Log.d("CallRecordingService", "Recording started: $fileUri")
        } catch (e: Exception) {
            Log.e("CallRecordingService", "Recording failed", e)
        }
    }

    private fun getRecordingFileUri(fileName: String): File? {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        // Ensure the directory exists
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Create a new file in the Downloads directory
        return File(directory, fileName)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Call Recorder Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Recording Service")
                .setContentText("Recording in progress")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
            Log.d("CallRecordingService", "Recording stopped")
        }
        recorder = null
    }

    override fun onBind(intent: Intent?): IBinder? = null
}


