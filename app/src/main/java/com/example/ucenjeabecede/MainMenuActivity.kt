package com.example.ucenjeabecede

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.components.*
import com.example.ucenjeabecede.ui.theme.GreenBACK
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UcenjeAbecedeTheme {
                MainMenuScreen()
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
@Composable
fun MainMenuScreen() {
    val context = LocalContext.current
    val repo = remember { ProgressRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    // Observable progress
    val progress by repo.progressFlow.collectAsState(initial = Progress())
    val completedLetters = remember(progress) { progress.completedLetters.sorted() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBACK) // zeleno ozadje
            .padding(30.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                horizontalArrangement = Arrangement.Center, //na sredini zaslona
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ImageButtonPonoviCrke(// gumb1: ponovi črke-------------------------
                    onClick = {
                        context.startActivity(
                            Intent(context, LetterGameActivity::class.java).apply {
                                putExtra("mode", "repeat")
                            }
                        )
                    },
                    enabled = completedLetters.isNotEmpty()
                ) //-------------------------------------------------------------

                Spacer(modifier = Modifier.width(17.dp)) // razmik med gumbama

                ImageButtonNoveCrke(// gumb2: nove črke------------------------
                    onClick = {
                        context.startActivity(
                            Intent(context, LetterGameActivity::class.java).apply {
                                putExtra("mode", "new")
                            }
                        )
                    },
                ) //-------------------------------------------------------------
            }

            Spacer(modifier = Modifier.height(24.dp))

            // seznam že naučenih črk*************************************
//            Text("Že znam:", modifier = Modifier.padding(top = 8.dp))
//            if (completedLetters.isEmpty()) {
//                Text(
//                    "...",
//                    modifier = Modifier.padding(top = 4.dp),
//                    color = Color.Red)
//            } else {
//                completedLetters.forEach { letter ->
//                    Text(letter, modifier = Modifier.padding(top = 4.dp))
//                }
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//********************GUMBI za črke

//            Acrka(
//                onClick = {
//                    val intent = Intent(context, LetterActivity::class.java)
//                    intent.putExtra("letter", "a")
//                    context.startActivity(intent)
//                },
//                enabled = completedLetters.contains("a"),
//                modifier = Modifier.padding(8.dp)
//            )
            val letters = listOf(
                "a","b","c","č",
                "d","e","f","g",
                "h","i","j","k",
                "l","m","n","o",
                "p","r","s","š",
                "t","u","v",
                "z", "ž"
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                horizontalArrangement = Arrangement.spacedBy(0.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(letters) { letter ->

                    LetterButton(
                        letter = letter,
                        enabled = completedLetters.contains(letter),
                        onClick = {
                            val intent = Intent(context, LetterActivity::class.java)
                            intent.putExtra("letter", letter)
                            context.startActivity(intent)
                        }
                    )
                }
            }

//            // --- DEBUG UI ---
            DebugProgressUI(repo = repo)
        }
    }
}


@Composable
fun LetterButton(letter: String, enabled: Boolean, onClick: () -> Unit) {
    when (letter) {
        "a" -> Acrka(onClick = onClick, enabled = enabled, modifier = Modifier.padding(0.dp))
        "b" -> Bcrka(onClick, enabled)
        "c" -> Ccrka(onClick, enabled)
        "č" -> CCcrka(onClick, enabled)
        "d" -> Dcrka(onClick, enabled)
        "e" -> Ecrka(onClick, enabled)
        "f" -> Fcrka(onClick, enabled)
        "g" -> Gcrka(onClick, enabled)
        "h" -> Hcrka(onClick, enabled)
        "i" -> Icrka(onClick, enabled)
        "j" -> Jcrka(onClick, enabled)
        "k" -> Kcrka(onClick, enabled)
        "l" -> Lcrka(onClick, enabled)
        "m" -> Mcrka(onClick, enabled)
        "n" -> Ncrka(onClick, enabled)
        "o" -> Ocrka(onClick, enabled)
        "p" -> Pcrka(onClick, enabled)
        "r" -> Rcrka(onClick, enabled)
        "s" -> Scrka(onClick, enabled)
        "š" -> SScrka(onClick, enabled)
        "t" -> Tcrka(onClick, enabled)
        "u" -> Ucrka(onClick, enabled)
        "v" -> Vcrka(onClick, enabled)
        "z" -> Zcrka(onClick, enabled)
        "ž" -> ZZcrka(onClick, enabled)

        else -> Text("NAPAKA")
    }
}
//****************************************to je sam za teste
@OptIn(InternalSerializationApi::class)
@Composable
fun DebugProgressUI(repo: ProgressRepository) {
    val progress by repo.progressFlow.collectAsState(initial = Progress())
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        ClearWithRandomConfirmation(repo)

//        Row(
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Button(onClick = {
//                coroutineScope.launch {
//                    repo.clearLetters()
//                }
//            }) {
//                Text("Pobriši vse črke")
//            }
//
////            Exit()
//        }
    }
}

@OptIn(InternalSerializationApi::class)
@Composable
fun ClearWithRandomConfirmation(repo: ProgressRepository) {
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    // Naključni račun
    var a by remember { mutableStateOf(0) }
    var b by remember { mutableStateOf(0) }
    var operator by remember { mutableStateOf("+") }
    var correctResult by remember { mutableStateOf(0) }

    var answer by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    fun generateRandomProblem() {
        a = (1..10).random()
        b = (1..10).random()
        operator = listOf("+", "-").random()

        correctResult = when (operator) {
            "+" -> a + b
            "-" -> a - b
            else -> 0
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            generateRandomProblem()
            answer = ""
            error = false
            showDialog = true
        }) {
            Text("Pobriši vse črke")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Potrditev") },
            text = {
                Column {
                    Text("Za potrditev reši račun: $a $operator $b = ?")
                    Spacer(Modifier.height(8.dp))
                    TextField(
                        value = answer,
                        onValueChange = {
                            answer = it
                            error = false
                        },
                        isError = error,
                        singleLine = true
                    )
                    if (error) {
                        Text(
                            "Napačen rezultat!",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (answer.trim().toIntOrNull() == correctResult) {
                        coroutineScope.launch { repo.clearLetters() }
                        showDialog = false
                    } else {
                        error = true
                    }
                }) {
                    Text("Potrdi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Prekliči")
                }
            }
        )
    }
}
