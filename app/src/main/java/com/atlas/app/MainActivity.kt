package com.atlas.app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.atlas.app.databinding.ActivityMainBinding
import com.atlas.app.TouchActivity
import com.atlas.app.ScreenActivity
import com.atlas.app.SystemActivity
import com.atlas.app.AIAgentActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbarAndDrawer()
        setupBottomNavigation()
        setupDrawerNavigation()
        setupCardClickListeners()
        setupOnBackPressed()
    }

    private fun setupToolbarAndDrawer() {
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.menu_home,
            R.string.menu_home
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    updateStatus("Mod: Ana Sayfa")
                    true
                }

                R.id.nav_control -> {
                    updateStatus("Mod: Kontrol Paneli")
                    true
                }

                R.id.nav_ai -> {
                    updateStatus("Mod: AI Agent")
                    true
                }

                R.id.nav_logs -> {
                    updateStatus("Mod: Log İzleyici")
                    true
                }

                else -> false
            }
        }
    }

    private fun setupDrawerNavigation() {
        binding.navView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.menu_settings -> {
                    startActivity(
                        Intent(this, SettingsActivity::class.java)
                    )
                }

                R.id.menu_apps -> {
                    startActivity(
                        Intent(this, AppsActivity::class.java)
                    )
                }

                R.id.menu_touch -> {
                    startActivity(Intent(this, TouchActivity::class.java))
                }

                R.id.menu_screen -> {
                    startActivity(Intent(this, ScreenActivity::class.java))
                }

                R.id.menu_system -> {
                    startActivity(Intent(this, SystemActivity::class.java))
                }

                R.id.menu_ai -> {
                    startActivity(Intent(this, AIAgentActivity::class.java))
                }

                else -> {
                    updateStatus("Menü: ${item.title}")
                }
            }

            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupCardClickListeners() {

        binding.cardApps.setOnClickListener {
            startActivity(
                Intent(this, AppsActivity::class.java)
            )
        }

        binding.cardTouch.setOnClickListener {
            startActivity(Intent(this, TouchActivity::class.java))
        }

        binding.cardScreen.setOnClickListener {
            startActivity(Intent(this, ScreenActivity::class.java))
        }

        binding.cardSystem.setOnClickListener {
            startActivity(Intent(this, SystemActivity::class.java))
        }

        binding.cardAi.setOnClickListener {
            startActivity(Intent(this, AIAgentActivity::class.java))
        }

        binding.cardTerminal.setOnClickListener {
            startActivity(
                Intent(this, TerminalActivity::class.java)
            )
        }
    }

    private fun updateStatus(message: String) {
        binding.tvStatusAndroid.text = message

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (
                        binding.drawerLayout.isDrawerOpen(
                            GravityCompat.START
                        )
                    ) {
                        binding.drawerLayout.closeDrawer(
                            GravityCompat.START
                        )
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }
}
