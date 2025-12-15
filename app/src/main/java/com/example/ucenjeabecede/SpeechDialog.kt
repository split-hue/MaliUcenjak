package com.example.ucenjeabecede

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.URLEncoder

enum class SpeechPhase {
    PLAYING,
    COUNTDOWN,
    LISTENING,
    RESULT
}

@Composable
fun SpeechDialog(
    letter: String,
    onResult: (String) -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var finalText by remember { mutableStateOf("") }
    var startListening by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    var phase by remember { mutableStateOf(SpeechPhase.PLAYING) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var countdownNumber by remember { mutableStateOf(3) }

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Success/failure animation
    val resultScale by animateFloatAsState(
        targetValue = if (phase == SpeechPhase.RESULT) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "resultScale"
    )

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

                        // COUNTDOWN SE ZAČNE TAKOJ, MED TTS
                        phase = SpeechPhase.COUNTDOWN
                        coroutineScope.launch {
                            var number = 3
                            while (isPlaying && number > 0) {  // countdown teče le, dokler predvaja TTS
                                countdownNumber = number
                                delay(1000L)
                                number--
                            }
                        }

                    }
                    setOnCompletionListener {
                        Log.d("SpeechDialog", "Playback completed, starting countdown")
                        isPlaying = false
                        phase = SpeechPhase.COUNTDOWN
                    }

                    setOnCompletionListener {
                        Log.d("SpeechDialog", "Playback completed, start listening")

                        isPlaying = false
                        startListening = true
                        phase = SpeechPhase.LISTENING

                        coroutineScope.launch { // auto-stop po 3s
                            delay(3000L)
                            if (phase == SpeechPhase.LISTENING) {
                                isCorrect = false
                                phase = SpeechPhase.RESULT
                            }
                        }
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
                phase = SpeechPhase.COUNTDOWN
            }
        }

        onDispose {
            job.cancel()
            mediaPlayer?.release()
        }
    }

    Dialog(
        onDismissRequest = {
            if (phase == SpeechPhase.RESULT) {
                mediaPlayer?.release()
                if (isCorrect == true) {
                    SoundPlayer.playPop(context)
                    onNext()
                }
                onClose()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = phase == SpeechPhase.RESULT,
            dismissOnClickOutside = phase == SpeechPhase.RESULT
        )
    ) {
        Box( //popopopoo-up
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFF),// HAHA B)
                            Color(0xFFFFFF) // *nevidn* pop-up
                        )
                    )
                )
                .padding(48.dp)
                .then(
                    if (phase == SpeechPhase.RESULT) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            mediaPlayer?.release()
                            if (isCorrect == true) {
                                SoundPlayer.playPop(context)
                                onNext()
                            }
                            onClose()
                        }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                when (phase) {
                    SpeechPhase.PLAYING -> { //poslušanje izgovorjave
                        Box(modifier = Modifier
                            .size(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
//                            Icon( //se ne uporabla k začne kuj counter
//                                painter = painterResource(id = R.drawable.zvocnik),
//                                contentDescription = null,
//                                modifier = Modifier
//                                    .size(100.dp)
//                                    .scale(pulseScale),
//                                tint = Color.White
//                            )
                        }
//                        Text(
//                            text = "Poslušaj...",
//                            fontSize = 28.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            textAlign = TextAlign.Center
//                        )
                    }

                    SpeechPhase.COUNTDOWN -> { //ODŠTEVALNIK
                        Box( //velikost kvadratka
                            modifier = Modifier
                                .size(180.dp),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = countdownNumber.toString(),
                                fontSize = 120.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.scale(pulseScale)
                            )
                        }
//                        Text(
//                            text = "Pripravi se...",
//                            fontSize = 28.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            textAlign = TextAlign.Center
//                        )
                    }

                    SpeechPhase.LISTENING -> { // POSLUŠA
                        Box(
                            modifier = Modifier
                                .size(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.govori),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 20.dp) //eeee
                                    .size(200.dp)
                                    .scale(pulseScale),
                                tint = Color.White
                            )
                        }
//                        Text(
//                            text = "GOVORI!",
//                            fontSize = 40.sp,
//                            fontWeight = FontWeight.ExtraBold,
//                            color = Color(0xFFFFD700),
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.scale(pulseScale)
//                        )
                    }

                    SpeechPhase.RESULT -> { // REZULTAT
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .scale(resultScale),
                            contentAlignment = Alignment.Center
                        ) {
                            Box( // krog uzadi
                                modifier = Modifier
                                    .size(180.dp)
                                    .background(
                                        color = if (isCorrect == true)
                                            Color(0xFF4CAF50).copy(alpha = 0.3f)
                                        else
                                            Color(0xFFF44336).copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                            )
                            Icon(
                                painter = painterResource(
                                    id = if (isCorrect == true)
                                        R.drawable.pravilno
                                    else
                                        R.drawable.narobe
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(120.dp),
                                tint = Color.White
                            )
                        }
//                        Text(
//                            text = if (isCorrect == true) "Bravo!" else "Poskusi ponovno!",
//                            fontSize = 36.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            textAlign = TextAlign.Center
//                        )
//
//                        Text(
//                            text = if (isCorrect == true)
//                                "Klikni kjerkoli za naprej"
//                            else
//                                "Klikni kjerkoli za izhod",
//                            fontSize = 18.sp,
//                            color = Color.White.copy(alpha = 0.8f),
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.alpha(0.7f)
//                        )
                    }
                }

                if (startListening && phase == SpeechPhase.LISTENING) {
                    UseSpeechRecognizer(
                        onPartial = { },
                        onFinal = { spokenText ->
                            finalText = spokenText
                            val correct = spokenText.equals(letter, ignoreCase = true)
                            isCorrect = correct
                            onResult(spokenText)
                            phase = SpeechPhase.RESULT

                            // Play success/failure sound
                            if (correct) {
                                //SoundPlayer.playSuccess(context)
                            } else {
                                //SoundPlayer.playError(context)
                            }
                        }
                    )
                }
            }
        }
    }
}

// Govornik TTS downloader
fun downloadGovornikTTS(context: Context, letter: String, voice: String = "lars"): File {
    val base = "https://s1.govornik.eu/"

    Log.d("GovornikTTS", "Requesting TTS for letter: '$letter'")

    val textToSpeak = "    . Izgovori črkoo:     $letter."
    Log.d("GovornikTTS", "Text to speak: '$textToSpeak'")

    val encodedText = URLEncoder.encode(textToSpeak, "UTF-8")
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
    val file = File(context.cacheDir, "tts_${letter}_${System.currentTimeMillis()}.mp3")
    file.writeBytes(bytes)

    Log.d("GovornikTTS", "TTS file saved: ${file.absolutePath}, size: ${bytes.size} bytes")

    return file
}