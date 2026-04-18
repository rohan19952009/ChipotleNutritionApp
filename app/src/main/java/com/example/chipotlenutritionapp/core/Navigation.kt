package com.example.chipotlenutritionapp.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.example.chipotlenutritionapp.feature.builder.MealBuilderScreen
import com.example.chipotlenutritionapp.feature.tracking.DailyTrackingScreen
import com.example.chipotlenutritionapp.feature.settings.SettingsScreen

@Composable
fun AppNavigation(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val isTablet = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    Scaffold(
        bottomBar = {
            if (!isTablet) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = "Builder") },
                        label = { Text("Builder") },
                        selected = currentDestination?.hierarchy?.any { it.route == "builder" } == true,
                        onClick = { navController.navigate("builder") { launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Daily") },
                        label = { Text("Daily log") },
                        selected = currentDestination?.hierarchy?.any { it.route == "daily" } == true,
                        onClick = { navController.navigate("daily") { launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentDestination?.hierarchy?.any { it.route == "settings" } == true,
                        onClick = { navController.navigate("settings") { launchSingleTop = true } }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "builder", Modifier.padding(innerPadding)) {
            composable("builder") {
                MealBuilderScreen(isTablet = isTablet)
            }
            composable("daily") {
                DailyTrackingScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}
