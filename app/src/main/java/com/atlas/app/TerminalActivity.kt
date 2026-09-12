package com.atlas.app

import android.os.Bundle
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.atlas.app.command.CommandDispatcher
import com.atlas.app.command.ShellCommandExecutor
import com.atlas.app.databinding.ActivityTerminalBinding
import com.atlas.app.logging.AtlasLogger
import com.atlas.app.runtime.AtlasRuntime

class TerminalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminalBinding
    private lateinit var logger: AtlasLogger
    private lateinit var dispatcher: CommandDispatcher
    private lateinit var shell: ShellCommandExecutor
    private lateinit var runtime: AtlasRuntime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupTerminal()
        setupButtons()

        logger.system("ATLAS Terminal başlatıldı")
        binding.tvOutput.text = "ATLAS Terminal\n\n"
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Terminal"
    }

    private fun setupTerminal() {
        logger = AtlasLogger(
            object : AtlasLogger.Listener {
                override fun onLog(line: String) {
                    runOnUiThread {
                        appendOutput(line)
                    }
                }
            }
        )

        shell = ShellCommandExecutor(filesDir.absolutePath)

        runtime = AtlasRuntime(
            this,
            filesDir.absolutePath
        )

        dispatcher = CommandDispatcher(
            logger,
            object : CommandDispatcher.Output {
                override fun print(message: String) {
                    runOnUiThread {
                        if (message == "__CLEAR__") {
                            binding.tvOutput.text = ""
                        } else {
                            appendOutput(message)
                        }
                    }
                }
            },
            shell,
            runtime
        )
    }

    private fun setupButtons() {
        binding.btnExecute.setOnClickListener {
            executeCommand()
        }

        binding.etCommand.setOnEditorActionListener { _, _, _ ->
            executeCommand()
            true
        }

        binding.btnClear.setOnClickListener {
            binding.tvOutput.text = ""
            logger.system("Terminal çıktısı temizlendi")
        }
    }

    private fun executeCommand() {
        val command = binding.etCommand.text.toString()

        if (command.isBlank()) {
            return
        }

        appendOutput("> $command")
        binding.etCommand.text.clear()

        dispatcher.execute(command)
    }

    private fun appendOutput(message: String) {
        binding.tvOutput.append(message)
        binding.tvOutput.append("\n")

        binding.terminalScroll.post {
            binding.terminalScroll.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
