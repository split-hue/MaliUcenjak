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

    fun crka(context: Context, letter: String) {
        val resId = context.resources.getIdentifier(letter, "raw", context.packageName)

        if (resId == 0) {
            Log.e("crka", "Ga ni najdl '$letter' ne v raw/")
            return
        }

        val popSound = MediaPlayer.create(context, resId)
        popSound.setOnCompletionListener {
            it.release()
        }
        popSound.start()
    }

}