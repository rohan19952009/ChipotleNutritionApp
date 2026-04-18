package com.example.chipotlenutritionapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MenuDao {
    @Query("SELECT * FROM ingredients")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun getIngredientCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMeal(meal: SavedMealEntity): Long

    @Query("SELECT * FROM saved_meals")
    fun getSavedMeals(): Flow<List<SavedMealEntity>>

    @Insert
    suspend fun insertDailyLog(log: DailyLogEntity)

    @Query("SELECT * FROM daily_logs WHERE dateString = :date")
    fun getDailyLogsForDate(date: String): Flow<List<DailyLogEntity>>
    
    @Query("SELECT COUNT(*) FROM daily_logs WHERE dateString = :date")
    suspend fun getDailyLogCountForDate(date: String): Int
}

@Database(entities = [IngredientEntity::class, SavedMealEntity::class, DailyLogEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao
}
