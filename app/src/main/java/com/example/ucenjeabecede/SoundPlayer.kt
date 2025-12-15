package com.example.ucenjeabecede

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

object SoundPlayer {
    fun playPop(context: Context) {
        val popSound = MediaPlayer.create(context, R.raw.bubble)
        popSound.setOnCompletionListener {
            it.release()
        }
        popSound.start()
    }
    fun playError(context: Context) {
        val popSound = MediaPlayer.create(context, R.raw.error)
        popSound.setOnCompletionListener {
            it.release()
        }
        popSound.start()
    }

    fun crka(context: Context, letter: String) {
        val crke = when (letter) {
            "š" -> "ss"
            "č" -> "cc"
            "ž" -> "zz"
            else -> letter
        }
        val resId = context.resources.getIdentifier(crke, "raw", context.packageName)

        if (resId == 0) {
            Log.e("crka", "Ga ni najdl '$crke' ne v raw/")
            return
        }

        val popSound = MediaPlayer.create(context, resId)
        popSound.setOnCompletionListener {
            it.release()
        }
        popSound.start()
    }

}