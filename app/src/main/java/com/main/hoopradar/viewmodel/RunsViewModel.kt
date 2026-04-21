package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.data.repository.RunRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CreateRunState { // represents the possible states when creating a run
    object Idle : CreateRunState() // no action yet
    object Loading : CreateRunState() // run creation is in progress
    object Success : CreateRunState() // run was created successfully
    data class Error(val message: String) : CreateRunState() // run creation failed with an error message
}

sealed class JoinRunState { // represents the possible states when joining a run
    object Idle : JoinRunState() // no action yet
    object Loading : JoinRunState() // join/request action in progress
    object Joined : JoinRunState()       // joined instantly (open run)
    object Requested : JoinRunState()   // request sent (invite-only run)
    data class Error(val message: String) : JoinRunState() // failed with error message
}

class RunsViewModel : ViewModel() { // viewmodel that manages run data and run actions

    private val repo = RunRepository() // repository used to interact with run data

    private val _runs = MutableStateFlow<List<Run>>(emptyList()) // holds the current list of runs
    val runs: StateFlow<List<Run>> = _runs // read only runs state exposed to ui

    private val _createRunState = MutableStateFlow<CreateRunState>(CreateRunState.Idle)
    val createRunState: StateFlow<CreateRunState> = _createRunState // read only create run state for ui

    private val _joinRunState = MutableStateFlow<JoinRunState>(JoinRunState.Idle)
    val joinRunState: StateFlow<JoinRunState> = _joinRunState // read only join state for ui

    init { // load runs when viewmodel is first created
        loadRuns()
    }

    fun loadRuns() { // gets all runs from the repository
        viewModelScope.launch {
            try {
                _runs.value = repo.getRuns()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createRun(run: Run) { // creates a new run
        viewModelScope.launch {
            _createRunState.value = CreateRunState.Loading // show loading while creating run
            try {
                repo.createRun(run)
                loadRuns() // refresh run list after successful creation
                _createRunState.value = CreateRunState.Success // update state to success
            } catch (e: Exception) { // update state with error message
                _createRunState.value = CreateRunState.Error(e.message ?: "Failed to create run")
            }
        }
    }

    fun resetCreateRunState() { // resets create run state after ui handles it
        _createRunState.value = CreateRunState.Idle
    }

    fun joinOrRequest(run: Run, userId: String) { // join an open run or sends request for invite only run
        viewModelScope.launch {
            _joinRunState.value = JoinRunState.Loading // show loading while joining/requesting
            try {
                if (run.inviteOnly) { // send join request for private run
                    repo.requestJoin(run.id, userId)
                    _joinRunState.value = JoinRunState.Requested
                } else { // join immediately if run is public
                    repo.joinRun(run.id, userId)
                    _joinRunState.value = JoinRunState.Joined
                }
                loadRuns() // refresh runs after action
            } catch (e: Exception) { // update state with error message
                _joinRunState.value = JoinRunState.Error(e.message ?: "Failed")
            }
        }
    }

    fun resetJoinRunState() { // resets join/request state after ui handles it
        _joinRunState.value = JoinRunState.Idle
    }
}
