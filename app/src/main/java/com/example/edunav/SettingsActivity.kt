package com.example.edunav

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

class SettingsActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
            }
        }
        setContent {
            SettingsScreen(this, tts)
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}

@Composable
fun SettingsScreen(activity: ComponentActivity, tts: TextToSpeech) {
    val sharedPreferences = activity.getSharedPreferences("EduNavPrefs", Context.MODE_PRIVATE)
    var voiceSpeed by remember { mutableStateOf(sharedPreferences.getFloat("voiceSpeed", 1.0f)) }
    var selectedLanguage by remember { mutableStateOf(sharedPreferences.getString("language", "en") ?: "en") }

    val languageOptions = listOf("en", "fr", "sw", "es")
    var expanded by remember { mutableStateOf(false) }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Voice Speed",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.clickable { speak("Voice Speed") }
        )
        Slider(
            value = voiceSpeed,
            onValueChange = {
                voiceSpeed = it
                speak("Voice speed $it")
            },
            valueRange = 0.5f..2.0f,
            steps = 3,
            modifier = Modifier.padding(8.dp)
        )
        Text(
            String.format("%.2f", voiceSpeed),
            color = Color.White,
            modifier = Modifier.clickable { speak("Current voice speed is ${String.format("%.2f", voiceSpeed)}") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Language",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.clickable { speak("Language") }
        )
        Box {
            Button(
                onClick = {
                    expanded = true
                    speak("Select language. Current: $selectedLanguage")
                }
            ) {
                Text(selectedLanguage, color = Color.White)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                languageOptions.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang) },
                        onClick = {
                            selectedLanguage = lang
                            expanded = false
                            speak("Language set to $lang")
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = {
            with(sharedPreferences.edit()) {
                putFloat("voiceSpeed", voiceSpeed)
                putString("language", selectedLanguage)
                apply()
            }
            speak("Settings saved")
        }) {
            Text("Save", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            speak("Back")
            activity.finish()
        }) {
            Text("Back", fontSize = 18.sp, color = Color.White)
        }
    }
}