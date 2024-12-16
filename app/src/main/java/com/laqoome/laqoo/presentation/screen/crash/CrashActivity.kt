package com.laqoome.laqoo.presentation.screen.crash

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.laqoome.laqoo.MainActivity
import com.laqoome.laqoo.presentation.theme.laqooTheme

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 获取传递的崩溃日志
        val crashLog = intent.getStringExtra("crash_log") ?: "No crash log available"

        setContent {
            laqooTheme {
                CrashScreen(
                    crashLog = crashLog,
                    onRestartClick = {
                        finishAffinity()
                        startActivity(Intent(this@CrashActivity, MainActivity::class.java))
                    },
                )
            }
        }
    }
}