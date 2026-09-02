package com.atlas.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.databinding.ActivitySystemBinding

class SystemActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySystemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySystemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupActions()
        loadSystemInfo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarSystem)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupActions() {
        binding.btnSystemRefresh.setOnClickListener {
            loadSystemInfo()
        }

        binding.btnSystemCopy.setOnClickListener {
            copySystemInfo()
        }
    }

    private fun loadSystemInfo() {

        val packageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            null
        }

        val appVersion =
            packageInfo?.versionName ?: "Bilinmiyor"

        binding.tvAndroidVersion.text =
            "Android ${Build.VERSION.RELEASE}"

        binding.tvAndroidSdk.text =
            "SDK sürümü: ${Build.VERSION.SDK_INT}"

        binding.tvAndroidCodename.text =
            "Kod adı: ${Build.VERSION.CODENAME}"

        binding.tvDeviceManufacturer.text =
            "${Build.MANUFACTURER} ${Build.BRAND}"

        binding.tvDeviceModel.text =
            "Model: ${Build.MODEL}"

        binding.tvDeviceProduct.text =
            "Ürün: ${Build.PRODUCT}"

        binding.tvDeviceName.text =
            "Cihaz: ${Build.DEVICE}"

        binding.tvBuildId.text =
            "Build ID: ${Build.ID}"

        binding.tvBuildHardware.text =
            "Donanım: ${Build.HARDWARE}"

        binding.tvBuildBootloader.text =
            "Bootloader: ${Build.BOOTLOADER}"

        binding.tvAtlasVersion.text =
            "Sürüm: $appVersion"

        binding.tvAtlasPackage.text =
            "Paket: $packageName"
    }

    private fun copySystemInfo() {

        val packageInfo = try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: Exception) {
            null
        }

        val appVersion =
            packageInfo?.versionName ?: "Bilinmiyor"

        val info = """
            ATLAS SYSTEM INFORMATION
            =========================

            ANDROID
            Android sürümü : ${Build.VERSION.RELEASE}
            SDK sürümü     : ${Build.VERSION.SDK_INT}
            Kod adı        : ${Build.VERSION.CODENAME}

            CİHAZ
            Üretici        : ${Build.MANUFACTURER}
            Marka          : ${Build.BRAND}
            Model          : ${Build.MODEL}
            Cihaz          : ${Build.DEVICE}
            Ürün           : ${Build.PRODUCT}

            BUILD
            Build ID       : ${Build.ID}
            Donanım        : ${Build.HARDWARE}
            Bootloader     : ${Build.BOOTLOADER}

            ATLAS
            Paket          : $packageName
            Sürüm          : $appVersion

            DURUM
            SystemActivity : ÇALIŞIYOR
        """.trimIndent()

        val clipboard =
            getSystemService(Context.CLIPBOARD_SERVICE)
                    as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(
                "ATLAS System Information",
                info
            )
        )

        Toast.makeText(
            this,
            "Sistem bilgileri kopyalandı",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
