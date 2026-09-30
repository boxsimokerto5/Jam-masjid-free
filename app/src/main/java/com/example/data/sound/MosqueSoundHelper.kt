package com.example.data.sound

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class MosqueSoundHelper(private val context: Context) {

  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 85)
    } catch (e: Exception) {
      Log.w("MosqueSoundHelper", "Could not init ToneGenerator: ${e.message}")
    }
  }

  fun playAdzanChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 400)
      vibrate(500)
    } catch (e: Exception) {
      Log.e("MosqueSoundHelper", "Error playing chime", e)
    }
  }

  fun playIqomahCountdownBeep() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 150)
      vibrate(150)
    } catch (e: Exception) {
      Log.e("MosqueSoundHelper", "Error playing beep", e)
    }
  }

  fun playFinalIqomahBell() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 1000)
      vibrate(1000)
    } catch (e: Exception) {
      Log.e("MosqueSoundHelper", "Error playing final bell", e)
    }
  }

  private fun vibrate(millis: Long) {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      if (vibrator != null && vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
          @Suppress("DEPRECATION")
          vibrator.vibrate(millis)
        }
      }
    } catch (e: Exception) {
      // Ignore if vibration not permitted
    }
  }

  fun release() {
    try {
      toneGenerator?.release()
      toneGenerator = null
    } catch (e: Exception) {
      // Ignore
    }
  }
}
