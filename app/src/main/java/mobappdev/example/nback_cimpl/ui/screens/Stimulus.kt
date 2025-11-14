package mobappdev.example.nback_cimpl.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable //Shows a 3x3 grid
fun VisualStimulus(value: Int) {
    val activeIndex = value - 1

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until 3) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col

                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .padding(4.dp)
                            .background(
                                if (index == activeIndex) Color(0xFF4CAF50)
                                else Color(0xFFDDDDDD)
                            )
                    )
                }
            }
        }
    }
}

@Composable //Plays audio
fun AudioStimulus(
    value: Int,
    trigger: Int
) {
    val context = LocalContext.current

    val letter = if (value in 1..9) {
        ('A' + (value - 1)).toString()
    } else "-"

    val tts = remember {
        TextToSpeech(context) {}
    }.apply {
        language = Locale.ENGLISH
    }

    LaunchedEffect(trigger) {
        if (letter != "-" && value != -1) {
            tts.speak(letter, TextToSpeech.QUEUE_FLUSH, null, "nback_letter")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Text(
        text = letter,
        style = MaterialTheme.typography.displayLarge,
        textAlign = TextAlign.Center
    )
}


