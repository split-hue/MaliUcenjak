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
import androidx.compose.ui.unit.sp
import com.example.ucenjeabecede.components.Check
import com.example.ucenjeabecede.components.Erasor
import com.example.ucenjeabecede.components.Home
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlin.math.min
import java.text.Collator
import java.util.Locale

class LetterGameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1)
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

    val progress by repo.progressFlow.collectAsState(initial = Progress())

    val collator = Collator.getInstance(Locale("sl", "SI"))
    val assetLetters = remember {
        (context.assets.list("abeceda") ?: arrayOf("A"))
            .map { it.removeSuffix(".svg") }
            .sortedWith { a, b -> collator.compare(a, b) }
    }

    var currentLetter by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    var showSpeechDialog by remember { mutableStateOf(false) }
    var speechCorrect by remember { mutableStateOf<Boolean?>(null) }
    var lastSpoken by remember { mutableStateOf("") }

    LaunchedEffect(currentLetter, context) {
        if (currentLetter.isNotEmpty()) {
            SoundPlayer.crka(context, currentLetter)
            val rawPath = loadSvgPath(context, "$currentLetter.svg")
            letterPath = normalizePathToCanvas(rawPath, Size(1080f, 1920f))
        } else {
            letterPath = Path()
        }
    }

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
        //SoundPlayer.crka(context, currentLetter)
    }

    LaunchedEffect(Unit) {
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
            onClick = {
                context.startActivity(android.content.Intent(context, MainMenuActivity::class.java))
                SoundPlayer.playPop(context)},
            modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
        )

        Check(
            onClick = {
                matchPercent = calculateMatchPercentInside(segments.flatten() + currentSegment, letterPath)
                if (matchPercent >= 70f && currentLetter.isNotEmpty()) {
                    showSpeechDialog = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp, bottom = 70.dp)
        )

        Erasor(
            onClick = {
            segments.clear()
            currentSegment.clear()
            matchPercent = 0f
        },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp, bottom = 60.dp)
        )

        //gumb je zdej na pop-upu
//        val repeatEnabled = mode != "repeat" || progress.completedLetters.isNotEmpty()
//        Next(
//            onClick = { loadNextLetter() },
//            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp, bottom = 60.dp, end = 20.dp),
//            enabled = repeatEnabled
//        )



        // --- info uspešnost
        Text(
            text = "${matchPercent.toInt()}%",
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 35.dp, end = 30.dp)
        )
        //-- info --
//        Text(
//            text = "Črka: $currentLetter",
//            color = Color.Red,
//            modifier = Modifier.align(Alignment.Center)
//        )
        //????
//        if (mode == "repeat" && progress.completedLetters.isEmpty()) {
//            Text("Ne poznam še nobene črke", modifier = Modifier.align(Alignment.Center))
//        }
        if (showSpeechDialog) {
            SpeechDialog(
                letter = currentLetter,
                onResult = { spoken ->
                    lastSpoken = spoken
                    speechCorrect = spoken.equals(currentLetter, ignoreCase = true)
                    if (speechCorrect == true && currentLetter !in progress.completedLetters) {
                        coroutineScope.launch { repo.addLetter(currentLetter) }
                    }
                },
                onNext = { loadNextLetter() },
                onClose = { showSpeechDialog = false }
            )
        }
//        speechCorrect?.let { ok ->
//            Text(
//                text = "Govor: \"$lastSpoken\"",
//                color = Color.Magenta,
//                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
//            )
//        }

    }
}


// ---pomagači za risanje lmao
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

    if (bounds.isEmpty || bounds.height() <= 0) return 0f

    val region = letterPath.toRegion()

    // KORAK 1: Preveri da je VSE narisano ZNOTRAJ mej črke
    var outsideCount = 0
    for (point in userPath) {
        if (!region.contains(point.x.toInt(), point.y.toInt())) {
            outsideCount++
        }
    }

    // Če je več kot 10% izven mej -> takoj 0%
    val outsidePercent = (outsideCount.toFloat() / userPath.size) * 100f
    if (outsidePercent > 10f) {
        return 0f
    }

    // KORAK 2: Gledaj SAMO višino črke - ali so vse vrstice pokrite
    val edgeTolerance = 80  // Ne preverjamo prvih/zadnjih 80px
    val topY = bounds.top.toInt() + edgeTolerance
    val bottomY = bounds.bottom.toInt() - edgeTolerance

    if (bottomY <= topY) return 0f  // Če ni dovolj prostora

    // Zberi vse Y koordinate kjer je otrok risal
    val userYCoordinates = userPath.map { it.y.toInt() }.toSet()

    // Preveri vsako vrstico od topY do bottomY
    val rowHeight = 5  // Preverjamo vsako 5. vrstico
    val tolerance = 15  // Toleranca ± 15 pixlov

    var totalRowsNeeded = 0
    var rowsCovered = 0

    var currentY = topY
    while (currentY <= bottomY) {
        totalRowsNeeded++

        // Preveri če je otrok risal v tem območju (currentY ± tolerance)
        val hasCoverage = userYCoordinates.any { userY ->
            userY >= (currentY - tolerance) && userY <= (currentY + tolerance)
        }

        if (hasCoverage) {
            rowsCovered++
        }

        currentY += rowHeight
    }

    // Izračunaj odstotek pokritih vrstic
    return if (totalRowsNeeded > 0) {
        (rowsCovered.toFloat() / totalRowsNeeded) * 100f
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