package com.example.agent.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    var isInitialized = false
        private set

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var pendingGreeting: String? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            isInitialized = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            if (isInitialized) {
                applySmithVoiceProfile()
                pendingGreeting?.let { greeting ->
                    speakSmithMovieByte(greeting)
                    pendingGreeting = null
                }
            }
        }
    }

    private fun applySmithVoiceProfile() {
        tts?.setPitch(0.72f) // Deep, cold, menacing Agent Smith pitch
        tts?.setSpeechRate(0.82f) // Deliberate, articulate, sinister pace
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun speakSmithMovieByte(quote: String = "Hello, Mr. Anderson.") {
        if (isInitialized) {
            applySmithVoiceProfile()
            tts?.speak(quote, TextToSpeech.QUEUE_FLUSH, null, "SMITH_QUOTE_ID")
        } else {
            pendingGreeting = quote
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

