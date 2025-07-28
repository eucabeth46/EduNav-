package com.example.edunav

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import java.util.Locale

class SplashActivity : AppCompatActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
            }
        }

        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Add a small delay before showing biometric prompt
                Handler(Looper.getMainLooper()).postDelayed({
                    showBiometricPrompt()
                }, 200)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric hardware", Toast.LENGTH_LONG).show()
                redirectToMain()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_LONG).show()
                redirectToMain()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No face or fingerprint registered", Toast.LENGTH_LONG).show()
                redirectToMain()
            }
        }
    }

    private fun redirectToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }




    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            == BiometricManager.BIOMETRIC_SUCCESS
        ) {
            val executor = ContextCompat.getMainExecutor(this)
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        speakWelcomeAndLaunch()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        tts.speak("Authentication failed. Exiting.", TextToSpeech.QUEUE_FLUSH, null, null)
                        finish()
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Face Unlock")
                .setSubtitle("Authenticate to access EduNav")
                .setNegativeButtonText("Cancel")
                .build()

            biometricPrompt.authenticate(promptInfo)
        } else {
            tts.speak("Biometric authentication not available.", TextToSpeech.QUEUE_FLUSH, null, null)
            finish()
        }
    }

    private fun speakWelcomeAndLaunch() {
        tts.speak(
            "Welcome to EduNav. Use the buttons on the screen: Navigate to Class, Transcribe a lecture, or access Braille tutorials.",
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
        // Wait a bit before launching
        tts.playSilentUtterance(1500, TextToSpeech.QUEUE_ADD, null)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}
