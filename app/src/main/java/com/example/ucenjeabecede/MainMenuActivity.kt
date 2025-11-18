package com.example.ucenjeabecede

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Gumb 1: Ponovi črke
        Button(
            onClick = {
                context.startActivity(
                    Intent(context, LetterGameActivity::class.java).apply {
                        putExtra("mode", "repeat")
                    }
                )
            },
            modifier = Modifier.padding(vertical = 8.dp),
            enabled = completedLetters.isNotEmpty() // disabled if empty
        ) {
            Text("Ponovi črke")
        }

        if (completedLetters.isEmpty()) {
            Text("Ne poznam še nobene črke", modifier = Modifier.padding(top = 4.dp))
        }

        // Gumb 2: Nove črke
        Button(
            onClick = {
                context.startActivity(
                    Intent(context, LetterGameActivity::class.java).apply {
                        putExtra("mode", "new")
                    }
                )
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Nove črke")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Seznam že naučenih črk
        Text("Že znam:", modifier = Modifier.padding(top = 8.dp))
        if (completedLetters.isEmpty()) {
            Text("ne poznam še nobene črke", modifier = Modifier.padding(top = 4.dp))
        } else {
            completedLetters.forEach { letter ->
                Text(letter, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- DEBUG UI ---
        DebugProgressUI(repo = repo)
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
        Text("DEBUG: trenutni seznam naučenih črk:", modifier = Modifier.padding(bottom = 8.dp))
        progress.completedLetters.forEach { letter ->
            Text(letter)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                repo.setLetters(listOf("A","B")) // ročno nastavi test črke
            }
        }) {
            Text("Nastavi test črke A,B")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            coroutineScope.launch {
                repo.clearLetters() // pobriše vse
            }
        }) {
            Text("Pobriši vse črke")
        }
    }
}
