package com.example.chipotlenutritionapp.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NutritionRepository @Inject constructor(private val dao: MenuDao) {
    val allIngredients: Flow<List<IngredientEntity>> = dao.getAllIngredients()

    suspend fun seedDatabaseIfNeeded() {
        if (dao.getIngredientCount() == 0) {
            val items = listOf(
                IngredientEntity("rice_white", "White Rice", MenuCategory.BASE.name, true, NutritionInfo(210, 4, 40, 4, 350)),
                IngredientEntity("beans_black", "Black Beans", MenuCategory.BEANS.name, false, NutritionInfo(130, 8, 22, 2, 210)),
                IngredientEntity("prot_chicken", "Chicken", MenuCategory.PROTEIN.name, false, NutritionInfo(180, 32, 0, 7, 310)),
                IngredientEntity("prot_steak", "Steak", MenuCategory.PROTEIN.name, false, NutritionInfo(150, 21, 1, 6, 330)),
                IngredientEntity("salsa_hot", "Hot Salsa", MenuCategory.SALSA.name, false, NutritionInfo(30, 0, 4, 0, 500)),
                IngredientEntity("top_cheese", "Cheese", MenuCategory.TOPPINGS.name, false, NutritionInfo(110, 6, 1, 8, 190)),
                IngredientEntity("ext_guac", "Guacamole", MenuCategory.EXTRAS.name, false, NutritionInfo(230, 2, 8, 22, 370))
            )
            dao.insertIngredients(items)
        }
    }

    suspend fun saveMealAndLogDaily(name: String, type: MealType, ingredients: List<IngredientEntity>) {
        val totalNutrition = ingredients.fold(NutritionInfo()) { acc, inv -> acc + inv.nutrition }
        
        val mealId = dao.saveMeal(
            SavedMealEntity(
                name = name,
                mealType = type.name,
                ingredientIds = ingredients.joinToString(",") { it.id }
            )
        )
        
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        dao.insertDailyLog(DailyLogEntity(dateString = today, mealId = mealId, computedNutrition = totalNutrition))
    }

    fun getTodayLogs(): Flow<List<DailyLogEntity>> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return dao.getDailyLogsForDate(today)
    }
}
