package com.example.edunav

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import android.speech.tts.TextToSpeech
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import android.app.Activity
import java.util.*
import androidx.compose.runtime.Composable

class ClassActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("EduNavPrefs", Context.MODE_PRIVATE)
        val voiceSpeed = prefs.getFloat("voiceSpeed", 1.0f)
        val languageCode = prefs.getString("language", "en") ?: "en"
        val locale = when (languageCode) {
            "fr" -> Locale.FRENCH
            "sw" -> Locale("sw")
            "es" -> Locale("es")
            else -> Locale.ENGLISH
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = locale
                tts.setSpeechRate(voiceSpeed)
            }
        }

        val message = intent.getStringExtra("message") ?: "No message received"
        setContent {
            ClassScreen(message, this, tts)
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

@Composable
fun ClassScreen(message: String, activity: Activity, tts: TextToSpeech) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Class", fontSize = 24.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is your class activity screen.",
            fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            activity.setResult(Activity.RESULT_OK)
            activity.finish()
        }) {
            Text("Back", color = Color.White, fontSize = 24.sp)
        }
    }
}