package com.atlas.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.databinding.ActivitySettingsBinding
import com.atlas.app.shizuku.ShizukuManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var shizukuManager: ShizukuManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarSettings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        shizukuManager = ShizukuManager { status ->
            runOnUiThread {
                updateShizukuStatus(status)
            }
        }

        binding.btnShizukuPermission.setOnClickListener {
            shizukuManager.requestPermission()
        }

        shizukuManager.check()
    }

    private fun updateShizukuStatus(status: ShizukuManager.Status) {
        when (status) {
            ShizukuManager.Status.NOT_RUNNING -> {
                binding.tvShizukuStatus.text = "Shizuku: Çalışmıyor"
                binding.tvShizukuDetail.text =
                    "Shizuku servisi bulunamadı."
                binding.btnShizukuPermission.visibility =
                    android.view.View.GONE
            }

            ShizukuManager.Status.PERMISSION_REQUIRED -> {
                binding.tvShizukuStatus.text =
                    "Shizuku: Yetki gerekli"
                binding.tvShizukuDetail.text =
                    "Shizuku çalışıyor ancak ATLAS henüz yetkilendirilmemiş."
                binding.btnShizukuPermission.visibility =
                    android.view.View.VISIBLE
            }

            ShizukuManager.Status.PERMISSION_GRANTED -> {
                binding.tvShizukuStatus.text =
                    "Shizuku: Bağlı"
                binding.tvShizukuDetail.text =
                    "ATLAS Shizuku üzerinden Android sistem erişimine hazır."
                binding.btnShizukuPermission.visibility =
                    android.view.View.GONE
            }
        }
    }
}
