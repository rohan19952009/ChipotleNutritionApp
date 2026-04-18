package com.example.chipotlenutritionapp.feature.builder

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    var showBreakdown by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (!isTablet) {
                NutritionBottomBar(
                    nutrition = nutrition,
                    warnings = warnings,
                    onSave = { showSaveDialog = true },
                    onImageClick = { showBreakdown = true }
                )
            }
        }
    ) { paddingVals ->
        if (isTablet) {
            Row(Modifier.fillMaxSize().padding(paddingVals).background(MaterialTheme.colorScheme.background)) {
                Box(Modifier.weight(0.6f)) { BuilderGrid(ingredients, selected, viewModel) }
                Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
                Box(Modifier.weight(0.4f)) { NutritionSideBar(nutrition, warnings, { showSaveDialog = true }, { showBreakdown = true }) }
            }
        } else {
            Box(Modifier.fillMaxSize().padding(paddingVals).background(MaterialTheme.colorScheme.background)) {
                BuilderGrid(ingredients, selected, viewModel)
            }
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

    if (showBreakdown) {
        BreakdownModal(nutrition = nutrition, onDismiss = { showBreakdown = false })
    }
}

@Composable
fun BuilderGrid(ingredients: List<IngredientEntity>, selected: Set<IngredientEntity>, viewModel: MealBuilderViewModel) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    "Craft Your Masterpiece", 
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Select ingredients to instantly calculate your macros.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        MenuCategory.values().forEach { category ->
            val catItems = ingredients.filter { it.category == category.name }
            if (catItems.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = category.displayName.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }
                items(catItems) { item ->
                    IngredientCard(item, selected.contains(item)) { viewModel.toggleIngredient(item) }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun IngredientCard(ingredient: IngredientEntity, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val outlineColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(Modifier.fillMaxSize().padding(12.dp)) {
            Column(Modifier.align(Alignment.TopStart)) {
                Text(ingredient.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${ingredient.nutrition.calories} Cal", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.tertiary)
            }
            if (isSelected) {
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primary).padding(6.dp)
                ) {
                    Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NutritionBottomBar(nutrition: NutritionInfo, warnings: List<String>, onSave: () -> Unit, onImageClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.clickable { onImageClick() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("TOTAL CALORIES", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.Info, contentDescription = "Breakdown", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                    }
                    Text("${nutrition.calories}", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("LOG MEAL", fontWeight = FontWeight.Bold)
                }
            }
            
            AnimatedVisibility(warnings.isNotEmpty()) {
                Column(Modifier.padding(top = 8.dp)) {
                    warnings.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                MacroPill("Protein", "${nutrition.protein}g")
                MacroPill("Carbs", "${nutrition.carbs}g")
                MacroPill("Fat", "${nutrition.fat}g")
            }
        }
    }
}

@Composable
fun MacroPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
fun NutritionSideBar(nutrition: NutritionInfo, warnings: List<String>, onSave: () -> Unit, onBreakdown: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text("Nutrition Summary", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onBreakdown() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text("CALS", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary)
                Text("${nutrition.calories}", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp), color = MaterialTheme.colorScheme.primary)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MacroPill("Protein", "${nutrition.protein}g")
            MacroPill("Carbs", "${nutrition.carbs}g")
            MacroPill("Fat", "${nutrition.fat}g")
        }
        Spacer(Modifier.height(24.dp))
        Text("Sodium: ${nutrition.sodium}mg", style = MaterialTheme.typography.titleLarge)
        
        Spacer(Modifier.height(24.dp))
        warnings.forEach { Text("⚠ $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge) }
        
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onSave, 
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) { 
            Text("LOG MEAL TODAY", fontWeight = FontWeight.Bold, fontSize = 18.sp) 
        }
    }
}

@Composable
fun SaveMealDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Meal", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        text = { 
            OutlinedTextField(
                value = text, 
                onValueChange = { text = it }, 
                label = { Text("Give it a name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            ) 
        },
        confirmButton = { Button(onClick = { onSave(text.ifEmpty { "Custom Bowl" }) }) { Text("Save to Diary") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownModal(nutrition: NutritionInfo, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().padding(24.dp)) {
            Text("Detailed Breakdown", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(24.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Fat", style = MaterialTheme.typography.titleLarge)
                Text("${nutrition.fat}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Carbohydrates", style = MaterialTheme.typography.titleLarge)
                Text("${nutrition.carbs}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Protein", style = MaterialTheme.typography.titleLarge)
                Text("${nutrition.protein}g", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sodium", style = MaterialTheme.typography.titleLarge)
                Text("${nutrition.sodium}mg", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}
