package com.example.chipotlenutritionapp.feature.builder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chipotlenutritionapp.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealBuilderScreen(
    isTablet: Boolean,
    viewModel: MealBuilderViewModel = hiltViewModel()
) {
    val ingredients by viewModel.allIngredients.collectAsState()
    val selected by viewModel.selectedIngredients.collectAsState()
    val nutrition by viewModel.currentNutrition.collectAsState(initial = NutritionInfo())
    val warnings by viewModel.warningBadges.collectAsState(initial = emptyList())
    var showSaveDialog by remember { mutableStateOf(false) }

    if (isTablet) {
        // Responsive tablet layout: Builder on left, Status on right
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.weight(0.6f)) { BuilderList(ingredients, selected, viewModel) }
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
            Box(Modifier.weight(0.4f)) { NutritionSideBar(nutrition, warnings) { showSaveDialog = true } }
        }
    } else {
        // Standard phone layout
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) { BuilderList(ingredients, selected, viewModel) }
            NutritionBottomBar(nutrition, warnings) { showSaveDialog = true }
        }
    }

    if (showSaveDialog) {
        SaveMealDialog(
            onDismiss = { showSaveDialog = false },
            onSave = { name -> 
                viewModel.saveMeal(name)
                showSaveDialog = false 
            }
        )
    }
}

@Composable
fun BuilderList(ingredients: List<IngredientEntity>, selected: Set<IngredientEntity>, viewModel: MealBuilderViewModel) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        item { Text("Build Your Meal", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp)) }
        MenuCategory.values().forEach { category ->
            val catItems = ingredients.filter { it.category == category.name }
            if (catItems.isNotEmpty()) {
                item { Text(category.displayName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)) }
                items(catItems) { item ->
                    IngredientRow(item, selected.contains(item)) { viewModel.toggleIngredient(item) }
                }
            }
        }
    }
}

@Composable
fun IngredientRow(ingredient: IngredientEntity, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(ingredient.name, style = MaterialTheme.typography.bodyLarge)
            Text("${ingredient.nutrition.calories} Cal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Checkbox(checked = isSelected, onCheckedChange = null)
    }
}

@Composable
fun NutritionBottomBar(nutrition: NutritionInfo, warnings: List<String>, onSave: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Column {
                Text("Total: ${nutrition.calories} Cal", style = MaterialTheme.typography.titleMedium)
                Row { warnings.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(end = 4.dp)) } }
            }
            Button(onClick = onSave) { Text("Log Meal") }
        }
    }
}

@Composable
fun NutritionSideBar(nutrition: NutritionInfo, warnings: List<String>, onSave: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Nutrition Summary", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Calories: ${nutrition.calories}", style = MaterialTheme.typography.titleMedium)
        Text("Protein: ${nutrition.protein}g")
        Text("Carbs: ${nutrition.carbs}g")
        Text("Fat: ${nutrition.fat}g")
        Text("Sodium: ${nutrition.sodium}mg")
        Spacer(Modifier.height(16.dp))
        warnings.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) { Text("Log Meal Today") }
    }
}

@Composable
fun SaveMealDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Meal") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Meal Name") }) },
        confirmButton = { Button(onClick = { onSave(text.ifEmpty { "My Meal" }) }) { Text("Save to Diary") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
