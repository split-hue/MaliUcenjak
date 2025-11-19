package com.example.ucenjeabecede

import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun SpeechDialog(
    letter: String,
    onResult: (String) -> Unit,
    onClose: () -> Unit
) {
    var partialText by remember { mutableStateOf("") }
    var finalText by remember { mutableStateOf("") }
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    var startListening by remember { mutableStateOf(false) } // kontrola začetka poslušanja
    val listenDurationMillis = 5000L // koliko časa bo poslušal (5 sekund)

    // Inicializacija TTS
    LaunchedEffect(Unit) {
        //delay(300)
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setSpeechRate(0.8f)
                tts?.setPitch(1.0f)
                tts?.language = Locale("sl", "SI")
                tts?.speak("Črka $letter$letter$letter", TextToSpeech.QUEUE_FLUSH, null, "letterID")
            }
        }

        delay(1500) //čas pred mic-om
        startListening = true
    }

    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Button(onClick = onClose) { Text("X") }
        },
        title = { Text("Izgovori črko »$letter« 👂🏻") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Status sproti zaznanega govora
                Text(
                    text = if (partialText.isBlank()) "Poslušam..." else "Sproti: $partialText",
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Končni rezultat
                if (finalText.isNotBlank()) {
                    Text(
                        text = "Končno: $finalText",
                        color = Color.Black
                    )
                }

                // SpeechRecognizer se aktivira šele po startListening
                if (startListening) {
                    UseSpeechRecognizer(
                        onPartial = { txt -> partialText = txt },
                        onFinal = { txt ->
                            finalText = txt
                            onResult(txt)
                        },
                    )
                }
            }
        }
    )
}
