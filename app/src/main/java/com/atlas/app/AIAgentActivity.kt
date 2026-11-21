package com.atlas.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.ai.AIProviderBootstrap
import com.atlas.app.ai.AIProviderRouter
import com.atlas.app.policy.PolicyEngine
import com.atlas.app.permission.PermissionResolver
import com.atlas.app.permission.PermissionChecker
import com.atlas.app.runtime.*
import com.atlas.app.tool.ToolRegistry
import com.atlas.app.tool.control.ControlTool
import com.atlas.app.tool.apps.AppsTool
import com.atlas.app.tool.screen.ScreenTool
import java.util.UUID
import java.util.Properties

class AIAgentActivity : AppCompatActivity(), ApprovalManager.Listener {

    private lateinit var etTaskInput: EditText
    private lateinit var btnStartAgent: Button
    private lateinit var tvAgentStatus: TextView

    private val approvalManager = ApprovalManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_agent)

        etTaskInput = findViewById(R.id.etTaskInput)
        btnStartAgent = findViewById(R.id.btnStartAgent)
        tvAgentStatus = findViewById(R.id.tvAgentStatus)

        approvalManager.setListener(this)

        btnStartAgent.setOnClickListener {
            val task = etTaskInput.text.toString().trim()
            if (task.isNotEmpty()) {
                etTaskInput.text.clear()
                startAgentTask(task)
            }
        }
    }

    private fun startAgentTask(taskDescription: String) {
        appendLog("> Görev Alındı: $taskDescription")
        btnStartAgent.isEnabled = false
        appendLog("[SİSTEM] Ajan başlatılıyor...")

        Thread {
            try {
                loadAiConfigFromFile()

                val registry = AIProviderBootstrap.createRegistry()
                val router = AIProviderRouter(registry)
                val toolRegistry = ToolRegistry()

                // GERCEK ARACLARI KAYDET
                toolRegistry.register(ControlTool())
                toolRegistry.register(AppsTool(this@AIAgentActivity))
                toolRegistry.register(ScreenTool(this@AIAgentActivity))
                appendLog("[TOOLS] 3 arac kayit edildi: control, apps, screen")

                val executor = AtlasExecutor(
                    toolRegistry,
                    PolicyEngine(),
                    PermissionResolver(),
                    PermissionChecker()
                )

                val agentLoop = AgentLoop(
                    executor,
                    router,
                    approvalManager,
                    10,  // maxSteps
                    3    // maxRetries
                )

                val taskId = UUID.randomUUID().toString()

                val listener = object : AgentLoop.Listener {
                    override fun onProgress(taskId: String, step: Int, message: String) {
                        appendLog("[ADIM $step] $message")
                    }

                    override fun onToolCall(taskId: String, call: ToolCall, result: ToolResult) {
                        appendLog("[ARAÇ] ${call.toolName()}/${call.action()} -> ${result.isSuccess()}")
                    }

                    override fun onComplete(result: AgentLoop.LoopResult) {
                        appendLog("[BİTTİ] Durum: ${result.finalState}")
                        appendLog("[SONUÇ] ${result.finalOutput}")
                        runOnUiThread { btnStartAgent.isEnabled = true }
                    }
                }

                // GERÇEK ÇALIŞTIRMA!
                agentLoop.run(taskId, taskDescription, listener)

            } catch (e: Exception) {
                appendLog("[HATA] Ajan çalışırken hata: ${e.message}")
                runOnUiThread { btnStartAgent.isEnabled = true }
            }
        }.start()
    }

    override fun onApprovalRequired(
        taskId: String,
        call: ToolCall,
        response: ApprovalManager.ApprovalResponse
    ) {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("ATLAS Onay İstiyor")
                .setMessage("Görev ID: $taskId\nEylem: ${call.toolName()} / ${call.action()}")
                .setPositiveButton("Onayla (E)") { _, _ ->
                    appendLog("[ONAY] Onaylandı.")
                    response.respond(true)
                }
                .setNegativeButton("Reddet (H)") { _, _ ->
                    appendLog("[RED] Reddedildi.")
                    response.respond(false)
                }
                .setCancelable(false)
                .show()
        }
    }

    fun appendLog(message: String) {
        runOnUiThread {
            tvAgentStatus.append("$message\n")
        }
    }

    private fun loadAiConfigFromFile() {
        try {
            val file = java.io.File(filesDir, "atlas-ai-config.properties")
            if (!file.exists()) {
                appendLog("[CONFIG] atlas-ai-config.properties bulunamadi")
                return
            }
            val props = Properties()
            file.inputStream().use { props.load(it) }
            var count = 0
            for (key in props.stringPropertyNames()) {
                System.setProperty(key, props.getProperty(key))
                count++
            }
            appendLog("[CONFIG] $count AI ayari yuklendi")
        } catch (e: Exception) {
            appendLog("[CONFIG HATASI] ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        approvalManager.setListener(null)
    }
}
