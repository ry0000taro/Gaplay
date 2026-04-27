package com.example.ry0000tarodojo2026.ui.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// クラス名を衝突しない名前に変更 ★
class ExerciseTimerManager(private val scope: CoroutineScope) {
    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds = _remainingSeconds.asStateFlow()

    private val _isExercisePhase = MutableStateFlow(false)
    val isExercisePhase = _isExercisePhase.asStateFlow()

    private var timerJob: Job? = null

    fun start(videoSeconds: Long, exerciseSeconds: Long) {
        timerJob?.cancel()
        _isExercisePhase.value = false
        _remainingSeconds.value = videoSeconds
        
        timerJob = scope.launch {
            // Video phase
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
            
            // Transition to exercise phase
            if (exerciseSeconds > 0) {
                _isExercisePhase.value = true
                _remainingSeconds.value = exerciseSeconds
                while (_remainingSeconds.value > 0) {
                    delay(1000)
                    _remainingSeconds.value -= 1
                }
            }
            _isExercisePhase.value = false
        }
    }

    fun stop() {
        timerJob?.cancel()
        _remainingSeconds.value = 0
        _isExercisePhase.value = false
    }
}