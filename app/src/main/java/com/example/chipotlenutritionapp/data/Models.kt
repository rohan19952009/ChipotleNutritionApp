package com.example.chipotlenutritionapp.data

import androidx.room.*

enum class MealType { BURRITO, BOWL, TACOS, SALAD }
enum class MenuCategory(val displayName: String) {
    BASE("Base & Rice"), BEANS("Beans"), PROTEIN("Protein"), 
    TOPPINGS("Toppings"), SALSA("Salsas"), EXTRAS("Extras")
}

data class NutritionInfo(
    val calories: Int = 0, val protein: Int = 0, val carbs: Int = 0,
    val fat: Int = 0, val sodium: Int = 0
) {
    operator fun plus(other: NutritionInfo) = NutritionInfo(
        calories + other.calories, protein + other.protein, carbs + other.carbs,
        fat + other.fat, sodium + other.sodium
    )
}

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val mutuallyExclusive: Boolean = false,
    @Embedded val nutrition: NutritionInfo
)

@Entity(tableName = "saved_meals")
data class SavedMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mealType: String,
    val ingredientIds: String // Comma-separated for offline simplicity
)

@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val dateString: String, // format yyyy-MM-dd
    val mealId: Long,
    @Embedded(prefix = "daily_") val computedNutrition: NutritionInfo
)
