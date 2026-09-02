package com.atlas.app

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.databinding.ActivityAppDetailBinding

class AppDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val EXTRA_LABEL = "label"
        const val EXTRA_IS_SYSTEM = "is_system"
    }

    private lateinit var binding: ActivityAppDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAppDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadAppDetails()
        setupActions()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarAppDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Uygulama Detayı"
    }

    private fun loadAppDetails() {
        val packageName =
            intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return

        val label =
            intent.getStringExtra(EXTRA_LABEL) ?: packageName

        val isSystem =
            intent.getBooleanExtra(EXTRA_IS_SYSTEM, false)

        binding.tvAppName.text = label
        binding.tvPackageName.text = packageName

        binding.tvAppType.text =
            if (isSystem) {
                "Sistem uygulaması"
            } else {
                "Kullanıcı uygulaması"
            }

        try {
            val packageInfo =
                packageManager.getPackageInfo(
                    packageName,
                    0
                )

            binding.tvVersion.text =
                "Sürüm: ${packageInfo.versionName ?: "Bilinmiyor"}"

        } catch (exception: PackageManager.NameNotFoundException) {

            binding.tvVersion.text =
                "Sürüm: Bilinmiyor"
        }
    }

    private fun setupActions() {

        binding.btnOpenApp.setOnClickListener {

            val packageName =
                intent.getStringExtra(EXTRA_PACKAGE_NAME)

            if (packageName == null) {
                return@setOnClickListener
            }

            val launchIntent =
                packageManager.getLaunchIntentForPackage(
                    packageName
                )

            if (launchIntent != null) {

                startActivity(launchIntent)

            } else {

                Toast.makeText(
                    this,
                    "Bu uygulama doğrudan başlatılamıyor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
