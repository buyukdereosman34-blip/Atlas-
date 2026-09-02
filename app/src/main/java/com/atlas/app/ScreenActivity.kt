package com.atlas.app

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.view.Surface
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.atlas.app.databinding.ActivityScreenBinding

class ScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScreenBinding

    private lateinit var projectionManager: MediaProjectionManager

    private var captureActive = false

    private val screenFrameReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action != ScreenCaptureService.ACTION_FRAME) {
                return
            }

            val frame =
                ScreenCaptureService.instance?.getLatestFrame()
                    ?: return

            showFrame(frame)
        }
    }

    private val projectionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode != Activity.RESULT_OK) {
                setCaptureStatus(
                    "Yakalama durumu: İZİN VERİLMEDİ"
                )

                binding.tvScreenPreviewStatus.text =
                    "Ekran yakalama izni verilmedi"

                return@registerForActivityResult
            }

            val resultData = result.data

            if (resultData == null) {
                setCaptureStatus(
                    "Yakalama durumu: VERİ ALINAMADI"
                )

                return@registerForActivityResult
            }

            startCaptureService(
                result.resultCode,
                resultData
            )
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityScreenBinding.inflate(layoutInflater)

        setContentView(binding.root)

        projectionManager =
            getSystemService(
                MEDIA_PROJECTION_SERVICE
            ) as MediaProjectionManager

        setupToolbar()
        setupActions()
        registerFrameReceiver()
        loadScreenInfo()
        updateCaptureUi()
    }

    private fun setupToolbar() {

        setSupportActionBar(
            binding.toolbarScreen
        )

        supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupActions() {

        binding.btnScreenRefresh.setOnClickListener {
            loadScreenInfo()
            updateCaptureUi()
        }

        binding.btnScreenCapture.setOnClickListener {

            if (captureActive) {
                stopCapture()
            } else {
                requestScreenCapturePermission()
            }
        }
    }

    private fun requestScreenCapturePermission() {

        if (captureActive) {
            return
        }

        setCaptureStatus(
            "Yakalama durumu: İZİN BEKLENİYOR"
        )

        binding.tvScreenPreviewStatus.text =
            "Android ekran yakalama izni bekleniyor..."

        val intent =
            projectionManager.createScreenCaptureIntent()

        projectionLauncher.launch(intent)
    }

    private fun startCaptureService(
        resultCode: Int,
        resultData: Intent
    ) {

        val serviceIntent =
            Intent(
                this,
                ScreenCaptureService::class.java
            ).apply {
                action =
                    ScreenCaptureService.ACTION_START

                putExtra(
                    ScreenCaptureService.EXTRA_RESULT_CODE,
                    resultCode
                )

                putExtra(
                    ScreenCaptureService.EXTRA_RESULT_DATA,
                    resultData
                )
            }

        try {

            ContextCompat.startForegroundService(
                this,
                serviceIntent
            )

            captureActive = true

            setCaptureStatus(
                "Yakalama durumu: AKTİF"
            )

            binding.tvScreenPreviewStatus.text =
                "İlk ekran karesi bekleniyor..."

            binding.btnScreenCapture.text =
                "Yakalamayı Durdur"

            binding.tvScreenAtlasStatus.text =
                "ScreenActivity: YAKALAMA AKTİF"

        } catch (exception: Exception) {

            captureActive = false

            setCaptureStatus(
                "Yakalama durumu: BAŞLATILAMADI"
            )

            binding.tvScreenPreviewStatus.text =
                "Yakalama servisi başlatılamadı"

            Toast.makeText(
                this,
                "Ekran yakalama başlatılamadı: ${exception.message}",
                Toast.LENGTH_LONG
            ).show()

            updateCaptureUi()
        }
    }

    private fun stopCapture() {

        val serviceIntent =
            Intent(
                this,
                ScreenCaptureService::class.java
            ).apply {
                action =
                    ScreenCaptureService.ACTION_STOP
            }

        try {
            startService(serviceIntent)
        } catch (_: Exception) {
        }

        captureActive = false

        binding.ivScreenPreview.setImageDrawable(null)
        binding.ivScreenPreview.visibility =
            android.view.View.GONE

        binding.tvScreenPreviewStatus.visibility =
            android.view.View.VISIBLE

        binding.tvScreenPreviewStatus.text =
            "Ekran yakalama durduruldu"

        binding.tvScreenFrameStatus.text =
            "Son kare: Yok"

        setCaptureStatus(
            "Yakalama durumu: DURDURULDU"
        )

        binding.tvScreenAtlasStatus.text =
            "ScreenActivity: ÇALIŞIYOR"

        updateCaptureUi()
    }

    private fun showFrame(frame: Bitmap) {

        if (isFinishing || isDestroyed) {
            frame.recycle()
            return
        }

        binding.ivScreenPreview.setImageBitmap(frame)

        binding.ivScreenPreview.visibility =
            android.view.View.VISIBLE

        binding.tvScreenPreviewStatus.visibility =
            android.view.View.GONE

        binding.tvScreenFrameStatus.text =
            "Son kare: ${frame.width} × ${frame.height} px"

        setCaptureStatus(
            "Yakalama durumu: AKTİF"
        )
    }

    private fun setCaptureStatus(
        text: String
    ) {
        binding.tvScreenCaptureStatus.text =
            text
    }

    private fun updateCaptureUi() {

        binding.btnScreenCapture.text =
            if (captureActive) {
                "Yakalamayı Durdur"
            } else {
                "Ekran Yakala"
            }
    }

    private fun registerFrameReceiver() {

        val filter =
            IntentFilter(
                ScreenCaptureService.ACTION_FRAME
            )

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            registerReceiver(
                screenFrameReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )

        } else {

            @Suppress("DEPRECATION")
            registerReceiver(
                screenFrameReceiver,
                filter
            )
        }
    }

    private fun loadScreenInfo() {

        val displayMetrics =
            resources.displayMetrics

        val width =
            displayMetrics.widthPixels

        val height =
            displayMetrics.heightPixels

        val density =
            displayMetrics.density

        val dpi =
            displayMetrics.densityDpi

        binding.tvScreenResolution.text =
            "Çözünürlük: ${width} × ${height} px"

        binding.tvScreenDensity.text =
            "Yoğunluk: %.2f".format(density)

        binding.tvScreenDpi.text =
            "DPI: $dpi"

        val windowManager =
            getSystemService(WINDOW_SERVICE)
                    as WindowManager

        val display =
            if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.R
            ) {
                display
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay
            }

        val refreshRate =
            display?.refreshRate ?: 0f

        binding.tvScreenRefreshRate.text =
            "Yenileme hızı: %.2f Hz".format(
                refreshRate
            )

        val rotation =
            display?.rotation
                ?: Surface.ROTATION_0

        val orientation =
            when (rotation) {

                Surface.ROTATION_0,
                Surface.ROTATION_180 ->
                    "Dikey"

                Surface.ROTATION_90,
                Surface.ROTATION_270 ->
                    "Yatay"

                else ->
                    "Bilinmiyor"
            }

        binding.tvScreenOrientation.text =
            "Yön: $orientation"

        binding.tvScreenState.text =
            "Ekran durumu: AKTİF"

        if (!captureActive) {
            binding.tvScreenAtlasStatus.text =
                "ScreenActivity: ÇALIŞIYOR"
        }
    }

    override fun onResume() {
        super.onResume()

        if (::binding.isInitialized) {
            loadScreenInfo()
        }
    }

    override fun onDestroy() {

        try {
            unregisterReceiver(
                screenFrameReceiver
            )
        } catch (_: Exception) {
        }

        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {

        finish()

        return true
    }
}
