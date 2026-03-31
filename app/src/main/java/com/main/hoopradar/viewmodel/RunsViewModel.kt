package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.data.repository.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RunsViewModel(
    private val repo: RunRepository = RunRepository()
) : ViewModel() {
    private val _runs = MutableStateFlow(repo.getMockRuns())
    val runs: StateFlow<List<Run>> = _runs

    fun joinRun(runId: String, userId: String) {
        _runs.value = _runs.value.map { run ->
            if (run.id == runId && userId !in run.playerIds && run.currentPlayers < run.maxPlayers) {
                run.copy(
                    currentPlayers = run.currentPlayers + 1,
                    playerIds = run.playerIds + userId
                )
            } else run
        }
    }
}