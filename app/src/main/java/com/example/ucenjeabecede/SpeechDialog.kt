package com.example.ucenjeabecede

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.components.Next
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URLEncoder

@Composable
fun SpeechDialog(
    letter: String,
    onResult: (String) -> Unit,
    onNext: () -> Unit,  // Add this parameter
    onClose: () -> Unit
) {

    val context = LocalContext.current
    var partialText by remember { mutableStateOf("") }
    var finalText by remember { mutableStateOf("") }
    var startListening by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // IMPORTANT: Use DisposableEffect to clean up MediaPlayer
    DisposableEffect(letter) {
        val job = coroutineScope.launch {
            try {
                Log.d("SpeechDialog", "Downloading TTS for letter: $letter")
                val audioFile = withContext(Dispatchers.IO) {
                    downloadGovornikTTS(context, letter)
                }

                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFile.absolutePath)
                    setOnPreparedListener {
                        Log.d("SpeechDialog", "MediaPlayer prepared, starting playback")
                        it.start()
                    }
                    setOnCompletionListener {
                        Log.d("SpeechDialog", "Playback completed, starting speech recognition")
                        isPlaying = false
                        startListening = true
                    }
                    setOnErrorListener { _, what, extra ->
                        Toast.makeText(context, "Napaka TTS: $what/$extra", Toast.LENGTH_SHORT).show()
                        true
                    }
                    prepareAsync()
                }
            } catch (e: Exception) {
                Log.e("SpeechDialog", "Error loading TTS", e)
                Toast.makeText(context, "Napaka pri Govornik TTS: ${e.message}", Toast.LENGTH_LONG).show()
                isPlaying = false
                startListening = true
            }
        }

        onDispose {
            job.cancel()
            mediaPlayer?.release()
        }
    }

    AlertDialog(
        onDismissRequest = {
            mediaPlayer?.release()
            onClose()
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Next(
                    onClick = {
                        mediaPlayer?.release()
                        onNext()
                        onClose()
                })

//                Button(onClick = {
//                    mediaPlayer?.release()
//                    onClose()
//                }) {
//                    Text("X")
//                }
            }
        },
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Izgovori črko: ${letter.uppercase()}")
            }},
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isPlaying) {
                    Text("Predvajam zvok...", color = MaterialTheme.colorScheme.primary)
                } else {
                    Text("Poslušam...", color = MaterialTheme.colorScheme.secondary)
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (partialText.isBlank()) "" else "Sproti: $partialText",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (finalText.isNotBlank()) {
                    Text("Končno: $finalText", color = MaterialTheme.colorScheme.onSurface)
                }

                if (startListening) {
                    UseSpeechRecognizer(
                        onPartial = { partialText = it },
                        onFinal = {
                            finalText = it
                            onResult(it)
                        }
                    )
                }
            }
        }
    )
}

// Govornik TTS downloader
fun downloadGovornikTTS(context: Context, letter: String, voice: String = "lars"): File {
    val base = "https://s1.govornik.eu/"

    // Log to verify what letter we're requesting
    Log.d("GovornikTTS", "Requesting TTS for letter: '$letter'")

    val textToSpeak = "    . Izgovori črkoo:     $letter."
    Log.d("GovornikTTS", "Text to speak: '$textToSpeak'")

    // Properly encode the text for URL (use UTF-8 encoding)
    val encodedText = URLEncoder.encode(textToSpeak, "UTF-8")

    // Build URL with proper parameters
    val url = "${base}?voice=$voice&text=$encodedText&source=GovornikDemoApp&version=6&format=mp3"

    Log.d("GovornikTTS", "Full URL: $url")
    Log.d("GovornikTTS", "Encoded text: $encodedText")

    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0")
        .build()

    val response = client.newCall(request).execute()

    if (!response.isSuccessful) {
        val errorBody = response.body?.string() ?: "no error body"
        Log.e("GovornikTTS", "Error response: $errorBody")
        throw Exception("Neuspešen prenos TTS: ${response.code} - $errorBody")
    }

    val bytes = response.body?.bytes() ?: throw Exception("Prazna TTS datoteka")

    // Use unique filename for each letter to avoid caching issues
    val file = File(context.cacheDir, "tts_${letter}_${System.currentTimeMillis()}.mp3")
    file.writeBytes(bytes)

    Log.d("GovornikTTS", "TTS file saved: ${file.absolutePath}, size: ${bytes.size} bytes")

    return file
}