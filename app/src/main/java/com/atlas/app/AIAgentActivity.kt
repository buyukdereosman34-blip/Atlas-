package com.atlas.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AIAgentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "ATLAS AI AGENT\n\nAI Agent"
            textSize = 20f
            setPadding(32, 32, 32, 32)
        }

        setContentView(textView)
    }
}
