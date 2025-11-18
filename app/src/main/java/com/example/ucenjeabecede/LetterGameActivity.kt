package com.example.ucenjeabecede

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.Region
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlin.math.min

class LetterGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val mode = intent.getStringExtra("mode") ?: "repeat"

        setContent {
            UcenjeAbecedeTheme {
                LetterGameScreen(mode = mode)
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
@Composable
fun LetterGameScreen(mode: String) {
    val context = LocalContext.current
    val repo = remember { ProgressRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val segments = remember { mutableStateListOf<List<Offset>>() }
    val currentSegment = remember { mutableStateListOf<Offset>() }

    var matchPercent by remember { mutableStateOf(0f) }
    var letterPath by remember { mutableStateOf(Path()) }
    var currentLetter by remember { mutableStateOf("") }

    // Observe DataStore progress
    val progress by repo.progressFlow.collectAsState(initial = Progress())
    val completedLetters = remember(progress) { progress.completedLetters.toMutableList() }

    val assetLetters = remember {
        (context.assets.list("abeceda") ?: arrayOf("A")).map { it.removeSuffix(".svg") }
    }

    var newModeIndex by remember { mutableStateOf(0) }

    // Pick starting letter
    LaunchedEffect(mode, progress) {
        when (mode) {
            "repeat" -> {
                currentLetter =
                    if (completedLetters.isNotEmpty()) completedLetters.random() else ""
            }
            "new" -> {
                val sorted = assetLetters.sorted()
                newModeIndex = sorted.indexOfFirst { it !in completedLetters }
                if (newModeIndex == -1) newModeIndex = 0
                currentLetter = sorted.getOrNull(newModeIndex) ?: ""
            }
        }
    }

    fun loadNextLetter() {
        segments.clear()
        currentSegment.clear()
        matchPercent = 0f
        letterPath = Path()

        when (mode) {
            "repeat" -> {
                currentLetter =
                    if (completedLetters.isNotEmpty()) completedLetters.random() else ""
            }
            "new" -> {
                val sorted = assetLetters.sorted()
                if (newModeIndex < sorted.lastIndex) newModeIndex++
                currentLetter = sorted.getOrNull(newModeIndex) ?: ""
            }
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

        // Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (letterPath.isEmpty && currentLetter.isNotEmpty()) {
                val rawPath = loadSvgPath(context, "$currentLetter.svg")
                letterPath = normalizePathToCanvas(rawPath, size)
            }

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

        // DOMOV button
        Button(
            onClick = {
                context.startActivity(
                    android.content.Intent(context, MainMenuActivity::class.java)
                )
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Text("Domov")
        }

        // PREVERI button
        Button(
            onClick = {
                matchPercent = calculateMatchPercentInside(
                    segments.flatten() + currentSegment,
                    letterPath
                )
                if (matchPercent >= 100f && currentLetter.isNotEmpty()) {
                    coroutineScope.launch {
                        repo.addLetter(currentLetter)
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Preveri")
        }

        // POBRIŠI button
        Button(
            onClick = {
                segments.clear()
                currentSegment.clear()
                matchPercent = 0f
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Text("Pobriši")
        }

        // NEXT LETTER button
        val repeatButtonEnabled = mode != "repeat" || completedLetters.isNotEmpty()
        Button(
            onClick = { loadNextLetter() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            enabled = repeatButtonEnabled
        ) {
            Text("→")
        }

        // Matching %
        Text(
            text = "Ujemanje: ${matchPercent.toInt()}%",
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        )

        // No letters message
        if (mode == "repeat" && completedLetters.isEmpty()) {
            Text(
                "Ne poznam še nobene črke",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Drawing helpers
fun DrawScope.drawSegmentWithFeedback(segment: List<Offset>, region: Region) {
    if (segment.size > 1) {
        for (i in 0 until segment.size - 1) {
            val start = segment[i]
            val end = segment[i + 1]
            val color = if (region.contains(start.x.toInt(), start.y.toInt()) &&
                region.contains(end.x.toInt(), end.y.toInt())
            ) Color.Green else Color.Red
            drawLine(color = color, start = start, end = end, strokeWidth = 20f)
        }
    }
}

fun loadSvgPath(context: Context, fileName: String): Path {
    return try {
        val svgText =
            context.assets.open("abeceda/$fileName").bufferedReader().use { it.readText() }

        val regex = Regex("""<path[^>]*d="([^"]+)"""")
        val matches = regex.findAll(svgText)
        val combinedPath = Path()
        for (match in matches) {
            val svgPathString = match.groupValues[1]
            val path = PathParser().parsePathString(svgPathString).toPath()
            combinedPath.addPath(path)
        }
        combinedPath
    } catch (e: Exception) {
        e.printStackTrace()
        Path()
    }
}

fun normalizePathToCanvas(path: Path, canvasSize: Size, padding: Float = 50f): Path {
    val androidPath = path.asAndroidPath()
    val bounds = RectF()
    androidPath.computeBounds(bounds, true)

    val scaleX = (canvasSize.width - 2 * padding) / bounds.width()
    val scaleY = (canvasSize.height - 2 * padding) / bounds.height()
    val scale = min(scaleX, scaleY)

    val dx =
        -bounds.left * scale + (canvasSize.width - bounds.width() * scale) / 2f
    val dy =
        -bounds.top * scale + (canvasSize.height - bounds.height() * scale) / 2f

    val matrix = Matrix().apply {
        setScale(scale, scale)
        postTranslate(dx, dy)
    }

    val scaledPath = android.graphics.Path()
    scaledPath.addPath(androidPath, matrix)

    val composePath = Path()
    composePath.addPath(Path().apply { asAndroidPath().set(scaledPath) })
    return composePath
}

fun calculateMatchPercentInside(userPath: List<Offset>, letterPath: Path): Float {
    if (userPath.isEmpty()) return 0f
    val region = letterPath.toRegion()
    var insideCount = 0
    for (p in userPath) if (region.contains(p.x.toInt(), p.y.toInt())) insideCount++
    return insideCount.toFloat() / userPath.size * 100f
}

fun Path.toRegion(): Region {
    val androidPath = this.asAndroidPath()
    val bounds = RectF()
    androidPath.computeBounds(bounds, true)
    val region = Region()
    region.setPath(
        androidPath,
        Region(
            bounds.left.toInt(),
            bounds.top.toInt(),
            bounds.right.toInt(),
            bounds.bottom.toInt()
        )
    )
    return region
}
