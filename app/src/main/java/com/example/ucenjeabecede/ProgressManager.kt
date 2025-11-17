package com.example.ucenjeabecede

import android.content.Context
import org.json.JSONObject
import java.io.File

data class Progress(val completedLetters: List<String>)

object ProgressManager {
    private const val FILE_NAME = "napredek.json"

    fun load(context: Context): Progress {
        val file = File(context.filesDir, FILE_NAME)
        return if (file.exists()) {
            val json = JSONObject(file.readText())
            val letters = mutableListOf<String>()
            val arr = json.optJSONArray("completedLetters")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    letters.add(arr.getString(i))
                }
            }
            Progress(letters)
        } else Progress(emptyList())
    }

    fun save(context: Context, progress: Progress) {
        val file = File(context.filesDir, FILE_NAME)
        val json = JSONObject()
        json.put("completedLetters", progress.completedLetters)
        file.writeText(json.toString())
    }
}
