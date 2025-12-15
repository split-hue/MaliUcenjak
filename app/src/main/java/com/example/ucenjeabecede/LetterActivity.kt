package com.example.ucenjeabecede

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ucenjeabecede.components.Check
import com.example.ucenjeabecede.components.Erasor
import com.example.ucenjeabecede.components.Home
import com.example.ucenjeabecede.components.SoundLetter
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.coroutines.launch
import kotlin.math.min

class LetterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val letter = intent.getStringExtra("letter") ?: "a"

        setContent {
            UcenjeAbecedeTheme {
                SingleLetterGameScreenGL(letter = letter)
            }
        }
    }
}

@Composable
fun SingleLetterGameScreenGL(letter: String) {
    val context = LocalContext.current

    val segments = remember { mutableStateListOf<List<Offset>>() }
    val currentSegment = remember { mutableStateListOf<Offset>() }

    var matchPercent by remember { mutableStateOf(0f) }
    var letterPath by remember { mutableStateOf(Path()) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(letter, context) {
        try {
            val rawPath = loadSvgPath(context, "$letter.svg")
            if (rawPath.isEmpty) {
                errorMessage = "Could not load SVG for letter: $letter"
            } else {
                letterPath = normalizePathToCanvas(rawPath, Size(1080f, 1920f))
                errorMessage = ""
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { start ->
                        currentSegment.clear()
                        currentSegment.add(start)
                    },
                    onDrag = { change, _ ->
                        currentSegment.add(change.position)
                    },
                    onDragEnd = {
                        if (currentSegment.isNotEmpty()) segments.add(currentSegment.toList())
                    }
                )
            }
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!letterPath.isEmpty) {
                drawPath(
                    path = letterPath,
                    color = Color.LightGray.copy(alpha = 0.3f),
                    style = Fill
                )
            }
            val region = letterPath.toRegion()
            for (segment in segments) drawSegmentWithFeedback(segment, region)
            drawSegmentWithFeedback(currentSegment, region)
        }

        //-------------gumbi---------------
        Home(
            onClick = { context.startActivity(android.content.Intent(context, MainMenuActivity::class.java))
                SoundPlayer.playPop(context)}, //<<<< sound efekt
            modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
        )

        SoundLetter(
            onClick = {
                SoundPlayer.crka(context, letter)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp, bottom = 60.dp, end = 18.dp)
        )

        Check(
            onClick = {
                matchPercent = calculateMatchPercentInside(segments.flatten() + currentSegment, letterPath)
                if (matchPercent >= 70f && letter.isNotEmpty()) {
                    SoundPlayer.crka(context, letter)
                    context.startActivity(android.content.Intent(context, MainMenuActivity::class.java))
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp, bottom = 70.dp)
        )

        Erasor(
            onClick = {
                segments.clear()
                currentSegment.clear()
                matchPercent = 0f
                SoundPlayer.playPop(context) // bubbles
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp, bottom = 60.dp)
        )

        // --- info uspešnost
        Text(
            text = "${matchPercent.toInt()}%",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 35.dp, end = 30.dp)
        )





        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }
    }
}