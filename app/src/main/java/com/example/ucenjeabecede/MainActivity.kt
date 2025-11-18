package com.example.ucenjeabecede

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.ui.theme.UcenjeAbecedeTheme
import kotlinx.serialization.InternalSerializationApi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UcenjeAbecedeTheme {
                WelcomeScreen()
            }
        }
    }
}

@OptIn(InternalSerializationApi::class)
@Composable
fun WelcomeScreen() {
    val context = LocalContext.current

    var completedLetters by remember { mutableStateOf(listOf<String>()) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                "Dobrodošli v Mali Učenjak!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Kliknite spodnji gumb, da začnete risati črke.")
            Spacer(modifier = Modifier.height(32.dp))

            // Gumbi za začetek igre
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    val intent = Intent(context, MainMenuActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text("Začni igro")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))


        }
    }
}
