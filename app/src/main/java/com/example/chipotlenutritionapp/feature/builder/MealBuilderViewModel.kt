package com.example.chipotlenutritionapp.feature.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chipotlenutritionapp.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealBuilderViewModel @Inject constructor(
    private val repository: NutritionRepository
) : ViewModel() {

    private val _selectedIngredients = MutableStateFlow<Set<IngredientEntity>>(emptySet())
    val selectedIngredients = _selectedIngredients.asStateFlow()

    private val _currentMealType = MutableStateFlow(MealType.BOWL)
    val currentMealType = _currentMealType.asStateFlow()

    val allIngredients = repository.allIngredients.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val currentNutrition: Flow<NutritionInfo> = _selectedIngredients.map { ingredients ->
        ingredients.fold(NutritionInfo()) { acc, ingredient -> acc + ingredient.nutrition }
    }

    val warningBadges: Flow<List<String>> = currentNutrition.map { nutrition ->
        val badges = mutableListOf<String>()
        if (nutrition.sodium > 2000) badges.add("High Sodium")
        if (nutrition.calories > 1500) badges.add("High Calorie")
        if (nutrition.fat > 60) badges.add("High Fat")
        badges
    }

    init {
        viewModelScope.launch { repository.seedDatabaseIfNeeded() }
    }

    fun setMealType(type: MealType) {
        _currentMealType.value = type
        _selectedIngredients.value = emptySet()
    }

    fun toggleIngredient(ingredient: IngredientEntity) {
        val current = _selectedIngredients.value.toMutableSet()
        if (current.contains(ingredient)) {
            current.remove(ingredient)
        } else {
            if (ingredient.mutuallyExclusive) {
                current.removeAll { it.category == ingredient.category }
            }
            current.add(ingredient)
        }
        _selectedIngredients.value = current
    }

    fun saveMeal(name: String) {
        viewModelScope.launch {
            repository.saveMealAndLogDaily(name, _currentMealType.value, _selectedIngredients.value.toList())
        }
    }
}
