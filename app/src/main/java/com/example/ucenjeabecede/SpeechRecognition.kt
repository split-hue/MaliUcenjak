package com.example.ucenjeabecede

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

@Composable
fun UseSpeechRecognizer(
    listenSeconds: Long = 4000,
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,

) {
    val context = LocalContext.current
    val recognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val intent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "sl-SI")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
    }

    LaunchedEffect(Unit) {
        val listener = object : RecognitionListener {

            override fun onPartialResults(results: Bundle?) {
                val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = list?.getOrNull(0) ?: ""
                Log.d("SPEECH", "Partial: $text")
                onPartial(text)
            }

            override fun onResults(results: Bundle?) {
                val list = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = list?.getOrNull(0) ?: ""
                Log.d("SPEECH", "Final: $text")
                onFinal(text)
            }

            override fun onError(error: Int) {
                Log.d("SPEECH", "Error: $error")
                onFinal("")
            }

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SPEECH", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SPEECH", "User started talking")
            }

            override fun onRmsChanged(rms: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        }

        recognizer.setRecognitionListener(listener)
        recognizer.startListening(intent)

        delay(listenSeconds)
        recognizer.stopListening()
    }

    DisposableEffect(Unit) {
        onDispose {
            recognizer.destroy()
        }
    }
}
