package com.example.chipotlenutritionapp.feature.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chipotlenutritionapp.data.DailyLogEntity
import com.example.chipotlenutritionapp.data.NutritionInfo
import com.example.chipotlenutritionapp.data.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class DailyTrackingViewModel @Inject constructor(
    private val repository: NutritionRepository
) : ViewModel() {

    val todayLogs = repository.getTodayLogs().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val todayTotal = todayLogs.map { logs ->
        logs.fold(NutritionInfo()) { acc, log -> acc + log.computedNutrition }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NutritionInfo())

    // Define smart goals - this could come from settings later
    val calorieGoal = 2200
    val proteinGoal = 150
}
