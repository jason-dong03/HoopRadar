package com.main.hoopradar.viewmodel
import androidx.lifecycle.ViewModel
import com.main.hoopradar.data.model.Court
import com.main.hoopradar.data.repository.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CourtsViewModel(
    private val repo: CourtRepository = CourtRepository()
) : ViewModel() {
    private val _courts = MutableStateFlow(repo.getMockCourts())
    val courts: StateFlow<List<Court>> = _courts
}