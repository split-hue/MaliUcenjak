package com.example.ucenjeabecede.components


import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ucenjeabecede.R


@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text)
    }
}

@Composable
fun ImageButtonPonoviCrke(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { if (enabled) onClick() }, //klik samo če je ima naučene črke
        modifier = modifier.size(102.dp) //velkost gumba
    ) {
        Image(
            painter = painterResource(id = R.drawable.ponovi_crke),
            contentDescription = "Ponovi črke",
            modifier = Modifier
                .size(90.dp) //velikost ikone v gumbu
                .alpha(if (enabled) 1f else 0.3f) // zatemnitev, če ni enabled
        )
    }
}
@Composable
fun ImageButtonNoveCrke(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.nove_crke),
            contentDescription = "Nove črke",
            modifier = Modifier.size(90.dp) //velikost ikone v gumbu
        )
    }
}
//============================
@Composable
fun Exit(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            (context as? ComponentActivity)?.finish()
        },
        modifier = modifier.size(500.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.izhod),
            contentDescription = "izhod",
            modifier = Modifier.size(190.dp)
        )
    }
}
//============================
@Composable
fun Erasor(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pobrisi),
            contentDescription = "Nove črke",
            modifier = Modifier.size(90.dp)
        )
    }
}
@Composable
fun Home(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(90.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.domov),
            contentDescription = "domov",
            modifier = Modifier.size(80.dp)
        )
    }
}
@Composable
fun Next(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(90.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.puscica),
            contentDescription = "naprej",
            modifier = Modifier
                .size(80.dp)
                .alpha(if (enabled) 1f else 0.3f) //ne dela zatemnitev lmao
        )
    }
}
@Composable
fun Check(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(110.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.preveri),
            contentDescription = "Nove črke",
            modifier = Modifier.size(100.dp)
        )
    }
}
//***********************gumbi za pos. črko mew*****************************************
@Composable
fun Acrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.a),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Bcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.b),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Ccrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.c),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun CCcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.cc),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Dcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.d),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Ecrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.e),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Fcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.f),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Gcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.g),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Hcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.h),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Icrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.i),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Jcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.j),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Kcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.k),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Lcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.l),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Mcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.m),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Ncrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.n),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Ocrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.o),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Pcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.p),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Rcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.r),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Scrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.s),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun SScrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ss),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Tcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.t),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Ucrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.u),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Vcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.v),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun Zcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.z),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}
@Composable
fun ZZcrka(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { if (enabled) onClick() },
        modifier = modifier.size(102.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.zz),
            contentDescription = "a",
            modifier = Modifier
                .size(90.dp)
                .alpha(if (enabled) 1f else 0.3f)
        )
    }
}