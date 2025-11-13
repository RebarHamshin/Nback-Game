package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    mode: String,
    vm: GameViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(mode) {
        val type = when (mode.lowercase()) {
            "visual" -> GameType.Visual
            "audio" -> GameType.Audio
            "audiovisual" -> GameType.AudioVisual
            else -> GameType.Visual
        }
        vm.setGameType(type)
    }

    val state = vm.gameState.collectAsState().value
    val score = vm.score.collectAsState().value

    val modeLabel = when (state.gameType) {
        GameType.Visual -> "Visual"
        GameType.Audio -> "Audio"
        GameType.AudioVisual -> "Audio + Visual"
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Round: ${state.currentRound} / ${state.totalRounds}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        "Score: $score",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onBack) { Text("Back") }

                    Text(
                        "Mode: $modeLabel",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            when (state.gameType) {
                GameType.Visual -> VisualStimulus(state.eventValue)

                GameType.Audio ->
                    AudioStimulus(
                        value = state.eventValue,
                        trigger = state.currentIndex
                    )

                GameType.AudioVisual -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    VisualStimulus(state.eventValue)
                    Spacer(Modifier.height(30.dp))
                    AudioStimulus(
                        value = state.eventValue,
                        trigger = state.currentIndex
                    )
                }
            }

            Spacer(Modifier.height(40.dp))


            val rawFeedback = state.lastGuessCorrect

            var transientFeedback by remember { mutableStateOf<Boolean?>(null) }

            var pressCounter by remember { mutableStateOf(0) }

            LaunchedEffect(pressCounter) {
                if (rawFeedback != null) {
                    transientFeedback = rawFeedback
                    delay(500)
                    transientFeedback = null
                }
            }

            val buttonColor by animateColorAsState(
                when (transientFeedback) {
                    true -> Color(0xFF4CAF50)
                    false -> Color(0xFFF44336)
                    null -> MaterialTheme.colorScheme.primary
                }
            )

            val buttonScale by animateFloatAsState(
                if (transientFeedback == false) 1.1f else 1f
            )

            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.7f)
                    .graphicsLayer(
                        scaleX = buttonScale,
                        scaleY = buttonScale
                    ),
                colors = ButtonDefaults.buttonColors(buttonColor),
                onClick = {
                    vm.checkMatch()
                    pressCounter++
                }
            ) {
                Text("Match!")
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}
