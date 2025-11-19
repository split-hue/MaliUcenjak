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

    // Observe DataStore progress
    val progress by repo.progressFlow.collectAsState(initial = Progress())

    // Sorted list of asset letters
    val assetLetters = remember {
        (context.assets.list("abeceda") ?: arrayOf("A")).map { it.removeSuffix(".svg") }.sorted()
    }

    // Current letter
    var currentLetter by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    // --- Update letterPath whenever currentLetter changes ---
    LaunchedEffect(currentLetter, context) {
        if (currentLetter.isNotEmpty()) {
            val rawPath = loadSvgPath(context, "$currentLetter.svg")
            letterPath = normalizePathToCanvas(rawPath, Size(1080f, 1920f))
        } else {
            letterPath = Path()
        }
    }

    // Load next letter
    fun loadNextLetter() {
        segments.clear()
        currentSegment.clear()
        matchPercent = 0f

        val learned = progress.completedLetters.toSet()
        currentLetter = when (mode) {
            "repeat" -> learned.randomOrNull() ?: ""
            "new" -> assetLetters.firstOrNull { it !in learned } ?: ""
            else -> ""
        }
    }

    // --- Load initial letter only once when screen opens ---
    LaunchedEffect(Unit) {
        // Wait for progress to be loaded
        repo.progressFlow.collect { loadedProgress ->
            if (!isInitialized) {
                isInitialized = true
                val learned = loadedProgress.completedLetters.toSet()
                currentLetter = when (mode) {
                    "repeat" -> learned.randomOrNull() ?: ""
                    "new" -> assetLetters.firstOrNull { it !in learned } ?: ""
                    else -> ""
                }
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

        // --- Canvas ---
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

        // --- Buttons ---
        Button(
            onClick = { context.startActivity(android.content.Intent(context, MainMenuActivity::class.java)) },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) { Text("Domov") }

        Button(
            onClick = {
                matchPercent = calculateMatchPercentInside(segments.flatten() + currentSegment, letterPath)
                if (matchPercent >= 100f && currentLetter.isNotEmpty()) {
                    if (currentLetter !in progress.completedLetters) {
                        coroutineScope.launch { repo.addLetter(currentLetter) }
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) { Text("Preveri") }

        Button(
            onClick = {
                segments.clear()
                currentSegment.clear()
                matchPercent = 0f
            },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) { Text("Pobriši") }

        val repeatEnabled = mode != "repeat" || progress.completedLetters.isNotEmpty()
        Button(
            onClick = { loadNextLetter() },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            enabled = repeatEnabled
        ) { Text("→") }

        // --- Info Texts ---
        Text(
            text = "Ujemanje: ${matchPercent.toInt()}%",
            modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
        )

        // Debug: show current letter
        Text(
            text = "Črka: $currentLetter",
            color = Color.Red,
            modifier = Modifier.align(Alignment.Center)
        )

        // Message if no learned letters in repeat mode
        if (mode == "repeat" && progress.completedLetters.isEmpty()) {
            Text("Ne poznam še nobene črke", modifier = Modifier.align(Alignment.Center))
        }
    }
}

// --- Drawing helpers ---
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
        val svgText = context.assets.open("abeceda/$fileName").bufferedReader().use { it.readText() }
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

    val dx = -bounds.left * scale + (canvasSize.width - bounds.width() * scale) / 2f
    val dy = -bounds.top * scale + (canvasSize.height - bounds.height() * scale) / 2f

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

    val androidPath = letterPath.asAndroidPath()
    val bounds = RectF()
    androidPath.computeBounds(bounds, true)

    // Če meje niso veljavne, vrni 0
    if (bounds.isEmpty || bounds.height() <= 0) return 0f

    val region = letterPath.toRegion()

    // Preveri če so VSE točke znotraj mej - če ne, ne more biti 100%
    var pointsOutside = 0
    var pointsInside = 0
    for (p in userPath) {
        if (region.contains(p.x.toInt(), p.y.toInt())) {
            pointsInside++
        } else {
            pointsOutside++
        }
    }

    // Če je več kot 5% točk izven mej, takoj vrni nižji %
    val outsidePercent = (pointsOutside.toFloat() / userPath.size) * 100f
    if (outsidePercent > 5f) {
        // Vrni maksimalno 95% če riše izven mej
        return (95f * (pointsInside.toFloat() / userPath.size)).coerceAtMost(95f)
    }

    // Grid spacing - preverjamo vsako vrstico (lahko prilagodiš)
    val rowSpacing = 5f
    val edgeTolerance = 80 // Toleranca na vrhu in dnu (v pixlih)
    val topY = bounds.top.toInt() + edgeTolerance
    val bottomY = bounds.bottom.toInt() - edgeTolerance

    // Najdi vse Y koordinate ki jih uporabnik narisal
    val userYCoords = userPath.map { it.y.toInt() }.toSet()

    // Preveri koliko vrstic je pokritih
    var totalRows = 0
    var coveredRows = 0

    var y = topY
    while (y <= bottomY) {
        // Preveri če ta vrstica seka črko (tj. ali ima črka vsaj en pixel v tej vrstici)
        var rowIntersectsLetter = false
        for (x in bounds.left.toInt()..bounds.right.toInt()) {
            if (region.contains(x, y)) {
                rowIntersectsLetter = true
                break
            }
        }

        if (rowIntersectsLetter) {
            totalRows++
            // Preveri če je uporabnik narisal v tej vrstici (z tolerance)
            val tolerance = rowSpacing.toInt() + 5
            val rowCovered = userYCoords.any { userY ->
                userY in (y - tolerance)..(y + tolerance)
            }
            if (rowCovered) coveredRows++
        }

        y += rowSpacing.toInt()
    }

    return if (totalRows > 0) {
        (coveredRows.toFloat() / totalRows) * 100f
    } else {
        0f
    }
}

fun Path.toRegion(): Region {
    val androidPath = this.asAndroidPath()
    val bounds = RectF()
    androidPath.computeBounds(bounds, true)
    val region = Region()
    region.setPath(androidPath, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
    return region
}