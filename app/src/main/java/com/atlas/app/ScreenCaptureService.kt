package com.atlas.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenCaptureService : Service() {

    companion object {
        const val ACTION_START = "com.atlas.app.action.SCREEN_CAPTURE_START"
        const val ACTION_STOP = "com.atlas.app.action.SCREEN_CAPTURE_STOP"

        const val EXTRA_RESULT_CODE = "result_code"
        const val EXTRA_RESULT_DATA = "result_data"

        const val ACTION_FRAME = "com.atlas.app.action.SCREEN_FRAME"

        const val CHANNEL_ID = "atlas_screen_capture"
        const val NOTIFICATION_ID = 4101

        @Volatile
        var instance: ScreenCaptureService? = null
            private set
    }

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    private var captureWidth = 0
    private var captureHeight = 0
    private var captureDensity = 0

    private var latestFrame: Bitmap? = null

    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            releaseCaptureResources()
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_START -> {
                val resultCode =
                    intent.getIntExtra(EXTRA_RESULT_CODE, -1)

                val resultData =
                    intent.getParcelableExtra<Intent>(EXTRA_RESULT_DATA)

                if (resultCode == -1 || resultData == null) {
                    stopSelf()
                    return START_NOT_STICKY
                }

                startCapture(resultCode, resultData)
            }

            ACTION_STOP -> {
                stopCapture()
            }
        }

        return START_NOT_STICKY
    }

    private fun startCapture(
        resultCode: Int,
        resultData: Intent
    ) {
        if (mediaProjection != null) {
            return
        }

        startForegroundNotification()

        val projectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE)
                    as MediaProjectionManager

        mediaProjection =
            projectionManager.getMediaProjection(
                resultCode,
                resultData
            )

        mediaProjection?.registerCallback(
            projectionCallback,
            null
        )

        val metrics = resources.displayMetrics

        captureWidth = metrics.widthPixels
        captureHeight = metrics.heightPixels
        captureDensity = metrics.densityDpi

        imageReader = ImageReader.newInstance(
            captureWidth,
            captureHeight,
            android.graphics.PixelFormat.RGBA_8888,
            2
        )

        virtualDisplay =
            mediaProjection?.createVirtualDisplay(
                "ATLAS-ScreenCapture",
                captureWidth,
                captureHeight,
                captureDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                null
            )

        imageReader?.setOnImageAvailableListener(
            { reader ->
                processLatestImage(reader)
            },
            null
        )
    }

    private fun processLatestImage(reader: ImageReader) {

        val image = try {
            reader.acquireLatestImage()
        } catch (_: Exception) {
            null
        } ?: return

        try {
            val plane = image.planes.firstOrNull()
                ?: return

            val buffer = plane.buffer
            val pixelStride = plane.pixelStride
            val rowStride = plane.rowStride
            val rowPadding =
                rowStride - pixelStride * captureWidth

            val bitmapWidth =
                captureWidth + rowPadding / pixelStride

            val bitmap = Bitmap.createBitmap(
                bitmapWidth,
                captureHeight,
                Bitmap.Config.ARGB_8888
            )

            bitmap.copyPixelsFromBuffer(buffer)

            val cropped =
                if (bitmapWidth != captureWidth) {
                    Bitmap.createBitmap(
                        bitmap,
                        0,
                        0,
                        captureWidth,
                        captureHeight
                    )
                } else {
                    bitmap
                }

            latestFrame?.recycle()
            latestFrame = cropped

            sendBroadcast(
                Intent(ACTION_FRAME).apply {
                    setPackage(packageName)
                }
            )

            if (cropped !== bitmap) {
                bitmap.recycle()
            }

        } finally {
            image.close()
        }
    }

    fun getLatestFrame(): Bitmap? {
        return latestFrame?.copy(
            Bitmap.Config.ARGB_8888,
            false
        )
    }

    private fun stopCapture() {
        releaseCaptureResources()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun releaseCaptureResources() {

        mediaProjection?.unregisterCallback(
            projectionCallback
        )

        virtualDisplay?.release()
        virtualDisplay = null

        imageReader?.close()
        imageReader = null

        latestFrame?.recycle()
        latestFrame = null

        mediaProjection?.stop()
        mediaProjection = null
    }

    private fun startForegroundNotification() {

        val notification: Notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setContentTitle("ATLAS ekran yakalama")
                .setContentText("Ekran yakalama aktif")
                .setSmallIcon(
                    android.R.drawable.ic_menu_view
                )
                .setOngoing(true)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT <
            Build.VERSION_CODES.O
        ) {
            return
        }

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "ATLAS Ekran Yakalama",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description =
                    "ATLAS ekran yakalama servisi"
            }

        val manager =
            getSystemService(
                NotificationManager::class.java
            )

        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        releaseCaptureResources()
        instance = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
