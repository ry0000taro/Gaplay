package com.example.ry0000tarodojo2026.ui.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerManager(private val scope: CoroutineScope) {
    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds = _remainingSeconds.asStateFlow()

    private var timerJob: Job? = null

    fun start(initialSeconds: Long) {
        _remainingSeconds.value = initialSeconds
        timerJob?.cancel() // 二重起動防止
        timerJob = scope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000)
                _remainingSeconds.value -= 1
            }
        }
    }

    fun stop() {
        timerJob?.cancel()
        _remainingSeconds.value = 0
    }
}