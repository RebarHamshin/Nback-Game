package mobappdev.example.nback_cimpl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mobappdev.example.nback_cimpl.GameApplication
import mobappdev.example.nback_cimpl.NBackHelper
import mobappdev.example.nback_cimpl.data.UserPreferencesRepository

interface GameViewModel {
    val gameState: StateFlow<GameState>
    val score: StateFlow<Int>
    val highscore: StateFlow<Int>
    val nBack: Int

    fun setGameType(gameType: GameType)
    fun startGame()
    fun checkMatch()
    fun stopGame()
}

class GameVM(
    private val userPreferencesRepository: UserPreferencesRepository
) : GameViewModel, ViewModel() {



    private val _gameState = MutableStateFlow(GameState())
    override val gameState: StateFlow<GameState> get() = _gameState.asStateFlow()

    private val _score = MutableStateFlow(0)
    override val score: StateFlow<Int> get() = _score

    private val _highscore = MutableStateFlow(0)
    override val highscore: StateFlow<Int> get() = _highscore

    override val nBack: Int = 2

    private var job: Job? = null
    private val eventInterval = 2000L

    private val nBackHelper = NBackHelper()
    private var events: Array<Int> = emptyArray()



    override fun setGameType(gameType: GameType) {
        _gameState.value = _gameState.value.copy(gameType = gameType)
    }

    override fun startGame() {
        stopGame()

        _score.value = 0

        events = nBackHelper.generateNBackString(
            25,     // antal rundor
            9,
            30,
            nBack
        ).toList().toTypedArray()

        _gameState.value = _gameState.value.copy(
            currentRound = 0,
            currentIndex = 0,
            totalRounds = events.size,
            canMatch = false,
            lastGuessCorrect = null
        )

        job = viewModelScope.launch {
            when (_gameState.value.gameType) {
                GameType.Visual -> runVisualGame()
                GameType.Audio -> runAudioGame()
                GameType.AudioVisual -> runAudioVisualGame()
            }
            saveIfHighScore()
        }
    }



    override fun checkMatch() {
        val state = _gameState.value
        val index = state.currentIndex

        if (!state.canMatch) return

        var correct = false

        if (index >= nBack) {
            val curr = events[index]
            val prev = events[index - nBack]
            if (curr == prev) {
                _score.value += 1
                correct = true
            }
        }

        _gameState.value = state.copy(
            canMatch = false,
            lastGuessCorrect = correct
        )
    }



    override fun stopGame() {
        job?.cancel()
        job = null

        _gameState.value = _gameState.value.copy(
            eventValue = -1,
            currentRound = 0,
            currentIndex = 0,
            canMatch = false,
            lastGuessCorrect = null
        )
    }


    private suspend fun updateState(i: Int) {
        _gameState.value = _gameState.value.copy(
            eventValue = events[i],
            currentRound = i + 1,
            currentIndex = i,
            canMatch = true,
            lastGuessCorrect = null
        )
    }

    private suspend fun runVisualGame() {
        for (i in events.indices) {
            updateState(i)
            delay(eventInterval)
        }
    }

    private suspend fun runAudioGame() {
        for (i in events.indices) {
            updateState(i)
            delay(eventInterval)
        }
    }

    private suspend fun runAudioVisualGame() {
        for (i in events.indices) {
            updateState(i)
            delay(eventInterval)
        }
    }



    private fun saveIfHighScore() {
        viewModelScope.launch {
            if (_score.value > _highscore.value) {
                userPreferencesRepository.saveHighScore(_score.value)
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as GameApplication
                GameVM(app.userPreferencesRespository)
            }
        }
    }

    init {
        viewModelScope.launch {
            userPreferencesRepository.highscore.collect {
                _highscore.value = it
            }
        }
    }
}

enum class GameType {
    Audio,
    Visual,
    AudioVisual
}

data class GameState(
    val gameType: GameType = GameType.Visual,
    val eventValue: Int = -1,
    val currentIndex: Int = 0,
    val currentRound: Int = 0,
    val totalRounds: Int = 25,
    val nBackValue: Int = 2,
    val canMatch: Boolean = true,
    val lastGuessCorrect: Boolean? = null
)
