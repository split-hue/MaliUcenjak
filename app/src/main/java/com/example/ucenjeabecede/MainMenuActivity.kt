package com.example.ucenjeabecede


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
@kotlinx.serialization.InternalSerializationApi
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
@kotlinx.serialization.InternalSerializationApi
fun MainMenuScreen() {
    val context = LocalContext.current
    val repo = remember { ProgressRepository(context) }


// Collect the DataStore Flow as Compose state. Automatically updates when DataStore changes.
    val progress by repo.progressFlow.collectAsState(initial = Progress())
    val completedLetters = progress.completedLetters


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                context.startActivity(
                    Intent(context, LetterGameActivity::class.java).apply {
                        putExtra("mode", "repeat")
                    }
                )
            },
            modifier = Modifier.padding(vertical = 8.dp),
            enabled = completedLetters.isNotEmpty()
        ) {
            Text("Ponovi črke")
        }


        if (completedLetters.isEmpty()) {
            Text("Ne poznam še nobene črke", modifier = Modifier.padding(top = 4.dp))
        }


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


        Text("Že znam:")
        if (completedLetters.isEmpty()) {
            Text("ne poznam še nobene črke", modifier = Modifier.padding(top = 4.dp))
        } else {
            completedLetters.sorted().forEach { letter ->
                Text(letter, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}