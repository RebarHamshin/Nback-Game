package mobappdev.example.nback_cimpl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import mobappdev.example.nback_cimpl.R
import mobappdev.example.nback_cimpl.ui.viewmodels.GameType
import mobappdev.example.nback_cimpl.ui.viewmodels.GameViewModel

@Composable
fun HomeScreen(
    vm: GameViewModel,
    onStartVisual: () -> Unit,
    onStartAudio: () -> Unit
) {
    val highscore by vm.highscore.collectAsState()
    val gameState by vm.gameState.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(remember { SnackbarHostState() }) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.padding(16.dp),
                text = "High Score: $highscore",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Current Settings",
                style = MaterialTheme.typography.headlineSmall
            )

            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("n-Back: ${vm.nBack}")
                Text("Event Interval: 2 sec")
                Text("Rounds per game: ${gameState.totalRounds}")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.padding(16.dp),
                text = "Start Game",
                style = MaterialTheme.typography.displaySmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // 🔊 AUDIO GAME BUTTON
                Button(
                    onClick = {
                        vm.stopGame()                // Stoppa ev. gammalt spel
                        vm.setGameType(GameType.Audio)
                        vm.startGame()               // Starta nytt
                        onStartAudio()               // Navigera
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sound_on),
                        contentDescription = "Audio",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }

                // 👁 VISUAL GAME BUTTON
                Button(
                    onClick = {
                        vm.stopGame()
                        vm.setGameType(GameType.Visual)
                        vm.startGame()
                        onStartVisual()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.visual),
                        contentDescription = "Visual",
                        modifier = Modifier
                            .height(48.dp)
                            .aspectRatio(3f / 2f)
                    )
                }
            }
        }
    }
}
