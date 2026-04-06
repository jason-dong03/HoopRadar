package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.data.repository.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreateRunState {
    object Idle : CreateRunState()
    object Loading : CreateRunState()
    object Success : CreateRunState()
    data class Error(val message: String) : CreateRunState()
}

sealed class JoinRunState {
    object Idle : JoinRunState()
    object Loading : JoinRunState()
    object Joined : JoinRunState()       // joined instantly (open run)
    object Requested : JoinRunState()   // request sent (invite-only run)
    data class Error(val message: String) : JoinRunState()
}

class RunsViewModel : ViewModel() {

    private val repo = RunRepository()

    private val _runs = MutableStateFlow<List<Run>>(emptyList())
    val runs: StateFlow<List<Run>> = _runs

    private val _createRunState = MutableStateFlow<CreateRunState>(CreateRunState.Idle)
    val createRunState: StateFlow<CreateRunState> = _createRunState

    private val _joinRunState = MutableStateFlow<JoinRunState>(JoinRunState.Idle)
    val joinRunState: StateFlow<JoinRunState> = _joinRunState

    init {
        loadRuns()
    }

    fun loadRuns() {
        viewModelScope.launch {
            try {
                _runs.value = repo.getRuns()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createRun(run: Run) {
        viewModelScope.launch {
            _createRunState.value = CreateRunState.Loading
            try {
                repo.createRun(run)
                loadRuns()
                _createRunState.value = CreateRunState.Success
            } catch (e: Exception) {
                _createRunState.value = CreateRunState.Error(e.message ?: "Failed to create run")
            }
        }
    }

    fun resetCreateRunState() {
        _createRunState.value = CreateRunState.Idle
    }

    fun joinOrRequest(run: Run, userId: String) {
        viewModelScope.launch {
            _joinRunState.value = JoinRunState.Loading
            try {
                if (run.inviteOnly) {
                    repo.requestJoin(run.id, userId)
                    _joinRunState.value = JoinRunState.Requested
                } else {
                    repo.joinRun(run.id, userId)
                    _joinRunState.value = JoinRunState.Joined
                }
                loadRuns()
            } catch (e: Exception) {
                _joinRunState.value = JoinRunState.Error(e.message ?: "Failed")
            }
        }
    }

    fun resetJoinRunState() {
        _joinRunState.value = JoinRunState.Idle
    }
}
