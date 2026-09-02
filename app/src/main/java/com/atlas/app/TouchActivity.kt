package com.atlas.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TouchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "ATLAS TOUCH\n\nDokunmatik Otomasyon"
            textSize = 20f
            setPadding(32, 32, 32, 32)
        }

        setContentView(textView)
    }
}
