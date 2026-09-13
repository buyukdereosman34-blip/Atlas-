package com.atlas.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.databinding.ActivityLogsBinding

class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Loglar"

        binding.btnClearLogs.setOnClickListener {
            binding.tvLogs.text = "Loglar temizlendi."
        }

        binding.btnRefreshLogs.setOnClickListener {
            binding.tvLogs.text = "Log görüntüleme hazır."
        }

        binding.tvLogs.text = "ATLAS Log Viewer hazır."
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
