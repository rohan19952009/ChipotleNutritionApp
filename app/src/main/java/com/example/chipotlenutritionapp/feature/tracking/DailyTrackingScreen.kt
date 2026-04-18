package com.example.chipotlenutritionapp.feature.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DailyTrackingScreen(viewModel: DailyTrackingViewModel = hiltViewModel()) {
    val logs by viewModel.todayLogs.collectAsState()
    val total by viewModel.todayTotal.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Today's Diary", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Intake", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = (total.calories.toFloat() / viewModel.calorieGoal).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${total.calories} / ${viewModel.calorieGoal} kcal", style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = (total.protein.toFloat() / viewModel.proteinGoal).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.secondary)
                Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Protein: ${total.protein} / ${viewModel.proteinGoal}g", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        Text("Logged Meals", style = MaterialTheme.typography.titleMedium)
        
        if (logs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No meals logged today yet.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(logs) { log ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Meal #${log.mealId}", style = MaterialTheme.typography.bodyLarge)
                            Text("${log.computedNutrition.calories} Cal", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
