package com.atlas.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.databinding.ActivityControlBinding

class ControlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityControlBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityControlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Control Panel"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
