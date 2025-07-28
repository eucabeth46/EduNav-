package com.example.edunav

import android.app.Activity
import android.content.Context
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.get(0)?.lowercase(Locale.ROOT) ?: ""
            handleVoiceCommand(spokenText)
        }
    }

    private val featureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            intent.putExtra("fromFeature", true)
        }
    }

    private fun handleVoiceCommand(command: String) {
        when {
            command.contains("class") -> {
                tts.speak("Navigating to Class", TextToSpeech.QUEUE_FLUSH, null, null)
                featureLauncher.launch(Intent(this, ClassActivity::class.java))
            }
            command.contains("transcribe") -> {
                tts.speak("Starting lecture transcription", TextToSpeech.QUEUE_FLUSH, null, null)
                featureLauncher.launch(Intent(this, TranscribeActivity::class.java))
            }
            command.contains("braille") -> {
                tts.speak("Opening Braille Tutorials", TextToSpeech.QUEUE_FLUSH, null, null)
                featureLauncher.launch(Intent(this, TutorialsActivity::class.java))
            }
            else -> {
                tts.speak(
                    "Sorry, I didn't understand the command",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command like 'open class'")
        }

        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val prefs = getSharedPreferences("EduNavPrefs", Context.MODE_PRIVATE)
                val speed = prefs.getFloat("voiceSpeed", 1.0f)
                val langCode = prefs.getString("language", "en")
                tts.language = Locale(langCode ?: "en")
                tts.setSpeechRate(speed)
                speakOnLaunch()
            }
        }

        setContent {
            EduNavApp(this)
        }
    }

    private fun speakOnLaunch() {
        val welcomeText =
            "Welcome to EduNav. Use the first button to navigate to Class, second button to Transcribe a lecture, and third button to access Braille tutorials."
        tts.speak(welcomeText, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun speakWelcomeBack() {
        val welcomeBackText = "Welcome back to the EduNav Home screen."
        tts.speak(welcomeBackText, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onResume() {
        super.onResume()
        if (intent.getBooleanExtra("fromFeature", false)) {
            speakWelcomeBack()
            intent.removeExtra("fromFeature")
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    @Composable
    fun EduNavApp(activity: ComponentActivity) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .semantics { contentDescription = "EduNav Home Screen" }
                .background(Color.Blue),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    tts.speak("Listening", TextToSpeech.QUEUE_FLUSH, null, null)
                    startListening()
                },
                modifier = Modifier.semantics { contentDescription = "Voice command" }
            ) {
                Text("🎤 Voice Command", color = Color.White, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    tts.speak("Navigating to Class", TextToSpeech.QUEUE_FLUSH, null, null)
                    val intent = Intent(activity, ClassActivity::class.java)
                    intent.putExtra("message", "Hello, Welcome to Class")
                    featureLauncher.launch(intent)
                },
                modifier = Modifier.semantics { contentDescription = "Navigate to class" }
            ) {
                Text(
                    text = "Navigate to Class",
                    color = Color.White,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    tts.speak(
                        "Starting lecture transcription",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                    val intent = Intent(activity, TranscribeActivity::class.java)
                    intent.putExtra("message", "Hello you are Transcribing to a lecture")
                    featureLauncher.launch(intent)
                },
                modifier = Modifier.semantics { contentDescription = "Start lecture transcription" }
            ) {
                Text("Transcribe to the lecture", color = Color.White, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    tts.speak("Opening Braille Tutorials", TextToSpeech.QUEUE_FLUSH, null, null)
                    val intent = Intent(activity, TutorialsActivity::class.java)
                    intent.putExtra("message", "Hello, Welcome to Braille Tutorials")
                    featureLauncher.launch(intent)
                },
                modifier = Modifier.semantics { contentDescription = "Open Braille tutorials" }
            ) {
                Text("Open Braille Tutorials", color = Color.White, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val intent = Intent(activity, SettingsActivity::class.java)
                    activity.startActivity(intent)
                },
                modifier = Modifier.semantics { contentDescription = "Open Settings" }
            ) {
                Text("Settings", color = Color.White, fontSize = 24.sp)
            }
        }
    }
}