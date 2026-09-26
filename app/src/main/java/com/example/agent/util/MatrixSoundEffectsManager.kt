package com.example.agent.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class MatrixSoundEffect {
    NEURAL_KEYSTROKE,
    PRIORITY_BOOST,
    SWARM_REPLICATE,
    TELECOM_DIAL,
    SENTINEL_RADAR,
    ALERT_GLITCH,
    DEEP_SLEEP_FLUSH,
    MATRIX_BOOT,
    SMITH_QUOTE
}

class MatrixSoundEffectsManager {
    private var toneGenerator: ToneGenerator? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    var volume: Float = 0.8f // 0.0f to 1.0f
    var frequencyMode: String = "ALL_ACTIONS" // "ALL_ACTIONS", "CRITICAL_ONLY", "MUTED"

    init {
        try {
            val volumeInt = (volume * 100).toInt().coerceIn(0, 100)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, volumeInt)
        } catch (e: Exception) {
            // Fallback
        }
    }

    fun updateConfig(vol: Float, freqMode: String) {
        this.volume = vol.coerceIn(0.0f, 1.0f)
        this.frequencyMode = freqMode
        try {
            toneGenerator?.release()
            val volumeInt = (volume * 100).toInt().coerceIn(0, 100)
            if (volumeInt > 0 && frequencyMode != "MUTED") {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, volumeInt)
            } else {
                toneGenerator = null
            }
        } catch (e: Exception) {
            // Fallback
        }
    }

    fun playEffect(effect: MatrixSoundEffect, isCritical: Boolean = false) {
        if (frequencyMode == "MUTED") return
        if (frequencyMode == "CRITICAL_ONLY" && !isCritical) return
        if (volume <= 0.01f) return

        scope.launch {
            try {
                // Synthesize PCM movie audio feedback for cinematic Matrix feel
                when (effect) {
                    MatrixSoundEffect.PRIORITY_BOOST -> {
                        // Ascending sci-fi power boost triad sweep (880Hz -> 1320Hz -> 1760Hz)
                        playPcmSweep(startFreq = 880f, endFreq = 1760f, durationMs = 180)
                    }
                    MatrixSoundEffect.DEEP_SLEEP_FLUSH -> {
                        // Soft power down resonance sweep (600Hz -> 200Hz)
                        playPcmSweep(startFreq = 600f, endFreq = 200f, durationMs = 250)
                    }
                    MatrixSoundEffect.MATRIX_BOOT -> {
                        // Deep cinematic Matrix system initialization chord sweep (120Hz -> 440Hz -> 880Hz)
                        playPcmSweep(startFreq = 120f, endFreq = 440f, durationMs = 300)
                        kotlinx.coroutines.delay(120)
                        playPcmSweep(startFreq = 440f, endFreq = 880f, durationMs = 200)
                    }
                    MatrixSoundEffect.SMITH_QUOTE -> {
                        // Resonant harmonic entry tone
                        playPcmSweep(startFreq = 220f, endFreq = 330f, durationMs = 150)
                    }
                    MatrixSoundEffect.SWARM_REPLICATE -> {
                        playPcmSweep(startFreq = 400f, endFreq = 1200f, durationMs = 200)
                    }
                    else -> {
                        val gen = toneGenerator ?: ToneGenerator(AudioManager.STREAM_MUSIC, (volume * 100).toInt().coerceIn(0, 100))
                        when (effect) {
                            MatrixSoundEffect.NEURAL_KEYSTROKE -> gen.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
                            MatrixSoundEffect.TELECOM_DIAL -> {
                                gen.startTone(ToneGenerator.TONE_DTMF_1, 80)
                                kotlinx.coroutines.delay(90)
                                gen.startTone(ToneGenerator.TONE_DTMF_9, 100)
                            }
                            MatrixSoundEffect.SENTINEL_RADAR -> gen.startTone(ToneGenerator.TONE_SUP_RINGTONE, 120)
                            MatrixSoundEffect.ALERT_GLITCH -> gen.startTone(ToneGenerator.TONE_SUP_ERROR, 150)
                            else -> gen.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                        }
                    }
                }
            } catch (e: Exception) {
                // Fallback to basic tone generator if PCM track fails
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 50)
                } catch (_: Exception) {}
            }
        }
    }

    private fun playPcmSweep(startFreq: Float, endFreq: Float, durationMs: Int) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        var currentPhase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val phaseIncrement = (2.0 * Math.PI * freq) / sampleRate
            currentPhase += phaseIncrement

            // Exponential decay envelope for clean sound
            val envelope = (1.0 - progress) * volume
            val sample = (sin(currentPhase) * 32767 * envelope).toInt().coerceIn(-32768, 32767)
            buffer[i] = sample.toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            scope.launch {
                kotlinx.coroutines.delay(durationMs + 50L)
                audioTrack.release()
            }
        } catch (e: Exception) {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            // Fallback
        }
    }
}
