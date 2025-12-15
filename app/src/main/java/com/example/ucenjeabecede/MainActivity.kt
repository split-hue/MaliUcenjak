package com.example.ucenjeabecede

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.ui.theme.*
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

    LaunchedEffect(Unit) {
        val player = MediaPlayer.create(context, R.raw.matija1)
        player.setOnCompletionListener { it.release() }
        player.start()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenBACK) // zeleno ozadje
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=50.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "Živijo,\nMali Učenjak!",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = GreenBACK
            )

            //Spacer(Modifier.height(1.dp))

            Image(
                painter = painterResource(id = R.drawable.balon),
                contentDescription = "mja mjau balon",
                modifier = Modifier
                    .size(300.dp)
                    //.padding(vertical = 0.dp)
            )

//            Text(
//                "Pridruži se mi na dogodivščini spoznanja črk.",
//                style = MaterialTheme.typography.bodyLarge
//            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainMenuActivity::class.java))
                    SoundPlayer.playPop(context)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("ŠTART")
            }
        }
    }
}
