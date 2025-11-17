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

@Composable
fun MainMenuScreen() {
    val context = LocalContext.current

    // reactive stanje, vedno aktualno
    var completedLetters by remember { mutableStateOf(loadCompletedLetters(context)) }

    // osveži ob prikazu
    LaunchedEffect(Unit) {
        completedLetters = loadCompletedLetters(context)
    }

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
            enabled = completedLetters.isNotEmpty() // onemogočen, če ni naučenih črk
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
            completedLetters.sorted().forEach { letter ->
                Text(letter, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

// Funkcija za branje naučenih črk iz datoteke
fun loadCompletedLetters(context: android.content.Context): List<String> {
    return ProgressManager.load(context).completedLetters
}
