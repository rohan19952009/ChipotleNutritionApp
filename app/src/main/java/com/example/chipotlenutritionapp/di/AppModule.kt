package com.example.chipotlenutritionapp.di

import android.content.Context
import androidx.room.Room
import com.example.chipotlenutritionapp.data.AppDatabase
import com.example.chipotlenutritionapp.data.MenuDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "nutrition_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMenuDao(db: AppDatabase): MenuDao = db.menuDao()
}
